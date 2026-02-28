package QLTN.Dao;



import QLTN.Entity.Service;
import QLTN.Utils.JdbcHelperQLTN;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for Service entity
 */
public class ServiceDAO {

    /**
     * Get all services from the database
     * @return List of Service objects
     */
    public List<Service> getAllServices() {
        List<Service> services = new ArrayList<>();
        ResultSet rs = null;

        try {
            String sql = "SELECT service_id, name, description, price, status, quantity, image FROM [Service]";
            rs = JdbcHelperQLTN.query(sql);

            while (rs.next()) {
                Service service = new Service();
                service.setServiceId(rs.getInt("service_id"));
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setPrice(rs.getDouble("price"));
                service.setStatus(rs.getString("status"));
                service.setQuantity(rs.getInt("quantity"));
                service.setImage(rs.getString("image"));

                services.add(service);
            }
        } catch (SQLException e) {
            System.err.println("Error getting services: " + e.getMessage());
        } finally {
            closeResultSet(rs);
        }

        return services;
    }

    /**
     * Get a service by ID
     * @param serviceId ID of the service to retrieve
     * @return Service object or null if not found
     */
    public Service getServiceById(int serviceId) {
        ResultSet rs = null;
        Service service = null;

        try {
            String sql = "SELECT service_id, name, description, price, status, quantity, image FROM [Service] WHERE service_id = ?";
            rs = JdbcHelperQLTN.query(sql, serviceId);

            if (rs.next()) {
                service = new Service();
                service.setServiceId(rs.getInt("service_id"));
                service.setName(rs.getString("name"));
                service.setDescription(rs.getString("description"));
                service.setPrice(rs.getDouble("price"));
                service.setStatus(rs.getString("status"));
                service.setQuantity(rs.getInt("quantity"));
                service.setImage(rs.getString("image"));
            }
        } catch (SQLException e) {
            System.err.println("Error getting service by ID: " + e.getMessage());
        } finally {
            closeResultSet(rs);
        }

        return service;
    }

    /**
     * Add a new service to the database
     * @param service Service object to add
     * @return true if successful, false otherwise
     */
    public boolean addService(Service service) {
        try {
            String sql = "INSERT INTO [Service] (name, description, price, status, quantity, image) VALUES (?, ?, ?, ?, ?, ?)";
            JdbcHelperQLTN.update(sql,
                    service.getName(),
                    service.getDescription(),
                    service.getPrice(),
                    service.getStatus(),
                    service.getQuantity(),
                    service.getImage()
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error adding service: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing service in the database
     * @param service Service object to update
     * @return true if successful, false otherwise
     */
    public boolean updateService(Service service) {
        try {
            String sql = "UPDATE [Service] SET name = ?, description = ?, price = ?, status = ?, quantity = ?, image = ? WHERE service_id = ?";
            JdbcHelperQLTN.update(sql,
                    service.getName(),
                    service.getDescription(),
                    service.getPrice(),
                    service.getStatus(),
                    service.getQuantity(),
                    service.getImage(),
                    service.getServiceId()
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error updating service: " + e.getMessage());
            return false;
        }
    }


    /**
     * Update the quantity of a service
     * @param serviceId ID of the service
     * @param quantity New quantity
     * @return true if successful, false otherwise
     */
    public boolean updateServiceQuantity(int serviceId, int quantity) {
        try {
            String sql = "UPDATE [Service] SET quantity = ? WHERE service_id = ?";
            JdbcHelperQLTN.update(sql, quantity, serviceId);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating service quantity: " + e.getMessage());
            return false;
        }
    }
    public void updateServiceStatus(int serviceId, String status) {
        String sql = "UPDATE Service SET status = ? WHERE service_id = ?";
        JdbcHelperQLTN.update(sql, status, serviceId);
    }

    // Lấy danh sách sản phẩm đang hoạt động (cho CustomerServicePanel)
    public List<Service> getAvailableServices() {
        List<Service> list = new ArrayList<>();
        String sql = "SELECT * FROM Service WHERE status = 'available'";
        try {
            ResultSet rs = JdbcHelperQLTN.query(sql);
            while (rs.next()) {
                Service s = new Service();
                s.setServiceId(rs.getInt("service_id"));
                s.setName(rs.getString("name"));
                s.setPrice(rs.getDouble("price"));
                s.setDescription(rs.getString("description"));
                s.setQuantity(rs.getInt("quantity"));
                s.setImage(rs.getString("image"));
                s.setStatus(rs.getString("status"));
                list.add(s);
            }
            rs.getStatement().getConnection().close();
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Không thể lấy danh sách sản phẩm đang hoạt động");
        }
        return list;
    }

    /**
     * Close ResultSet
     */
    private void closeResultSet(ResultSet rs) {
        if (rs != null) {
            try {
                rs.getStatement().getConnection().close();
            } catch (SQLException e) {
                System.err.println("Error closing ResultSet: " + e.getMessage());
            }
        }
    }
}