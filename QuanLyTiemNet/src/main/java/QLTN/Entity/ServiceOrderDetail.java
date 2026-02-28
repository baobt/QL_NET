package QLTN.Entity;

public class ServiceOrderDetail {
    private int detailId;
    private int orderId;
    private int serviceId;
    private int quantity;
    private double unitPrice;
    private double amount;

    // Additional fields for UI display (not in database)
    private String serviceName;

    // Default constructor
    public ServiceOrderDetail() {
    }

    // Constructor with all fields
    public ServiceOrderDetail(int detailId, int orderId, int serviceId, int quantity, double unitPrice, double amount) {
        this.detailId = detailId;
        this.orderId = orderId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
    }

    // Constructor without ID (for new order details)
    public ServiceOrderDetail(int orderId, int serviceId, int quantity, double unitPrice, double amount) {
        this.orderId = orderId;
        this.serviceId = serviceId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.amount = amount;
    }

    // Getters and Setters
    public int getDetailId() {
        return detailId;
    }

    public void setDetailId(int detailId) {
        this.detailId = detailId;
    }

    public int getOrderId() {
        return orderId;
    }

    public void setOrderId(int orderId) {
        this.orderId = orderId;
    }

    public int getServiceId() {
        return serviceId;
    }
    private Service service;

    public Service getService() {
        return service;
    }
    public void setService(Service service) {
        this.service = service;
    }
    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getUnitPrice() {
        return unitPrice;
    }

    public void setUnitPrice(double unitPrice) {
        this.unitPrice = unitPrice;
    }

    public double getAmount() {
        return amount;
    }

    public void setAmount(double amount) {
        this.amount = amount;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    @Override
    public String toString() {
        return "ServiceOrderDetail{" +
                "detailId=" + detailId +
                ", orderId=" + orderId +
                ", serviceId=" + serviceId +
                ", quantity=" + quantity +
                ", unitPrice=" + unitPrice +
                ", amount=" + amount +
                '}';
    }
}