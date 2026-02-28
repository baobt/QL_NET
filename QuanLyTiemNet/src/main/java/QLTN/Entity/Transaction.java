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
public class Transaction {

    private int transactionId;
    private int customerId;
    private Integer usageId;     // Có thể null
    private Integer orderId;     // Có thể null
    private String type;         // 'topup', 'usage_payment', 'service_payment'
    private BigDecimal amount;
    private LocalDateTime transactionTime;
    private String method;       // 'cash', 'card', 'momo'…
    private String status;       // Mặc định: 'completed'
    private String referenceId;

    public Transaction(int transactionId, int customerId, Integer usageId, Integer orderId, String type, BigDecimal amount, LocalDateTime transactionTime, String method, String status, String referenceId) {
        this.transactionId = transactionId;
        this.customerId = customerId;
        this.usageId = usageId;
        this.orderId = orderId;
        this.type = type;
        this.amount = amount;
        this.transactionTime = transactionTime;
        this.method = method;
        this.status = status;
        this.referenceId = referenceId;
    }

    public int getTransactionId() {
        return transactionId;
    }

    public int getCustomerId() {
        return customerId;
    }

    public Integer getUsageId() {
        return usageId;
    }

    public Integer getOrderId() {
        return orderId;
    }

    public String getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTransactionTime() {
        return transactionTime;
    }

    public String getMethod() {
        return method;
    }

    public String getStatus() {
        return status;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setTransactionId(int transactionId) {
        this.transactionId = transactionId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setUsageId(Integer usageId) {
        this.usageId = usageId;
    }

    public void setOrderId(Integer orderId) {
        this.orderId = orderId;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setTransactionTime(LocalDateTime transactionTime) {
        this.transactionTime = transactionTime;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }
    
    
}
