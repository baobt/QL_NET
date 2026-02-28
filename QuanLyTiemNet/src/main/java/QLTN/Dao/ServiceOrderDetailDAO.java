package QLTN.Dao;


import QLTN.Entity.ServiceOrderDetail;
import QLTN.Utils.JdbcHelperQLTN;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ServiceOrderDetail entity
 */
public class ServiceOrderDetailDAO {

    /**
     * Get all order details from the database
     * @return List of ServiceOrderDetail objects
     */
    public List<ServiceOrderDetail> getAllOrderDetails() {
        List<ServiceOrderDetail> details = new ArrayList<>();
        ResultSet rs = null;

        try {
            String sql = "SELECT sod.detail_id, sod.order_id, sod.service_id, sod.quantity, sod.unit_price, sod.amount, " +
                    "s.name AS service_name " +
                    "FROM ServiceOrderDetail sod " +
                    "JOIN [Service] s ON sod.service_id = s.service_id";
            rs = JdbcHelperQLTN.query(sql);

            while (rs.next()) {
                ServiceOrderDetail detail = new ServiceOrderDetail();
                detail.setDetailId(rs.getInt("detail_id"));
                detail.setOrderId(rs.getInt("order_id"));
                detail.setServiceId(rs.getInt("service_id"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setUnitPrice(rs.getDouble("unit_price"));
                detail.setAmount(rs.getDouble("amount"));
                detail.setServiceName(rs.getString("service_name"));

                details.add(detail);
            }
        } catch (SQLException e) {
            System.err.println("Error getting order details: " + e.getMessage());
        } finally {
            closeResultSet(rs);
        }

        return details;
    }

    /**
     * Get all details for a specific order
     * @param orderId ID of the order
     * @return List of ServiceOrderDetail objects
     */
    public List<ServiceOrderDetail> getOrderDetailsByOrderId(int orderId) {
        List<ServiceOrderDetail> details = new ArrayList<>();
        ResultSet rs = null;

        try {
            String sql = "SELECT sod.detail_id, sod.order_id, sod.service_id, sod.quantity, sod.unit_price, sod.amount, " +
                    "s.name AS service_name " +
                    "FROM ServiceOrderDetail sod " +
                    "JOIN [Service] s ON sod.service_id = s.service_id " +
                    "WHERE sod.order_id = ?";
            rs = JdbcHelperQLTN.query(sql, orderId);

            while (rs.next()) {
                ServiceOrderDetail detail = new ServiceOrderDetail();
                detail.setDetailId(rs.getInt("detail_id"));
                detail.setOrderId(rs.getInt("order_id"));
                detail.setServiceId(rs.getInt("service_id"));
                detail.setQuantity(rs.getInt("quantity"));
                detail.setUnitPrice(rs.getDouble("unit_price"));
                detail.setAmount(rs.getDouble("amount"));
                detail.setServiceName(rs.getString("service_name"));

                details.add(detail);
            }
        } catch (SQLException e) {
            System.err.println("Error getting order details by order ID: " + e.getMessage());
        } finally {
            closeResultSet(rs);
        }

        return details;
    }

    /**
     * Add a new order detail to the database
     * @param detail ServiceOrderDetail object to add
     * @return true if successful, false otherwise
     */
    public boolean addOrderDetail(ServiceOrderDetail detail) {
        try {
            String sql = "INSERT INTO ServiceOrderDetail (order_id, service_id, quantity, unit_price, amount) VALUES (?, ?, ?, ?, ?)";
            JdbcHelperQLTN.update(sql,
                    detail.getOrderId(),
                    detail.getServiceId(),
                    detail.getQuantity(),
                    detail.getUnitPrice(),
                    detail.getAmount()
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error adding order detail: " + e.getMessage());
            return false;
        }
    }

    /**
     * Update an existing order detail in the database
     * @param detail ServiceOrderDetail object to update
     * @return true if successful, false otherwise
     */
    public boolean updateOrderDetail(ServiceOrderDetail detail) {
        try {
            String sql = "UPDATE ServiceOrderDetail SET order_id = ?, service_id = ?, quantity = ?, unit_price = ?, amount = ? WHERE detail_id = ?";
            JdbcHelperQLTN.update(sql,
                    detail.getOrderId(),
                    detail.getServiceId(),
                    detail.getQuantity(),
                    detail.getUnitPrice(),
                    detail.getAmount(),
                    detail.getDetailId()
            );
            return true;
        } catch (Exception e) {
            System.err.println("Error updating order detail: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete an order detail from the database
     * @param detailId ID of the order detail to delete
     * @return true if successful, false otherwise
     */
    public boolean deleteOrderDetail(int detailId) {
        try {
            String sql = "DELETE FROM ServiceOrderDetail WHERE detail_id = ?";
            JdbcHelperQLTN.update(sql, detailId);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting order detail: " + e.getMessage());
            return false;
        }
    }

    /**
     * Delete all details for a specific order
     * @param orderId ID of the order
     * @return true if successful, false otherwise
     */
    public boolean deleteOrderDetailsByOrderId(int orderId) {
        try {
            String sql = "DELETE FROM ServiceOrderDetail WHERE order_id = ?";
            JdbcHelperQLTN.update(sql, orderId);
            return true;
        } catch (Exception e) {
            System.err.println("Error deleting order details by order ID: " + e.getMessage());
            return false;
        }
    }

    /**
     * Calculate total amount for an order
     * @param orderId ID of the order
     * @return Total amount
     */
    public double calculateOrderTotal(int orderId) {
        ResultSet rs = null;
        double total = 0.0;

        try {
            String sql = "SELECT SUM(amount) AS total FROM ServiceOrderDetail WHERE order_id = ?";
            rs = JdbcHelperQLTN.query(sql, orderId);

            if (rs.next()) {
                total = rs.getDouble("total");
            }
        } catch (SQLException e) {
            System.err.println("Error calculating order total: " + e.getMessage());
        } finally {
            closeResultSet(rs);
        }

        return total;
    }

    /**
     * Get the number of items in an order
     * @param orderId ID of the order
     * @return Number of items
     */
    public int getOrderItemCount(int orderId) {
        ResultSet rs = null;
        int count = 0;

        try {
            String sql = "SELECT COUNT(*) AS item_count FROM ServiceOrderDetail WHERE order_id = ?";
            rs = JdbcHelperQLTN.query(sql, orderId);

            if (rs.next()) {
                count = rs.getInt("item_count");
            }
        } catch (SQLException e) {
            System.err.println("Error getting order item count: " + e.getMessage());
        } finally {
            closeResultSet(rs);
        }

        return count;
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
