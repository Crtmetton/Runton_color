package com.colorrun.business;

import java.time.LocalDateTime;

/**
 * Représente une demande de devenir organisateur dans l'application Color Run.
 * 
 * Cette classe encapsule les informations relatives à une demande d'élévation
 * de privilèges d'un utilisateur USER vers le rôle ORGANIZER. Elle permet
 * de gérer le processus d'approbation des organisateurs par les administrateurs.
 * 
 * <p><strong>Processus de demande :</strong></p>
 * <ol>
 *   <li>Un participant soumet une demande avec ses motivations</li>
 *   <li>La demande est examinée par un administrateur</li>
 *   <li>L'administrateur approuve ou rejette la demande</li>
 *   <li>En cas d'approbation, le rôle de l'utilisateur est mis à jour</li>
 * </ol>
 * 
 * <p><strong>Statuts possibles :</strong></p>
 * <ul>
 *   <li><code>PENDING</code> : Demande en attente d'examen</li>
 *   <li><code>APPROVED</code> : Demande approuvée par un administrateur</li>
 *   <li><code>REJECTED</code> : Demande rejetée par un administrateur</li>
 * </ul>
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 * 
 * @see User Pour l'utilisateur demandeur et l'administrateur
 */
public class OrganizerRequest {
    /** Identifiant unique de la demande en base de données */
    private int id;
    
    /** Utilisateur qui fait la demande pour devenir organisateur */
    private User requester;
    
    /** Raison pour laquelle l'utilisateur souhaite devenir organisateur */
    private String reason;
    
    /** Statut actuel de la demande (PENDING, APPROVED, REJECTED) */
    private String status;
    
    /** Date et heure de soumission de la demande */
    private LocalDateTime submissionDate;
    
    /** Date et heure de traitement de la demande (approbation/rejet) */
    private LocalDateTime processedDate;
    
    /** Administrateur qui a traité la demande */
    private User processedBy;
    
    /** Commentaire de l'administrateur lors du traitement */
    private String adminComment;

    /**
     * Constructeur par défaut.
     * Initialise une demande avec le statut PENDING et la date actuelle.
     */
    public OrganizerRequest() {
        this.status = "PENDING";
        this.submissionDate = LocalDateTime.now();
    }

    /**
     * Constructeur pour créer une demande avec demandeur et raison.
     * 
     * @param requester L'utilisateur qui fait la demande
     * @param reason La raison de la demande
     */
    public OrganizerRequest(User requester, String reason) {
        this();
        this.requester = requester;
        this.reason = reason;
    }

    /**
     * Retourne l'identifiant unique de la demande.
     * 
     * @return L'ID de la demande en base de données
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique de la demande.
     * Généralement utilisé par le DAO lors de la récupération des données.
     * 
     * @param id L'ID de la demande en base de données
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retourne l'utilisateur qui fait la demande.
     * 
     * @return L'utilisateur demandeur
     */
    public User getRequester() {
        return requester;
    }

    /**
     * Définit l'utilisateur qui fait la demande.
     * 
     * @param requester L'utilisateur demandeur (ne doit pas être null)
     */
    public void setRequester(User requester) {
        this.requester = requester;
    }

    /**
     * Retourne la raison pour laquelle l'utilisateur souhaite devenir organisateur.
     * 
     * @return La motivation du demandeur
     */
    public String getReason() {
        return reason;
    }

    /**
     * Définit la raison de la demande d'organisation.
     * 
     * @param reason La motivation du demandeur (ne doit pas être vide)
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Retourne le statut actuel de la demande.
     * 
     * @return Le statut de la demande (PENDING, APPROVED, REJECTED)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Définit le statut de la demande.
     * 
     * @param status Le nouveau statut de la demande
     * @see #getStatus() pour la liste des statuts possibles
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Retourne la date et heure de soumission de la demande.
     * 
     * @return La date de soumission
     */
    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    /**
     * Définit la date et heure de soumission de la demande.
     * 
     * @param submissionDate La date de soumission
     */
    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }

    /**
     * Retourne la date et heure de traitement de la demande.
     * 
     * @return La date de traitement, ou null si pas encore traitée
     */
    public LocalDateTime getProcessedDate() {
        return processedDate;
    }

    /**
     * Définit la date et heure de traitement de la demande.
     * 
     * @param processedDate La date de traitement
     */
    public void setProcessedDate(LocalDateTime processedDate) {
        this.processedDate = processedDate;
    }

    /**
     * Retourne l'administrateur qui a traité la demande.
     * 
     * @return L'administrateur, ou null si pas encore traitée
     */
    public User getProcessedBy() {
        return processedBy;
    }

    /**
     * Définit l'administrateur qui traite la demande.
     * 
     * @param processedBy L'administrateur qui traite la demande
     */
    public void setProcessedBy(User processedBy) {
        this.processedBy = processedBy;
    }

    /**
     * Retourne le commentaire de l'administrateur.
     * 
     * @return Le commentaire administrateur, ou null si aucun commentaire
     */
    public String getAdminComment() {
        return adminComment;
    }

    /**
     * Définit le commentaire de l'administrateur lors du traitement.
     * 
     * @param adminComment Le commentaire explicatif de la décision
     */
    public void setAdminComment(String adminComment) {
        this.adminComment = adminComment;
    }

    /**
     * Vérifie si la demande est en attente de traitement.
     * 
     * @return true si la demande est en attente, false sinon
     */
    public boolean isPending() {
        return "PENDING".equals(status);
    }

    /**
     * Vérifie si la demande a été approuvée.
     * 
     * @return true si la demande est approuvée, false sinon
     */
    public boolean isApproved() {
        return "APPROVED".equals(status);
    }

    /**
     * Vérifie si la demande a été rejetée.
     * 
     * @return true si la demande est rejetée, false sinon
     */
    public boolean isRejected() {
        return "REJECTED".equals(status);
    }

    /**
     * Vérifie si la demande a été traitée (approuvée ou rejetée).
     * 
     * @return true si la demande a été traitée, false si encore en attente
     */
    public boolean isProcessed() {
        return isApproved() || isRejected();
    }

    /**
     * Approuve la demande avec l'administrateur et un commentaire optionnel.
     * 
     * @param admin L'administrateur qui approuve la demande
     * @param comment Commentaire optionnel expliquant la décision
     */
    public void approve(User admin, String comment) {
        this.status = "APPROVED";
        this.processedBy = admin;
        this.processedDate = LocalDateTime.now();
        this.adminComment = comment;
    }

    /**
     * Rejette la demande avec l'administrateur et un commentaire obligatoire.
     * 
     * @param admin L'administrateur qui rejette la demande
     * @param comment Commentaire expliquant les raisons du rejet
     */
    public void reject(User admin, String comment) {
        this.status = "REJECTED";
        this.processedBy = admin;
        this.processedDate = LocalDateTime.now();
        this.adminComment = comment;
    }

    /**
     * Retourne le nom complet du demandeur.
     * 
     * @return Le nom complet du demandeur
     */
    public String getRequesterName() {
        return requester != null ? requester.getFullName() : "Utilisateur inconnu";
    }

    /**
     * Retourne le nom complet de l'administrateur qui a traité la demande.
     * 
     * @return Le nom de l'administrateur, ou "Non traité" si pas encore traitée
     */
    public String getProcessedByName() {
        return processedBy != null ? processedBy.getFullName() : "Non traité";
    }

    /**
     * Calcule le délai de traitement de la demande.
     * 
     * @return Le nombre de jours entre soumission et traitement, ou -1 si pas encore traitée
     */
    public long getProcessingTimeInDays() {
        if (processedDate == null || submissionDate == null) {
            return -1;
        }
        return java.time.Duration.between(submissionDate, processedDate).toDays();
    }

    /**
     * Retourne un aperçu de la raison (premiers mots).
     * 
     * @param maxLength Longueur maximale de l'aperçu
     * @return Un aperçu tronqué de la raison
     */
    public String getReasonPreview(int maxLength) {
        if (reason == null || reason.isEmpty()) {
            return "Aucune raison spécifiée";
        }
        
        if (reason.length() <= maxLength) {
            return reason;
        }
        
        return reason.substring(0, maxLength) + "...";
    }

    /**
     * Retourne une représentation textuelle de la demande.
     * Utile pour le débogage et les logs.
     * 
     * @return Une chaîne contenant les informations principales de la demande
     */
    @Override
    public String toString() {
        return "OrganizerRequest{" +
                "id=" + id +
                ", requester=" + getRequesterName() +
                ", status='" + status + '\'' +
                ", submissionDate=" + submissionDate +
                ", processedBy=" + getProcessedByName() +
                ", reasonPreview='" + getReasonPreview(50) + '\'' +
                '}';
    }
} 