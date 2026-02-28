package QLTN.Dao;




import QLTN.Entity.EmployeeMessage;
import QLTN.Utils.JdbcHelperQLTN;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeMessageDAO {

    public int addMessage(EmployeeMessage message) {
        String sql = "INSERT INTO EmployeeMessage (session_id, employee_id, content, is_read) "
                + "VALUES (?, ?, ?, 0); "
                + "SELECT SCOPE_IDENTITY() AS message_id";

        try {
            ResultSet rs = JdbcHelperQLTN.query(sql,
                    message.getSessionId(),
                    message.getEmployeeId(),
                    message.getContent());

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

    public List<EmployeeMessage> getMessagesBySessionId(int sessionId) {
        String sql = "SELECT * FROM EmployeeMessage WHERE session_id = ? ORDER BY sent_at ASC";

        List<EmployeeMessage> messages = new ArrayList<>();

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

    private EmployeeMessage extractMessageFromResultSet(ResultSet rs) throws SQLException {
        EmployeeMessage message = new EmployeeMessage();

        message.setMessageId(rs.getInt("message_id"));
        message.setSessionId(rs.getInt("session_id"));
        message.setEmployeeId(rs.getInt("employee_id"));
        message.setSentAt(rs.getTimestamp("sent_at"));
        message.setContent(rs.getString("content"));
        message.setRead(rs.getBoolean("is_read"));

        return message;
    }
}
