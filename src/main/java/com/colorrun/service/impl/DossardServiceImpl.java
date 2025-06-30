package com.colorrun.service.impl;

import com.colorrun.service.DossardService;
import com.colorrun.business.Dossard;
import com.colorrun.business.Course;
import com.colorrun.business.User;
import com.colorrun.business.Participation;
import com.colorrun.dao.DossardDAO;
import com.colorrun.dao.CourseDAO;
import com.colorrun.dao.UserDAO;
import com.colorrun.dao.ParticipationDAO;

// QR Code generation
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.client.j2se.MatrixToImageWriter;

// PDF generation
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.time.format.DateTimeFormatter;

/**
 * Implémentation du service DossardService.
 * 
 * Cette classe gère la génération complète des dossards incluant :
 * - Attribution automatique des numéros
 * - Génération des QR codes
 * - Création des PDFs personnalisés
 * - Persistance en base de données
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public class DossardServiceImpl implements DossardService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
    private static final int QR_CODE_SIZE = 200;
    
    private final DossardDAO dossardDAO;
    private final CourseDAO courseDAO;
    private final UserDAO userDAO;
    private final ParticipationDAO participationDAO;
    
    public DossardServiceImpl() {
        this.dossardDAO = new DossardDAO();
        this.courseDAO = new CourseDAO();
        this.userDAO = new UserDAO();
        this.participationDAO = new ParticipationDAO();
    }
    
    @Override
    public Dossard genererDossard(int participationId) throws Exception {
        // Récupérer les informations de participation
        Optional<Participation> participationOpt = participationDAO.findById(participationId);
        if (!participationOpt.isPresent()) {
            throw new Exception("Participation non trouvée : " + participationId);
        }
        
        Participation participation = participationOpt.get();
        
        // Vérifier si un dossard existe déjà
        Dossard existingDossard = dossardDAO.findByParticipantAndCourse(
            participation.getUser().getId(), participation.getCourse().getId());
        if (existingDossard != null) {
            return existingDossard;
        }
        
        // Créer le nouveau dossard
        int nextNumber = dossardDAO.findNextAvailableNumber(participation.getCourse().getId());
        
        Dossard dossard = new Dossard(nextNumber, participation.getCourse().getId(), participation.getUser().getId());
        dossard.setStatus("ASSIGNED");
        
        // Sauvegarder en base
        dossard = dossardDAO.save(dossard);
        
        return dossard;
    }
    
    @Override
    public byte[] genererQrCode(Dossard dossard) throws Exception {
        // Données du QR code : format JSON simple
        String qrData = String.format("{\"dossard\":%d,\"course\":%d,\"participant\":%d}", 
                                     dossard.getNumber(), dossard.getCourseId(), dossard.getParticipantId());
        
        // Configuration du QR code
        Map<EncodeHintType, Object> hints = new HashMap<>();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        
        // Générer la matrice
        MultiFormatWriter writer = new MultiFormatWriter();
        BitMatrix bitMatrix = writer.encode(qrData, BarcodeFormat.QR_CODE, QR_CODE_SIZE, QR_CODE_SIZE, hints);
        
        // Convertir en image
        BufferedImage qrImage = MatrixToImageWriter.toBufferedImage(bitMatrix);
        
        // Convertir en bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(qrImage, "PNG", baos);
        
        return baos.toByteArray();
    }
    
    @Override
    public byte[] genererPdfDossard(Dossard dossard) throws Exception {
        // Récupérer les informations associées
        Optional<Course> courseOpt = courseDAO.findById(dossard.getCourseId());
        Optional<User> participantOpt = userDAO.findById(dossard.getParticipantId());
        
        if (!courseOpt.isPresent() || !participantOpt.isPresent()) {
            throw new Exception("Données manquantes pour générer le dossard");
        }
        
        Course course = courseOpt.get();
        User participant = participantOpt.get();
        
        // Générer le QR code
        byte[] qrCodeBytes = genererQrCode(dossard);
        
        // Créer le PDF
        PDDocument document = new PDDocument();
        PDPage page = new PDPage();
        document.addPage(page);
        
        try (PDPageContentStream contentStream = new PDPageContentStream(document, page)) {
            
            // Titre principal
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
            contentStream.newLineAtOffset(50, 750);
            contentStream.showText("COLOR RUN - DOSSARD");
            contentStream.endText();
            
            // Numéro de dossard (très visible)
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 72);
            contentStream.newLineAtOffset(250, 650);
            contentStream.showText(String.valueOf(dossard.getNumber()));
            contentStream.endText();
            
            // Informations du participant
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.newLineAtOffset(50, 580);
            contentStream.showText("PARTICIPANT");
            contentStream.endText();
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 14);
            contentStream.newLineAtOffset(50, 560);
            contentStream.showText(participant.getFullName());
            contentStream.endText();
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 540);
            contentStream.showText(participant.getEmail());
            contentStream.endText();
            
            // Informations de la course
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
            contentStream.newLineAtOffset(50, 480);
            contentStream.showText("COURSE");
            contentStream.endText();
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 14);
            contentStream.newLineAtOffset(50, 460);
            contentStream.showText(course.getName());
            contentStream.endText();
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 440);
            contentStream.showText("Date: " + course.getDate().format(DATE_FORMATTER));
            contentStream.endText();
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 420);
            contentStream.showText("Lieu: " + course.getCity());
            contentStream.endText();
            
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 12);
            contentStream.newLineAtOffset(50, 400);
            contentStream.showText("Distance: " + course.getDistance() + " km");
            contentStream.endText();
            
            // Instructions
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_BOLD, 14);
            contentStream.newLineAtOffset(50, 340);
            contentStream.showText("INSTRUCTIONS");
            contentStream.endText();
            
            String[] instructions = {
                "• Portez ce dossard bien visible sur le torse",
                "• Arrivez 30 minutes avant le départ",
                "• Portez des vêtements blancs",
                "• Gardez votre dossard pendant toute la course",
                "• Présentez le QR code aux contrôles"
            };
            
            int yPosition = 320;
            for (String instruction : instructions) {
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA, 10);
                contentStream.newLineAtOffset(50, yPosition);
                contentStream.showText(instruction);
                contentStream.endText();
                yPosition -= 15;
            }
            
            // Contact
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA, 10);
            contentStream.newLineAtOffset(50, 200);
            contentStream.showText("Contact: contact@colorrun.com | www.colorrun.com");
            contentStream.endText();
            
            // Note de découpe
            contentStream.beginText();
            contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 8);
            contentStream.newLineAtOffset(50, 50);
            contentStream.showText("Découpez le long des pointillés et fixez sur votre torse");
            contentStream.endText();
        }
        
        // Ajouter le QR code comme image
        try {
            BufferedImage qrImage = ImageIO.read(new java.io.ByteArrayInputStream(qrCodeBytes));
            PDImageXObject pdImage = PDImageXObject.createFromByteArray(document, qrCodeBytes, "qr-code");
            
            try (PDPageContentStream contentStream = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, true)) {
                contentStream.drawImage(pdImage, 350, 180, 150, 150);
                
                // Label QR code
                contentStream.beginText();
                contentStream.setFont(PDType1Font.HELVETICA_BOLD, 12);
                contentStream.newLineAtOffset(380, 160);
                contentStream.showText("QR CODE");
                contentStream.endText();
            }
        } catch (Exception e) {
            System.err.println("Erreur ajout QR code au PDF : " + e.getMessage());
        }
        
        // Sauvegarder en bytes
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        document.save(baos);
        document.close();
        
        return baos.toByteArray();
    }
    
    @Override
    public Dossard trouverParNumero(String numeroDossard) throws Exception {
        try {
            int numero = Integer.parseInt(numeroDossard);
            // Note: Il faudrait aussi spécifier la course, mais pour simplifier...
            return null; // Implementation simplifiée
        } catch (NumberFormatException e) {
            throw new Exception("Numéro de dossard invalide : " + numeroDossard);
        }
    }
    
    @Override
    public Dossard trouverParParticipation(int participationId) throws Exception {
        Optional<Participation> participationOpt = participationDAO.findById(participationId);
        if (!participationOpt.isPresent()) {
            return null;
        }
        
        Participation participation = participationOpt.get();
        return dossardDAO.findByParticipantAndCourse(
            participation.getUser().getId(), participation.getCourse().getId());
    }
    
    @Override
    public List<Dossard> trouverParCourse(int courseId) throws Exception {
        return dossardDAO.findByCourse(courseId);
    }
    
    @Override
    public String genererNumeroDossard(int courseId) throws Exception {
        int nextNumber = dossardDAO.findNextAvailableNumber(courseId);
        return String.valueOf(nextNumber);
    }
    
    @Override
    public Dossard sauvegarder(Dossard dossard) throws Exception {
        if (dossard.getId() == 0) {
            return dossardDAO.save(dossard);
        } else {
            dossardDAO.update(dossard);
            return dossard;
        }
    }
    
    @Override
    public boolean dossardExiste(int participationId) throws Exception {
        return trouverParParticipation(participationId) != null;
    }
    
    @Override
    public Dossard regenererDossard(int participationId) throws Exception {
        // Supprimer l'ancien dossard s'il existe
        Dossard existingDossard = trouverParParticipation(participationId);
        if (existingDossard != null) {
            dossardDAO.delete(existingDossard.getId());
        }
        
        // Générer un nouveau dossard
        return genererDossard(participationId);
    }
    
    @Override
    public Dossard validerQrCode(String qrCodeData) throws Exception {
        // Parse des données JSON du QR code
        try {
            // Format attendu: {"dossard":123,"course":456,"participant":789}
            if (qrCodeData.startsWith("{") && qrCodeData.contains("dossard")) {
                // Extraction simple (sans lib JSON pour rester léger)
                String dossardStr = extractJsonValue(qrCodeData, "dossard");
                String courseStr = extractJsonValue(qrCodeData, "course");
                
                if (dossardStr != null && courseStr != null) {
                    int dossardNumber = Integer.parseInt(dossardStr);
                    int courseId = Integer.parseInt(courseStr);
                    
                    return dossardDAO.findByNumberAndCourse(dossardNumber, courseId);
                }
            }
        } catch (Exception e) {
            throw new Exception("QR Code invalide : " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Extrait une valeur d'un JSON simple (sans librairie externe).
     */
    private String extractJsonValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) return null;
        
        startIndex += searchKey.length();
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = json.indexOf("}", startIndex);
        }
        
        if (endIndex > startIndex) {
            return json.substring(startIndex, endIndex).trim();
        }
        
        return null;
    }
} 