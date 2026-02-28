package QLTN.Entity;



import java.util.Date;

public class ChatSession {
    private int sessionId;
    private int customerId;
    private Integer employeeId; // Có thể null
    private int computerId;
    private String computerName;
    private Date startedAt;
    private Date endedAt; // Có thể null
    private String status; // 'waiting', 'active', 'closed'
    private boolean hasUnread;

    // Constructors
    public ChatSession() {
    }

    public ChatSession(int customerId, int computerId, String computerName) {
        this.customerId = customerId;
        this.computerId = computerId;
        this.computerName = computerName;
        this.status = "waiting";
        this.hasUnread = true;
    }

    // Getters and Setters
    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public int getComputerId() {
        return computerId;
    }

    public void setComputerId(int computerId) {
        this.computerId = computerId;
    }

    public String getComputerName() {
        return computerName;
    }

    public void setComputerName(String computerName) {
        this.computerName = computerName;
    }

    public Date getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    public Date getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(Date endedAt) {
        this.endedAt = endedAt;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isHasUnread() {
        return hasUnread;
    }

    public void setHasUnread(boolean hasUnread) {
        this.hasUnread = hasUnread;
    }

    @Override
    public String toString() {
        return "Phiên chat #" + sessionId + " - " + computerName;
    }
}
