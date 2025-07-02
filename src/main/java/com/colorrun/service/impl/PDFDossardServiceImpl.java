package com.colorrun.service.impl;

import com.colorrun.service.PDFDossardService;
import com.colorrun.business.User;
import com.colorrun.business.Course;
import com.colorrun.business.Participation;
import com.colorrun.util.Logger;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType1Font;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.format.DateTimeFormatter;

/**
 * Implémentation du service PDFDossardService utilisant PDFBox.
 * 
 * Cette classe génère des PDF de dossards avec QR codes intégrés
 * pour les participants aux courses Color Run.
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public class PDFDossardServiceImpl implements PDFDossardService {
    
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm");
    private static final float PAGE_WIDTH = PDRectangle.A4.getWidth();
    private static final float PAGE_HEIGHT = PDRectangle.A4.getHeight();
    private static final float MARGIN = 50;
    
    @Override
    public byte[] generateDossardPDF(User user, Course course, Participation participation, byte[] qrCodeBytes) throws Exception {
        Logger.step("PDFDossardService", "🔄 Génération PDF dossard pour " + user.getEmail());
        
        PDDocument document = new PDDocument();
        
        try {
            PDPage page = new PDPage(PDRectangle.A4);
            document.addPage(page);
            
            // Convertir le QR code en image PDFBox
            BufferedImage qrImage = ImageIO.read(new ByteArrayInputStream(qrCodeBytes));
            PDImageXObject qrImagePDF = PDImageXObject.createFromByteArray(document, qrCodeBytes, "QR Code");
            
            PDPageContentStream contentStream = new PDPageContentStream(document, page);
            
            try {
                // Dessiner le contenu du dossard
                drawDossardContent(contentStream, user, course, participation, qrImagePDF);
            } finally {
                contentStream.close();
            }
            
            // Convertir en bytes
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.save(outputStream);
            
            byte[] pdfBytes = outputStream.toByteArray();
            Logger.success("PDFDossardService", "✅ PDF généré avec succès (" + pdfBytes.length + " bytes)");
            
            return pdfBytes;
            
        } finally {
            document.close();
        }
    }
    
    /**
     * Dessine le contenu du dossard sur la page PDF.
     */
    private void drawDossardContent(PDPageContentStream contentStream, User user, Course course, 
                                   Participation participation, PDImageXObject qrImage) throws IOException {
        
        float y = PAGE_HEIGHT - MARGIN;
        
        // Titre principal
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 24);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText("DOSSARD COLOR RUN");
        contentStream.endText();
        y -= 60;
        
        // Informations de la course - Titre
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 18);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText(course.getName());
        contentStream.endText();
        y -= 40;
        
        // Informations de la course - Détails
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(MARGIN, y);
        
        String courseInfo = String.format("Lieu: %s", course.getCity());
        contentStream.showText(courseInfo);
        contentStream.newLineAtOffset(0, -20);
        
        String dateInfo = String.format("Date: %s", course.getDate().format(DATE_FORMATTER));
        contentStream.showText(dateInfo);
        contentStream.newLineAtOffset(0, -20);
        
        String distanceInfo = String.format("Distance: %.1f km", course.getDistance());
        contentStream.showText(distanceInfo);
        contentStream.endText();
        y -= 80;
        
        // Séparateur
        contentStream.moveTo(MARGIN, y);
        contentStream.lineTo(PAGE_WIDTH - MARGIN, y);
        contentStream.stroke();
        y -= 40;
        
        // Informations du participant
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText("PARTICIPANT");
        contentStream.endText();
        y -= 30;
        
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 12);
        contentStream.newLineAtOffset(MARGIN, y);
        
        String participantName = String.format("Nom: %s %s", user.getFirstName(), user.getLastName());
        contentStream.showText(participantName);
        contentStream.newLineAtOffset(0, -20);
        
        String participantEmail = String.format("Email: %s", user.getEmail());
        contentStream.showText(participantEmail);
        contentStream.newLineAtOffset(0, -20);
        
        String participationId = String.format("ID Participation: %d", participation.getId());
        contentStream.showText(participationId);
        contentStream.endText();
        y -= 80;
        
        // QR Code
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_BOLD, 16);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText("QR CODE DE PARTICIPATION");
        contentStream.endText();
        y -= 40;
        
        // Dessiner le QR code (centré)
        float qrSize = 150;
        float qrX = (PAGE_WIDTH - qrSize) / 2;
        contentStream.drawImage(qrImage, qrX, y - qrSize, qrSize, qrSize);
        y -= qrSize + 30;
        
        // Instructions
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA, 10);
        contentStream.newLineAtOffset(MARGIN, y);
        contentStream.showText("INSTRUCTIONS:");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("• Présentez ce dossard le jour de la course");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("• Gardez le QR code visible et lisible");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("• Arrivez 30 minutes avant le départ");
        contentStream.newLineAtOffset(0, -15);
        contentStream.showText("• N'oubliez pas vos vêtements blancs !");
        contentStream.endText();
        
        // Footer
        contentStream.beginText();
        contentStream.setFont(PDType1Font.HELVETICA_OBLIQUE, 8);
        contentStream.newLineAtOffset(MARGIN, 30);
        contentStream.showText("Color Run - Généré automatiquement");
        contentStream.endText();
    }
    
    @Override
    public String generateDossardFileName(String courseName, int participationId) {
        // Nettoyer le nom de la course pour le nom de fichier
        String cleanCourseName = courseName.replaceAll("[^a-zA-Z0-9\\s]", "")
                                           .replaceAll("\\s+", "_")
                                           .toLowerCase();
        
        return String.format("dossard_%s_%d.pdf", cleanCourseName, participationId);
    }
} 