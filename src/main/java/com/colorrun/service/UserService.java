package com.colorrun.service;

import com.colorrun.business.User;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface UserService {
    
    User register(String firstName, String lastName, String email, String password) throws SQLException;
    
    User authenticate(String email, String password) throws SQLException;
    
    User getUserById(int id) throws SQLException;
    
    User getUserByEmail(String email) throws SQLException;
    
    List<User> getAllUsers() throws SQLException;
    
    void updateUser(User user) throws SQLException;
    
    void changeUserRole(int userId, String role) throws SQLException;
    
    void deleteUser(int id) throws SQLException;
    
    String createVerificationToken(User user) throws SQLException;
    
    boolean verifyEmail(String token) throws SQLException;
    
    // Méthodes de compatibilité avec les servlets existants
    void register(User user) throws SQLException;
    
    Optional<User> login(String email, String password) throws SQLException;
    
    Optional<User> findById(int id) throws SQLException;
    
    Optional<User> findByEmail(String email) throws SQLException;
    
    List<User> findAll() throws SQLException;
    
    void updateProfile(User user) throws SQLException;
    
    void updateRole(int userId, String role) throws SQLException;
    
    void delete(int id) throws SQLException;
    
    void sendVerificationEmail(User user, String baseUrl) throws SQLException;
} 