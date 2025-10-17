package org.example.medicale.enums;

public enum TypeActe {
    RADIOGRAPHIE("Radiographie", 200.0),
    ECHOGRAPHIE("Échographie", 300.0),
    IRM("IRM", 1500.0),
    ELECTROCARDIOGRAMME("Électrocardiogramme (ECG)", 150.0),
    LASER_DERMATOLOGIQUE("Laser dermatologique", 500.0),
    FOND_OEIL("Fond d'œil", 250.0),
    ANALYSE_SANG("Analyse de sang", 180.0),
    ANALYSE_URINE("Analyse d'urine", 120.0);

    private final String description;
    private final Double coutDefaut;

    TypeActe(String description, Double coutDefaut) {
        this.description = description;
        this.coutDefaut = coutDefaut;
    }

    public String getDescription() {
        return description;
    }

    public Double getCoutDefaut() {
        return coutDefaut;
    }
}
