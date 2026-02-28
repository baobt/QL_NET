package QLTN.Entity;

import java.util.Date;

public class EmployeeMessage {
    private int messageId;
    private int sessionId;
    private int employeeId;
    private Date sentAt;
    private String content;
    private boolean isRead;

    // Constructors
    public EmployeeMessage() {
    }

    public EmployeeMessage(int sessionId, int employeeId, String content) {
        this.sessionId = sessionId;
        this.employeeId = employeeId;
        this.content = content;
        this.isRead = false;
    }

    // Getters and Setters
    public int getMessageId() {
        return messageId;
    }

    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }

    public int getSessionId() {
        return sessionId;
    }

    public void setSessionId(int sessionId) {
        this.sessionId = sessionId;
    }

    public int getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }

    public Date getSentAt() {
        return sentAt;
    }

    public void setSentAt(Date sentAt) {
        this.sentAt = sentAt;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean isRead) {
        this.isRead = isRead;
    }
}
