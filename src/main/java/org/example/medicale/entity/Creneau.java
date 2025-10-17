package org.example.medicale.entity;

import jakarta.persistence.*;
import org.example.medicale.enums.StatutCreneau;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "creneaux")
public class Creneau {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialiste_id", nullable = false)
    private User specialiste;

    @Column(nullable = false)
    private LocalDateTime dateHeure;

    @Column(nullable = false)
    private Integer duree = 30; // en minutes

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatutCreneau statut = StatutCreneau.DISPONIBLE;

    @OneToOne(mappedBy = "creneau")
    private DemandeExpertise demandeExpertise;

    // Constructeurs
    public Creneau() {}

    public Creneau(User specialiste, LocalDateTime dateHeure) {
        this.specialiste = specialiste;
        this.dateHeure = dateHeure;
    }

    public Creneau(User specialiste, LocalDateTime dateHeure, Integer duree) {
        this.specialiste = specialiste;
        this.dateHeure = dateHeure;
        this.duree = duree;
    }

    // Getters et Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getSpecialiste() {
        return specialiste;
    }

    public void setSpecialiste(User specialiste) {
        this.specialiste = specialiste;
    }

    public LocalDateTime getDateHeure() {
        return dateHeure;
    }

    public void setDateHeure(LocalDateTime dateHeure) {
        this.dateHeure = dateHeure;
    }

    public Integer getDuree() {
        return duree;
    }

    public void setDuree(Integer duree) {
        this.duree = duree;
    }

    public StatutCreneau getStatut() {
        return statut;
    }

    public void setStatut(StatutCreneau statut) {
        this.statut = statut;
    }

    public DemandeExpertise getDemandeExpertise() {
        return demandeExpertise;
    }

    public void setDemandeExpertise(DemandeExpertise demandeExpertise) {
        this.demandeExpertise = demandeExpertise;
    }

    // Méthodes utilitaires
    public LocalDateTime getHeureDebut() {
        return dateHeure;
    }

    public LocalDateTime getHeureFin() {
        return dateHeure.plusMinutes(duree);
    }

    public String getHeureFormatee() {
        LocalTime debut = dateHeure.toLocalTime();
        LocalTime fin = debut.plusMinutes(duree);
        return String.format("%02d:%02d - %02d:%02d",
                debut.getHour(), debut.getMinute(),
                fin.getHour(), fin.getMinute());
    }

    public boolean estDisponible() {
        return statut == StatutCreneau.DISPONIBLE &&
                dateHeure.isAfter(LocalDateTime.now());
    }

    public boolean estPasse() {
        return dateHeure.isBefore(LocalDateTime.now());
    }

    public void reserver() {
        if (estDisponible()) {
            this.statut = StatutCreneau.RESERVE;
        }
    }

    public void liberer() {
        if (statut == StatutCreneau.RESERVE && !estPasse()) {
            this.statut = StatutCreneau.DISPONIBLE;
        }
    }

    public void marquerCommePasse() {
        if (estPasse() && statut != StatutCreneau.RESERVE) {
            this.statut = StatutCreneau.PASSE;
        }
    }
}
