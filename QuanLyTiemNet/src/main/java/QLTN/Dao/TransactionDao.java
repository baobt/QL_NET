package QLTN.Dao;

import QLTN.Entity.Transaction;
import QLTN.Utils.JdbcHelperQLTN;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho Transaction
 */
public class TransactionDao extends QLTNSysDao<Transaction, String> {
    
    private final String INSERT_SQL = "INSERT INTO [Transaction] (customer_id, usage_id, type, amount, method, reference_id) VALUES (?, ?, ?, ?, ?, ?)";
    private final String SELECT_ALL_SQL = "SELECT * FROM [Transaction]";
    private final String SELECT_BY_ID_SQL = "SELECT * FROM [Transaction] WHERE transaction_id = ?";
    
    @Override
    public void insert(Transaction entity) {
        JdbcHelperQLTN.update(INSERT_SQL, 
            entity.getCustomerId(),
            entity.getUsageId(),
            entity.getType(),
            entity.getAmount(),
            entity.getMethod(),
            entity.getReferenceId()
        );
    }
    
    @Override
    public void update(Transaction entity) {
        // Implement if needed
    }
    
    @Override
    public void delete(String id) {
        // Implement if needed
    }
    
    @Override
    public List<Transaction> selectAll() {
        return selectbySql(SELECT_ALL_SQL);
    }
    
    @Override
    public Transaction SelectbyID(String id) {
        List<Transaction> list = selectbySql(SELECT_BY_ID_SQL, id);
        return list.isEmpty() ? null : list.get(0);
    }
    
    @Override
    protected List<Transaction> selectbySql(String sql, Object... args) {
        List<Transaction> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelperQLTN.query(sql, args);
            while (rs.next()) {
                Transaction transaction = new Transaction(
                    rs.getInt("transaction_id"),
                    rs.getInt("customer_id"),
                    rs.getObject("usage_id") != null ? rs.getInt("usage_id") : null,
                    rs.getObject("order_id") != null ? rs.getInt("order_id") : null,
                    rs.getString("type"),
                    rs.getBigDecimal("amount"),
                    rs.getTimestamp("transaction_time").toLocalDateTime(),
                    rs.getString("method"),
                    rs.getString("status"),
                    rs.getString("reference_id")
                );
                list.add(transaction);
            }
            rs.getStatement().getConnection().close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }
    
    @Override
    public Transaction Login(String username, String password) {
        throw new UnsupportedOperationException("Not supported for Transaction");
    }
    
    /**
     * Tạo transaction cho việc nạp tiền
     */
    public void createTopupTransaction(int customerId, Integer usageId, double amount, String method) {
        try {
            JdbcHelperQLTN.update(INSERT_SQL, 
                customerId, 
                usageId, 
                "topup", 
                amount, 
                method, 
                "TOPUP_" + System.currentTimeMillis()
            );
        } catch (Exception e) {
            throw new RuntimeException("Lỗi tạo transaction: " + e.getMessage(), e);
        }
    }
}
