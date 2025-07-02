package com.colorrun.business;

import java.util.List;

/**
 * Représente un organisateur d'événements Color Run.
 *
 * Un organisateur est un utilisateur disposant du droit de créer et de gérer
 * des courses. Cette classe stocke la relation avec l'objet
 * {@link Utilisateur} ainsi que la liste des {@link Course} qu'il a créées.
 *
 * @author Équipe Runton
 * @version 1.0
 */
public class Organisateur {
    private int id;
    private Utilisateur utilisateur;
    private List<Course> coursesCreees;

    public Organisateur(int unId, Utilisateur unUtilisateur, List<Course> coursesCreees) {
        this.id = unId;
        this.utilisateur = unUtilisateur;
        this.coursesCreees = coursesCreees;
    }

    public int getId() {
        return id;
    }

    public void setId(int unId) {
        this.id = unId;
    }

    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur unUtilisateur) {
        this.utilisateur = unUtilisateur;
    }

    public List<Course> getCoursesCreees() {
        return coursesCreees;
    }

    public void setCoursesCreees(List<Course> coursesCreees) {
        this.coursesCreees = coursesCreees;
    }

    /**
     * Crée une nouvelle course et l'ajoute à la liste des courses créées.
     * L'implémentation réelle sera effectuée dans le service.
     *
     * @param course la course à ajouter
     */
    public void CreerCourse(Course course) {

    }

    /**
     * Modifie une course existante créée par l'organisateur.
     * L'implémentation réelle sera effectuée dans le service.
     *
     * @param course la course à modifier
     */
    public void ModifierCourse(Course course) {

    }
}
