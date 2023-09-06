package com.example.application.data.entity;

import jakarta.persistence.Entity;

@Entity
public class House extends AbstractEntity {

    private String address;
    private Integer capacity;
    private String manager;

    public String getAddress() {
        return address;
    }
    public void setAddress(String address) {
        this.address = address;
    }
    public Integer getCapacity() {
        return capacity;
    }
    public void setCapacity(Integer capacity) {
        this.capacity = capacity;
    }
    public String getManager() {
        return manager;
    }
    public void setManager(String manager) {
        this.manager = manager;
    }

}
