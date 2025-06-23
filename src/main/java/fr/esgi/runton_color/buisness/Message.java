package fr.esgi.runton_color.buiness;

import java.time.LocalDate;

public class Message {
    private int id;
    private String conteneu;
    private Utilisateur utilisateur;
    private LocalDate dateEnvoie;

    public Message(int unId, String unContenu, Utilisateur unUtilisateur, LocalDate uneDateEnvoie) {
        this.id = unId;
        this.conteneu = unContenu;
        this.utilisateur = unUtilisateur;
        this.dateEnvoie = uneDateEnvoie;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int unId) {
        this.id = unId;
    }

    public String getConteneu() {
        return this.conteneu;
    }

    public void setConteneu(String unContenu) {
        this.conteneu = unContenu;
    }


    public Utilisateur getUtilisateur() {
        return this.utilisateur;
    }

    public void setUtilisateur(Utilisateur unUtilisateur) {
        this.utilisateur = unUtilisateur;
    }

    public LocalDate getDateEnvoie() {
        return this.dateEnvoie;
    }

    public void setDateEnvoie(LocalDate uneDateEnvoie) {
        this.dateEnvoie = uneDateEnvoie;
    }
}
