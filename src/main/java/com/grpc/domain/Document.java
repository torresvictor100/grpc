package com.grpc.domain;

import jakarta.persistence.*;

@Entity(name = "tb_document")
public class Document {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String data;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    public Document() {
    }

    public Document(String name, String data, User user) {
        this.name = name;
        this.data = data;
        this.user = user;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getData() {
        return data;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
