package org.example.medicale.enums;

public enum Specialite {
    CARDIOLOGY("Cardiologie"),
    PNEUMOLOGY("Pneumologie"),
    NEUROLOGY("Neurologie"),
    GASTROENTEROLOGY("Gastroentérologie"),
    ENDOCRINOLOGY("Endocrinologie"),
    DERMATOLOGY("Dermatologie"),
    RHEUMATOLOGY("Rhumatologie"),
    PSYCHIATRY("Psychiatrie"),
    NEPHROLOGY("Néphrologie"),
    ORTHOPEDICS("Orthopédie");

    private final String label;

    Specialite(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}
