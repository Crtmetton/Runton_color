package com.colorrun.business;

/**
 * Modèle simplifié d'utilisateur utilisé par certaines classes historiques du
 * projet. Il sera progressivement remplacé par {@link User}, plus complet.
 *
 * Cette classe stocke les informations basiques d'un utilisateur ainsi que
 * quelques opérations rudimentaires telles que la connexion ou la mise à jour
 * de profil. Aucune logique de sécurité réelle n'est implémentée ici ; ces
 * aspects sont gérés dans les couches service et sécurité.
 *
 * @author Équipe Runton
 * @version 1.0
 */
public class Utilisateur {
    private int id;
    private String nom;
    private String prenom;
    private String email;
    private String role;
    private String photoProfile;

    public Utilisateur(int unId, String unNom, String unPrenom, String unEmail, String unRole, String unPhotoProfile) {
        this.id = unId;
        this.nom = unNom;
        this.prenom = unPrenom;
        this.email = unEmail;
        this.role = unRole;
        this.photoProfile = unPhotoProfile;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getPhotoProfile() {
        return photoProfile;
    }

    public void setPhotoProfile(String photoProfile) {
        this.photoProfile = photoProfile;
    }

    /**
     * Tente de connecter l'utilisateur à l'aide de l'email et du mot de passe
     * fournis.
     *
     * <strong>Attention :</strong> cette implémentation est purement illustrative
     * et ne réalise <em>aucune</em> vérification du mot de passe. La vraie
     * authentification est assurée par la couche de sécurité.
     *
     * @param email       email saisi
     * @param motDePasse  mot de passe saisi
     * @return {@code true} si l'email correspond, {@code false} sinon
     */
    public boolean seConnecter(String email, String motDePasse){
        if (this.email == email){
            return true;
        }
        else{
            return false;
        }
    }

    /**
     * Met à jour les informations du profil utilisateur.
     *
     * @param nom           nouveau nom
     * @param prenom        nouveau prénom
     * @param email         nouvel email
     * @param role          nouveau rôle
     * @param photoProfile  nouvelle photo de profil
     */
    public void ModifierProfile(String nom, String prenom, String email, String role, String photoProfile){
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.photoProfile = photoProfile;
    }
}
