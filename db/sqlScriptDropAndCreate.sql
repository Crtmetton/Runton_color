DROP TABLE IF EXISTS Message;
DROP TABLE IF EXISTS Discussion;
DROP TABLE IF EXISTS Organisation;
DROP TABLE IF EXISTS Organisateur;
DROP TABLE IF EXISTS Admin;
DROP TABLE IF EXISTS Inscription;
DROP TABLE IF EXISTS Participant;
DROP TABLE IF EXISTS Course;
DROP TABLE IF EXISTS Utilisateur;


CREATE TABLE Utilisateur (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             nom VARCHAR(255) NOT NULL,
                             prenom VARCHAR(255) NOT NULL,
                             email VARCHAR(255) UNIQUE NOT NULL,
                             role VARCHAR(50) NOT NULL,
                             hashMotDePasse VARCHAR(255) NOT NULL,
                             photoProfile TEXT
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

CREATE TABLE Message (
                         id INT PRIMARY KEY AUTO_INCREMENT,
                         contenu TEXT NOT NULL,
                         auteur_id INT NOT NULL,
                         discussion_id INT NOT NULL,
                         dateEnvoi TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                         FOREIGN KEY (auteur_id) REFERENCES Utilisateur(id) ON DELETE CASCADE,
                         FOREIGN KEY (discussion_id) REFERENCES Discussion(id) ON DELETE CASCADE
);
