-- Insertion de courses de test

-- Suppression des courses existantes
DELETE FROM Course;

-- Insertion de plusieurs courses avec différentes caractéristiques
INSERT INTO Course (NOM, DESCRIPTION, DATE, LIEU, DISTANCE, MAXPARTICIPANTS, PRIX, CAUSE) VALUES
('Color Run Paris 2024', 
 'Une course colorée exceptionnelle dans les rues de Paris ! Parcourez 5km à travers les plus beaux monuments de la capitale while getting splashed with non-toxic colored powders.', 
 '2024-07-15 10:00:00', 
 'Paris', 
 5.0, 
 500, 
 25, 
 'Association Sourire d\'Enfant'),

('Rainbow Marathon Lyon', 
 'Le marathon le plus coloré de France ! 42km de pure joie à travers Lyon, avec des stations de couleurs tous les 5km.', 
 '2024-08-20 08:30:00', 
 'Lyon', 
 42.0, 
 200, 
 45, 
 'Lutte contre le Cancer'),

('Mini Color Run Marseille', 
 'Une course parfaite pour toute la famille ! 3km de plaisir coloré sur le Vieux-Port de Marseille.', 
 '2024-06-30 09:00:00', 
 'Marseille', 
 3.0, 
 300, 
 15, 
 'Protection de l\'Environnement'),

('Color Run Toulouse', 
 'Découvrez Toulouse sous un jour nouveau avec cette course de 10km à travers la ville rose qui deviendra multicolore !', 
 '2024-09-10 10:30:00', 
 'Toulouse', 
 10.0, 
 400, 
 30, 
 'Éducation pour Tous'),

('Ultra Color Challenge Nice', 
 'Pour les plus courageux ! 21km le long de la Côte d\'Azur avec des vues imprenables et des couleurs éclatantes.', 
 '2024-07-25 07:00:00', 
 'Nice', 
 21.0, 
 150, 
 40, 
 'Recherche Médicale'),

('Color Fun Run Bordeaux', 
 'Une course décontractée de 7km à travers les vignobles bordelais. Courez, marchez, dansez !', 
 '2024-08-05 16:00:00', 
 'Bordeaux', 
 7.0, 
 350, 
 20, 
 'Aide aux Personnes Âgées'),

('Color Run Express Lille', 
 'Course express de 2km en centre-ville de Lille. Parfait pour les débutants et les familles !', 
 '2024-06-25 18:30:00', 
 'Lille', 
 2.0, 
 250, 
 10, 
 'Sport pour Tous'),

('Mega Color Run Nantes', 
 'La plus grande course colorée de l\'Ouest ! 15km à travers Nantes avec des animations spectaculaires.', 
 '2024-09-15 09:30:00', 
 'Nantes', 
 15.0, 
 600, 
 35, 
 'Protection Animale'),

('Color Walk Strasbourg', 
 'Une marche colorée de 5km à travers le centre historique de Strasbourg. Accessible à tous !', 
 '2024-07-05 14:00:00', 
 'Strasbourg', 
 5.0, 
 400, 
 18, 
 'Patrimoine Culturel'),

('Night Color Run Montpellier', 
 'Course nocturne unique ! 8km sous les étoiles avec des couleurs phosphorescentes. Expérience magique garantie !', 
 '2024-08-12 21:00:00', 
 'Montpellier', 
 8.0, 
 300, 
 28, 
 'Innovation Sociale');

-- Vérification des données insérées
SELECT ID, NOM, LIEU, DISTANCE, DATE, MAXPARTICIPANTS FROM Course ORDER BY DATE; 