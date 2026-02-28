package QLTN.Dao;

import QLTN.Entity.ServiceOrder;
import QLTN.Entity.ServiceOrderDetail;
import QLTN.Utils.JdbcHelperQLTN;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object for ServiceOrder entity
 */
public class ServiceOrderDAO {

    /**
     * Get all service orders from the database
     * @return List of ServiceOrder objects
     */
    public List<ServiceOrder> getAllOrders() {
        List<ServiceOrder> orders = new ArrayList<>();
        ResultSet rs = null;

        try {
            String sql = "SELECT so.order_id, so.usage_id, so.order_time, so.total_amount, so.payment_status, " +
                    "cu.computer_id, c.name AS computer_name " +
                    "FROM ServiceOrder so " +
                    "JOIN ComputerUsage cu ON so.usage_id = cu.usage_id " +
                    "JOIN Computer c ON cu.computer_id = c.computer_id " +
                    "ORDER BY so.order_time DESC";
            rs = JdbcHelperQLTN.query(sql);

            while (rs.next()) {
                ServiceOrder order = new ServiceOrder();
                order.setOrderId(rs.getInt("order_id"));
                order.setUsageId(rs.getInt("usage_id"));
                order.setOrderTime(rs.getTimestamp("order_time"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setPaymentStatus(rs.getString("payment_status"));
                order.setComputerName(rs.getString("computer_name"));

                orders.add(order);
            }
        } catch (SQLException e) {
            System.err.println("Error getting orders: " + e.getMessage());
        } finally {
            closeResultSet(rs);
        }

        return orders;
    }

    /**
     * Get a service order by ID
     * @param orderId ID of the order to retrieve
     * @return ServiceOrder object or null if not found
     */
    public ServiceOrder getOrderById(int orderId) {
        ResultSet rs = null;
        ServiceOrder order = null;

        try {
            String sql = "SELECT so.order_id, so.usage_id, so.order_time, so.total_amount, so.payment_status, " +
                    "cu.computer_id, c.name AS computer_name " +
                    "FROM ServiceOrder so " +
                    "JOIN ComputerUsage cu ON so.usage_id = cu.usage_id " +
                    "JOIN Computer c ON cu.computer_id = c.computer_id " +
                    "WHERE so.order_id = ?";
            rs = JdbcHelperQLTN.query(sql, orderId);

            if (rs.next()) {
                order = new ServiceOrder();
                order.setOrderId(rs.getInt("order_id"));
                order.setUsageId(rs.getInt("usage_id"));
                order.setOrderTime(rs.getTimestamp("order_time"));
                order.setTotalAmount(rs.getDouble("total_amount"));
                order.setPaymentStatus(rs.getString("payment_status"));
                order.setComputerName(rs.getString("computer_name"));

                // Get order details
                order.setOrderDetails(getOrderDetails(orderId));
            }
        } catch (SQLException e) {
            System.err.println("Error getting order by ID: " + e.getMessage());
        } finally {
            closeResultSet(rs);
        }

        return order;
    }

    /**
     * Get all details for a specific order
     * @param orderId ID of the order
     * @return List of ServiceOrderDetail objects
     */
    public List<ServiceOrderDetail> getOrderDetails(int orderId) {
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
            System.err.println("Error getting order details: " + e.getMessage());
        } finally {
            closeResultSet(rs);
        }

        return details;
    }

    /**
     * Add a new service order to the database
     * @param order ServiceOrder object to add
     * @return ID of the new order, or -1 if failed
     */
    public int addOrder(ServiceOrder order) {
        ResultSet rs = null;
        int newOrderId = -1;

        try {
            // First, insert the order
            String sql = "INSERT INTO ServiceOrder (usage_id, order_time, total_amount, payment_status) VALUES (?, ?, ?, ?); SELECT SCOPE_IDENTITY() AS new_id";
            rs = JdbcHelperQLTN.query(sql,
                    order.getUsageId(),
                    new Timestamp(order.getOrderTime().getTime()),
                    order.getTotalAmount(),
                    order.getPaymentStatus()
            );

            if (rs.next()) {
                newOrderId = rs.getInt("new_id");

                // Add order details if available
                if (order.getOrderDetails() != null && !order.getOrderDetails().isEmpty()) {
                    for (ServiceOrderDetail detail : order.getOrderDetails()) {
                        addOrderDetail(newOrderId, detail);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Error adding order: " + e.getMessage());
        } finally {
            closeResultSet(rs);
        }

        return newOrderId;
    }

    /**
     * Add a detail to an order
     * @param orderId ID of the order
     * @param detail ServiceOrderDetail object to add
     * @return true if successful, false otherwise
     */
    private boolean addOrderDetail(int orderId, ServiceOrderDetail detail) {
        try {
            String sql = "INSERT INTO ServiceOrderDetail (order_id, service_id, quantity, unit_price, amount) VALUES (?, ?, ?, ?, ?)";
            JdbcHelperQLTN.update(sql,
                    orderId,
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
     * Update the payment status of an order
     * @param orderId ID of the order
     * @param status New payment status
     * @return true if successful, false otherwise
     */
    public boolean updateOrderPaymentStatus(int orderId, String status) {
        try {
            String sql = "UPDATE ServiceOrder SET payment_status = ? WHERE order_id = ?";
            JdbcHelperQLTN.update(sql, status, orderId);
            return true;
        } catch (Exception e) {
            System.err.println("Error updating order payment status: " + e.getMessage());
            return false;
        }
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