package org.example.medicale.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "signes_vitaux")
public class SigneVital {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id", nullable = false)
    private Patient patient;

    @Column(nullable = false)
    private LocalDateTime dateMesure;

    // Tension artérielle (ex: 120/80)
    private Integer tensionSystolique;
    private Integer tensionDiastolique;

    // Fréquence cardiaque (battements par minute)
    private Integer frequenceCardiaque;

    // Température corporelle (°C)
    private Double temperature;

    // Fréquence respiratoire (respirations par minute)
    private Integer frequenceRespiratoire;

    // Poids (kg)
    private Double poids;

    // Taille (cm)
    private Double taille;

    // Saturation en oxygène (%)
    private Integer saturationOxygene;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "infirmier_id")
    private User infirmier;

    // Constructeurs
    public SigneVital() {
        this.dateMesure = LocalDateTime.now();
    }

    public SigneVital(Patient patient) {
        this.patient = patient;
        this.dateMesure = LocalDateTime.now();
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

    public LocalDateTime getDateMesure() {
        return dateMesure;
    }

    public void setDateMesure(LocalDateTime dateMesure) {
        this.dateMesure = dateMesure;
    }

    public Integer getTensionSystolique() {
        return tensionSystolique;
    }

    public void setTensionSystolique(Integer tensionSystolique) {
        this.tensionSystolique = tensionSystolique;
    }

    public Integer getTensionDiastolique() {
        return tensionDiastolique;
    }

    public void setTensionDiastolique(Integer tensionDiastolique) {
        this.tensionDiastolique = tensionDiastolique;
    }

    public Integer getFrequenceCardiaque() {
        return frequenceCardiaque;
    }

    public void setFrequenceCardiaque(Integer frequenceCardiaque) {
        this.frequenceCardiaque = frequenceCardiaque;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getFrequenceRespiratoire() {
        return frequenceRespiratoire;
    }

    public void setFrequenceRespiratoire(Integer frequenceRespiratoire) {
        this.frequenceRespiratoire = frequenceRespiratoire;
    }

    public Double getPoids() {
        return poids;
    }

    public void setPoids(Double poids) {
        this.poids = poids;
    }

    public Double getTaille() {
        return taille;
    }

    public void setTaille(Double taille) {
        this.taille = taille;
    }

    public Integer getSaturationOxygene() {
        return saturationOxygene;
    }

    public void setSaturationOxygene(Integer saturationOxygene) {
        this.saturationOxygene = saturationOxygene;
    }

    public User getInfirmier() {
        return infirmier;
    }

    public void setInfirmier(User infirmier) {
        this.infirmier = infirmier;
    }

    // Méthodes utilitaires
    public String getTensionFormatee() {
        if (tensionSystolique != null && tensionDiastolique != null) {
            return tensionSystolique + "/" + tensionDiastolique;
        }
        return "N/A";
    }

    public Double getIMC() {
        if (poids != null && taille != null && taille > 0) {
            double tailleEnMetres = taille / 100.0;
            return poids / (tailleEnMetres * tailleEnMetres);
        }
        return null;
    }
}