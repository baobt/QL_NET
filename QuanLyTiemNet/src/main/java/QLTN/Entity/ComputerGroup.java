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
public class ComputerGroup {

    private int groupId;
    private String name;
    private String description;
    private BigDecimal hourlyRate;
    private String status;

    public ComputerGroup() {
    }

    public ComputerGroup(int groupId, String name, String description, BigDecimal hourlyRate, String status) {
        this.groupId = groupId;
        this.name = name;
        this.description = description;
        this.hourlyRate = hourlyRate;
        this.status = status;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getHourlyRate() {
        return hourlyRate;
    }

    public String getStatus() {
        return status;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setHourlyRate(BigDecimal hourlyRate) {
        this.hourlyRate = hourlyRate;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComputerGroup) {
            ComputerGroup other = (ComputerGroup) obj;
            return other.groupId == this.groupId;
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + this.groupId;
        return hash;
    }

}
