package com.colorrun.business;

import java.util.List;

/**
 * Représente un participant aux courses Color Run.
 *
 * Un participant est généralement un simple utilisateur inscrit à une ou
 * plusieurs courses. Ce modèle conserve la liste des {@link Course} auxquelles
 * il est inscrit.
 *
 * Remarque : cette classe est simplifiée ; la logique métier réelle (inscription
 * ou téléchargement de dossard) sera implémentée dans la couche service.
 *
 * @author Équipe Runton
 * @version 1.0
 */
public class Participant {
    private int id;
    private Participant participant;
    private List<Course> coursesInscrites;

    public Participant(int id, Participant participant, List<Course> DesCoursesInscrites) {
        this.id = id;
        this.participant = participant;
        this.coursesInscrites = DesCoursesInscrites;
    }

    public int getId() {
        return id;
    }

    public void setId(int unId) {
        this.id = unId;
    }

    public Participant getParticipant() {
        return participant;
    }

    public void setParticipant(Participant unParticipant) {
        this.participant = unParticipant;
    }

    public List<Course> getCoursesInscrites() {
        return coursesInscrites;
    }

    public void setCoursesInscrites(List<Course> desCoursesInscrites) {
        this.coursesInscrites = desCoursesInscrites;
    }

    /**
     * Inscrit le participant à la course indiquée.
     *
     * @param course course à laquelle s'inscrire
     * @return {@code true} si l'inscription est acceptée
     */
    public boolean Sinscrire(Course course) {
        return true;
    }

    /**
     * Télécharge le dossard associé à la course.
     * L'implémentation réelle sera gérée par le service.
     *
     * @param course course concernée
     */
    public void telechargerDossard(Course course) {

    }
}
