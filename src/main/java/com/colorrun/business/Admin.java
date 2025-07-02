package com.colorrun.business;

/**
 * Représente un administrateur de la plateforme Runton Color.
 *
 * Un administrateur possède des droits élevés : il peut notamment valider les
 * demandes pour devenir organisateur, ou supprimer des comptes utilisateurs.
 * Cette classe fait simplement le lien entre l'entité « Admin » et
 * l'objet {@link Utilisateur} correspondant.
 *
 * @author Équipe Runton
 * @version 1.0
 */
public class Admin {
    private int id;
    private Utilisateur utilisateur;

    /**
     * Construit un administrateur.
     *
     * @param unId          identifiant unique de l'administrateur
     * @param unUtilisateur utilisateur rattaché à cet administrateur
     */
    public Admin(int unId, Utilisateur unUtilisateur) {
        this.id = unId;
        this.utilisateur = unUtilisateur;
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

    /**
     * Valide la demande de passage au rôle organisateur pour l'utilisateur
     * fourni.
     *
     * @param unUtilisateur utilisateur dont on souhaite valider la demande
     * @return {@code true} si la validation est effectuée avec succès
     */
    public boolean validerDemandeOrganisateur(Utilisateur unUtilisateur) {
        return true;
    }

    /**
     * Supprime définitivement un utilisateur de la plateforme.
     *
     * @param unUtilisateur utilisateur à supprimer
     */
    public void supprimerUtilisateur(Utilisateur unUtilisateur) {

    }


}
