package org.example.medicale.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.medicale.entity.SigneVital;
import org.example.medicale.entity.Patient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class SigneVitalDAO {

    private EntityManagerFactory emf;

    public SigneVitalDAO() {
        this.emf = Persistence.createEntityManagerFactory("medicale");
    }

    public SigneVitalDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // Créer ou mettre à jour un signe vital
    public SigneVital save(SigneVital signeVital) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (signeVital.getId() == null) {
                em.persist(signeVital);
            } else {
                signeVital = em.merge(signeVital);
            }
            em.getTransaction().commit();
            return signeVital;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la sauvegarde des signes vitaux", e);
        } finally {
            em.close();
        }
    }

//

    public Optional<SigneVital> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            SigneVital signeVital = em.find(SigneVital.class, id);
            return Optional.ofNullable(signeVital);
        } finally {
            em.close();
        }
    }

    // Trouver tous les signes vitaux d'un patient
    public List<SigneVital> findByPatient(Patient patient) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<SigneVital> query = em.createQuery(
                    "SELECT sv FROM SigneVital sv WHERE sv.patient = :patient ORDER BY sv.dateMesure DESC",
                    SigneVital.class
            );
            query.setParameter("patient", patient);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<SigneVital> findByPatientId(Long patientId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<SigneVital> query = em.createQuery(
                    "SELECT sv FROM SigneVital sv WHERE sv.patient.id = :patientId ORDER BY sv.dateMesure DESC",
                    SigneVital.class
            );
            query.setParameter("patientId", patientId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public Optional<SigneVital> findLastByPatientId(Long patientId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<SigneVital> query = em.createQuery(
                    "SELECT sv FROM SigneVital sv WHERE sv.patient.id = :patientId ORDER BY sv.dateMesure DESC",
                    SigneVital.class
            );
            query.setParameter("patientId", patientId);
            query.setMaxResults(1);
            List<SigneVital> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    public List<SigneVital> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<SigneVital> query = em.createQuery(
                    "SELECT sv FROM SigneVital sv ORDER BY sv.dateMesure DESC",
                    SigneVital.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public List<SigneVital> findByDate(LocalDate date) {
        EntityManager em = emf.createEntityManager();
        try {
            LocalDateTime debut = date.atStartOfDay();
            LocalDateTime fin = debut.plusDays(1);

            TypedQuery<SigneVital> query = em.createQuery(
                    "SELECT sv FROM SigneVital sv WHERE sv.dateMesure BETWEEN :debut AND :fin ORDER BY sv.dateMesure DESC",
                    SigneVital.class
            );
            query.setParameter("debut", debut);
            query.setParameter("fin", fin);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            SigneVital signeVital = em.find(SigneVital.class, id);
            if (signeVital != null) {
                em.remove(signeVital);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression des signes vitaux", e);
        } finally {
            em.close();
        }
    }

public void close() {
    if (emf != null && emf.isOpen()) {
        emf.close();
    }
}

}