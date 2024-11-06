package com.schedule_hanaro.server.db.entity;

import jakarta.persistence.*;

@Entity
@Table(name="users")
public class User {
    @Id
    private Long id;

    private String username;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

}
