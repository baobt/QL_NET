package QLTN.Dao;



import QLTN.Entity.ChatSession;
import QLTN.Utils.JdbcHelperQLTN;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChatSessionDAO {

    public int createChatSession(ChatSession session) {
        String sql = "INSERT INTO ChatSession (customer_id, computer_id, computer_name, status, has_unread) "
                + "VALUES (?, ?, ?, 'waiting', 1); "
                + "SELECT SCOPE_IDENTITY() AS session_id";

        try {
            ResultSet rs = JdbcHelperQLTN.query(sql,
                    session.getCustomerId(),
                    session.getComputerId(),
                    session.getComputerName());

            if (rs.next()) {
                int sessionId = rs.getInt("session_id");
                rs.getStatement().getConnection().close();
                return sessionId;
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public boolean updateChatSession(ChatSession session) {
        String sql = "UPDATE ChatSession SET "
                + "employee_id = ?, "
                + "status = ?, "
                + "has_unread = ? "
                + "WHERE session_id = ?";

        try {
            JdbcHelperQLTN.update(sql,
                    session.getEmployeeId(),
                    session.getStatus(),
                    session.isHasUnread(),
                    session.getSessionId());
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean endChatSession(int sessionId) {
        String sql = "UPDATE ChatSession SET "
                + "status = 'closed', "
                + "ended_at = CURRENT_TIMESTAMP "
                + "WHERE session_id = ?";

        try {
            JdbcHelperQLTN.update(sql, sessionId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean markAsRead(int sessionId, boolean isEmployee) {
        String sql;

        if (isEmployee) {
            // Đánh dấu tin nhắn của khách hàng là đã đọc
            sql = "UPDATE CustomerMessage SET is_read = 1 WHERE session_id = ? AND is_read = 0";
        } else {
            // Đánh dấu tin nhắn của nhân viên là đã đọc
            sql = "UPDATE EmployeeMessage SET is_read = 1 WHERE session_id = ? AND is_read = 0";
        }

        try {
            JdbcHelperQLTN.update(sql, sessionId);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public ChatSession getChatSessionById(int sessionId) {
        String sql = "SELECT * FROM ChatSession WHERE session_id = ?";

        try {
            ResultSet rs = JdbcHelperQLTN.query(sql, sessionId);

            if (rs.next()) {
                ChatSession session = extractChatSessionFromResultSet(rs);
                rs.getStatement().getConnection().close();
                return session;
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public ChatSession getActiveChatSessionByComputer(int computerId) {
        String sql = "SELECT * FROM ChatSession WHERE computer_id = ? AND status != 'closed' ORDER BY started_at DESC";

        try {
            ResultSet rs = JdbcHelperQLTN.query(sql, computerId);

            if (rs.next()) {
                ChatSession session = extractChatSessionFromResultSet(rs);
                rs.getStatement().getConnection().close();
                return session;
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<ChatSession> getAllActiveChatSessions() {
        String sql = "SELECT * FROM ChatSession WHERE status != 'closed' ORDER BY started_at DESC";

        List<ChatSession> sessions = new ArrayList<>();

        try {
            ResultSet rs = JdbcHelperQLTN.query(sql);

            while (rs.next()) {
                sessions.add(extractChatSessionFromResultSet(rs));
            }
            rs.getStatement().getConnection().close();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return sessions;
    }

    private ChatSession extractChatSessionFromResultSet(ResultSet rs) throws SQLException {
        ChatSession session = new ChatSession();

        session.setSessionId(rs.getInt("session_id"));
        session.setCustomerId(rs.getInt("customer_id"));

        // employee_id có thể là null
        if (rs.getObject("employee_id") != null) {
            session.setEmployeeId(rs.getInt("employee_id"));
        }

        session.setComputerId(rs.getInt("computer_id"));
        session.setComputerName(rs.getString("computer_name"));
        session.setStartedAt(rs.getTimestamp("started_at"));

        // ended_at có thể là null
        if (rs.getObject("ended_at") != null) {
            session.setEndedAt(rs.getTimestamp("ended_at"));
        }

        session.setStatus(rs.getString("status"));
        session.setHasUnread(rs.getBoolean("has_unread"));

        return session;
    }
}
