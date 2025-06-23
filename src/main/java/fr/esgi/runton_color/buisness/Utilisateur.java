package fr.esgi.runton_color.buiness;

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

    public boolean seConnecter(String email, String motDePasse){
        if (this.email == email){
            return true;
        }
        else{
            return false;
        }
    }

    public void ModifierProfile(String nom, String prenom, String email, String role, String photoProfile){
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
        this.role = role;
        this.photoProfile = photoProfile;
    }
}
