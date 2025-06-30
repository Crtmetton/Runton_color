package com.colorrun.business;

import java.time.LocalDateTime;

/**
 * Représente un dossard (numéro de course) dans l'application Color Run.
 * 
 * Cette classe encapsule les informations relatives à un dossard attribué
 * à un participant pour une course spécifique. Le dossard sert d'identification
 * unique du participant durant l'événement et permet le suivi de sa progression.
 * 
 * <p><strong>Fonctionnalités du dossard :</strong></p>
 * <ul>
 *   <li>Attribution automatique de numéros uniques par course</li>
 *   <li>Gestion des statuts (réservé, attribué, utilisé, annulé)</li>
 *   <li>Association avec un participant et une course</li>
 *   <li>Suivi de l'utilisation (arrivée, temps de course)</li>
 * </ul>
 * 
 * <p><strong>Statuts possibles :</strong></p>
 * <ul>
 *   <li><code>RESERVED</code> : Dossard réservé mais pas encore attribué</li>
 *   <li><code>ASSIGNED</code> : Dossard attribué à un participant</li>
 *   <li><code>USED</code> : Dossard utilisé (participant a terminé la course)</li>
 *   <li><code>CANCELLED</code> : Dossard annulé (participation annulée)</li>
 * </ul>
 * 
 * <p><strong>Règles de gestion :</strong></p>
 * <ul>
 *   <li>Un dossard est unique par course</li>
 *   <li>Les numéros sont attribués séquentiellement</li>
 *   <li>Un dossard annulé peut être réattribué</li>
 *   <li>Le statut USED est définitif</li>
 * </ul>
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 * 
 * @see Participation Pour la participation associée
 * @see Course Pour la course concernée
 * @see User Pour le participant
 */
public class Dossard {
    /** Identifiant unique du dossard en base de données */
    private int id;
    
    /** Numéro du dossard affiché sur le bib */
    private int number;
    
    /** Identifiant de la course pour laquelle ce dossard est valide */
    private int courseId;
    
    /** Identifiant du participant à qui le dossard est attribué */
    private Integer participantId;
    
    /** Statut actuel du dossard (RESERVED, ASSIGNED, USED, CANCELLED) */
    private String status;
    
    /** Temps de course en millisecondes (null si pas encore terminé) */
    private Long raceTime;
    
    /** Position d'arrivée du participant (null si pas encore terminé) */
    private Integer finishPosition;
    
    /** Indique si le dossard a été récupéré par le participant */
    private boolean collected;

    /**
     * Constructeur par défaut.
     * Initialise un dossard avec le statut RESERVED et non collecté.
     */
    public Dossard() {
        this.status = "RESERVED";
        this.collected = false;
    }

    /**
     * Constructeur pour créer un dossard avec numéro et course.
     * 
     * @param number Le numéro du dossard
     * @param courseId L'identifiant de la course
     */
    public Dossard(int number, int courseId) {
        this();
        this.number = number;
        this.courseId = courseId;
    }

    /**
     * Constructeur complet pour un dossard attribué.
     * 
     * @param number Le numéro du dossard
     * @param courseId L'identifiant de la course
     * @param participantId L'identifiant du participant
     */
    public Dossard(int number, int courseId, Integer participantId) {
        this(number, courseId);
        this.participantId = participantId;
        if (participantId != null) {
            this.status = "ASSIGNED";
        }
    }

    /**
     * Retourne l'identifiant unique du dossard.
     * 
     * @return L'ID du dossard en base de données
     */
    public int getId() {
        return id;
    }

    /**
     * Définit l'identifiant unique du dossard.
     * Généralement utilisé par le DAO lors de la récupération des données.
     * 
     * @param id L'ID du dossard en base de données
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Retourne le numéro du dossard.
     * 
     * @return Le numéro affiché sur le dossard
     */
    public int getNumber() {
        return number;
    }

    /**
     * Définit le numéro du dossard.
     * 
     * @param number Le numéro du dossard (doit être positif et unique par course)
     */
    public void setNumber(int number) {
        this.number = number;
    }

    /**
     * Retourne l'identifiant de la course.
     * 
     * @return L'ID de la course associée
     */
    public int getCourseId() {
        return courseId;
    }

    /**
     * Définit l'identifiant de la course.
     * 
     * @param courseId L'ID de la course
     */
    public void setCourseId(int courseId) {
        this.courseId = courseId;
    }

    /**
     * Retourne l'identifiant du participant.
     * 
     * @return L'ID du participant, ou null si dossard non attribué
     */
    public Integer getParticipantId() {
        return participantId;
    }

    /**
     * Définit l'identifiant du participant.
     * Met automatiquement à jour le statut vers ASSIGNED si un participant est attribué.
     * 
     * @param participantId L'ID du participant
     */
    public void setParticipantId(Integer participantId) {
        this.participantId = participantId;
        if (participantId != null && "RESERVED".equals(this.status)) {
            this.status = "ASSIGNED";
        }
    }

    /**
     * Retourne le statut actuel du dossard.
     * 
     * @return Le statut du dossard (RESERVED, ASSIGNED, USED, CANCELLED)
     */
    public String getStatus() {
        return status;
    }

    /**
     * Définit le statut du dossard.
     * 
     * @param status Le nouveau statut
     * @see #getStatus() pour la liste des statuts possibles
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Retourne le temps de course en millisecondes.
     * 
     * @return Le temps de course, ou null si pas encore terminé
     */
    public Long getRaceTime() {
        return raceTime;
    }

    /**
     * Définit le temps de course.
     * Met automatiquement le statut à USED si un temps est enregistré.
     * 
     * @param raceTime Le temps de course en millisecondes
     */
    public void setRaceTime(Long raceTime) {
        this.raceTime = raceTime;
        if (raceTime != null) {
            this.status = "USED";
        }
    }

    /**
     * Retourne la position d'arrivée.
     * 
     * @return La position d'arrivée, ou null si pas encore terminé
     */
    public Integer getFinishPosition() {
        return finishPosition;
    }

    /**
     * Définit la position d'arrivée.
     * 
     * @param finishPosition La position d'arrivée (doit être positive)
     */
    public void setFinishPosition(Integer finishPosition) {
        this.finishPosition = finishPosition;
    }

    /**
     * Indique si le dossard a été collecté par le participant.
     * 
     * @return true si collecté, false sinon
     */
    public boolean isCollected() {
        return collected;
    }

    /**
     * Définit le statut de collecte du dossard.
     * 
     * @param collected true si collecté, false sinon
     */
    public void setCollected(boolean collected) {
        this.collected = collected;
    }

    /**
     * Vérifie si le dossard est réservé (pas encore attribué).
     * 
     * @return true si réservé, false sinon
     */
    public boolean isReserved() {
        return "RESERVED".equals(status);
    }

    /**
     * Vérifie si le dossard est attribué à un participant.
     * 
     * @return true si attribué, false sinon
     */
    public boolean isAssigned() {
        return "ASSIGNED".equals(status);
    }

    /**
     * Vérifie si le dossard a été utilisé (course terminée).
     * 
     * @return true si utilisé, false sinon
     */
    public boolean isUsed() {
        return "USED".equals(status);
    }

    /**
     * Vérifie si le dossard est annulé.
     * 
     * @return true si annulé, false sinon
     */
    public boolean isCancelled() {
        return "CANCELLED".equals(status);
    }

    /**
     * Vérifie si le dossard est disponible pour attribution.
     * 
     * @return true si disponible (réservé ou annulé), false sinon
     */
    public boolean isAvailable() {
        return isReserved() || isCancelled();
    }

    /**
     * Attribue le dossard à un participant.
     * 
     * @param participantId L'ID du participant
     * @throws IllegalStateException si le dossard n'est pas disponible
     */
    public void assignTo(Integer participantId) {
        if (!isAvailable()) {
            throw new IllegalStateException("Le dossard n'est pas disponible pour attribution");
        }
        this.participantId = participantId;
        this.status = "ASSIGNED";
    }

    /**
     * Annule le dossard (libère l'attribution).
     * 
     * @throws IllegalStateException si le dossard est déjà utilisé
     */
    public void cancel() {
        if (isUsed()) {
            throw new IllegalStateException("Impossible d'annuler un dossard déjà utilisé");
        }
        this.status = "CANCELLED";
        this.participantId = null;
        this.collected = false;
    }

    /**
     * Marque le dossard comme collecté par le participant.
     * 
     * @throws IllegalStateException si le dossard n'est pas attribué
     */
    public void markAsCollected() {
        if (!isAssigned()) {
            throw new IllegalStateException("Seul un dossard attribué peut être marqué comme collecté");
        }
        this.collected = true;
    }

    /**
     * Enregistre le temps de course et la position d'arrivée.
     * 
     * @param raceTimeMs Le temps de course en millisecondes
     * @param position La position d'arrivée
     * @throws IllegalStateException si le dossard n'est pas attribué
     */
    public void recordFinish(long raceTimeMs, int position) {
        if (!isAssigned()) {
            throw new IllegalStateException("Seul un dossard attribué peut enregistrer un temps");
        }
        this.raceTime = raceTimeMs;
        this.finishPosition = position;
        this.status = "USED";
    }

    /**
     * Retourne le temps de course formaté en minutes:secondes.
     * 
     * @return Le temps formaté, ou "N/A" si pas de temps enregistré
     */
    public String getFormattedRaceTime() {
        if (raceTime == null) {
            return "N/A";
        }
        
        long totalSeconds = raceTime / 1000;
        long minutes = totalSeconds / 60;
        long seconds = totalSeconds % 60;
        
        return String.format("%d:%02d", minutes, seconds);
    }

    /**
     * Retourne la position d'arrivée formatée.
     * 
     * @return La position avec suffixe (1er, 2ème, etc.), ou "N/A" si pas de position
     */
    public String getFormattedPosition() {
        if (finishPosition == null) {
            return "N/A";
        }
        
        if (finishPosition == 1) {
            return "1er";
        } else {
            return finishPosition + "ème";
        }
    }

    /**
     * Retourne une représentation textuelle du dossard.
     * Utile pour le débogage et les logs.
     * 
     * @return Une chaîne contenant les informations principales du dossard
     */
    @Override
    public String toString() {
        return "Dossard{" +
                "id=" + id +
                ", number=" + number +
                ", courseId=" + courseId +
                ", participantId=" + participantId +
                ", status='" + status + '\'' +
                ", collected=" + collected +
                ", raceTime=" + getFormattedRaceTime() +
                ", position=" + getFormattedPosition() +
                '}';
    }
} 