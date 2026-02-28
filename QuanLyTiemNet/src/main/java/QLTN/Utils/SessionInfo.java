/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
/*
 * Cập nhật SessionInfo để hỗ trợ khách hàng không có tài khoản
 */
package QLTN.Utils;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Thông tin phiên sử dụng máy (cập nhật)
 */
public class SessionInfo {
    private int computerId;
    private int customerId; // THÊM MỚI: ID khách hàng
    private BigDecimal prepaidAmount;
    private BigDecimal hourlyRate;
    private LocalDateTime startTime;
    private int totalTimeSeconds;
    private int remainingTimeSeconds;
    private int usedTimeSeconds;
    
    public SessionInfo() {
        this.usedTimeSeconds = 0;
        this.customerId = 1; // Mặc định là khách vãng lai
    }
    
    // THÊM MỚI: Getter và Setter cho customerId
    public int getCustomerId() {
        return customerId;
    }
    
    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }
    
    // Các getter/setter khác giữ nguyên
    public int getComputerId() {
        return computerId;
    }
    
    public void setComputerId(int computerId) {
        this.computerId = computerId;
    }
    
    public BigDecimal getPrepaidAmount() {
        return prepaidAmount;
    }
    
    public void setPrepaidAmount(BigDecimal prepaidAmount) {
        this.prepaidAmount = prepaidAmount;
    }
    
    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }
    
    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public int getTotalTimeSeconds() {
        return totalTimeSeconds;
    }
    
    public void setTotalTimeSeconds(int totalTimeSeconds) {
        this.totalTimeSeconds = totalTimeSeconds;
    }
    
    public int getRemainingTimeSeconds() {
        return remainingTimeSeconds;
    }
    
    public void setRemainingTimeSeconds(int remainingTimeSeconds) {
        this.remainingTimeSeconds = remainingTimeSeconds;
    }
    
    public int getUsedTimeSeconds() {
        return usedTimeSeconds;
    }
    
    public void setUsedTimeSeconds(int usedTimeSeconds) {
        this.usedTimeSeconds = usedTimeSeconds;
    }
    
    // Tính số dư hiện tại dựa trên thời gian còn lại
    public BigDecimal getCurrentBalance() {
        if (totalTimeSeconds == 0) return BigDecimal.ZERO;
        
        double ratio = (double) remainingTimeSeconds / totalTimeSeconds;
        return prepaidAmount.multiply(BigDecimal.valueOf(ratio));
    }
    
    // THÊM MỚI: Kiểm tra xem có phải khách hàng có tài khoản không
    public boolean isRegisteredCustomer() {
        return customerId > 1;
    }
}