package com.colorrun.business;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe représentant un message de discussion dans une course.
 * Correspond à la table DISCUSSION de la base de données.
 */
public class Discussion {
    private int id;
    private int courseId;
    private LocalDateTime date;
    private String contenu;
    private int expediteurId;
    private User expediteur; // Pour les jointures
    
    // Constructeurs
    public Discussion() {}
    
    public Discussion(int courseId, String contenu, int expediteurId) {
        this.courseId = courseId;
        this.contenu = contenu;
        this.expediteurId = expediteurId;
        this.date = LocalDateTime.now();
    }
    
    // Getters et Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getCourseId() {
        return courseId;
    }
    
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }
    
    public LocalDateTime getDate() {
        return date;
    }
    
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    public String getContenu() {
        return contenu;
    }
    
    public void setContenu(String contenu) {
        this.contenu = contenu;
    }
    
    public int getExpediteurId() {
        return expediteurId;
    }
    
    public void setExpediteurId(int expediteurId) {
        this.expediteurId = expediteurId;
    }
    
    public User getExpediteur() {
        return expediteur;
    }
    
    public void setExpediteur(User expediteur) {
        this.expediteur = expediteur;
    }
    
    /**
     * Retourne le nom complet de l'expéditeur
     */
    public String getExpediteurName() {
        return expediteur != null ? expediteur.getFullName() : "Utilisateur inconnu";
    }
    
    @Override
    public String toString() {
        return "Discussion{" +
                "id=" + id +
                ", courseId=" + courseId +
                ", date=" + date +
                ", contenu='" + contenu + '\'' +
                ", expediteurId=" + expediteurId +
                '}';
    }
}
