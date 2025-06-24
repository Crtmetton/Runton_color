package com.colorrun.business;

import java.time.LocalDateTime;

public class OrganizerRequest {
    private int id;
    private User user;
    private LocalDateTime date;
    private String motivation;
    private String status; // PENDING, APPROVED, REJECTED

    public OrganizerRequest() {
    }

    public OrganizerRequest(User user, LocalDateTime date, String motivation, String status) {
        this.user = user;
        this.date = date;
        this.motivation = motivation;
        this.status = status;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getMotivation() {
        return motivation;
    }

    public void setMotivation(String motivation) {
        this.motivation = motivation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
} 