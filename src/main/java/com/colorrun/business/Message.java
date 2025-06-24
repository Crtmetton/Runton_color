package com.colorrun.business;

import java.time.LocalDateTime;

public class Message {
    private int id;
    private User sender;
    private User receiver;
    private LocalDateTime date;
    private String content;
    private boolean read;

    public Message() {
    }

    public Message(User sender, User receiver, LocalDateTime date, String content, boolean read) {
        this.sender = sender;
        this.receiver = receiver;
        this.date = date;
        this.content = content;
        this.read = read;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public User getSender() {
        return sender;
    }

    public void setSender(User sender) {
        this.sender = sender;
    }

    public User getReceiver() {
        return receiver;
    }

    public void setReceiver(User receiver) {
        this.receiver = receiver;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }
}
