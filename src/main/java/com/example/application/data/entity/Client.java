package com.example.application.data.entity;

import jakarta.persistence.Entity;
import java.time.LocalDate;

@Entity
public class Client extends AbstractEntity {

    private String first_name;
    private String last_name;
    private LocalDate dob;
    private String access_number;
    private String status;
    private String house;

    public String getFirst_name() {
        return first_name;
    }
    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }
    public String getLast_name() {
        return last_name;
    }
    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
    public LocalDate getDob() {
        return dob;
    }
    public void setDob(LocalDate dob) {
        this.dob = dob;
    }
    public String getAccess_number() {
        return access_number;
    }
    public void setAccess_number(String access_number) {
        this.access_number = access_number;
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public String getHouse() {
        return house;
    }
    public void setHouse(String house) {
        this.house = house;
    }

}
