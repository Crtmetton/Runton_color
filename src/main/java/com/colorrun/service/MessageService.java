package com.colorrun.service;

import com.colorrun.business.Message;
import com.colorrun.business.User;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface de service pour la gestion des messages dans l'application Color Run.
 * 
 * Cette interface définit les opérations métier pour le système de messagerie
 * et de communication de l'application. Elle gère les messages publics, privés,
 * les notifications système et les annonces officielles.
 * 
 * <p><strong>Types de messages supportés :</strong></p>
 * <ul>
 *   <li><strong>PUBLIC</strong> : Messages visibles par tous dans une discussion de course</li>
 *   <li><strong>PRIVATE</strong> : Messages privés entre deux utilisateurs</li>
 *   <li><strong>SYSTEM</strong> : Notifications automatiques du système</li>
 *   <li><strong>ANNOUNCEMENT</strong> : Annonces officielles des organisateurs/admins</li>
 * </ul>
 * 
 * <p><strong>Fonctionnalités principales :</strong></p>
 * <ul>
 *   <li>Envoi et réception de messages avec validation</li>
 *   <li>Gestion des statuts de lecture (lu/non lu)</li>
 *   <li>Filtrage et recherche dans l'historique</li>
 *   <li>Modération automatique du contenu</li>
 *   <li>Notifications en temps réel</li>
 *   <li>Archivage et suppression de messages</li>
 * </ul>
 * 
 * <p><strong>Règles métier :</strong></p>
 * <ul>
 *   <li>Les messages publics sont visibles par tous les participants d'une course</li>
 *   <li>Les messages privés ne sont visibles que par l'expéditeur et le destinataire</li>
 *   <li>Les messages système sont générés automatiquement</li>
 *   <li>Les annonces ne peuvent être créées que par les organisateurs/admins</li>
 *   <li>La modération peut masquer des messages inappropriés</li>
 * </ul>
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 * 
 * @see Message Pour le modèle métier des messages
 * @see User Pour les utilisateurs expéditeur/destinataire
 * @see com.colorrun.dao.MessageDAO Pour la persistance des données
 */
public interface MessageService {
    
    /**
     * Envoie un nouveau message avec validation du contenu et des permissions.
     * 
     * <p>Validations effectuées :</p>
     * <ul>
     *   <li>Vérification de l'existence de l'expéditeur</li>
     *   <li>Validation du contenu (longueur, contenu approprié)</li>
     *   <li>Contrôle des permissions selon le type de message</li>
     *   <li>Vérification du destinataire pour les messages privés</li>
     * </ul>
     * 
     * @param message Le message à envoyer
     * @return Le message envoyé avec son ID généré et timestamp
     * @throws IllegalArgumentException Si le message ou ses données sont invalides
     * @throws SecurityException Si l'utilisateur n'a pas les droits d'envoi
     * @throws SQLException En cas d'erreur lors de la sauvegarde
     * 
     * @example
     * Message msg = new Message(sender, "Bonjour à tous!");
     * msg.setCourseId(courseId);
     * msg.setType("PUBLIC");
     * Message sent = messageService.sendMessage(msg);
     */
    Message sendMessage(Message message) throws SQLException;
    
    /**
     * Envoie un message privé entre deux utilisateurs.
     * 
     * @param senderId L'identifiant de l'expéditeur
     * @param recipientId L'identifiant du destinataire
     * @param content Le contenu du message
     * @return Le message privé envoyé
     * @throws IllegalArgumentException Si les IDs sont invalides ou le contenu vide
     * @throws SecurityException Si l'envoi de messages privés n'est pas autorisé
     * @throws SQLException En cas d'erreur lors de l'envoi
     */
    Message sendPrivateMessage(int senderId, int recipientId, String content) throws SQLException;
    
    /**
     * Envoie un message public dans une discussion de course.
     * 
     * @param senderId L'identifiant de l'expéditeur
     * @param courseId L'identifiant de la course
     * @param content Le contenu du message
     * @return Le message public envoyé
     * @throws IllegalArgumentException Si les IDs sont invalides ou le contenu vide
     * @throws SecurityException Si l'utilisateur ne peut pas participer à cette discussion
     * @throws SQLException En cas d'erreur lors de l'envoi
     */
    Message sendPublicMessage(int senderId, int courseId, String content) throws SQLException;
    
    /**
     * Envoie une annonce officielle (organisateurs/admins uniquement).
     * 
     * @param senderId L'identifiant de l'expéditeur (doit être organisateur/admin)
     * @param courseId L'identifiant de la course concernée (optionnel)
     * @param content Le contenu de l'annonce
     * @return L'annonce envoyée
     * @throws IllegalArgumentException Si les données sont invalides
     * @throws SecurityException Si l'utilisateur n'a pas les droits d'annonce
     * @throws SQLException En cas d'erreur lors de l'envoi
     */
    Message sendAnnouncement(int senderId, Integer courseId, String content) throws SQLException;
    
    /**
     * Crée une notification système automatique.
     * 
     * @param recipientId L'identifiant du destinataire (null pour notification globale)
     * @param content Le contenu de la notification
     * @param courseId L'identifiant de la course associée (optionnel)
     * @return La notification système créée
     * @throws IllegalArgumentException Si le contenu est invalide
     * @throws SQLException En cas d'erreur lors de la création
     */
    Message createSystemNotification(Integer recipientId, String content, Integer courseId) throws SQLException;
    
    /**
     * Récupère un message par son identifiant.
     * 
     * @param messageId L'identifiant du message
     * @return Le message correspondant, ou null si non trouvé
     * @throws IllegalArgumentException Si l'ID est invalide
     * @throws SQLException En cas d'erreur lors de la récupération
     */
    Message getMessageById(int messageId) throws SQLException;
    
    /**
     * Récupère tous les messages d'une conversation privée entre deux utilisateurs.
     * 
     * @param user1Id L'identifiant du premier utilisateur
     * @param user2Id L'identifiant du second utilisateur
     * @return Liste des messages de la conversation, triés par date
     * @throws IllegalArgumentException Si les IDs sont invalides
     * @throws SQLException En cas d'erreur lors de la récupération
     */
    List<Message> getPrivateConversation(int user1Id, int user2Id) throws SQLException;
    
    /**
     * Récupère tous les messages publics d'une course.
     * 
     * @param courseId L'identifiant de la course
     * @return Liste des messages publics de la course, triés par date
     * @throws IllegalArgumentException Si l'ID de course est invalide
     * @throws SQLException En cas d'erreur lors de la récupération
     */
    List<Message> getCourseMessages(int courseId) throws SQLException;
    
    /**
     * Récupère tous les messages reçus par un utilisateur (privés + notifications).
     * 
     * @param userId L'identifiant de l'utilisateur
     * @return Liste des messages reçus, triés par date (plus récents en premier)
     * @throws IllegalArgumentException Si l'ID utilisateur est invalide
     * @throws SQLException En cas d'erreur lors de la récupération
     */
    List<Message> getReceivedMessages(int userId) throws SQLException;
    
    /**
     * Récupère tous les messages envoyés par un utilisateur.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @return Liste des messages envoyés, triés par date (plus récents en premier)
     * @throws IllegalArgumentException Si l'ID utilisateur est invalide
     * @throws SQLException En cas d'erreur lors de la récupération
     */
    List<Message> getSentMessages(int userId) throws SQLException;
    
    /**
     * Récupère les messages non lus d'un utilisateur.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @return Liste des messages non lus
     * @throws IllegalArgumentException Si l'ID utilisateur est invalide
     * @throws SQLException En cas d'erreur lors de la récupération
     */
    List<Message> getUnreadMessages(int userId) throws SQLException;
    
    /**
     * Récupère les dernières annonces (globales ou pour une course).
     * 
     * @param courseId L'identifiant de la course (null pour les annonces globales)
     * @param limit Nombre maximum d'annonces à retourner
     * @return Liste des dernières annonces
     * @throws IllegalArgumentException Si la limite est négative
     * @throws SQLException En cas d'erreur lors de la récupération
     */
    List<Message> getRecentAnnouncements(Integer courseId, int limit) throws SQLException;
    
    /**
     * Marque un message comme lu par un utilisateur.
     * 
     * @param messageId L'identifiant du message
     * @param userId L'identifiant de l'utilisateur qui lit le message
     * @return true si le marquage a réussi
     * @throws IllegalArgumentException Si les IDs sont invalides
     * @throws SecurityException Si l'utilisateur ne peut pas lire ce message
     * @throws SQLException En cas d'erreur lors de la mise à jour
     */
    boolean markAsRead(int messageId, int userId) throws SQLException;
    
    /**
     * Marque plusieurs messages comme lus.
     * 
     * @param messageIds Liste des identifiants des messages
     * @param userId L'identifiant de l'utilisateur qui lit les messages
     * @return Nombre de messages marqués comme lus
     * @throws IllegalArgumentException Si les données sont invalides
     * @throws SQLException En cas d'erreur lors de la mise à jour
     */
    int markMultipleAsRead(List<Integer> messageIds, int userId) throws SQLException;
    
    /**
     * Marque tous les messages reçus d'un utilisateur comme lus.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @return Nombre de messages marqués comme lus
     * @throws IllegalArgumentException Si l'ID utilisateur est invalide
     * @throws SQLException En cas d'erreur lors de la mise à jour
     */
    int markAllAsRead(int userId) throws SQLException;
    
    /**
     * Supprime un message (soft delete - marque comme supprimé).
     * 
     * @param messageId L'identifiant du message à supprimer
     * @param userId L'identifiant de l'utilisateur qui supprime
     * @return true si la suppression a réussi
     * @throws IllegalArgumentException Si les IDs sont invalides
     * @throws SecurityException Si l'utilisateur ne peut pas supprimer ce message
     * @throws SQLException En cas d'erreur lors de la suppression
     */
    boolean deleteMessage(int messageId, int userId) throws SQLException;
    
    /**
     * Recherche des messages par contenu (recherche textuelle).
     * 
     * @param userId L'identifiant de l'utilisateur qui effectue la recherche
     * @param searchQuery Le texte à rechercher
     * @param messageType Le type de message à rechercher (null pour tous)
     * @return Liste des messages correspondants
     * @throws IllegalArgumentException Si les paramètres sont invalides
     * @throws SQLException En cas d'erreur lors de la recherche
     */
    List<Message> searchMessages(int userId, String searchQuery, String messageType) throws SQLException;
    
    /**
     * Compte le nombre de messages non lus pour un utilisateur.
     * 
     * @param userId L'identifiant de l'utilisateur
     * @return Le nombre de messages non lus
     * @throws IllegalArgumentException Si l'ID utilisateur est invalide
     * @throws SQLException En cas d'erreur lors du comptage
     */
    int getUnreadMessageCount(int userId) throws SQLException;
    
    /**
     * Récupère les messages dans une plage de dates.
     * 
     * @param userId L'identifiant de l'utilisateur (pour filtrer les permissions)
     * @param startDate Date de début de la période
     * @param endDate Date de fin de la période
     * @return Liste des messages dans la période
     * @throws IllegalArgumentException Si les dates sont invalides
     * @throws SQLException En cas d'erreur lors de la récupération
     */
    List<Message> getMessagesByDateRange(int userId, LocalDateTime startDate, LocalDateTime endDate) throws SQLException;
    
    /**
     * Valide le contenu d'un message selon les règles de modération.
     * 
     * <p>Validations effectuées :</p>
     * <ul>
     *   <li>Longueur du message (minimum et maximum)</li>
     *   <li>Détection de contenu inapproprié</li>
     *   <li>Vérification des liens et mentions</li>
     *   <li>Contrôle anti-spam</li>
     * </ul>
     * 
     * @param content Le contenu à valider
     * @param messageType Le type de message
     * @throws IllegalArgumentException Si le contenu est invalide, avec détail de l'erreur
     */
    void validateMessageContent(String content, String messageType);
    
    /**
     * Vérifie si un utilisateur peut envoyer un message à un destinataire.
     * 
     * @param senderId L'identifiant de l'expéditeur
     * @param recipientId L'identifiant du destinataire
     * @param messageType Le type de message
     * @return true si l'envoi est autorisé
     * @throws IllegalArgumentException Si les IDs sont invalides
     * @throws SQLException En cas d'erreur lors de la vérification
     */
    boolean canSendMessage(int senderId, int recipientId, String messageType) throws SQLException;
    
    /**
     * Archive les anciens messages selon la politique de rétention.
     * 
     * @param olderThanDays Nombre de jours au-delà duquel archiver
     * @return Nombre de messages archivés
     * @throws SQLException En cas d'erreur lors de l'archivage
     */
    int archiveOldMessages(int olderThanDays) throws SQLException;
    
    // Méthodes de compatibilité avec les servlets existants
    void create(int courseId, int userId, String content) throws SQLException;
    
    List<Message> findByCourse(int courseId) throws SQLException;
    
    Optional<Message> findById(int id) throws SQLException;
    
    void delete(int id) throws SQLException;
} 