DROP TABLE if exists Utilisateur;
CREATE TABLE Utilisateur (
                             id INT PRIMARY KEY AUTO_INCREMENT,
                             nom VARCHAR(255) NOT NULL,
                             prenom VARCHAR(255) NOT NULL,
                             email VARCHAR(255) UNIQUE NOT NULL,
                             role VARCHAR(50) NOT NULL,
                             photoProfile TEXT
);