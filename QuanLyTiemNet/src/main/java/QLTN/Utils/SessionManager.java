package QLTN.Utils;

import QLTN.Entity.ComputerGroup;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

/**
 * Quản lý phiên sử dụng máy tính (cập nhật cho khách hàng có tài khoản)
 */
public class SessionManager {
    private static SessionManager instance;
    private Map<Integer, SessionInfo> activeSessions;
    private Map<Integer, List<SessionEndListener>> sessionEndListeners;
    private Map<Integer, List<ComputerStatusChangeListener>> statusChangeListeners;

    private SessionManager() {
        activeSessions = new HashMap<>();
        sessionEndListeners = new HashMap<>();
        statusChangeListeners = new HashMap<>();
    }

    public static SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    // Interface để lắng nghe sự kiện kết thúc phiên
    public interface SessionEndListener {
        void onSessionEnd(String reason);
    }

    // Interface để lắng nghe sự kiện thay đổi trạng thái máy
    public interface ComputerStatusChangeListener {
        void onComputerStatusChange(int computerId, String newStatus);
    }

    // Đăng ký listener cho phiên
    public void addSessionEndListener(int computerId, SessionEndListener listener) {
        sessionEndListeners.computeIfAbsent(computerId, k -> new ArrayList<>()).add(listener);
    }

    // Đăng ký listener cho thay đổi trạng thái
    public void addComputerStatusChangeListener(ComputerStatusChangeListener listener) {
        statusChangeListeners.computeIfAbsent(0, k -> new ArrayList<>()).add(listener);
    }

    // Xóa listener
    public void removeSessionEndListener(int computerId, SessionEndListener listener) {
        List<SessionEndListener> listeners = sessionEndListeners.get(computerId);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                sessionEndListeners.remove(computerId);
            }
        }
    }

    // Xóa listener thay đổi trạng thái
    public void removeComputerStatusChangeListener(ComputerStatusChangeListener listener) {
        List<ComputerStatusChangeListener> listeners = statusChangeListeners.get(0);
        if (listeners != null) {
            listeners.remove(listener);
            if (listeners.isEmpty()) {
                statusChangeListeners.remove(0);
            }
        }
    }

    // Thông báo kết thúc phiên
    private void notifySessionEnd(int computerId, String reason) {
        List<SessionEndListener> listeners = sessionEndListeners.get(computerId);
        if (listeners != null) {
            for (SessionEndListener listener : new ArrayList<>(listeners)) {
                listener.onSessionEnd(reason);
            }
        }
    }

    // Thông báo thay đổi trạng thái
    public void notifyComputerStatusChange(int computerId, String newStatus) {
        List<ComputerStatusChangeListener> listeners = statusChangeListeners.get(0);
        if (listeners != null) {
            for (ComputerStatusChangeListener listener : new ArrayList<>(listeners)) {
                listener.onComputerStatusChange(computerId, newStatus);
            }
        }
    }

    // Tạo phiên cho khách vãng lai
    public void createSession(int computerId, double prepaidAmount, ComputerGroup group) {
        SessionInfo session = new SessionInfo();
        session.setComputerId(computerId);
        session.setCustomerId(1); // Khách vãng lai
        session.setPrepaidAmount(BigDecimal.valueOf(prepaidAmount));
        session.setHourlyRate(group.getHourlyRate());
        session.setStartTime(LocalDateTime.now());
        
        // Tính tổng thời gian (giây)
        double hours = prepaidAmount / group.getHourlyRate().doubleValue();
        session.setTotalTimeSeconds((int)(hours * 3600));
        session.setRemainingTimeSeconds(session.getTotalTimeSeconds());
        
        activeSessions.put(computerId, session);
        
        // Thông báo trạng thái active
        notifyComputerStatusChange(computerId, "active");
    }

    // XÓA: Loại bỏ createCustomerSession vì CustomerSessionManager xử lý khách có tài khoản
    /*
    public void createCustomerSession(int computerId, double accountBalance, ComputerGroup group, int customerId) {
        ...
    }
    */

    // XÓA: Loại bỏ updateCustomerSession vì CustomerSessionManager xử lý
    /*
    public void updateCustomerSession(int computerId, double newBalance, ComputerGroup group) {
        ...
    }
    */

    public SessionInfo getSession(int computerId) {
        return activeSessions.get(computerId);
    }

    public void removeSession(int computerId) {
        activeSessions.remove(computerId);
        sessionEndListeners.remove(computerId);
    }

    // Kết thúc phiên với lý do
    public void endSession(int computerId, String reason) {
        notifySessionEnd(computerId, reason);
        removeSession(computerId);
        // Thông báo trạng thái inactive
        notifyComputerStatusChange(computerId, "inactive");
    }

    // Nạp tiền cho phiên hiện tại (cho khách vãng lai)
    public void addMoney(int computerId, double amount, ComputerGroup group) {
        SessionInfo session = activeSessions.get(computerId);
        if (session != null) {
            session.setPrepaidAmount(session.getPrepaidAmount().add(BigDecimal.valueOf(amount)));
            
            double additionalHours = amount / group.getHourlyRate().doubleValue();
            int additionalSeconds = (int)(additionalHours * 3600);
            
            session.setTotalTimeSeconds(session.getTotalTimeSeconds() + additionalSeconds);
            session.setRemainingTimeSeconds(session.getRemainingTimeSeconds() + additionalSeconds);
        }
    }

    public boolean hasActiveSession(int computerId) {
        return activeSessions.containsKey(computerId);
    }
}