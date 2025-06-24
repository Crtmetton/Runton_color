package com.colorrun.business;

public class Admin {
    private int id;
    private Utilisateur utilisateur;

    public Admin(int unId, Utilisateur unUtilisateur) {
        this.id = id;
        this.utilisateur = utilisateur;
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

    public boolean validerDemandeOrganisateur(Utilisateur unUtilisateur) {
        return true;
    }

    public void supprimerUtilisateur(Utilisateur unUtilisateur) {

    }


}
