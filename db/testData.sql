-- Insertion de données de test dans la table Utilisateur
INSERT INTO Utilisateur (nom, prenom, email, role, hashMotDePasse, photoProfile, enabled)
VALUES 
('Dupont', 'Jean', 'jean.dupont@example.com', 'USER', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'profile1.jpg', TRUE),
('Martin', 'Sophie', 'sophie.martin@example.com', 'USER', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'profile2.jpg', TRUE),
('Bernard', 'Pierre', 'pierre.bernard@example.com', 'ORGANIZER', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'profile3.jpg', TRUE),
('Admin', 'System', 'admin@runton.com', 'ADMIN', '$2a$10$hKDVYxLefVHV/vtuPhWD3OigtRyOykRLDdUAp80Z1crSoS1lFqaFS', 'admin.jpg', TRUE);

-- Insertion de données de test dans la table Course
INSERT INTO Course (nom, description, date, lieu, distance, maxParticipants, prix, cause)
VALUES 
('Color Run Paris', 'Course colorée à travers Paris', '2023-06-15 10:00:00', 'Paris', 5.0, 1000, 25, 'Association pour la recherche contre le cancer'),
('Color Run Lyon', 'Course colorée au cœur de Lyon', '2023-07-20 09:30:00', 'Lyon', 10.0, 500, 30, 'Fondation pour la protection de l''environnement'),
('Color Run Marseille', 'Course colorée sur la Canebière', '2023-08-05 08:00:00', 'Marseille', 7.5, 750, 20, 'Aide aux enfants défavorisés');

-- Insertion de données de test dans la table Participation
INSERT INTO Participation (utilisateurId, courseId, date, statut)
VALUES 
(1, 1, '2023-05-10 14:30:00', 'REGISTERED'),
(2, 1, '2023-05-12 09:15:00', 'REGISTERED'),
(1, 2, '2023-06-05 16:45:00', 'REGISTERED'),
(3, 3, '2023-07-01 11:20:00', 'REGISTERED');

-- Insertion de données de test dans la table DemandeOrganisateur
INSERT INTO DemandeOrganisateur (utilisateurId, date, motivations, statut)
VALUES 
(2, '2023-04-25 10:30:00', 'Je souhaite organiser des courses car j''ai une grande expérience dans l''événementiel sportif.', 'PENDING'),
(1, '2023-05-02 16:15:00', 'Passionné de course à pied, je souhaite partager ma passion en organisant des événements.', 'APPROVED');

-- Insertion de données de test dans la table Message
INSERT INTO Message (expediteurId, destinataireId, date, contenu, lu)
VALUES 
(1, 3, '2023-05-15 09:20:00', 'Bonjour, pouvez-vous me donner plus d''informations sur la Color Run Lyon ?', FALSE),
(3, 1, '2023-05-15 11:45:00', 'Bonjour, bien sûr ! La course se déroulera au centre-ville de Lyon, le départ est prévu Place Bellecour.', TRUE),
(2, 4, '2023-05-16 14:30:00', 'Bonjour, j''ai fait une demande pour devenir organisateur, pouvez-vous me dire où en est ma demande ?', FALSE),
(4, 2, '2023-05-16 15:10:00', 'Bonjour, votre demande est en cours d''examen, nous vous tiendrons informé rapidement.', TRUE);

-- Insertion de données de test dans les tables de compatibilité
INSERT INTO Participant (utilisateur_id)
VALUES (1), (2);

INSERT INTO Organisateur (utilisateur_id)
VALUES (3);

INSERT INTO Admin (utilisateur_id)
VALUES (4);

INSERT INTO Organisation (organisateur_id, course_id)
VALUES (1, 3);

INSERT INTO Discussion (course_id)
VALUES (1), (2), (3);

-- Note: Le mot de passe pour tous les utilisateurs est 'password' 