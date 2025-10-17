package org.example.medicale.entity;


import jakarta.persistence.*;
import org.example.medicale.enums.StatutConsultation;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "consultations")
public class Consultation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_id", nullable = false)
    private User medecin;

    @Column(nullable = false)
    private LocalDateTime dateConsultation;

    @Column(columnDefinition = "TEXT")
    private String motif;

    @Column(columnDefinition = "TEXT")
    private String examenClinique;

    @Column(columnDefinition = "TEXT")
    private String symptomes;

    @Column(columnDefinition = "TEXT")
    private String observations;

    @Column(columnDefinition = "TEXT")
    private String diagnostic;

    @Column(columnDefinition = "TEXT")
    private String traitement;

    @Column(nullable = false)
    private Double coutConsultation = 150.0; // Fixe à 150 DH

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutConsultation statut = StatutConsultation.EN_COURS;

    @OneToMany(mappedBy = "consultation", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ActeTechnique> actesTechniques = new ArrayList<>();

    @OneToOne(mappedBy = "consultation", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DemandeExpertise demandeExpertise;


    // Constructeurs
    public Consultation() {
        this.dateConsultation = LocalDateTime.now();
    }

    public Consultation(Patient patient, User medecin, String motif) {
        this.patient = patient;
        this.medecin = medecin;
        this.motif = motif;
        this.dateConsultation = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public User getMedecin() {
        return medecin;
    }

    public void setMedecin(User medecin) {
        this.medecin = medecin;
    }

    public LocalDateTime getDateConsultation() {
        return dateConsultation;
    }

    public void setDateConsultation(LocalDateTime dateConsultation) {
        this.dateConsultation = dateConsultation;
    }

    public String getMotif() {
        return motif;
    }

    public void setMotif(String motif) {
        this.motif = motif;
    }

    public String getExamenClinique() {
        return examenClinique;
    }

    public void setExamenClinique(String examenClinique) {
        this.examenClinique = examenClinique;
    }

    public String getSymptomes() {
        return symptomes;
    }

    public void setSymptomes(String symptomes) {
        this.symptomes = symptomes;
    }

    public String getObservations() {
        return observations;
    }

    public void setObservations(String observations) {
        this.observations = observations;
    }

    public String getDiagnostic() {
        return diagnostic;
    }

    public void setDiagnostic(String diagnostic) {
        this.diagnostic = diagnostic;
    }

    public String getTraitement() {
        return traitement;
    }

    public void setTraitement(String traitement) {
        this.traitement = traitement;
    }

    public Double getCoutConsultation() {
        return coutConsultation;
    }

    public void setCoutConsultation(Double coutConsultation) {
        this.coutConsultation = coutConsultation;
    }

    public StatutConsultation getStatut() {
        return statut;
    }

    public void setStatut(StatutConsultation statut) {
        this.statut = statut;
    }

    public List<ActeTechnique> getActesTechniques() {
        return actesTechniques;
    }

    public void setActesTechniques(List<ActeTechnique> actesTechniques) {
        this.actesTechniques = actesTechniques;
    }

    public DemandeExpertise getDemandeExpertise() {
        return demandeExpertise;
    }

    public void setDemandeExpertise(DemandeExpertise demandeExpertise) {
        this.demandeExpertise = demandeExpertise;
    }

    // Méthodes utilitaires avec Stream API et Lambda
    public Double calculerCoutTotal() {
        Double coutActes = actesTechniques.stream()
                .map(ActeTechnique::getCout)
                .reduce(0.0, Double::sum);

        Double coutExpertise = (demandeExpertise != null &&
                demandeExpertise.getCreneau() != null)
                ? demandeExpertise.getCreneau().getSpecialiste().getTarif()
                : 0.0;

        return coutConsultation + coutActes + coutExpertise;
    }

    public void ajouterActeTechnique(ActeTechnique acte) {
        actesTechniques.add(acte);
        acte.setConsultation(this);
    }

    public boolean necessiteExpertise() {
        return statut == StatutConsultation.EN_ATTENTE_AVIS_SPECIALISTE;
    }

    public boolean estTerminee() {
        return statut == StatutConsultation.TERMINEE;
    }
}
