package com.example.application.data.entity;

import jakarta.persistence.Entity;
import jakarta.validation.constraints.Email;
import java.time.LocalDateTime;

@Entity
public class Clinic extends AbstractEntity {

    private String name;
    private String country;
    private String phone;
    @Email
    private String email;
    private LocalDateTime created_on;
    private String created_by;

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getCountry() {
        return country;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public String getPhone() {
        return phone;
    }
    public void setPhone(String phone) {
        this.phone = phone;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public LocalDateTime getCreated_on() {
        return created_on;
    }
    public void setCreated_on(LocalDateTime created_on) {
        this.created_on = created_on;
    }
    public String getCreated_by() {
        return created_by;
    }
    public void setCreated_by(String created_by) {
        this.created_by = created_by;
    }

}
