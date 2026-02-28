package QLTN.Entity;

public class Service {
    private int serviceId;
    private String name;
    private String description;
    private double price;
    private String status;
    private int quantity;
    private String image;

    // Default constructor
    public Service() {
    }

    // Constructor with all fields
    public Service(int serviceId, String name, String description, double price, String status, int quantity, String image) {
        this.serviceId = serviceId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.quantity = quantity;
        this.image = image;
    }

    // Constructor without ID (for new services)
    public Service(String name, String description, double price, String status, int quantity, String image) {
        this.name = name;
        this.description = description;
        this.price = price;
        this.status = status;
        this.quantity = quantity;
        this.image = image;
    }

    // Getters and Setters
    public int getServiceId() {
        return serviceId;
    }

    public void setServiceId(int serviceId) {
        this.serviceId = serviceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    @Override
    public String toString() {
        return "Service{" +
                "serviceId=" + serviceId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", status='" + status + '\'' +
                ", quantity=" + quantity +
                ", image='" + image + '\'' +
                '}';
    }
}