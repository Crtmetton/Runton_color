package com.colorrun.service;

import com.colorrun.business.Course;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Interface de service pour la gestion des courses Color Run.
 * 
 * Cette interface définit les opérations métier disponibles pour la gestion
 * des courses dans l'application. Elle fait le lien entre la couche présentation
 * (servlets) et la couche d'accès aux données (DAO), en ajoutant la logique
 * métier nécessaire.
 * 
 * <p><strong>Responsabilités du service :</strong></p>
 * <ul>
 *   <li>Validation des règles métier lors de la création/modification</li>
 *   <li>Gestion des transitions d'état des courses</li>
 *   <li>Contrôle des capacités et des inscriptions</li>
 *   <li>Calculs de statistiques et d'analyses</li>
 *   <li>Coordination avec d'autres services (participants, notifications)</li>
 * </ul>
 * 
 * <p><strong>Règles métier importantes :</strong></p>
 * <ul>
 *   <li>Une course ne peut être modifiée que par son organisateur</li>
 *   <li>Les inscriptions se ferment automatiquement à la date limite</li>
 *   <li>Une course complète ne peut plus accepter d'inscriptions</li>
 *   <li>Les courses passées ne peuvent plus être modifiées</li>
 *   <li>Seuls les administrateurs peuvent annuler une course</li>
 * </ul>
 * 
 * <p><strong>États de course supportés :</strong></p>
 * <ul>
 *   <li><code>PLANNED</code> : Course planifiée, inscriptions pas encore ouvertes</li>
 *   <li><code>OPEN</code> : Inscriptions ouvertes</li>
 *   <li><code>FULL</code> : Course complète, liste d'attente possible</li>
 *   <li><code>CLOSED</code> : Inscriptions fermées, en attente de l'événement</li>
 *   <li><code>COMPLETED</code> : Course terminée</li>
 *   <li><code>CANCELLED</code> : Course annulée</li>
 * </ul>
 * 
 * @author Équipe Color Run
 * @version 1.0
 * @since 1.0
 * 
 * @see Course Pour le modèle métier des courses
 * @see com.colorrun.dao.CourseDAO Pour la persistance des données
 * @see com.colorrun.service.impl.CourseServiceImpl Pour l'implémentation
 */
public interface CourseService {
    
    /**
     * Crée une nouvelle course avec validation des règles métier.
     * 
     * <p>Validations effectuées :</p>
     * <ul>
     *   <li>Vérification des champs obligatoires</li>
     *   <li>Validation de la cohérence des dates</li>
     *   <li>Contrôle de la capacité maximale</li>
     *   <li>Vérification des droits de l'organisateur</li>
     * </ul>
     * 
     * @param course La course à créer (sans ID)
     * @return La course créée avec son ID généré
     * @throws IllegalArgumentException Si les données de la course sont invalides
     * @throws RuntimeException En cas d'erreur lors de la création
     * 
     * @example
     * Course newCourse = new Course("Color Run Paris", "Une course colorée dans Paris");
     * newCourse.setDate(LocalDateTime.now().plusMonths(2));
     * newCourse.setMaxParticipants(500);
     * Course created = courseService.createCourse(newCourse);
     */
    void createCourse(Course course) throws SQLException;
    
    /**
     * Récupère une course par son identifiant.
     * 
     * @param id L'identifiant de la course
     * @return La course correspondante, ou null si non trouvée
     * @throws IllegalArgumentException Si l'ID est invalide
     */
    Course getCourseById(int id) throws SQLException;
    
    /**
     * Récupère toutes les courses disponibles.
     * 
     * @return Liste de toutes les courses, triées par date
     */
    List<Course> getAllCourses() throws SQLException;
    
    /**
     * Recherche des courses par nom (recherche partielle).
     * 
     * @param name Le nom ou partie du nom à rechercher (insensible à la casse)
     * @return Liste des courses correspondantes
     * @throws IllegalArgumentException Si le nom est null ou vide
     */
    List<Course> findCoursesByName(String name);
    
    /**
     * Recherche des courses par lieu (recherche partielle).
     * 
     * @param location Le lieu ou partie du lieu à rechercher
     * @return Liste des courses correspondantes
     * @throws IllegalArgumentException Si le lieu est null ou vide
     */
    List<Course> findCoursesByLocation(String location);
    
    /**
     * Recherche des courses dans une plage de dates.
     * 
     * @param startDate Date de début de la recherche
     * @param endDate Date de fin de la recherche
     * @return Liste des courses dans la période spécifiée
     * @throws IllegalArgumentException Si les dates sont invalides ou incohérentes
     */
    List<Course> findCoursesByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Récupère les courses organisées par un utilisateur spécifique.
     * 
     * @param organizerId L'identifiant de l'organisateur
     * @return Liste des courses de cet organisateur
     * @throws IllegalArgumentException Si l'ID organisateur est invalide
     */
    List<Course> getCoursesByOrganizer(int organizerId);
    
    /**
     * Récupère les courses ouvertes aux inscriptions.
     * 
     * <p>Critères pour qu'une course soit ouverte :</p>
     * <ul>
     *   <li>Statut : OPEN</li>
     *   <li>Date limite d'inscription non dépassée</li>
     *   <li>Capacité maximale non atteinte</li>
     * </ul>
     * 
     * @return Liste des courses ouvertes aux inscriptions
     */
    List<Course> getOpenCourses();
    
    /**
     * Récupère les prochaines courses (futures) avec une limite.
     * 
     * @param limit Nombre maximum de courses à retourner
     * @return Liste des prochaines courses
     * @throws IllegalArgumentException Si la limite est négative
     */
    List<Course> getUpcomingCourses(int limit);
    
    /**
     * Supprime une course avec vérifications de sécurité.
     * 
     * <p>Conditions pour la suppression :</p>
     * <ul>
     *   <li>L'utilisateur doit être administrateur ou organisateur de la course</li>
     *   <li>La course ne doit pas avoir de participants inscrits (sauf si forcée)</li>
     *   <li>La course ne doit pas être déjà terminée</li>
     * </ul>
     * 
     * @param courseId L'identifiant de la course à supprimer
     * @param force true pour forcer la suppression même avec des participants
     * @return true si la suppression a réussi
     * @throws IllegalArgumentException Si l'ID est invalide
     * @throws SecurityException Si l'utilisateur n'a pas les droits
     * @throws IllegalStateException Si la course ne peut pas être supprimée
     */
    boolean deleteCourse(int courseId, boolean force);
    
    /**
     * Change le statut d'une course avec validation des transitions.
     * 
     * <p>Transitions autorisées :</p>
     * <ul>
     *   <li>PLANNED → OPEN : Ouverture des inscriptions</li>
     *   <li>OPEN → FULL : Course complète</li>
     *   <li>OPEN → CLOSED : Fermeture anticipée des inscriptions</li>
     *   <li>FULL → OPEN : Désistements, places disponibles</li>
     *   <li>CLOSED → COMPLETED : Course terminée</li>
     *   <li>* → CANCELLED : Annulation (admin uniquement)</li>
     * </ul>
     * 
     * @param courseId L'identifiant de la course
     * @param newStatus Le nouveau statut souhaité
     * @return true si le changement a réussi
     * @throws IllegalArgumentException Si l'ID ou le statut est invalide
     * @throws IllegalStateException Si la transition n'est pas autorisée
     */
    boolean changeCourseStatus(int courseId, String newStatus);
    
    /**
     * Vérifie si une course a encore des places disponibles.
     * 
     * @param courseId L'identifiant de la course
     * @return true s'il reste des places, false sinon
     * @throws IllegalArgumentException Si l'ID est invalide
     */
    boolean hasAvailableSpots(int courseId);
    
    /**
     * Calcule le nombre de places restantes pour une course.
     * 
     * @param courseId L'identifiant de la course
     * @return Le nombre de places restantes (0 si complète)
     * @throws IllegalArgumentException Si l'ID est invalide
     */
    int getAvailableSpots(int courseId);
    
    /**
     * Calcule le taux de remplissage d'une course en pourcentage.
     * 
     * @param courseId L'identifiant de la course
     * @return Le taux de remplissage (0-100%)
     * @throws IllegalArgumentException Si l'ID est invalide
     */
    double getFillPercentage(int courseId);
    
    /**
     * Compte le nombre total de courses.
     * 
     * @return Le nombre total de courses en base
     */
    long getTotalCourseCount();
    
    /**
     * Compte le nombre de courses par statut.
     * 
     * @param status Le statut à compter
     * @return Le nombre de courses avec ce statut
     * @throws IllegalArgumentException Si le statut est invalide
     */
    long getCourseCountByStatus(String status);
    
    /**
     * Vérifie si un utilisateur peut modifier une course.
     * 
     * <p>Conditions pour pouvoir modifier :</p>
     * <ul>
     *   <li>Être l'organisateur de la course</li>
     *   <li>Ou être administrateur</li>
     *   <li>Et la course ne doit pas être terminée ou annulée</li>
     * </ul>
     * 
     * @param courseId L'identifiant de la course
     * @param userId L'identifiant de l'utilisateur
     * @return true si l'utilisateur peut modifier la course
     * @throws IllegalArgumentException Si les IDs sont invalides
     */
    boolean canUserModifyCourse(int courseId, int userId);
    
    /**
     * Valide les données d'une course selon les règles métier.
     * 
     * <p>Validations effectuées :</p>
     * <ul>
     *   <li>Nom non vide et longueur raisonnable</li>
     *   <li>Date future et cohérente avec la date limite d'inscription</li>
     *   <li>Capacité maximale positive et raisonnable</li>
     *   <li>Prix positif ou nul</li>
     *   <li>Distance positive</li>
     * </ul>
     * 
     * @param course La course à valider
     * @throws IllegalArgumentException Si les données sont invalides, avec détail de l'erreur
     */
    void validateCourseData(Course course);
    
    // Méthodes de compatibilité avec les servlets existants
    void create(Course course) throws SQLException;
    
    Optional<Course> findById(int id) throws SQLException;
    
    List<Course> findAll() throws SQLException;
    
    List<Course> findFiltered(String date, String city, String distance, String sort) throws SQLException;
    
    void update(Course course) throws SQLException;
    
    void delete(int id) throws SQLException;
    
    /**
     * Recherche des courses par texte libre (nom, description, lieu).
     * 
     * @param searchTerm Le terme de recherche
     * @return Liste des courses correspondantes
     * @throws SQLException En cas d'erreur lors de la recherche
     */
    List<Course> searchCourses(String searchTerm) throws SQLException;
    
    /**
     * Récupère toutes les villes où des courses ont lieu.
     * 
     * @return Liste des villes uniques
     * @throws SQLException En cas d'erreur lors de la récupération
     */
    List<String> getAllCities() throws SQLException;
    
    /**
     * Récupère toutes les distances de courses disponibles.
     * 
     * @return Liste des distances uniques
     * @throws SQLException En cas d'erreur lors de la récupération
     */
    List<Double> getAllDistances() throws SQLException;
} 