package com.colorrun.service;

import com.colorrun.business.User;
import com.colorrun.business.Course;
import com.colorrun.business.Dossard;

/**
 * Service pour la gestion des envois d'emails dans l'application Color Run.
 * 
 * Ce service centralise tous les envois d'emails automatiques de l'application,
 * incluant les confirmations d'inscription, les notifications de course
 * et l'envoi des dossards.
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public interface EmailService {
    
    /**
     * Envoie un email de vérification lors de la création de compte.
     * 
     * @param user L'utilisateur qui vient de s'inscrire
     * @param verificationToken Le token de vérification à inclure dans l'email
     * @throws Exception si erreur lors de l'envoi
     */
    void sendVerificationEmail(User user, String verificationToken) throws Exception;
    
    /**
     * Envoie un email de bienvenue après vérification du compte.
     * 
     * @param user L'utilisateur qui vient de vérifier son compte
     * @throws Exception si erreur lors de l'envoi
     */
    void sendWelcomeEmail(User user) throws Exception;
    
    /**
     * Envoie le dossard par email lors de l'inscription à une course.
     * 
     * @param user Le participant
     * @param course La course à laquelle il s'est inscrit
     * @param dossard Le dossard généré avec QR code
     * @param pdfBytes Les bytes du PDF du dossard
     * @throws Exception si erreur lors de l'envoi
     */
    void sendDossardEmail(User user, Course course, Dossard dossard, byte[] pdfBytes) throws Exception;
    
    /**
     * Envoie une notification de confirmation d'inscription à une course.
     * 
     * @param user Le participant
     * @param course La course à laquelle il s'est inscrit
     * @throws Exception si erreur lors de l'envoi
     */
    void sendCourseRegistrationConfirmation(User user, Course course) throws Exception;
    
    /**
     * Envoie un rappel de course (1-2 jours avant l'événement).
     * 
     * @param user Le participant
     * @param course La course qui approche
     * @throws Exception si erreur lors de l'envoi
     */
    void sendCourseReminder(User user, Course course) throws Exception;
    
    /**
     * Envoie un email de réinitialisation de mot de passe.
     * 
     * @param user L'utilisateur qui demande la réinitialisation
     * @param resetToken Le token de réinitialisation
     * @throws Exception si erreur lors de l'envoi
     */
    void sendPasswordResetEmail(User user, String resetToken) throws Exception;
    
    /**
     * Envoie un email administratif personnalisé.
     * 
     * @param recipient L'email du destinataire
     * @param subject Le sujet de l'email
     * @param content Le contenu HTML de l'email
     * @throws Exception si erreur lors de l'envoi
     */
    void sendCustomEmail(String recipient, String subject, String content) throws Exception;
    
    /**
     * Teste la configuration email en envoyant un email de test.
     * 
     * @param testRecipient L'adresse email de test
     * @return true si l'envoi a réussi, false sinon
     */
    boolean testEmailConfiguration(String testRecipient);
} 