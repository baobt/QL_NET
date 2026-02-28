package QLTN.Dao;

import QLTN.Entity.Employee;
import QLTN.Utils.JdbcHelperQLTN;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class EmployeeDAO {

    public Employee login(String username, String password) {
        Employee employee = null;
        String sql = "SELECT employee_id, username, password_hash, full_name, role, status, phone FROM Employee WHERE username = ?";

        try ( ResultSet rs = JdbcHelperQLTN.query(sql, username)) {
            if (rs.next()) {
                String storedPasswordHash = rs.getString("password_hash");
                if (verifyPassword(password, storedPasswordHash)) {
                    employee = new Employee(
                            rs.getInt("employee_id"),
                            rs.getString("username"),
                            rs.getString("password_hash"),
                            rs.getString("full_name"),
                            rs.getString("role"),
                            rs.getString("status"),
                            rs.getString("phone")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi truy vấn thông tin đăng nhập: " + e.getMessage());
        }
        return employee;
    }

    public List<Employee> getAllEmployees() {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT employee_id, username, password_hash, full_name, role, status, phone FROM Employee";
        try ( ResultSet rs = JdbcHelperQLTN.query(sql)) {
            while (rs.next()) {
                Employee emp = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("status"),
                        rs.getString("phone")
                );
                list.add(emp);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi truy vấn tất cả nhân viên: " + e.getMessage());
        }
        return list;
    }

    public Employee getEmployeeById(int id) {
        Employee emp = null;
        String sql = "SELECT employee_id, username, password_hash, full_name, role, status, phone FROM Employee WHERE employee_id = ?";
        try ( ResultSet rs = JdbcHelperQLTN.query(sql, id)) {
            if (rs.next()) {
                emp = new Employee(
                        rs.getInt("employee_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("full_name"),
                        rs.getString("role"),
                        rs.getString("status"),
                        rs.getString("phone")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("Lỗi khi truy vấn nhân viên theo ID: " + e.getMessage());
        }
        return emp;
    }

    public void insert(Employee entity, String rawPassword) {
        String sql = "INSERT INTO Employee (username, password_hash, full_name, role, status, phone) VALUES (?, ?, ?, ?, ?, ?)";
        String hashedPassword = hashPassword(rawPassword);
        JdbcHelperQLTN.update(sql, entity.getUsername(), hashedPassword, entity.getFullName(), entity.getRole(), entity.getStatus(), entity.getPhone());
    }

    public void update(Employee entity) {
        String sql = "UPDATE Employee SET username = ?, full_name = ?, role = ?, status = ?, phone = ? WHERE employee_id = ?";
        JdbcHelperQLTN.update(sql, entity.getUsername(), entity.getFullName(), entity.getRole(), entity.getStatus(), entity.getPhone(), entity.getEmployeeId());
    }
    public void update2(Employee entity, String rawPassword) {
        String sql = "UPDATE Employee SET username = ?, password_hash = ?, full_name = ?, role = ?, status = ?, phone = ? WHERE employee_id = ?";
        String hashedPassword = hashPassword(rawPassword);
        JdbcHelperQLTN.update(sql, entity.getUsername(), hashedPassword, entity.getFullName(), entity.getRole(), entity.getStatus(), entity.getPhone(), entity.getEmployeeId());
    }
    public void delete(int id) {
        String sql = "DELETE FROM Employee WHERE employee_id = ?";
        JdbcHelperQLTN.update(sql, id);
    }

    private boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }

    private String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }
}
