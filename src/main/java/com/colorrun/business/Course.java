package com.colorrun.business;

import java.time.LocalDateTime;

public class Course {
    private int id;
    private String name;
    private String description;
    private LocalDateTime date;
    private String city;
    private Double distance;
    private int maxParticipants;
    private int currentParticipants;
    private String cause;

    public Course() {
    }

    public Course(int id, String name, String description, LocalDateTime date, String city, Double distance, int maxParticipants, String cause) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.date = date;
        this.city = city;
        this.distance = distance;
        this.maxParticipants = maxParticipants;
        this.cause = cause;
        this.currentParticipants = 0;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDateTime getDate() {
        return date;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public Double getDistance() {
        return distance;
    }

    public void setDistance(Double distance) {
        this.distance = distance;
    }

    public int getMaxParticipants() {
        return maxParticipants;
    }

    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    public int getCurrentParticipants() {
        return currentParticipants;
    }

    public void setCurrentParticipants(int currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    public String getCause() {
        return cause;
    }

    public void setCause(String cause) {
        this.cause = cause;
    }
    
    public boolean hasAvailableSpots() {
        return currentParticipants < maxParticipants;
    }

    public void ajouterParticipant(Participant unUtilisateur) {
        // Implementation can be added here
    }

    public void modifierProfile(Participant unUtilisateur) {
        // Implementation can be added here
    }
}
