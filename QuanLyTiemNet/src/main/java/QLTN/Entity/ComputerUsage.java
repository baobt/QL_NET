/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QLTN.Entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 *
 * @author AD
 */
public class ComputerUsage {

    private int usageId;
    private int computerId;
    private int customerId;
    private Integer employeeId; // Có thể null
    private LocalDateTime startTime;
    private LocalDateTime endTime; // Có thể null
    private BigDecimal hourlyRate;
    private BigDecimal totalAmount; // Có thể null
    private Integer totalTime; // Có thể null

    public ComputerUsage() {
    }

    public ComputerUsage(int usageId, int computerId, int customerId, Integer employeeId, LocalDateTime startTime, LocalDateTime endTime, BigDecimal hourlyRate, BigDecimal totalAmount, Integer totalTime) {
        this.usageId = usageId;
        this.computerId = computerId;
        this.customerId = customerId;
        this.employeeId = employeeId;
        this.startTime = startTime;
        this.endTime = endTime;
        this.hourlyRate = hourlyRate;
        this.totalAmount = totalAmount;
        this.totalTime = totalTime;
    }

    public int getUsageId() {
        return usageId;
    }

    public int getComputerId() {
        return computerId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public Integer getEmployeeId() {
        return employeeId;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public Integer getTotalTime() {
        return totalTime;
    }

    public void setUsageId(int usageId) {
        this.usageId = usageId;
    }

    public void setComputerId(int computerId) {
        this.computerId = computerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setEmployeeId(Integer employeeId) {
        this.employeeId = employeeId;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public void setEndTime(LocalDateTime endTime) {
        this.endTime = endTime;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public void setTotalTime(Integer totalTime) {
        this.totalTime = totalTime;
    }
    
    
}
