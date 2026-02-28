package QLTN.Dao;




import QLTN.Entity.CustomerMessage;
import QLTN.Utils.JdbcHelperQLTN;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerMessageDAO {

    public int addMessage(CustomerMessage message) {
        String sql = "INSERT INTO CustomerMessage (session_id, customer_id, content, is_read) "
                + "VALUES (?, ?, ?, 0); "
                + "UPDATE ChatSession SET has_unread = 1 WHERE session_id = ?; "
                + "SELECT SCOPE_IDENTITY() AS message_id";

        try {
            ResultSet rs = JdbcHelperQLTN.query(sql,
                    message.getSessionId(),
                    message.getCustomerId(),
                    message.getContent(),
                    message.getSessionId());

            if (rs.next()) {
                int messageId = rs.getInt("message_id");
                rs.getStatement().getConnection().close();
                return messageId;
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<CustomerMessage> getMessagesBySessionId(int sessionId) {
        String sql = "SELECT * FROM CustomerMessage WHERE session_id = ? ORDER BY sent_at ASC";

        List<CustomerMessage> messages = new ArrayList<>();

        try {
            ResultSet rs = JdbcHelperQLTN.query(sql, sessionId);

            while (rs.next()) {
                messages.add(extractMessageFromResultSet(rs));
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return messages;
    }

    private CustomerMessage extractMessageFromResultSet(ResultSet rs) throws SQLException {
        CustomerMessage message = new CustomerMessage();

        message.setMessageId(rs.getInt("message_id"));
        message.setSessionId(rs.getInt("session_id"));
        message.setCustomerId(rs.getInt("customer_id"));
        message.setSentAt(rs.getTimestamp("sent_at"));
        message.setContent(rs.getString("content"));
        message.setRead(rs.getBoolean("is_read"));

        return message;
    }
}
