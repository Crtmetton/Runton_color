package com.colorrun.service.impl;

import com.colorrun.service.QRCodeService;
import com.colorrun.business.User;
import com.colorrun.business.Course;
import com.colorrun.business.Participation;
import com.colorrun.util.Logger;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

/**
 * Implémentation du service QRCodeService utilisant ZXing.
 * 
 * Cette classe génère des QR codes contenant les informations
 * nécessaires pour identifier un participant à une course.
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 */
public class QRCodeServiceImpl implements QRCodeService {
    
    private static final int QR_CODE_WIDTH = 300;
    private static final int QR_CODE_HEIGHT = 300;
    private static final String QR_DATA_SEPARATOR = "|";
    private static final String QR_TIMESTAMP_FORMAT = "yyyyMMddHHmmss";
    
    @Override
    public byte[] generateQRCode(Participation participation) throws Exception {
        Logger.step("QRCodeService", "🔄 Génération QR code pour participation " + participation.getId());
        
        String qrData = generateQRData(
            participation.getUser(), 
            participation.getCourse(), 
            participation.getId()
        );
        
        Logger.debug("QRCodeService", "Données QR: " + qrData);
        
        return generateQRCode(qrData);
    }
    
    @Override
    public byte[] generateQRCode(String data) throws Exception {
        try {
            QRCodeWriter qrCodeWriter = new QRCodeWriter();
            BitMatrix bitMatrix = qrCodeWriter.encode(data, BarcodeFormat.QR_CODE, QR_CODE_WIDTH, QR_CODE_HEIGHT);
            
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", outputStream);
            
            byte[] qrCodeBytes = outputStream.toByteArray();
            Logger.success("QRCodeService", "✅ QR code généré avec succès (" + qrCodeBytes.length + " bytes)");
            
            return qrCodeBytes;
            
        } catch (WriterException | IOException e) {
            Logger.error("QRCodeService", "❌ Erreur génération QR code: " + e.getMessage());
            throw new Exception("Erreur lors de la génération du QR code", e);
        }
    }
    
    @Override
    public String generateQRData(User user, Course course, int participationId) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern(QR_TIMESTAMP_FORMAT));
        
        // Format: PARTICIPATION_ID|USER_ID|COURSE_ID|USER_EMAIL|COURSE_NAME|TIMESTAMP
        String qrData = String.join(QR_DATA_SEPARATOR,
            String.valueOf(participationId),
            String.valueOf(user.getId()),
            String.valueOf(course.getId()),
            user.getEmail(),
            course.getName(),
            timestamp
        );
        
        // Encoder en Base64 pour sécuriser les données
        return Base64.getEncoder().encodeToString(qrData.getBytes());
    }
    
    @Override
    public QRCodeData validateQRCode(String qrData) throws Exception {
        try {
            // Décoder le Base64
            String decodedData = new String(Base64.getDecoder().decode(qrData));
            Logger.debug("QRCodeService", "Données décodées: " + decodedData);
            
            String[] parts = decodedData.split("\\" + QR_DATA_SEPARATOR);
            
            if (parts.length != 6) {
                throw new Exception("Format de QR code invalide");
            }
            
            int participationId = Integer.parseInt(parts[0]);
            int userId = Integer.parseInt(parts[1]);
            int courseId = Integer.parseInt(parts[2]);
            String timestamp = parts[5];
            
            Logger.success("QRCodeService", "✅ QR code validé - Participation: " + participationId);
            
            return new QRCodeData(participationId, userId, courseId, timestamp);
            
        } catch (Exception e) {
            Logger.error("QRCodeService", "❌ Erreur validation QR code: " + e.getMessage());
            throw new Exception("QR code invalide", e);
        }
    }
    
    /**
     * Génère un nom de fichier unique pour le QR code.
     * 
     * @param participationId L'ID de la participation
     * @return Le nom du fichier
     */
    public String generateQRCodeFileName(int participationId) {
        return "qrcode_participation_" + participationId + ".png";
    }
    
    /**
     * Génère un QR code de test pour validation.
     * 
     * @return Les bytes du QR code de test
     * @throws Exception si erreur lors de la génération
     */
    public byte[] generateTestQRCode() throws Exception {
        String testData = "TEST_QR_CODE_COLOR_RUN";
        return generateQRCode(testData);
    }
} 