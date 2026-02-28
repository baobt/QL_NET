/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QLTN.Entity;

import java.math.BigDecimal;

/**
 *
 * @author AD
 */
public class Customer {

    private int customerId;
    private String fullname;
    private String username;
    private String passwordHash;
    private String phone;
    private String status;
    private BigDecimal balance;

    public Customer() {
    }

    public Customer(int customerId, String fullname, String username, String passwordHash, String phone, String status, BigDecimal balance) {
        this.customerId = customerId;
        this.fullname = fullname;
        this.username = username;
        this.passwordHash = passwordHash;
        this.phone = phone;
        this.status = status;
        this.balance = balance;
    }

    public int getCustomerId() {
        return customerId;
    }

    public String getFullname() {
        return fullname;
    }

    public String getUsername() {
        return username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public String getPhone() {
        return phone;
    }

    public String getStatus() {
        return status;
    }

    public BigDecimal getBalance() {
        return balance;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
