/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package QLTN.Entity;

/**
 *
 * @author AD
 */
public class Computer {

    private int computerId;
    private int groupId;
    private String name;
    private String status;
    private String specifications;
    private String ipAddress;
    private int position;

    public Computer() {
    }

    public Computer(int computerId, int groupId, String name, String status, String specifications, String ipAddress, Integer position) {
        this.computerId = computerId;
        this.groupId = groupId;
        this.name = name;
        this.status = status;
        this.specifications = specifications;
        this.ipAddress = ipAddress;
        this.position = position;
    }

    public int getComputerId() {
        return computerId;
    }

    public int getGroupId() {
        return groupId;
    }

    public String getName() {
        return name;
    }

    public String getStatus() {
        return status;
    }

    public String getSpecifications() {
        return specifications;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public Integer getPosition() {
        return position;
    }

    public void setComputerId(int computerId) {
        this.computerId = computerId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setSpecifications(String specifications) {
        this.specifications = specifications;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public void setPosition(Integer position) {
        this.position = position;
    }

 

}
