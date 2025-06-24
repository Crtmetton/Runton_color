package com.colorrun.service.impl;

import com.colorrun.business.User;
import com.colorrun.dao.UserDAO;
import com.colorrun.service.UserService;
import com.colorrun.util.PasswordUtil;
import com.colorrun.config.EmailConfig;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import javax.mail.Message;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.sql.SQLException;
import javax.mail.MessagingException;

public class UserServiceImpl implements UserService {
    
    private UserDAO userDAO;
    
    public UserServiceImpl() {
        this.userDAO = new UserDAO();
    }
    
    @Override
    public User register(String firstName, String lastName, String email, String password) throws SQLException {
        // Check if user with email already exists
        Optional<User> existingUser = userDAO.findByEmail(email);
        if (existingUser.isPresent()) {
            throw new SQLException("User with email " + email + " already exists");
        }
        
        // Create new user
        User user = new User();
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setEmail(email);
        user.setPasswordHash(PasswordUtil.hashPassword(password));
        user.setRole("USER"); // Default role
        user.setEnabled(true); // Auto-enable for now, can be changed to false if email verification is needed
        
        // Save user
        userDAO.save(user);
        
        return user;
    }
    
    @Override
    public User authenticate(String email, String password) throws SQLException {
        Optional<User> userOpt = userDAO.findByEmail(email);
        if (!userOpt.isPresent()) {
            throw new SQLException("User not found");
        }
        
        User user = userOpt.get();
        
        if (!PasswordUtil.checkPassword(password, user.getPasswordHash())) {
            throw new SQLException("Invalid password");
        }
        
        if (!user.isEnabled()) {
            throw new SQLException("Account not verified");
        }
        
        return user;
    }
    
    @Override
    public User getUserById(int id) throws SQLException {
        Optional<User> user = userDAO.findById(id);
        if (!user.isPresent()) {
            throw new SQLException("User not found with ID: " + id);
        }
        return user.get();
    }
    
    @Override
    public User getUserByEmail(String email) throws SQLException {
        Optional<User> user = userDAO.findByEmail(email);
        if (!user.isPresent()) {
            throw new SQLException("User not found with email: " + email);
        }
        return user.get();
    }
    
    @Override
    public List<User> getAllUsers() throws SQLException {
        return userDAO.findAll();
    }
    
    @Override
    public void updateUser(User user) throws SQLException {
        userDAO.update(user);
    }
    
    @Override
    public void changeUserRole(int userId, String role) throws SQLException {
        userDAO.updateRole(userId, role);
    }
    
    @Override
    public void deleteUser(int id) throws SQLException {
        userDAO.delete(id);
    }
    
    @Override
    public String createVerificationToken(User user) throws SQLException {
        String token = UUID.randomUUID().toString();
        // Le code pour enregistrer le token a été supprimé car la table VerificationToken n'existe pas dans le schéma original
        // Si nécessaire, nous pouvons le réactiver plus tard
        return token;
    }
    
    @Override
    public boolean verifyEmail(String token) throws SQLException {
        // Le code pour vérifier le token a été supprimé car la table VerificationToken n'existe pas dans le schéma original
        // Si nécessaire, nous pouvons le réactiver plus tard
        return true;
    }
    
    // Méthodes de compatibilité avec les servlets existants
    
    @Override
    public void register(User user) throws SQLException {
        register(user.getFirstName(), user.getLastName(), user.getEmail(), user.getPasswordHash());
    }
    
    @Override
    public Optional<User> login(String email, String password) throws SQLException {
        try {
            User user = authenticate(email, password);
            return Optional.of(user);
        } catch (SQLException e) {
            return Optional.empty();
        }
    }
    
    @Override
    public Optional<User> findById(int id) throws SQLException {
        return userDAO.findById(id);
    }
    
    @Override
    public Optional<User> findByEmail(String email) throws SQLException {
        return userDAO.findByEmail(email);
    }
    
    @Override
    public List<User> findAll() throws SQLException {
        return getAllUsers();
    }
    
    @Override
    public void updateProfile(User user) throws SQLException {
        updateUser(user);
    }
    
    @Override
    public void updateRole(int userId, String role) throws SQLException {
        changeUserRole(userId, role);
    }
    
    @Override
    public void delete(int id) throws SQLException {
        deleteUser(id);
    }
    
    @Override
    public void sendVerificationEmail(User user, String baseUrl) throws SQLException {
        // Implémentation simplifiée pour la compatibilité
        try {
            System.out.println("Envoi d'un email de vérification à " + user.getEmail());
        } catch (Exception e) {
            throw new SQLException("Erreur lors de l'envoi de l'email de vérification", e);
        }
    }
} 