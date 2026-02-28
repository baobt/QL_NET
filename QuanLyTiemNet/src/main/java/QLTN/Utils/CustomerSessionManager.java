package QLTN.Utils;

import QLTN.Dao.CustomerDao;
import QLTN.Dao.ComputerDao;
import QLTN.Dao.ComputerGroupDao;
import QLTN.Dao.TransactionDao;
import QLTN.Entity.Customer;
import QLTN.Entity.Computer;
import QLTN.Entity.ComputerGroup;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;


public class CustomerSessionManager {
    private static CustomerSessionManager instance;
    private Map<Integer, CustomerSessionInfo> activeSessions;
    private Map<Integer, List<CustomerSessionEndListener>> sessionEndListeners;
    private CustomerDao customerDao;
    private ComputerDao computerDao;
    private ComputerGroupDao computerGroupDao;
    private TransactionDao transactionDao;
    
    private CustomerSessionManager() {
        activeSessions = new HashMap<>();
        sessionEndListeners = new HashMap<>();
        customerDao = new CustomerDao();
        computerDao = new ComputerDao();
        computerGroupDao = new ComputerGroupDao();
        transactionDao = new TransactionDao();
    }
    
    public static CustomerSessionManager getInstance() {
        if (instance == null) {
            instance = new CustomerSessionManager();
        }
        return instance;
    }
    
    public interface CustomerSessionEndListener {
        void onSessionEnd(String reason);
    }
    
    public void addSessionEndListener(int computerId, CustomerSessionEndListener listener) {
        sessionEndListeners.computeIfAbsent(computerId, k -> new ArrayList<>()).add(listener);
    }
    
    public void removeSessionEndListener(int computerId, CustomerSessionEndListener listener) {
        List<CustomerSessionEndListener> listeners = sessionEndListeners.get(computerId);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                sessionEndListeners.remove(computerId);
            }
        }
    }
    
    private void notifySessionEnd(int computerId, String reason) {
        List<CustomerSessionEndListener> listeners = sessionEndListeners.get(computerId);
        if (listeners != null) {
            for (CustomerSessionEndListener listener : new ArrayList<>(listeners)) {
                listener.onSessionEnd(reason);
            }
        }
    }
//  
    public void createCustomerSession(int computerId, Customer customer) {
        try {
            // Lấy thông tin máy và nhóm máy
            Computer computer = computerDao.GetComputer(String.valueOf(computerId));
            ComputerGroup group = computerGroupDao.SelectbyID(String.valueOf(computer.getGroupId()));
            
            // Tạo session info
            CustomerSessionInfo session = new CustomerSessionInfo();
            session.setComputerId(computerId);
            session.setCustomer(customer);
            session.setComputer(computer);
            session.setComputerGroup(group);
            session.setStartTime(LocalDateTime.now());
            session.setInitialBalance(customer.getBalance());
            session.setCurrentBalance(customer.getBalance());
            
            // Tính tổng thời gian từ số dư
            session.calculateTotalTime();
            session.setRemainingTimeSeconds(session.getTotalTimeSeconds());
            
            activeSessions.put(computerId, session);
            
            // Tạo record trong ComputerUsage
            JdbcHelperQLTN.update(
                "INSERT INTO ComputerUsage (computer_id, customer_id, start_time, hourly_rate, total_amount) VALUES (?, ?, CURRENT_TIMESTAMP, ?, ?)",
                computerId, customer.getCustomerId(), group.getHourlyRate(), customer.getBalance()
            );
            
            // Cập nhật trạng thái máy
            JdbcHelperQLTN.update("UPDATE Computer SET status = 'active' WHERE computer_id = ?", computerId);
            
            SessionManager.getInstance().notifyComputerStatusChange(computerId, "active");
            
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo phiên khách hàng: " + e.getMessage(), e);
        }
    }
    
    /**
     * Lấy thông tin phiên
     */
    public CustomerSessionInfo getCustomerSession(int computerId) {
        return activeSessions.get(computerId);
    }
    
    /**
     * Cập nhật số dư khi nhân viên nạp tiền
     */
    public void addMoneyToCustomerSession(int computerId, double amount) {
        CustomerSessionInfo session = activeSessions.get(computerId);
        if (session != null) {
            // Cập nhật số dư hiện tại
            BigDecimal newBalance = session.getCurrentBalance().add(BigDecimal.valueOf(amount));
            session.setCurrentBalance(newBalance);
            
            // Tính lại tổng thời gian
            session.calculateTotalTime();
            
            // Cộng thêm thời gian còn lại
            double additionalHours = amount / session.getComputerGroup().getHourlyRate().doubleValue();
            int additionalSeconds = (int)(additionalHours * 3600);
            session.setRemainingTimeSeconds(session.getRemainingTimeSeconds() + additionalSeconds);
            
            // Cập nhật số dư trong database
            JdbcHelperQLTN.update("UPDATE Customer SET balance = ? WHERE customer_id = ?", 
                newBalance, session.getCustomer().getCustomerId());
                
            try {
                ResultSet rs = JdbcHelperQLTN.query(
                    "SELECT usage_id FROM ComputerUsage WHERE computer_id = ? AND end_time IS NULL",
                    computerId
                );
                if (rs.next()) {
                    int usageId = rs.getInt("usage_id");
                    transactionDao.createTopupTransaction(
                        session.getCustomer().getCustomerId(),
                        usageId,
                        amount,
                        "cash"
                    );
                }
            } catch (Exception e) {
                System.err.println("Lỗi ghi giao dịch nạp tiền: " + e.getMessage());
            }
        }
    }
    
    /**
     * Kết thúc phiên và cập nhật số dư
     */
    public void endCustomerSession(int computerId, String reason) {
        CustomerSessionInfo session = activeSessions.get(computerId);
        if (session != null) {
            try {
                // Tính số tiền đã sử dụng
                double usedHours = session.getUsedTimeSeconds() / 3600.0;
                BigDecimal usedAmount = session.getComputerGroup().getHourlyRate()
                    .multiply(BigDecimal.valueOf(usedHours));
                
                // Tính số dư còn lại
                BigDecimal finalBalance = session.getInitialBalance().subtract(usedAmount);
                finalBalance = finalBalance.max(BigDecimal.ZERO); // Không để âm
                
                // Cập nhật số dư trong database
                JdbcHelperQLTN.update("UPDATE Customer SET balance = ? WHERE customer_id = ?", 
                    finalBalance, session.getCustomer().getCustomerId());
                
                // Cập nhật ComputerUsage
                JdbcHelperQLTN.update(
                    "UPDATE ComputerUsage SET end_time = CURRENT_TIMESTAMP, total_amount = ? WHERE computer_id = ? AND end_time IS NULL",
                    usedAmount, computerId
                );
                
                // Cập nhật trạng thái máy
                JdbcHelperQLTN.update("UPDATE Computer SET status = 'inactive' WHERE computer_id = ?", computerId);
                
                // Thông báo kết thúc phiên
                notifySessionEnd(computerId, reason);
                
                SessionManager.getInstance().notifyComputerStatusChange(computerId, "inactive");
                
                // Xóa session
                activeSessions.remove(computerId);
                sessionEndListeners.remove(computerId);
                
            } catch (Exception e) {
                System.err.println("Lỗi kết thúc phiên khách hàng: " + e.getMessage());
            }
        }
    }
    
    /**
     * Kiểm tra có phiên hoạt động không
     */
    public boolean hasActiveCustomerSession(int computerId) {
        return activeSessions.containsKey(computerId);
    }
}