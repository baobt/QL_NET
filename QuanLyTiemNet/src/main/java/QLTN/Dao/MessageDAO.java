package QLTN.Dao;


import QLTN.Utils.JdbcHelperQLTN;
import java.sql.ResultSet;

public class MessageDAO {
    public int countUnreadCustomerMessages() {
        String sql = "SELECT COUNT(*) FROM CustomerMessage WHERE is_read = 0";
        try {
            ResultSet rs = JdbcHelperQLTN.query(sql);
            if (rs.next()) {
                return rs.getInt(1);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }
}
