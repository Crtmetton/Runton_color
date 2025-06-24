-- Drop tables in correct order to respect foreign key constraints
DROP TABLE IF EXISTS VerificationToken;
DROP TABLE IF EXISTS Message;
DROP TABLE IF EXISTS Discussion;
DROP TABLE IF EXISTS Organisation;
DROP TABLE IF EXISTS Inscription;
DROP TABLE IF EXISTS Participation;
DROP TABLE IF EXISTS DemandeOrganisateur;
DROP TABLE IF EXISTS Organisateur;
DROP TABLE IF EXISTS Participant;
DROP TABLE IF EXISTS Admin;
DROP TABLE IF EXISTS Course;
DROP TABLE IF EXISTS Utilisateur;


CREATE TABLE Utilisateur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    role VARCHAR(50) NOT NULL,
    hashMotDePasse VARCHAR(255) NOT NULL,
    photoProfile TEXT,
    enabled BOOLEAN DEFAULT TRUE
);

CREATE TABLE Course (
    id INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(255) NOT NULL,
    description TEXT,
    date TIMESTAMP NOT NULL,
    lieu VARCHAR(255) NOT NULL,
    distance DOUBLE NOT NULL,
    maxParticipants INT NOT NULL,
    prix INT NOT NULL,
    cause VARCHAR(255)
);

CREATE TABLE Participation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    utilisateurId INT NOT NULL,
    courseId INT NOT NULL,
    date TIMESTAMP NOT NULL,
    statut VARCHAR(50) NOT NULL, -- REGISTERED, CANCELED, etc.
    FOREIGN KEY (utilisateurId) REFERENCES Utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (courseId) REFERENCES Course(id) ON DELETE CASCADE
);

CREATE TABLE DemandeOrganisateur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    utilisateurId INT NOT NULL,
    date TIMESTAMP NOT NULL,
    motivations TEXT NOT NULL,
    statut VARCHAR(50) NOT NULL, -- PENDING, APPROVED, REJECTED
    FOREIGN KEY (utilisateurId) REFERENCES Utilisateur(id) ON DELETE CASCADE
);

-- Conserver les tables du schéma original pour compatibilité
CREATE TABLE Participant (
    id INT PRIMARY KEY AUTO_INCREMENT,
    utilisateur_id INT NOT NULL,
    FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id) ON DELETE CASCADE
);

CREATE TABLE Inscription (
    id INT PRIMARY KEY AUTO_INCREMENT,
    participant_id INT NOT NULL,
    course_id INT NOT NULL,
    FOREIGN KEY (participant_id) REFERENCES Participant(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES Course(id) ON DELETE CASCADE
);

CREATE TABLE Admin (
    id INT PRIMARY KEY AUTO_INCREMENT,
    utilisateur_id INT NOT NULL,
    FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id) ON DELETE CASCADE
);

CREATE TABLE Organisateur (
    id INT PRIMARY KEY AUTO_INCREMENT,
    utilisateur_id INT NOT NULL,
    FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id) ON DELETE CASCADE
);

CREATE TABLE Organisation (
    id INT PRIMARY KEY AUTO_INCREMENT,
    organisateur_id INT NOT NULL,
    course_id INT NOT NULL,
    FOREIGN KEY (organisateur_id) REFERENCES Organisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (course_id) REFERENCES Course(id) ON DELETE CASCADE
);

CREATE TABLE Discussion (
    id INT PRIMARY KEY AUTO_INCREMENT,
    course_id INT NOT NULL,
    FOREIGN KEY (course_id) REFERENCES Course(id) ON DELETE CASCADE
);

-- Modifier la structure de Message pour correspondre à notre DAO
CREATE TABLE Message (
    id INT PRIMARY KEY AUTO_INCREMENT,
    expediteurId INT NOT NULL,
    destinataireId INT NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    contenu TEXT NOT NULL,
    lu BOOLEAN DEFAULT FALSE,
    FOREIGN KEY (expediteurId) REFERENCES Utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (destinataireId) REFERENCES Utilisateur(id) ON DELETE CASCADE
);

-- Optionnel: Table pour la vérification des emails
CREATE TABLE VerificationToken (
    id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL,
    token VARCHAR(255) NOT NULL,
    expiry_date TIMESTAMP NOT NULL,
    FOREIGN KEY (user_id) REFERENCES Utilisateur(id) ON DELETE CASCADE
); 