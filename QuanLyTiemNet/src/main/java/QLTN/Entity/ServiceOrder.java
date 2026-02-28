package QLTN.Entity;

import java.util.Date;
import java.util.List;

public class ServiceOrder {
    private int orderId;
    private int usageId;
    private Date orderTime;
    private double totalAmount;
    private String paymentStatus;
    private List<ServiceOrderDetail> orderDetails;

    // Additional fields for UI display (not in database)
    private String computerName;

    // Default constructor
    public ServiceOrder() {
    }

    // Constructor with all fields
    public ServiceOrder(int orderId, int usageId, Date orderTime, double totalAmount, String paymentStatus) {
        this.orderId = orderId;
        this.usageId = usageId;
        this.orderTime = orderTime;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
    }

    // Constructor without ID (for new orders)
    public ServiceOrder(int usageId, Date orderTime, double totalAmount, String paymentStatus) {
        this.usageId = usageId;
        this.orderTime = orderTime;
        this.totalAmount = totalAmount;
        this.paymentStatus = paymentStatus;
    }

    // Getters and Setters
    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getUsageId() {
        return usageId;
    }

    public void setUsageId(int usageId) {
        this.usageId = usageId;
    }

    public Date getOrderTime() {
        return orderTime;
    }

    public void setOrderTime(Date orderTime) {
        this.orderTime = orderTime;
    }

    public double getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }

    public String getPaymentStatus() {
        return paymentStatus;
    }

    public void setPaymentStatus(String paymentStatus) {
        this.paymentStatus = paymentStatus;
    }

    public List<ServiceOrderDetail> getOrderDetails() {
        return orderDetails;
    }

    public void setOrderDetails(List<ServiceOrderDetail> orderDetails) {
        this.orderDetails = orderDetails;
    }

    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    @Override
    public String toString() {
        return "ServiceOrder{" +
                "orderId=" + orderId +
                ", usageId=" + usageId +
                ", orderTime=" + orderTime +
                ", totalAmount=" + totalAmount +
                ", paymentStatus='" + paymentStatus + '\'' +
                '}';
    }
}
