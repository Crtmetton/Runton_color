package com.colorrun.service.impl;

import com.colorrun.service.EmailService;
import com.colorrun.business.User;
import com.colorrun.business.Course;
import com.colorrun.business.Dossard;
import com.colorrun.config.EmailConfig;
import com.colorrun.util.Logger;

import javax.mail.*;
import javax.mail.internet.*;
import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.time.format.DateTimeFormatter;
import java.util.Properties;

/**
 * Implémentation du service EmailService utilisant JavaMail.
 * 
 * Cette classe gère tous les envois d'emails de l'application en utilisant
 * la configuration centralisée dans EmailConfig.
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public class EmailServiceImpl implements EmailService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
    
    private final EmailConfig emailConfig;
    
    public EmailServiceImpl() {
        this.emailConfig = EmailConfig.getInstance();
    }
    
    @Override
    public void sendVerificationEmail(User user, String verificationToken) throws Exception {
        Logger.step("EmailService", "Envoi email de vérification");
        Logger.config("EmailService", "Service activé", String.valueOf(emailConfig.isEnabled()));
        Logger.config("EmailService", "Auth configurée", String.valueOf(emailConfig.isAuthenticationConfigured()));
        
        if (!emailConfig.isEnabled() || !emailConfig.isAuthenticationConfigured()) {
            Logger.warn("EmailService", "Service email non configuré - Email de vérification non envoyé à " + user.getEmail());
            return;
        }
        
        String subject = "Vérifiez votre compte Color Run";
        String verificationUrl = "http://localhost:8080/runton-color/verify?token=" + verificationToken;
        
        Logger.debug("EmailService", "URL de vérification: " + verificationUrl);
        Logger.debug("EmailService", "Sujet: " + subject);
        
        String content = buildVerificationEmailContent(user, verificationUrl);
        
        sendHtmlEmail(user.getEmail(), subject, content);
        Logger.stepSuccess("EmailService", "Email de vérification envoyé");
    }
    
    @Override
    public void sendWelcomeEmail(User user) throws Exception {
        if (!emailConfig.isEnabled() || !emailConfig.isAuthenticationConfigured()) {
            return;
        }
        
        String subject = "Bienvenue dans Color Run !";
        String content = buildWelcomeEmailContent(user);
        
        sendHtmlEmail(user.getEmail(), subject, content);
    }
    
    @Override
    public void sendDossardEmail(User user, Course course, Dossard dossard, byte[] pdfBytes) throws Exception {
        if (!emailConfig.isEnabled() || !emailConfig.isAuthenticationConfigured()) {
            System.out.println("Service email non configuré - Dossard non envoyé à " + user.getEmail());
            return;
        }
        
        String subject = "Votre dossard pour " + course.getName();
        String content = buildDossardEmailContent(user, course, dossard);
        
        sendHtmlEmailWithAttachment(user.getEmail(), subject, content, pdfBytes, 
                                  "dossard_" + dossard.getNumber() + ".pdf", "application/pdf");
    }
    
    @Override
    public void sendCourseRegistrationConfirmation(User user, Course course) throws Exception {
        if (!emailConfig.isEnabled() || !emailConfig.isAuthenticationConfigured()) {
            return;
        }
        
        String subject = "Confirmation d'inscription - " + course.getName();
        String content = buildRegistrationConfirmationContent(user, course);
        
        sendHtmlEmail(user.getEmail(), subject, content);
    }
    
    @Override
    public void sendCourseReminder(User user, Course course) throws Exception {
        if (!emailConfig.isEnabled() || !emailConfig.isAuthenticationConfigured()) {
            return;
        }
        
        String subject = "Rappel : " + course.getName() + " dans 2 jours !";
        String content = buildCourseReminderContent(user, course);
        
        sendHtmlEmail(user.getEmail(), subject, content);
    }
    
    @Override
    public void sendPasswordResetEmail(User user, String resetToken) throws Exception {
        if (!emailConfig.isEnabled() || !emailConfig.isAuthenticationConfigured()) {
            return;
        }
        
        String subject = "Réinitialisation de votre mot de passe Color Run";
        String resetUrl = "http://localhost:8080/runton-color/reset-password?token=" + resetToken;
        String content = buildPasswordResetContent(user, resetUrl);
        
        sendHtmlEmail(user.getEmail(), subject, content);
    }
    
    @Override
    public void sendCustomEmail(String recipient, String subject, String content) throws Exception {
        if (!emailConfig.isEnabled() || !emailConfig.isAuthenticationConfigured()) {
            return;
        }
        
        sendHtmlEmail(recipient, subject, content);
    }
    
    @Override
    public boolean testEmailConfiguration(String testRecipient) {
        try {
            if (!emailConfig.isEnabled() || !emailConfig.isAuthenticationConfigured()) {
                return false;
            }
            
            String subject = "Test de configuration Color Run";
            String content = "<h2>Test de configuration réussi !</h2>" +
                           "<p>Ce message confirme que la configuration email fonctionne correctement.</p>";
            
            sendHtmlEmail(testRecipient, subject, content);
            return true;
        } catch (Exception e) {
            System.err.println("Erreur lors du test de configuration email : " + e.getMessage());
            return false;
        }
    }
    /**
     * Envoie un email HTML simple.
     */
    private void sendHtmlEmail(String recipient, String subject, String htmlContent) throws Exception {
        Logger.step("EmailService", "Configuration du message email");
        Logger.config("EmailService", "Destinataire", recipient);
        Logger.config("EmailService", "Expéditeur", emailConfig.getFromEmail());
        Logger.config("EmailService", "Serveur SMTP", emailConfig.getSmtpHost() + ":" + emailConfig.getSmtpPort());
        
        Session session = createEmailSession();
        Logger.debug("EmailService", "Session SMTP créée");
        
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailConfig.getFromEmail(), emailConfig.getFromName()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html; charset=utf-8");
        Logger.debug("EmailService", "Message configuré");
        
        Logger.step("EmailService", "Envoi du message via SMTP");
        Transport.send(message);
        Logger.stepSuccess("EmailService", "Message envoyé via SMTP");
        Logger.success("EmailService", "Email envoyé avec succès à: " + recipient);
    }
    
    /**
     * Envoie un email HTML avec une pièce jointe.
     */
    private void sendHtmlEmailWithAttachment(String recipient, String subject, String htmlContent,
                                           byte[] attachmentBytes, String attachmentName, String mimeType) throws Exception {
        Session session = createEmailSession();
        
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(emailConfig.getFromEmail(), emailConfig.getFromName()));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(recipient));
        message.setSubject(subject);
        
        // Créer le multipart
        Multipart multipart = new MimeMultipart();
        
        // Partie HTML
        BodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlContent, "text/html; charset=utf-8");
        multipart.addBodyPart(htmlPart);
        
        // Pièce jointe
        BodyPart attachmentPart = new MimeBodyPart();
        DataSource source = new ByteArrayDataSource(attachmentBytes, mimeType);
        attachmentPart.setDataHandler(new DataHandler(source));
        attachmentPart.setFileName(attachmentName);
        multipart.addBodyPart(attachmentPart);
        
        message.setContent(multipart);
        
        Transport.send(message);
        System.out.println("Email avec pièce jointe envoyé avec succès à : " + recipient);
    }
    
    /**
     * Crée une session email avec authentification.
     */
    private Session createEmailSession() {
        Properties props = emailConfig.getProperties();
        
        return Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(emailConfig.getUsername(), emailConfig.getPassword());
            }
        });
    }
    
    /**
     * Construit le contenu HTML pour l'email de vérification.
     */
    private String buildVerificationEmailContent(User user, String verificationUrl) {
        return "<!DOCTYPE html>" +
               "<html><head><meta charset='UTF-8'></head><body>" +
               "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
               "<h1 style='color: #e74c3c;'>Bienvenue dans Color Run !</h1>" +
               "<p>Bonjour " + user.getFirstName() + ",</p>" +
               "<p>Merci de vous être inscrit sur Color Run. Pour activer votre compte, veuillez cliquer sur le lien ci-dessous :</p>" +
               "<div style='text-align: center; margin: 30px 0;'>" +
               "<a href='" + verificationUrl + "' style='background-color: #e74c3c; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block;'>Vérifier mon compte</a>" +
               "</div>" +
               "<p>Si le bouton ne fonctionne pas, copiez et collez ce lien dans votre navigateur :</p>" +
               "<p><a href='" + verificationUrl + "'>" + verificationUrl + "</a></p>" +
               "<p>Ce lien expire dans 24 heures.</p>" +
               "<hr style='margin: 30px 0; border: 1px solid #eee;'>" +
               "<p style='color: #666; font-size: 12px;'>Équipe Color Run<br>contact@colorrun.com</p>" +
               "</div></body></html>";
    }
    
    /**
     * Construit le contenu HTML pour l'email de bienvenue.
     */
    private String buildWelcomeEmailContent(User user) {
        return "<!DOCTYPE html>" +
               "<html><head><meta charset='UTF-8'></head><body>" +
               "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
               "<h1 style='color: #e74c3c;'>Compte activé avec succès !</h1>" +
               "<p>Félicitations " + user.getFirstName() + " !</p>" +
               "<p>Votre compte Color Run est maintenant actif. Vous pouvez :</p>" +
               "<ul>" +
               "<li>📅 Consulter les courses disponibles</li>" +
               "<li>🏃‍♂️ Vous inscrire à vos premières courses</li>" +
               "<li>👥 Rejoindre la communauté des coureurs</li>" +
               "</ul>" +
               "<div style='text-align: center; margin: 30px 0;'>" +
               "<a href='http://localhost:8080/runton-color/courses' style='background-color: #e74c3c; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block;'>Découvrir les courses</a>" +
               "</div>" +
               "<p>Bonne course !</p>" +
               "<hr style='margin: 30px 0; border: 1px solid #eee;'>" +
               "<p style='color: #666; font-size: 12px;'>Équipe Color Run<br>contact@colorrun.com</p>" +
               "</div></body></html>";
    }
    
    /**
     * Construit le contenu HTML pour l'email d'envoi de dossard.
     */
    private String buildDossardEmailContent(User user, Course course, Dossard dossard) {
        return "<!DOCTYPE html>" +
               "<html><head><meta charset='UTF-8'></head><body>" +
               "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
               "<h1 style='color: #e74c3c;'>Votre dossard est prêt ! 🏃‍♂️</h1>" +
               "<p>Bonjour " + user.getFirstName() + ",</p>" +
               "<p>Votre inscription à <strong>" + course.getName() + "</strong> est confirmée !</p>" +
               "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0;'>" +
               "<h3 style='color: #e74c3c; margin-top: 0;'>Détails de votre participation</h3>" +
               "<p><strong>📍 Lieu :</strong> " + course.getCity() + "</p>" +
               "<p><strong>📅 Date :</strong> " + course.getDate().format(DATE_FORMATTER) + "</p>" +
               "<p><strong>🏃‍♂️ Distance :</strong> " + course.getDistance() + " km</p>" +
               "<p><strong>🎯 Numéro de dossard :</strong> <span style='font-size: 24px; font-weight: bold; color: #e74c3c;'>" + dossard.getNumber() + "</span></p>" +
               "</div>" +
               "<p><strong>📎 Votre dossard est en pièce jointe de cet email.</strong></p>" +
               "<p>Veuillez l'imprimer et le porter le jour de la course. Le QR code permettra votre identification rapide.</p>" +
               "<h3>Instructions importantes :</h3>" +
               "<ul>" +
               "<li>Imprimez votre dossard en couleur si possible</li>" +
               "<li>Fixez-le bien visible sur votre torse</li>" +
               "<li>Arrivez 30 minutes avant le départ</li>" +
               "<li>N'oubliez pas vos vêtements blancs !</li>" +
               "</ul>" +
               "<p>Nous avons hâte de vous voir participer à cette Color Run !</p>" +
               "<hr style='margin: 30px 0; border: 1px solid #eee;'>" +
               "<p style='color: #666; font-size: 12px;'>Équipe Color Run<br>contact@colorrun.com</p>" +
               "</div></body></html>";
    }
    
    /**
     * Construit le contenu HTML pour l'email de confirmation d'inscription.
     */
    private String buildRegistrationConfirmationContent(User user, Course course) {
        return "<!DOCTYPE html>" +
               "<html><head><meta charset='UTF-8'></head><body>" +
               "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
               "<h1 style='color: #e74c3c;'>Inscription confirmée ! 🎉</h1>" +
               "<p>Bonjour " + user.getFirstName() + ",</p>" +
               "<p>Votre inscription à <strong>" + course.getName() + "</strong> a été confirmée !</p>" +
               "<div style='background-color: #f8f9fa; padding: 20px; border-radius: 8px; margin: 20px 0;'>" +
               "<h3 style='color: #e74c3c; margin-top: 0;'>Détails de la course</h3>" +
               "<p><strong>📍 Lieu :</strong> " + course.getCity() + "</p>" +
               "<p><strong>📅 Date :</strong> " + course.getDate().format(DATE_FORMATTER) + "</p>" +
               "<p><strong>🏃‍♂️ Distance :</strong> " + course.getDistance() + " km</p>" +
               "</div>" +
               "<p>Votre dossard vous sera envoyé par email quelques jours avant la course.</p>" +
               "<p>En attendant, n'hésitez pas à :</p>" +
               "<ul>" +
               "<li>Préparer vos vêtements blancs</li>" +
               "<li>Inviter vos amis à participer</li>" +
               "<li>Suivre nos actualités sur notre site</li>" +
               "</ul>" +
               "<hr style='margin: 30px 0; border: 1px solid #eee;'>" +
               "<p style='color: #666; font-size: 12px;'>Équipe Color Run<br>contact@colorrun.com</p>" +
               "</div></body></html>";
    }
    
    /**
     * Construit le contenu HTML pour l'email de rappel de course.
     */
    private String buildCourseReminderContent(User user, Course course) {
        return "<!DOCTYPE html>" +
               "<html><head><meta charset='UTF-8'></head><body>" +
               "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
               "<h1 style='color: #e74c3c;'>⏰ Plus que 2 jours !</h1>" +
               "<p>Bonjour " + user.getFirstName() + ",</p>" +
               "<p>La <strong>" + course.getName() + "</strong> aura lieu dans 2 jours !</p>" +
               "<div style='background-color: #fff3cd; padding: 20px; border-radius: 8px; margin: 20px 0; border-left: 4px solid #ffc107;'>" +
               "<h3 style='color: #856404; margin-top: 0;'>Rappel important</h3>" +
               "<p><strong>📅 Date :</strong> " + course.getDate().format(DATE_FORMATTER) + "</p>" +
               "<p><strong>📍 Lieu :</strong> " + course.getCity() + "</p>" +
               "</div>" +
               "<h3>Check-list pour le jour J :</h3>" +
               "<ul>" +
               "<li>✅ Vêtements blancs prêts</li>" +
               "<li>✅ Dossard imprimé et prêt à porter</li>" +
               "<li>✅ Chaussures de sport confortables</li>" +
               "<li>✅ Bouteille d'eau</li>" +
               "<li>✅ Bonne humeur !</li>" +
               "</ul>" +
               "<p>Nous avons hâte de vous voir !</p>" +
               "<hr style='margin: 30px 0; border: 1px solid #eee;'>" +
               "<p style='color: #666; font-size: 12px;'>Équipe Color Run<br>contact@colorrun.com</p>" +
               "</div></body></html>";
    }
    
    /**
     * Construit le contenu HTML pour l'email de réinitialisation de mot de passe.
     */
    private String buildPasswordResetContent(User user, String resetUrl) {
        return "<!DOCTYPE html>" +
               "<html><head><meta charset='UTF-8'></head><body>" +
               "<div style='font-family: Arial, sans-serif; max-width: 600px; margin: 0 auto;'>" +
               "<h1 style='color: #e74c3c;'>Réinitialisation de mot de passe</h1>" +
               "<p>Bonjour " + user.getFirstName() + ",</p>" +
               "<p>Vous avez demandé la réinitialisation de votre mot de passe Color Run.</p>" +
               "<div style='text-align: center; margin: 30px 0;'>" +
               "<a href='" + resetUrl + "' style='background-color: #e74c3c; color: white; padding: 15px 30px; text-decoration: none; border-radius: 5px; display: inline-block;'>Réinitialiser mon mot de passe</a>" +
               "</div>" +
               "<p>Si vous n'avez pas demandé cette réinitialisation, ignorez simplement cet email.</p>" +
               "<p>Ce lien expire dans 1 heure pour votre sécurité.</p>" +
               "<hr style='margin: 30px 0; border: 1px solid #eee;'>" +
               "<p style='color: #666; font-size: 12px;'>Équipe Color Run<br>contact@colorrun.com</p>" +
               "</div></body></html>";
    }
} 