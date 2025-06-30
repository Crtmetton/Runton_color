package com.colorrun.business;

import java.time.LocalDateTime;

/**
 * Représente la participation d'un utilisateur à une course Color Run.
 * 
 * Cette classe fait le lien entre un utilisateur et une course, stockant
 * les informations relatives à son inscription comme la date d'inscription
 * et le statut de sa participation. Elle sert de modèle pour la gestion
 * des inscriptions aux événements Color Run.
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public class Participation {
    /** Identifiant unique de la participation en base de données */
    private int id;
    
    /** Utilisateur inscrit à la course */
    private User user;
    
    /** Course à laquelle l'utilisateur s'est inscrit */
    private Course course;
    
    /** Date et heure d'inscription à la course */
    private LocalDateTime date;
    
    /** Statut de la participation (REGISTERED, CONFIRMED, CANCELLED, COMPLETED) */
    private String status;
    
    /**
     * Constructeur par défaut.
     * Initialise une participation avec des valeurs par défaut.
     */
    public Participation() {
        this.date = LocalDateTime.now();
        this.status = "REGISTERED";
    }
    
    /**
     * Constructeur complet pour créer une participation avec toutes ses informations.
     * 
     * @param user L'utilisateur qui s'inscrit
     * @param course La course à laquelle s'inscrire
     * @param date La date d'inscription
     * @param status Le statut initial de la participation
     */
    public Participation(User user, Course course, LocalDateTime date, String status) {
        this.user = user;
        this.course = course;
        this.date = date;
        this.status = status;
    }
    
    /**
     * Retourne l'identifiant unique de la participation.
     * 
     * @return L'ID de la participation en base de données
     */
    public int getId() {
        return id;
    }
    
    /**
     * Définit l'identifiant unique de la participation.
     * Généralement utilisé par le DAO lors de la récupération des données.
     * 
     * @param id L'ID de la participation en base de données
     */
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * Retourne l'utilisateur inscrit à la course.
     * 
     * @return L'utilisateur participant
     */
    public User getUser() {
        return user;
    }
    
    /**
     * Définit l'utilisateur inscrit à la course.
     * 
     * @param user L'utilisateur participant (ne doit pas être null)
     */
    public void setUser(User user) {
        this.user = user;
    }
    
    /**
     * Retourne la course à laquelle l'utilisateur s'est inscrit.
     * 
     * @return La course de l'événement
     */
    public Course getCourse() {
        return course;
    }
    
    /**
     * Définit la course à laquelle l'utilisateur s'inscrit.
     * 
     * @param course La course de l'événement (ne doit pas être null)
     */
    public void setCourse(Course course) {
        this.course = course;
    }
    
    /**
     * Retourne la date et heure d'inscription à la course.
     * 
     * @return La date d'inscription
     */
    public LocalDateTime getDate() {
        return date;
    }
    
    /**
     * Définit la date et heure d'inscription à la course.
     * 
     * @param date La date d'inscription (généralement la date actuelle)
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }
    
    /**
     * Retourne le statut actuel de la participation.
     * 
     * Les statuts possibles sont :
     * - REGISTERED : Inscrit mais non confirmé
     * - CONFIRMED : Inscription confirmée
     * - CANCELLED : Inscription annulée
     * - COMPLETED : Course terminée
     * 
     * @return Le statut de la participation
     */
    public String getStatus() {
        return status;
    }
    
    /**
     * Définit le statut de la participation.
     * 
     * @param status Le nouveau statut de la participation
     * @see #getStatus() pour la liste des statuts possibles
     */
    public void setStatus(String status) {
        this.status = status;
    }
    
    /**
     * Vérifie si la participation est active (inscrite et non annulée).
     * 
     * @return true si la participation est active, false sinon
     */
    public boolean isActive() {
        return !"CANCELLED".equals(status);
    }
    
    /**
     * Vérifie si la participation est confirmée.
     * 
     * @return true si la participation est confirmée, false sinon
     */
    public boolean isConfirmed() {
        return "CONFIRMED".equals(status);
    }
    
    /**
     * Vérifie si la participation est annulée.
     * 
     * @return true si la participation est annulée, false sinon
     */
    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }
    
    /**
     * Vérifie si la course associée à cette participation est terminée.
     * 
     * @return true si la course est terminée, false sinon
     */
    public boolean isCompleted() {
        return "COMPLETED".equals(status);
    }
    
    /**
     * Confirme la participation en changeant son statut.
     * Passe le statut de REGISTERED à CONFIRMED.
     */
    public void confirm() {
        if ("REGISTERED".equals(status)) {
            this.status = "CONFIRMED";
        }
    }
    
    /**
     * Annule la participation en changeant son statut.
     * Passe le statut à CANCELLED quel que soit l'état précédent.
     */
    public void cancel() {
        this.status = "CANCELLED";
    }
    
    /**
     * Marque la participation comme terminée.
     * Utilisé quand le participant a terminé la course.
     */
    public void complete() {
        this.status = "COMPLETED";
    }
    
    /**
     * Retourne une représentation textuelle de la participation.
     * Utile pour le débogage et les logs.
     * 
     * @return Une chaîne contenant les informations principales de la participation
     */
    @Override
    public String toString() {
        return "Participation{" +
                "id=" + id +
                ", user=" + (user != null ? user.getFullName() : "null") +
                ", course=" + (course != null ? course.getName() : "null") +
                ", date=" + date +
                ", status='" + status + '\'' +
                '}';
    }
} 