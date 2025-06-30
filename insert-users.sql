-- Suppression des utilisateurs existants
DELETE FROM Utilisateur;

-- Insertion d'utilisateurs de test avec mots de passe BCrypt
-- Utilisateur test: test@runton.com / password: "password"
-- Hash BCrypt valide pour "password"
INSERT INTO Utilisateur (prenom, nom, email, hashMotDePasse, role, enabled) 
VALUES ('Test', 'User', 'test@runton.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'PARTICIPANT', true);

-- Utilisateur admin: admin@runton.com / password: "password"  
-- Hash BCrypt valide pour "password"
INSERT INTO Utilisateur (prenom, nom, email, hashMotDePasse, role, enabled) 
VALUES ('Admin', 'System', 'admin@runton.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN', true);

-- Utilisateur organizer: organizer@runton.com / password: "password"
-- Hash BCrypt valide pour "password"
INSERT INTO Utilisateur (prenom, nom, email, hashMotDePasse, role, enabled) 
VALUES ('Jean', 'Organisateur', 'organizer@runton.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ORGANIZER', true);

-- Vérification des données insérées
SELECT id, prenom, nom, email, role, enabled FROM Utilisateur; 