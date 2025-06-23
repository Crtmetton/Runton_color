package fr.esgi.runton_color.buiness;

import java.time.LocalDateTime;

public class Course {

    private int id;
    private String name;
    private String description;
    private LocalDateTime date;
    private String lieu;
    private Double distance;
    private int MaxParticipants;
    private String cause;

    public Course(int unId, String unNom, String uneDescription, LocalDateTime uneDate, String unLieu, Double uneDistance, int unMaxParticipants, String unCause) {
        this.id = unId;
        this.name = unNom;
        this.description = uneDescription;
        this.date = uneDate;
        this.lieu = unLieu;
        this.distance = uneDistance;
        this.MaxParticipants = unMaxParticipants;
        this.cause = unCause;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int unId) {
        this.id = unId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String unNom) {
         this.name = unNom;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String uneDescription) {
        this.description = uneDescription;
    }

    public LocalDateTime getDate() {
        return this.date;
    }

    public void setDate(LocalDateTime uneDate) {
        this.date = uneDate;
    }

    public String getLieu() {
        return this.lieu;
    }

    public void setLieu(String uneLieu) {
        this.lieu = uneLieu;
    }

    public Double getDistance() {
        return this.distance;
    }

    public void setDistance(Double uneDistance) {
        this.distance = uneDistance;
    }

    public int getMaxParticipants() {
        return this.MaxParticipants;
    }

    public void setMaxParticipants(int unMaxParticipants) {
        this.MaxParticipants = unMaxParticipants;
    }

    public String getCause() {
        return this.cause;
    }

    public void setCause(String unCause) {
        this.cause = unCause;
    }

    public void ajouterParticipant(Participant unUtilisateur) {

    }

    public void modifierProfile(Participant unUtilisateur) {

    }
}
