package com.colorrun.service;

import com.colorrun.business.Dossard;
import java.util.List;

/**
 * Service pour la gestion des dossards Color Run
 * Génération de dossards avec QR codes pour les participants
 */
public interface DossardService {
    
    /**
     * Génère un dossard complet (avec QR code et PDF) pour une participation
     * @param participationId ID de la participation
     * @return Le dossard généré avec QR code et PDF
     * @throws Exception si erreur lors de la génération
     */
    Dossard genererDossard(int participationId) throws Exception;
    
    /**
     * Génère uniquement le QR code pour un dossard existant
     * @param dossard Le dossard pour lequel générer le QR code
     * @return Les bytes de l'image QR code (PNG)
     * @throws Exception si erreur lors de la génération
     */
    byte[] genererQrCode(Dossard dossard) throws Exception;
    
    /**
     * Génère le PDF du dossard avec toutes les informations
     * @param dossard Le dossard pour lequel générer le PDF
     * @return Les bytes du PDF généré
     * @throws Exception si erreur lors de la génération
     */
    byte[] genererPdfDossard(Dossard dossard) throws Exception;
    
    /**
     * Trouve un dossard par son numéro
     * @param numeroDossard Le numéro du dossard
     * @return Le dossard trouvé ou null
     * @throws Exception si erreur lors de la recherche
     */
    Dossard trouverParNumero(String numeroDossard) throws Exception;
    
    /**
     * Trouve un dossard par ID de participation
     * @param participationId L'ID de la participation
     * @return Le dossard trouvé ou null
     * @throws Exception si erreur lors de la recherche
     */
    Dossard trouverParParticipation(int participationId) throws Exception;
    
    /**
     * Récupère tous les dossards d'une course
     * @param courseId L'ID de la course
     * @return Liste des dossards de la course
     * @throws Exception si erreur lors de la recherche
     */
    List<Dossard> trouverParCourse(int courseId) throws Exception;
    
    /**
     * Génère un numéro de dossard unique pour une course
     * @param courseId L'ID de la course
     * @return Le numéro de dossard généré
     * @throws Exception si erreur lors de la génération
     */
    String genererNumeroDossard(int courseId) throws Exception;
    
    /**
     * Sauvegarde un dossard en base de données
     * @param dossard Le dossard à sauvegarder
     * @return Le dossard sauvegardé avec son ID
     * @throws Exception si erreur lors de la sauvegarde
     */
    Dossard sauvegarder(Dossard dossard) throws Exception;
    
    /**
     * Vérifie si un dossard existe déjà pour une participation
     * @param participationId L'ID de la participation
     * @return true si un dossard existe, false sinon
     * @throws Exception si erreur lors de la vérification
     */
    boolean dossardExiste(int participationId) throws Exception;
    
    /**
     * Régénère un dossard existant (nouveau QR code et PDF)
     * @param participationId L'ID de la participation
     * @return Le dossard régénéré
     * @throws Exception si erreur lors de la régénération
     */
    Dossard regenererDossard(int participationId) throws Exception;
    
    /**
     * Valide un QR code de dossard
     * @param qrCodeData Les données du QR code à valider
     * @return Le dossard correspondant ou null si invalide
     * @throws Exception si erreur lors de la validation
     */
    Dossard validerQrCode(String qrCodeData) throws Exception;
} 