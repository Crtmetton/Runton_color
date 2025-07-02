package com.colorrun.service;

import com.colorrun.business.User;
import com.colorrun.business.Course;
import com.colorrun.business.Participation;

/**
 * Service pour la génération de PDF de dossards avec QR codes intégrés.
 * 
 * Ce service génère des dossards PDF complets pour les participants
 * incluant toutes les informations nécessaires et le QR code.
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public interface PDFDossardService {
    
    /**
     * Génère un PDF de dossard complet avec QR code intégré.
     * 
     * @param user Le participant
     * @param course La course
     * @param participation La participation
     * @param qrCodeBytes Les bytes de l'image QR code
     * @return Les bytes du PDF généré
     * @throws Exception si erreur lors de la génération
     */
    byte[] generateDossardPDF(User user, Course course, Participation participation, byte[] qrCodeBytes) throws Exception;
    
    /**
     * Génère le nom de fichier pour le dossard PDF.
     * 
     * @param courseName Le nom de la course
     * @param participationId L'ID de la participation
     * @return Le nom du fichier
     */
    String generateDossardFileName(String courseName, int participationId);
} 