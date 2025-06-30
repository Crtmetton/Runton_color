package com.colorrun.business;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Représente une discussion ou conversation dans l'application Color Run.
 * 
 * Cette classe encapsule une discussion qui peut contenir plusieurs messages
 * entre utilisateurs. Elle sert de conteneur pour organiser les conversations
 * par thème ou par course, permettant un système de communication structuré.
 * 
 * <p><strong>Types de discussions :</strong></p>
 * <ul>
 *   <li><strong>Course Discussion</strong> : Discussion publique liée à une course spécifique</li>
 *   <li><strong>Private Conversation</strong> : Conversation privée entre utilisateurs</li>
 *   <li><strong>Group Discussion</strong> : Discussion de groupe pour les organisateurs</li>
 *   <li><strong>General Forum</strong> : Forum général ouvert à tous</li>
 * </ul>
 * 
 * <p><strong>Fonctionnalités :</strong></p>
 * <ul>
 *   <li>Gestion des participants à la discussion</li>
 *   <li>Organisation chronologique des messages</li>
 *   <li>Suivi de l'activité (dernier message, nombre de messages)</li>
 *   <li>Contrôle d'accès selon le type de discussion</li>
 * </ul>
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 * 
 * @see Message Pour les messages contenus dans la discussion
 * @see User Pour les participants à la discussion
 * @see Course Pour les discussions liées aux courses
 */
public class Discussion {
    /** Identifiant unique de la discussion en base de données */
    private int id;
    
    /** Titre ou sujet de la discussion */
    private String title;
    
    /** Description détaillée du sujet de discussion */
    private String description;
    
    /** Type de discussion (COURSE, PRIVATE, GROUP, GENERAL) */
    private String type;
    
    /** Date et heure de création de la discussion */
    private LocalDateTime createdDate;
    
    /** Utilisateur qui a créé la discussion */
    private User creator;
    
    /** Identifiant de la course associée (pour les discussions de course) */
    private Integer courseId;
    
    /** Liste des messages de la discussion */
    private List<Message> messages;
    
    /** Liste des participants autorisés à cette discussion */
    private List<User> participants;
    
    /** Indique si la discussion est active ou archivée */
    private boolean isActive;
    
    /** Date et heure du dernier message */
    private LocalDateTime lastActivity;

    /**
     * Constructeur par défaut.
     * Initialise une discussion avec les collections vides et le statut actif.
     */
    public Discussion() {
        this.messages = new ArrayList<>();
        this.participants = new ArrayList<>();
        this.isActive = true;
        this.createdDate = LocalDateTime.now();
        this.lastActivity = this.createdDate;
        this.type = "GENERAL";
    }

    /**
     * Constructeur pour créer une discussion avec titre et créateur.
     * 
     * @param title Le titre de la discussion
     * @param creator L'utilisateur qui crée la discussion
     */
    public Discussion(String title, User creator) {
        this();
        this.title = title;
        this.creator = creator;
        // Le créateur devient automatiquement participant
        this.participants.add(creator);
    }

    /**
     * Constructeur complet pour une discussion de course.
     * 
     * @param title Le titre de la discussion
     * @param description La description de la discussion
     * @param creator L'utilisateur qui crée la discussion
     * @param courseId L'identifiant de la course associée
     */
    public Discussion(String title, String description, User creator, Integer courseId) {
        this(title, creator);
        this.description = description;
        this.courseId = courseId;
        this.type = "COURSE";
    }

    /**
     * Retourne l'identifiant unique de la discussion.
     * 
     * @return L'ID de la discussion en base de données
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de la discussion.
     * Généralement utilisé par le DAO lors de la récupération des données.
     * 
     * @param id L'ID de la discussion en base de données
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retourne le titre de la discussion.
     * 
     * @return Le titre de la discussion
     */
    public String getTitle() {
        return title;
    }

    /**
     * Définit le titre de la discussion.
     * 
     * @param title Le titre de la discussion (ne doit pas être vide)
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Retourne la description de la discussion.
     * 
     * @return La description de la discussion
     */
    public String getDescription() {
        return description;
    }

    /**
     * Définit la description de la discussion.
     * 
     * @param description La description détaillée
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Retourne le type de discussion.
     * 
     * Types possibles :
     * - COURSE : Discussion liée à une course
     * - PRIVATE : Conversation privée
     * - GROUP : Discussion de groupe
     * - GENERAL : Forum général
     * 
     * @return Le type de discussion
     */
    public String getType() {
        return type;
    }

    /**
     * Définit le type de discussion.
     * 
     * @param type Le type de discussion
     * @see #getType() pour la liste des types possibles
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Retourne la date et heure de création de la discussion.
     * 
     * @return La date de création
     */
    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    /**
     * Définit la date et heure de création de la discussion.
     * 
     * @param createdDate La date de création
     */
    public void setCreatedDate(LocalDateTime createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * Retourne l'utilisateur qui a créé la discussion.
     * 
     * @return Le créateur de la discussion
     */
    public User getCreator() {
        return creator;
    }

    /**
     * Définit l'utilisateur créateur de la discussion.
     * 
     * @param creator Le créateur de la discussion
     */
    public void setCreator(User creator) {
        this.creator = creator;
    }

    /**
     * Retourne l'identifiant de la course associée.
     * 
     * @return L'ID de la course, ou null si pas liée à une course
     */
    public Integer getCourseId() {
        return courseId;
    }

    /**
     * Définit l'identifiant de la course associée.
     * 
     * @param courseId L'ID de la course associée
     */
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    /**
     * Retourne la liste des messages de la discussion.
     * 
     * @return La liste des messages, triée par ordre chronologique
     */
    public List<Message> getMessages() {
        return messages;
    }

    /**
     * Définit la liste des messages de la discussion.
     * 
     * @param messages La liste des messages
     */
    public void setMessages(List<Message> messages) {
        this.messages = messages;
        updateLastActivity();
    }

    /**
     * Retourne la liste des participants autorisés.
     * 
     * @return La liste des participants
     */
    public List<User> getParticipants() {
        return participants;
    }

    /**
     * Définit la liste des participants autorisés.
     * 
     * @param participants La liste des participants
     */
    public void setParticipants(List<User> participants) {
        this.participants = participants;
    }

    /**
     * Indique si la discussion est active.
     * 
     * @return true si la discussion est active, false si archivée
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Définit le statut d'activité de la discussion.
     * 
     * @param active true pour active, false pour archivée
     */
    public void setActive(boolean active) {
        isActive = active;
    }

    /**
     * Retourne la date et heure de la dernière activité.
     * 
     * @return La date de dernière activité
     */
    public LocalDateTime getLastActivity() {
        return lastActivity;
    }

    /**
     * Définit la date et heure de la dernière activité.
     * 
     * @param lastActivity La date de dernière activité
     */
    public void setLastActivity(LocalDateTime lastActivity) {
        this.lastActivity = lastActivity;
    }

    /**
     * Ajoute un message à la discussion.
     * Met automatiquement à jour la date de dernière activité.
     * 
     * @param message Le message à ajouter
     */
    public void addMessage(Message message) {
        this.messages.add(message);
        updateLastActivity();
    }

    /**
     * Ajoute un participant à la discussion.
     * 
     * @param participant L'utilisateur à ajouter comme participant
     * @return true si ajouté avec succès, false s'il était déjà participant
     */
    public boolean addParticipant(User participant) {
        if (!participants.contains(participant)) {
            participants.add(participant);
            return true;
        }
        return false;
    }

    /**
     * Retire un participant de la discussion.
     * Le créateur ne peut pas être retiré.
     * 
     * @param participant L'utilisateur à retirer
     * @return true si retiré avec succès, false sinon
     */
    public boolean removeParticipant(User participant) {
        // Le créateur ne peut pas être retiré
        if (participant.equals(creator)) {
            return false;
        }
        return participants.remove(participant);
    }

    /**
     * Vérifie si un utilisateur est participant à cette discussion.
     * 
     * @param user L'utilisateur à vérifier
     * @return true si l'utilisateur est participant, false sinon
     */
    public boolean isParticipant(User user) {
        return participants.contains(user);
    }

    /**
     * Vérifie si la discussion est liée à une course.
     * 
     * @return true si liée à une course, false sinon
     */
    public boolean isCourseDiscussion() {
        return "COURSE".equals(type) && courseId != null;
    }

    /**
     * Vérifie si la discussion est privée.
     * 
     * @return true si privée, false sinon
     */
    public boolean isPrivate() {
        return "PRIVATE".equals(type);
    }

    /**
     * Vérifie si la discussion est un forum général.
     * 
     * @return true si forum général, false sinon
     */
    public boolean isGeneral() {
        return "GENERAL".equals(type);
    }

    /**
     * Retourne le nombre de messages dans la discussion.
     * 
     * @return Le nombre de messages
     */
    public int getMessageCount() {
        return messages.size();
    }

    /**
     * Retourne le nombre de participants.
     * 
     * @return Le nombre de participants
     */
    public int getParticipantCount() {
        return participants.size();
    }

    /**
     * Archive la discussion (la marque comme inactive).
     */
    public void archive() {
        this.isActive = false;
    }

    /**
     * Réactive une discussion archivée.
     */
    public void reactivate() {
        this.isActive = true;
    }

    /**
     * Met à jour la date de dernière activité à maintenant.
     * Appelée automatiquement lors de l'ajout de messages.
     */
    private void updateLastActivity() {
        this.lastActivity = LocalDateTime.now();
    }

    /**
     * Retourne le nom complet du créateur.
     * 
     * @return Le nom du créateur
     */
    public String getCreatorName() {
        return creator != null ? creator.getFullName() : "Créateur inconnu";
    }

    /**
     * Retourne un aperçu de la description (premiers mots).
     * 
     * @param maxLength Longueur maximale de l'aperçu
     * @return Un aperçu tronqué de la description
     */
    public String getDescriptionPreview(int maxLength) {
        if (description == null || description.isEmpty()) {
            return "Aucune description";
        }
        
        if (description.length() <= maxLength) {
            return description;
        }
        
        return description.substring(0, maxLength) + "...";
    }

    /**
     * Retourne une représentation textuelle de la discussion.
     * Utile pour le débogage et les logs.
     * 
     * @return Une chaîne contenant les informations principales de la discussion
     */
    @Override
    public String toString() {
        return "Discussion{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", type='" + type + '\'' +
                ", creator=" + getCreatorName() +
                ", messageCount=" + getMessageCount() +
                ", participantCount=" + getParticipantCount() +
                ", isActive=" + isActive +
                ", lastActivity=" + lastActivity +
                '}';
    }
}
