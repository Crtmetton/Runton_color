package com.colorrun.business;

import jdk.jshell.execution.Util;

import java.util.List;

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

    public void CreerCourse(Course course) {

    }

    public void ModifierCourse(Course course) {

    }
}
