package com.colorrun.business;

import java.time.LocalDateTime;

/**
 * Représente une course Color Run dans l'application.
 * 
 * Cette classe encapsule toutes les informations relatives à une course colorée,
 * incluant les détails de l'événement, les limites de participation et la cause
 * soutenue. Elle sert de modèle principal pour la gestion des événements Color Run.
 * 
 * Les courses Color Run sont des événements festifs où les participants
 * courent à travers des stations de couleurs pour soutenir diverses causes.
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public class Course {
    /** Identifiant unique de la course en base de données */
    private int id;
    
    /** Nom de la course Color Run */
    private String name;
    
    /** Description détaillée de l'événement */
    private String description;
    
    /** Date et heure de début de la course */
    private LocalDateTime date;
    
    /** Ville où se déroule l'événement */
    private String city;
    
    /** Distance de la course en kilomètres */
    private Double distance;
    
    /** Nombre maximum de participants autorisés */
    private int maxParticipants;
    
    /** Nombre actuel de participants inscrits */
    private int currentParticipants;
    
    /** Cause ou association bénéficiaire de l'événement */
    private String cause;

    /** Prix d'inscription à la course en euros */
    private int prix;

    /** Identifiant de l'utilisateur qui a créé cette course */
    private int userCreateId;

    /**
     * Constructeur par défaut.
     * Initialise une course avec des valeurs par défaut.
     */
    public Course() {
        this.currentParticipants = 0;
    }

    /**
     * Constructeur complet pour créer une course avec toutes ses informations.
     * 
     * @param id L'identifiant unique de la course
     * @param name Le nom de la course
     * @param description La description de l'événement
     * @param date La date et heure de la course
     * @param city La ville de l'événement
     * @param distance La distance en kilomètres
     * @param maxParticipants Le nombre maximum de participants
     * @param cause La cause soutenue par l'événement
     */
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

    /**
     * Retourne l'identifiant unique de la course.
     * 
     * @return L'ID de la course en base de données
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de la course.
     * Généralement utilisé par le DAO lors de la récupération des données.
     * 
     * @param id L'ID de la course en base de données
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retourne le nom de la course.
     * 
     * @return Le nom de la course Color Run
     */
    public String getName() {
        return name;
    }

    /**
     * Définit le nom de la course.
     * 
     * @param name Le nom de la course (ne doit pas être vide)
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Retourne la description détaillée de l'événement.
     * 
     * @return La description de la course
     */
    public String getDescription() {
        return description;
    }

    /**
     * Définit la description détaillée de l'événement.
     * 
     * @param description La description de la course
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retourne la date et heure de début de la course.
     * 
     * @return La date et heure de l'événement
     */
    public LocalDateTime getDate() {
        return date;
    }

    /**
     * Définit la date et heure de début de la course.
     * 
     * @param date La date et heure de l'événement (doit être dans le futur)
     */
    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    /**
     * Retourne la ville où se déroule l'événement.
     * 
     * @return La ville de la course
     */
    public String getCity() {
        return city;
    }

    /**
     * Définit la ville où se déroule l'événement.
     * 
     * @param city La ville de la course (ne doit pas être vide)
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Retourne la distance de la course en kilomètres.
     * 
     * @return La distance en kilomètres
     */
    public Double getDistance() {
        return distance;
    }

    /**
     * Définit la distance de la course en kilomètres.
     * 
     * @param distance La distance en kilomètres (doit être positive)
     */
    public void setDistance(Double distance) {
        this.distance = distance;
    }

    /**
     * Retourne le nombre maximum de participants autorisés.
     * 
     * @return Le nombre maximum de participants
     */
    public int getMaxParticipants() {
        return maxParticipants;
    }

    /**
     * Définit le nombre maximum de participants autorisés.
     * 
     * @param maxParticipants Le nombre maximum de participants (doit être positif)
     */
    public void setMaxParticipants(int maxParticipants) {
        this.maxParticipants = maxParticipants;
    }

    /**
     * Retourne le nombre actuel de participants inscrits.
     * 
     * @return Le nombre actuel de participants
     */
    public int getCurrentParticipants() {
        return currentParticipants;
    }

    /**
     * Définit le nombre actuel de participants inscrits.
     * 
     * @param currentParticipants Le nombre actuel de participants
     */
    public void setCurrentParticipants(int currentParticipants) {
        this.currentParticipants = currentParticipants;
    }

    /**
     * Retourne la cause ou association bénéficiaire de l'événement.
     * 
     * @return La cause soutenue par la course
     */
    public String getCause() {
        return cause;
    }

    /**
     * Définit la cause ou association bénéficiaire de l'événement.
     * 
     * @param cause La cause soutenue par la course
     */
    public void setCause(String cause) {
        this.cause = cause;
    }

    /**
     * Retourne le prix d'inscription à la course.
     * 
     * @return Le prix en euros
     */
    public int getPrix() {
        return prix;
    }

    /**
     * Définit le prix d'inscription à la course.
     * 
     * @param prix Le prix en euros (doit être positif ou nul)
     */
    public void setPrix(int prix) {
        this.prix = prix;
    }

    /**
     * Retourne l'identifiant de l'utilisateur qui a créé cette course.
     * 
     * @return L'ID de l'utilisateur créateur
     */
    public int getUserCreateId() {
        return userCreateId;
    }

    /**
     * Définit l'identifiant de l'utilisateur qui a créé cette course.
     * 
     * @param userCreateId L'ID de l'utilisateur créateur
     */
    public void setUserCreateId(int userCreateId) {
        this.userCreateId = userCreateId;
    }
    
    /**
     * Vérifie s'il reste des places disponibles pour cette course.
     * 
     * @return true s'il reste des places, false si la course est complète
     */
    public boolean hasAvailableSpots() {
        return currentParticipants < maxParticipants;
    }

    /**
     * Calcule le nombre de places restantes pour cette course.
     * 
     * @return Le nombre de places disponibles
     */
    public int getAvailableSpots() {
        return maxParticipants - currentParticipants;
    }

    /**
     * Calcule le pourcentage de remplissage de la course.
     * 
     * @return Le pourcentage de participants inscrits par rapport au maximum
     */
    public double getFillPercentage() {
        if (maxParticipants == 0) {
            return 0.0;
        }
        return (double) currentParticipants / maxParticipants * 100;
    }

    /**
     * Vérifie si la course est complète (aucune place disponible).
     * 
     * @return true si la course est complète, false sinon
     */
    public boolean isFull() {
        return currentParticipants >= maxParticipants;
    }

    /**
     * Ajoute un participant à la course.
     * Incrémente le compteur des participants actuels si la course n'est pas complète.
     * 
     * @param unUtilisateur Le participant à ajouter (pour compatibilité legacy)
     * @return true si le participant a été ajouté, false si la course est complète
     */
    public boolean ajouterParticipant(Participant unUtilisateur) {
        if (hasAvailableSpots()) {
            currentParticipants++;
            return true;
        }
        return false;
    }

    /**
     * Retire un participant de la course.
     * Décrémente le compteur des participants actuels.
     */
    public void retirerParticipant() {
        if (currentParticipants > 0) {
            currentParticipants--;
        }
    }

    /**
     * Méthode legacy pour la modification de profil.
     * Conservée pour la compatibilité avec l'ancien code.
     * 
     * @param unUtilisateur Le participant concerné
     * @deprecated Cette méthode n'a pas d'implémentation réelle
     */
    @Deprecated
    public void modifierProfile(Participant unUtilisateur) {
        // Méthode conservée pour compatibilité legacy
        // L'implémentation réelle devrait être dans le service approprié
    }

    /**
     * Retourne une représentation textuelle de la course.
     * Utile pour le débogage et les logs.
     * 
     * @return Une chaîne contenant les informations principales de la course
     */
    @Override
    public String toString() {
        return "Course{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", city='" + city + '\'' +
                ", date=" + date +
                ", distance=" + distance + "km" +
                ", participants=" + currentParticipants + "/" + maxParticipants +
                ", cause='" + cause + '\'' +
                '}';
    }
}
