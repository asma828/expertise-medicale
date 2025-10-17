package org.example.medicale.entity;

import jakarta.persistence.*;
import org.example.medicale.enums.TypeActe;

@Entity
@Table(name = "actes_techniques")
public class ActeTechnique {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "consultation_id", nullable = false)
    private Consultation consultation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeActe typeActe;

    @Column(nullable = false)
    private Double cout;

    @Column(columnDefinition = "TEXT")
    private String description;

    // Constructeurs
    public ActeTechnique() {}

    public ActeTechnique(TypeActe typeActe) {
        this.typeActe = typeActe;
        this.cout = typeActe.getCoutDefaut();
        this.description = typeActe.getDescription();
    }

    public ActeTechnique(Consultation consultation, TypeActe typeActe) {
        this.consultation = consultation;
        this.typeActe = typeActe;
        this.cout = typeActe.getCoutDefaut();
        this.description = typeActe.getDescription();
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

    public TypeActe getTypeActe() {
        return typeActe;
    }

    public void setTypeActe(TypeActe typeActe) {
        this.typeActe = typeActe;
    }

    public Double getCout() {
        return cout;
    }

    public void setCout(Double cout) {
        this.cout = cout;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
