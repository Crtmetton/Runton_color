-- Script pour insérer des utilisateurs de test avec les nouveaux rôles
-- USER, ORGANIZER, ADMIN

-- Mise à jour des utilisateurs existants avec le rôle PARTICIPANT vers USER
UPDATE USER SET ROLE = 'USER' WHERE ROLE = 'PARTICIPANT';

-- Insertion d'utilisateurs de test avec les nouveaux rôles
-- Mot de passe: "password123" pour tous

-- Utilisateur standard (USER)
INSERT INTO USER (PRENOM, NOM, EMAIL, MOTDEPASSE, ROLE, ENABLED) VALUES 
('Marie', 'Martin', 'marie.martin@test.com', '$2a$10$6H8/YGz0fZ9lJx0fZ9lJx0e.QN2A2kJ3lJx0fZ9lJx0fZ9lJx0fZ9m', 'USER', TRUE);

-- Organisateur (ORGANIZER)  
INSERT INTO USER (PRENOM, NOM, EMAIL, MOTDEPASSE, ROLE, ENABLED) VALUES 
('Pierre', 'Organisateur', 'pierre.orga@test.com', '$2a$10$6H8/YGz0fZ9lJx0fZ9lJx0e.QN2A2kJ3lJx0fZ9lJx0fZ9lJx0fZ9m', 'ORGANIZER', TRUE);

-- Administrateur (ADMIN)
INSERT INTO USER (PRENOM, NOM, EMAIL, MOTDEPASSE, ROLE, ENABLED) VALUES 
('Sophie', 'Admin', 'sophie.admin@test.com', '$2a$10$6H8/YGz0fZ9lJx0fZ9lJx0e.QN2A2kJ3lJx0fZ9lJx0fZ9lJx0fZ9m', 'ADMIN', TRUE);

-- Vérification des rôles
SELECT ROLE, COUNT(*) as NOMBRE FROM USER GROUP BY ROLE ORDER BY ROLE; 