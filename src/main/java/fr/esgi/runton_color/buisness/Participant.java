package fr.esgi.runton_color.buiness;

import java.util.List;

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

    public boolean Sinscrire(Course course) {
        return true;
    }

    public void telechargerDossard(Course course) {

    }
}
