package org.example.medicale.entity;

import jakarta.persistence.*;
import org.example.medicale.enums.Role;
import org.example.medicale.enums.Specialite;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String motDePasse; // BCrypt hash

    @Column(nullable = false)
    private String nom;

    @Column(nullable = false)
    private String prenom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    // Champs spécifiques aux médecins
    @Enumerated(EnumType.STRING)
    private Specialite specialite;

    private Double tarif; // Tarif pour les spécialistes

    private Integer dureeConsultation = 30; // en minutes

    @Column(nullable = false)
    private Boolean actif = true;

    // Relations
    @OneToMany(mappedBy = "medecin", cascade = CascadeType.ALL)
    private List<Consultation> consultations = new ArrayList<>();

    @OneToMany(mappedBy = "specialiste", cascade = CascadeType.ALL)
    private List<DemandeExpertise> expertisesRecues = new ArrayList<>();

    @OneToMany(mappedBy = "medecinDemandeur", cascade = CascadeType.ALL)
    private List<DemandeExpertise> expertisesDemandees = new ArrayList<>();

    @OneToMany(mappedBy = "specialiste", cascade = CascadeType.ALL)
    private List<Creneau> creneaux = new ArrayList<>();

    // Constructeurs
    public User() {}

    public User(String email, String motDePasse, String nom,
                       String prenom, Role role) {
        this.email = email;
        this.motDePasse = motDePasse;
        this.nom = nom;
        this.prenom = prenom;
        this.role = role;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Specialite getSpecialite() {
        return specialite;
    }

    public void setSpecialite(Specialite specialite) {
        this.specialite = specialite;
    }

    public Double getTarif() {
        return tarif;
    }

    public void setTarif(Double tarif) {
        this.tarif = tarif;
    }

    public Integer getDureeConsultation() {
        return dureeConsultation;
    }

    public void setDureeConsultation(Integer dureeConsultation) {
        this.dureeConsultation = dureeConsultation;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public List<Consultation> getConsultations() {
        return consultations;
    }

    public void setConsultations(List<Consultation> consultations) {
        this.consultations = consultations;
    }

    public List<DemandeExpertise> getExpertisesRecues() {
        return expertisesRecues;
    }

    public void setExpertisesRecues(List<DemandeExpertise> expertisesRecues) {
        this.expertisesRecues = expertisesRecues;
    }

    public List<DemandeExpertise> getExpertisesDemandees() {
        return expertisesDemandees;
    }

    public void setExpertisesDemandees(List<DemandeExpertise> expertisesDemandees) {
        this.expertisesDemandees = expertisesDemandees;
    }

    public List<Creneau> getCreneaux() {
        return creneaux;
    }

    public void setCreneaux(List<Creneau> creneaux) {
        this.creneaux = creneaux;
    }

    // Méthodes utilitaires
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public boolean estSpecialiste() {
        return role == Role.SPECIALISTE && specialite != null;
    }

    public boolean estGeneraliste() {
        return role == Role.GENERALISTE;
    }

    public boolean estInfirmier() {
        return role == Role.INFIRMIER;
    }
}