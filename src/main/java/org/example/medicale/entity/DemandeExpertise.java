package org.example.medicale.entity;

import jakarta.persistence.*;
import org.example.medicale.enums.StatutExpertise;

import java.time.LocalDateTime;

@Entity
@Table(name = "demandes_expertise")
public class DemandeExpertise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "medecin_demandeur_id", nullable = false)
    private User medecinDemandeur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialiste_id", nullable = false)
    private User specialiste;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creneau_id", nullable = false)
    private Creneau creneau;

    @Column(nullable = false)
    private LocalDateTime dateDemande;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionPosee;

    @Column(columnDefinition = "TEXT")
    private String donneesAnalyses;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutExpertise statut = StatutExpertise.EN_ATTENTE;

    @Column(columnDefinition = "TEXT")
    private String avisMedical;

    @Column(columnDefinition = "TEXT")
    private String recommandations;

    private LocalDateTime dateReponse;

    // Constructeurs
    public DemandeExpertise() {
        this.dateDemande = LocalDateTime.now();
    }

    public DemandeExpertise(Consultation consultation, User medecinDemandeur,
                            User specialiste, Creneau creneau, String questionPosee) {
        this.consultation = consultation;
        this.medecinDemandeur = medecinDemandeur;
        this.specialiste = specialiste;
        this.creneau = creneau;
        this.questionPosee = questionPosee;
        this.dateDemande = LocalDateTime.now();
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Consultation getConsultation() {
        return consultation;
    }

    public void setConsultation(Consultation consultation) {
        this.consultation = consultation;
    }

    public User getMedecinDemandeur() {
        return medecinDemandeur;
    }

    public void setMedecinDemandeur(User medecinDemandeur) {
        this.medecinDemandeur = medecinDemandeur;
    }

    public User getSpecialiste() {
        return specialiste;
    }

    public void setSpecialiste(User specialiste) {
        this.specialiste = specialiste;
    }

    public Creneau getCreneau() {
        return creneau;
    }

    public void setCreneau(Creneau creneau) {
        this.creneau = creneau;
    }

    public LocalDateTime getDateDemande() {
        return dateDemande;
    }

    public void setDateDemande(LocalDateTime dateDemande) {
        this.dateDemande = dateDemande;
    }

    public String getQuestionPosee() {
        return questionPosee;
    }

    public void setQuestionPosee(String questionPosee) {
        this.questionPosee = questionPosee;
    }

    public String getDonneesAnalyses() {
        return donneesAnalyses;
    }

    public void setDonneesAnalyses(String donneesAnalyses) {
        this.donneesAnalyses = donneesAnalyses;
    }


    public StatutExpertise getStatut() {
        return statut;
    }

    public void setStatut(StatutExpertise statut) {
        this.statut = statut;
    }

    public String getAvisMedical() {
        return avisMedical;
    }

    public void setAvisMedical(String avisMedical) {
        this.avisMedical = avisMedical;
    }

    public String getRecommandations() {
        return recommandations;
    }

    public void setRecommandations(String recommandations) {
        this.recommandations = recommandations;
    }

    public LocalDateTime getDateReponse() {
        return dateReponse;
    }

    public void setDateReponse(LocalDateTime dateReponse) {
        this.dateReponse = dateReponse;
    }

    // Méthodes utilitaires
    public void repondre(String avisMedical, String recommandations) {
        this.avisMedical = avisMedical;
        this.recommandations = recommandations;
        this.statut = StatutExpertise.TERMINEE;
        this.dateReponse = LocalDateTime.now();
    }

    public boolean estEnAttente() {
        return statut == StatutExpertise.EN_ATTENTE;
    }

    public boolean estTerminee() {
        return statut == StatutExpertise.TERMINEE;
    }
}
