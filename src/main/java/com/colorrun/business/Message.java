package com.colorrun.business;

import java.time.LocalDateTime;

/**
 * Représente un message dans le système de communication de l'application Color Run.
 * 
 * Cette classe encapsule les informations d'un message échangé entre utilisateurs,
 * que ce soit dans le cadre de discussions publiques, de communications privées,
 * ou de notifications système. Elle sert de modèle pour la gestion des communications
 * au sein de la plateforme.
 * 
 * <p><strong>Types de messages supportés :</strong></p>
 * <ul>
 *   <li>Messages de discussion publique sur les courses</li>
 *   <li>Messages privés entre utilisateurs</li>
 *   <li>Notifications système</li>
 *   <li>Annonces des organisateurs</li>
 * </ul>
 * 
 * <p><strong>Fonctionnalités :</strong></p>
 * <ul>
 *   <li>Horodatage automatique des messages</li>
 *   <li>Traçabilité de l'expéditeur et du destinataire</li>
 *   <li>Support des messages de différents types</li>
 *   <li>Gestion du statut de lecture</li>
 * </ul>
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 * 
 * @see User Pour les utilisateurs expéditeur et destinataire
 * @see Discussion Pour les conversations groupées
 */
public class Message {
    /** Identifiant unique du message en base de données */
    private int id;
    
    /** Utilisateur qui a envoyé le message */
    private User sender;
    
    /** Utilisateur destinataire du message (null pour les messages publics) */
    private User recipient;
    
    /** Contenu textuel du message */
    private String content;
    
    /** Date et heure d'envoi du message */
    private LocalDateTime timestamp;
    
    /** Type de message (PUBLIC, PRIVATE, SYSTEM, ANNOUNCEMENT) */
    private String type;
    
    /** Indique si le message a été lu par le destinataire */
    private boolean isRead;
    
    /** Identifiant de la course associée (pour les messages publics) */
    private Integer courseId;

    /**
     * Constructeur par défaut.
     * Initialise un message avec la date actuelle et le statut non lu.
     */
    public Message() {
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
        this.type = "PUBLIC";
    }

    /**
     * Constructeur pour créer un message avec expéditeur et contenu.
     * 
     * @param sender L'utilisateur qui envoie le message
     * @param content Le contenu du message
     */
    public Message(User sender, String content) {
        this();
        this.sender = sender;
        this.content = content;
    }

    /**
     * Constructeur complet pour créer un message privé.
     * 
     * @param sender L'utilisateur qui envoie le message
     * @param recipient L'utilisateur qui reçoit le message
     * @param content Le contenu du message
     * @param type Le type de message
     */
    public Message(User sender, User recipient, String content, String type) {
        this();
        this.sender = sender;
        this.recipient = recipient;
        this.content = content;
        this.type = type;
    }

    /**
     * Retourne l'identifiant unique du message.
     * 
     * @return L'ID du message en base de données
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique du message.
     * Généralement utilisé par le DAO lors de la récupération des données.
     * 
     * @param id L'ID du message en base de données
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retourne l'utilisateur qui a envoyé le message.
     * 
     * @return L'expéditeur du message
     */
    public User getSender() {
        return sender;
    }

    /**
     * Définit l'utilisateur qui envoie le message.
     * 
     * @param sender L'expéditeur du message (ne doit pas être null)
     */
    public void setSender(User sender) {
        this.sender = sender;
    }

    /**
     * Retourne l'utilisateur destinataire du message.
     * 
     * @return Le destinataire du message, ou null pour les messages publics
     */
    public User getRecipient() {
        return recipient;
    }

    /**
     * Définit l'utilisateur destinataire du message.
     * 
     * @param recipient Le destinataire du message (null pour les messages publics)
     */
    public void setRecipient(User recipient) {
        this.recipient = recipient;
    }

    /**
     * Retourne le contenu textuel du message.
     * 
     * @return Le contenu du message
     */
    public String getContent() {
        return content;
    }

    /**
     * Définit le contenu textuel du message.
     * 
     * @param content Le contenu du message (ne doit pas être vide)
     */
    public void setContent(String content) {
        this.content = content;
    }

    /**
     * Retourne la date et heure d'envoi du message.
     * 
     * @return L'horodatage du message
     */
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Définit la date et heure d'envoi du message.
     * 
     * @param timestamp L'horodatage du message
     */
    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    /**
     * Retourne le type de message.
     * 
     * Types possibles :
     * - PUBLIC : Message visible par tous dans une discussion
     * - PRIVATE : Message privé entre deux utilisateurs
     * - SYSTEM : Notification système
     * - ANNOUNCEMENT : Annonce officielle
     * 
     * @return Le type de message
     */
    public String getType() {
        return type;
    }

    /**
     * Définit le type de message.
     * 
     * @param type Le type de message
     * @see #getType() pour la liste des types possibles
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Indique si le message a été lu par le destinataire.
     * 
     * @return true si le message a été lu, false sinon
     */
    public boolean isRead() {
        return isRead;
    }

    /**
     * Marque le message comme lu ou non lu.
     * 
     * @param read true pour marquer comme lu, false comme non lu
     */
    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * Retourne l'identifiant de la course associée au message.
     * 
     * @return L'ID de la course, ou null si le message n'est pas lié à une course
     */
    public Integer getCourseId() {
        return courseId;
    }

    /**
     * Définit l'identifiant de la course associée au message.
     * 
     * @param courseId L'ID de la course associée
     */
    public void setCourseId(Integer courseId) {
        this.courseId = courseId;
    }

    /**
     * Vérifie si le message est public (visible par tous).
     * 
     * @return true si le message est public, false sinon
     */
    public boolean isPublic() {
        return "PUBLIC".equals(type) || "ANNOUNCEMENT".equals(type);
    }

    /**
     * Vérifie si le message est privé (entre deux utilisateurs).
     * 
     * @return true si le message est privé, false sinon
     */
    public boolean isPrivate() {
        return "PRIVATE".equals(type);
    }

    /**
     * Vérifie si le message est une notification système.
     * 
     * @return true si c'est une notification système, false sinon
     */
    public boolean isSystemNotification() {
        return "SYSTEM".equals(type);
    }

    /**
     * Vérifie si le message est une annonce officielle.
     * 
     * @return true si c'est une annonce, false sinon
     */
    public boolean isAnnouncement() {
        return "ANNOUNCEMENT".equals(type);
    }

    /**
     * Marque le message comme lu.
     * Raccourci pour setRead(true).
     */
    public void markAsRead() {
        this.isRead = true;
    }

    /**
     * Marque le message comme non lu.
     * Raccourci pour setRead(false).
     */
    public void markAsUnread() {
        this.isRead = false;
    }

    /**
     * Retourne le nom complet de l'expéditeur.
     * 
     * @return Le nom complet de l'expéditeur, ou "Système" pour les messages système
     */
    public String getSenderName() {
        if (isSystemNotification()) {
            return "Système";
        }
        return sender != null ? sender.getFullName() : "Utilisateur inconnu";
    }

    /**
     * Retourne un aperçu du contenu du message (premiers mots).
     * 
     * @param maxLength Longueur maximale de l'aperçu
     * @return Un aperçu tronqué du contenu
     */
    public String getContentPreview(int maxLength) {
        if (content == null || content.isEmpty()) {
            return "";
        }
        
        if (content.length() <= maxLength) {
            return content;
        }
        
        return content.substring(0, maxLength) + "...";
    }

    /**
     * Retourne une représentation textuelle du message.
     * Utile pour le débogage et les logs.
     * 
     * @return Une chaîne contenant les informations principales du message
     */
    @Override
    public String toString() {
        return "Message{" +
                "id=" + id +
                ", sender=" + getSenderName() +
                ", recipient=" + (recipient != null ? recipient.getFullName() : "Public") +
                ", type='" + type + '\'' +
                ", timestamp=" + timestamp +
                ", isRead=" + isRead +
                ", contentPreview='" + getContentPreview(50) + '\'' +
                '}';
    }
}
