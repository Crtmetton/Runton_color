package com.colorrun.business;

import java.time.LocalDateTime;

public class Participation {
    private int id;
    private User user;
    private Course course;
    private LocalDateTime date;
    private String status;
    
    public Participation() {
    }
    
    public Participation(User user, Course course, LocalDateTime date, String status) {
        this.user = user;
        this.course = course;
        this.date = date;
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
    
    public Course getCourse() {
        return course;
    }
    
    public void setCourse(Course course) {
        this.course = course;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
} 