/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QLTN.Utils;

import QLTN.Entity.Customer;
import QLTN.Entity.Computer;
import QLTN.Entity.ComputerGroup;
import java.math.BigDecimal;
import java.time.LocalDateTime;


// Thông tin phiên sử dụng của khách hàng có tài khoản
 
public class CustomerSessionInfo {
    private int computerId;
    private Customer customer;
    private Computer computer;
    private ComputerGroup computerGroup;
    private LocalDateTime startTime;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private int totalTimeSeconds;
    private int remainingTimeSeconds;
    private int usedTimeSeconds;
    
    public CustomerSessionInfo() {
        this.usedTimeSeconds = 0;
    }
    
 
    public void calculateTotalTime() {
        if (currentBalance.compareTo(BigDecimal.ZERO) <= 0 || computerGroup == null) {
            totalTimeSeconds = 0;
            return;
        }
        
        double hours = currentBalance.doubleValue() / computerGroup.getHourlyRate().doubleValue();
        totalTimeSeconds = (int)(hours * 3600);
    }
    
    // Getters and Setters
    public int getComputerId() {
        return computerId;
    }
    
    public void setComputerId(int computerId) {
        this.computerId = computerId;
    }
    
    public Customer getCustomer() {
        return customer;
    }
    
    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
    
    public Computer getComputer() {
        return computer;
    }
    
    public void setComputer(Computer computer) {
        this.computer = computer;
    }
    
    public ComputerGroup getComputerGroup() {
        return computerGroup;
    }
    
    public void setComputerGroup(ComputerGroup computerGroup) {
        this.computerGroup = computerGroup;
    }
    
    public LocalDateTime getStartTime() {
        return startTime;
    }
    
    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }
    
    public BigDecimal getInitialBalance() {
        return initialBalance;
    }
    
    public void setInitialBalance(BigDecimal initialBalance) {
        this.initialBalance = initialBalance;
    }
    
    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }
    
    public void setCurrentBalance(BigDecimal currentBalance) {
        this.currentBalance = currentBalance;
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
}
