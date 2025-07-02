-- Script de mise à jour pour ajouter le support des messages de course
-- Ajouter le champ courseId à la table Message

-- Ajouter la colonne courseId (nullable pour les messages privés existants)
ALTER TABLE Message ADD COLUMN courseId INT NULL;

-- Ajouter la contrainte de clé étrangère
ALTER TABLE Message ADD CONSTRAINT FK_Message_Course 
    FOREIGN KEY (courseId) REFERENCES Course(id) ON DELETE CASCADE;

-- Les messages avec courseId sont des messages publics de course
-- Les messages sans courseId restent des messages privés entre utilisateurs

-- Pour les messages de course, destinataireId peut être NULL (message public)
-- Pour les messages privés, courseId est NULL et destinataireId est requis 