/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QLTN.Dao;

import QLTN.Entity.Customer;
import QLTN.Utils.JdbcHelperQLTN;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;

/**
 *
 * @author AD
 */
public class CustomerDao extends QLTNSysDao<Customer, String> {

    final String SELECT_ALL_SQL = "select * from Customer";
    final String INSERT_SQL = "INSERT INTO Customer VALUES(?, ?, ?, ?, ?, ?);";
    String UPDATE_SQL = "update Customer set fullname = ?, username = ?, password_hash = ?, phone = ?, status = ?, balance = ? where customer_id = ?";
    final String DELETE_SQL = "delete from Customer where customer_id = ?";
    final String SELECT_BY_ID_SQL = "select * from Customer where customer_id = ?";
    final String SELECT_BY_KEYWORD_SQL = "select * from Customer where fullname like ?";

    @Override
    public void insert(Customer entity) {
        JdbcHelperQLTN.update(INSERT_SQL, entity.getFullname(), entity.getUsername(), entity.getPasswordHash(), entity.getPhone(), entity.getStatus(), entity.getBalance());
    }

    @Override
    public void update(Customer entity) {
        JdbcHelperQLTN.update(UPDATE_SQL, entity.getFullname(), entity.getUsername(), entity.getPasswordHash(), entity.getPhone(), entity.getStatus(), entity.getBalance(), entity.getCustomerId());
    }

    @Override
    public void delete(String id) {
        JdbcHelperQLTN.update(DELETE_SQL, id);
    }

    @Override
    public List<Customer> selectAll() {
        return selectbySql(SELECT_ALL_SQL);
    }

    @Override
    public Customer SelectbyID(String id) {
        List<Customer> list = selectbySql(SELECT_BY_ID_SQL, id);
        if (list.isEmpty()) {
            return null;
        }
        return list.get(0);
    }

    @Override
    protected List<Customer> selectbySql(String sql, Object... args) {
        List<Customer> list = new ArrayList<>();
        try {
            ResultSet rs = JdbcHelperQLTN.query(sql, args);
            while (rs.next()) {
                Customer ct = new Customer();
                ct.setCustomerId(rs.getInt("customer_id"));
                ct.setFullname(rs.getString("fullname"));
                ct.setUsername(rs.getString("username"));
                ct.setPasswordHash(rs.getString("password_hash"));
                ct.setPhone(rs.getString("phone"));
                ct.setStatus(rs.getString("status"));
                ct.setBalance(rs.getBigDecimal("balance"));
                list.add(ct);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return list;
    }

    public List<Customer> selectByKeyword(String keyWord) {
        return selectbySql(SELECT_BY_KEYWORD_SQL, "%" + keyWord + "%");
    }

    @Override
    public Customer Login(String username, String password) {
        String LOGIN_SQL = "SELECT * FROM Customer WHERE username = ? AND password_hash = ?";
        try {
            List<Customer> customers = selectbySql(LOGIN_SQL, username, password);
            if (!customers.isEmpty()) {
                Customer customer = customers.get(0);
                return customer;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException("Lỗi khi đăng nhập: " + e.getMessage(), e);
        }
    }
}
