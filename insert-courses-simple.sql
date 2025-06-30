-- Suppression des courses existantes  
DELETE FROM Course;

-- Insertion de courses de test simples
INSERT INTO Course (NOM, DESCRIPTION, DATE, LIEU, DISTANCE, MAXPARTICIPANTS, PRIX, CAUSE) VALUES ('Color Run Paris 2024', 'Une course colorée dans Paris', '2024-07-15 10:00:00', 'Paris', 5.0, 500, 25, 'Association Enfants');

INSERT INTO Course (NOM, DESCRIPTION, DATE, LIEU, DISTANCE, MAXPARTICIPANTS, PRIX, CAUSE) VALUES ('Rainbow Marathon Lyon', 'Le marathon le plus coloré de France', '2024-08-20 08:30:00', 'Lyon', 42.0, 200, 45, 'Lutte contre le Cancer');

INSERT INTO Course (NOM, DESCRIPTION, DATE, LIEU, DISTANCE, MAXPARTICIPANTS, PRIX, CAUSE) VALUES ('Mini Color Run Marseille', 'Une course parfaite pour toute la famille', '2024-06-30 09:00:00', 'Marseille', 3.0, 300, 15, 'Protection Environnement');

INSERT INTO Course (NOM, DESCRIPTION, DATE, LIEU, DISTANCE, MAXPARTICIPANTS, PRIX, CAUSE) VALUES ('Color Run Toulouse', 'Découvrez Toulouse sous un jour nouveau', '2024-09-10 10:30:00', 'Toulouse', 10.0, 400, 30, 'Education pour Tous');

INSERT INTO Course (NOM, DESCRIPTION, DATE, LIEU, DISTANCE, MAXPARTICIPANTS, PRIX, CAUSE) VALUES ('Ultra Color Challenge Nice', 'Pour les plus courageux sur la Côte Azur', '2024-07-25 07:00:00', 'Nice', 21.0, 150, 40, 'Recherche Medicale');

INSERT INTO Course (NOM, DESCRIPTION, DATE, LIEU, DISTANCE, MAXPARTICIPANTS, PRIX, CAUSE) VALUES ('Color Fun Run Bordeaux', 'Une course décontractée dans les vignobles', '2024-08-05 16:00:00', 'Bordeaux', 7.0, 350, 20, 'Aide Personnes Agees');

INSERT INTO Course (NOM, DESCRIPTION, DATE, LIEU, DISTANCE, MAXPARTICIPANTS, PRIX, CAUSE) VALUES ('Color Run Express Lille', 'Course express en centre-ville', '2024-06-25 18:30:00', 'Lille', 2.0, 250, 10, 'Sport pour Tous');

INSERT INTO Course (NOM, DESCRIPTION, DATE, LIEU, DISTANCE, MAXPARTICIPANTS, PRIX, CAUSE) VALUES ('Mega Color Run Nantes', 'La plus grande course colorée de l Ouest', '2024-09-15 09:30:00', 'Nantes', 15.0, 600, 35, 'Protection Animale');

INSERT INTO Course (NOM, DESCRIPTION, DATE, LIEU, DISTANCE, MAXPARTICIPANTS, PRIX, CAUSE) VALUES ('Color Walk Strasbourg', 'Une marche colorée à travers le centre historique', '2024-07-05 14:00:00', 'Strasbourg', 5.0, 400, 18, 'Patrimoine Culturel');

INSERT INTO Course (NOM, DESCRIPTION, DATE, LIEU, DISTANCE, MAXPARTICIPANTS, PRIX, CAUSE) VALUES ('Night Color Run Montpellier', 'Course nocturne sous les étoiles', '2024-08-12 21:00:00', 'Montpellier', 8.0, 300, 28, 'Innovation Sociale'); 