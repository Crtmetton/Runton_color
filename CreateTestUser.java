import java.sql.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class CreateTestUser {
    public static void main(String[] args) {
        String url = "jdbc:h2:./dbFiles/Runton_color_Prod";
        String username = "Runton";
        String password = "";
        
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        try (Connection conn = DriverManager.getConnection(url, username, password)) {
            // Hash des mots de passe
            String testHash = encoder.encode("test123");
            String adminHash = encoder.encode("admin123");
            
            System.out.println("Hash pour test123: " + testHash);
            System.out.println("Hash pour admin123: " + adminHash);
            
            // Insertion utilisateur test
            String sql = "INSERT INTO Utilisateur (prenom, nom, email, hashMotDePasse, role, enabled) VALUES (?, ?, ?, ?, ?, ?)";
            
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // Utilisateur test
                stmt.setString(1, "Test");
                stmt.setString(2, "User");
                stmt.setString(3, "test@runton.com");
                stmt.setString(4, testHash);
                stmt.setString(5, "PARTICIPANT");
                stmt.setBoolean(6, true);
                stmt.executeUpdate();
                
                // Utilisateur admin
                stmt.setString(1, "Admin");
                stmt.setString(2, "System");
                stmt.setString(3, "admin@runton.com");
                stmt.setString(4, adminHash);
                stmt.setString(5, "ADMIN");
                stmt.setBoolean(6, true);
                stmt.executeUpdate();
                
                System.out.println("✅ Utilisateurs créés avec succès!");
            }
            
        } catch (SQLException e) {
            System.err.println("❌ Erreur: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 