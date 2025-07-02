package com.colorrun.service;

import com.colorrun.business.User;
import com.colorrun.business.Course;
import com.colorrun.business.Participation;

/**
 * Service pour la génération de QR codes pour les courses Color Run.
 * 
 * Les QR codes contiennent les informations nécessaires pour identifier
 * un participant à une course spécifique.
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public interface QRCodeService {
    
    /**
     * Génère un QR code pour une participation spécifique.
     * 
     * @param participation La participation pour laquelle générer le QR code
     * @return Les bytes de l'image QR code (PNG)
     * @throws Exception si erreur lors de la génération
     */
    byte[] generateQRCode(Participation participation) throws Exception;
    
    /**
     * Génère un QR code avec des données personnalisées.
     * 
     * @param data Les données à encoder dans le QR code
     * @return Les bytes de l'image QR code (PNG)
     * @throws Exception si erreur lors de la génération
     */
    byte[] generateQRCode(String data) throws Exception;
    
    /**
     * Génère les données à encoder dans le QR code pour une participation.
     * 
     * @param user Le participant
     * @param course La course
     * @param participationId L'ID de la participation
     * @return Les données formatées pour le QR code
     */
    String generateQRData(User user, Course course, int participationId);
    
    /**
     * Valide et décode les données d'un QR code.
     * 
     * @param qrData Les données décodées du QR code
     * @return Informations sur la participation ou null si invalide
     * @throws Exception si erreur lors de la validation
     */
    QRCodeData validateQRCode(String qrData) throws Exception;
    
    /**
     * Classe pour représenter les données décodées d'un QR code.
     */
    public static class QRCodeData {
        private final int participationId;
        private final int userId;
        private final int courseId;
        private final String timestamp;
        
        public QRCodeData(int participationId, int userId, int courseId, String timestamp) {
            this.participationId = participationId;
            this.userId = userId;
            this.courseId = courseId;
            this.timestamp = timestamp;
        }
        
        public int getParticipationId() { return participationId; }
        public int getUserId() { return userId; }
        public int getCourseId() { return courseId; }
        public String getTimestamp() { return timestamp; }
    }
} 