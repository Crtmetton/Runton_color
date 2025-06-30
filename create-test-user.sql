-- Script de création d'un utilisateur de test
-- Utilisateur: test@runton.com / password: test123

INSERT INTO Utilisateur (prenom, nom, email, hashMotDePasse, role, enabled) 
VALUES (
  'Test', 
  'User', 
  'test@runton.com', 
  '$2a$10$abcdefghijklmnopqrstuu7TpHRpNt1e0kDc5NP7TxNNllFMlGzNE6', -- BCrypt hash de "test123"
  'PARTICIPANT', 
  true
);

-- Utilisateur admin: admin@runton.com / password: admin123
INSERT INTO Utilisateur (prenom, nom, email, hashMotDePasse, role, enabled) 
VALUES (
  'Admin', 
  'System', 
  'admin@runton.com', 
  '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', -- BCrypt hash de "admin123"
  'ADMIN', 
  true
); 