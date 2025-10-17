package org.example.medicale.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.medicale.entity.Consultation;
import org.example.medicale.enums.StatutConsultation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class ConsultationDAO {

    private EntityManagerFactory emf;

    public ConsultationDAO() {
        this.emf = Persistence.createEntityManagerFactory("medicale");
    }

    public ConsultationDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // Créer ou mettre à jour une consultation
    public Consultation save(Consultation consultation) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (consultation.getId() == null) {
                em.persist(consultation);
            } else {
                consultation = em.merge(consultation);
            }
            em.getTransaction().commit();
            return consultation;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la sauvegarde de la consultation", e);
        } finally {
            em.close();
        }
    }

    // Trouver une consultation par ID avec ses relations
    public Optional<Consultation> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Consultation> query = em.createQuery(
                    "SELECT c FROM Consultation c " +
                            "LEFT JOIN FETCH c.patient " +
                            "LEFT JOIN FETCH c.medecin " +
                            "LEFT JOIN FETCH c.actesTechniques " +
                            "LEFT JOIN FETCH c.demandeExpertise " +
                            "WHERE c.id = :id",
                    Consultation.class
            );
            query.setParameter("id", id);
            List<Consultation> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    public Optional<Consultation> findByIdWithFetch(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Consultation> query = em.createQuery(
                    "SELECT c FROM Consultation c " +
                            "LEFT JOIN FETCH c.patient " +
                            "LEFT JOIN FETCH c.medecin " +
                            "LEFT JOIN FETCH c.actesTechniques " +
                            "LEFT JOIN FETCH c.demandeExpertise de " +
                            "LEFT JOIN FETCH de.creneau cr " +
                            "LEFT JOIN FETCH cr.specialiste s " +
                            "WHERE c.id = :id",
                    Consultation.class
            );
            query.setParameter("id", id);
            List<Consultation> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }



    // Trouver toutes les consultations
    public List<Consultation> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Consultation> query = em.createQuery(
                    "SELECT c FROM Consultation c ORDER BY c.dateConsultation DESC",
                    Consultation.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les consultations d'un médecin
    public List<Consultation> findByMedecin(Long medecinId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Consultation> query = em.createQuery(
                    "SELECT c FROM Consultation c " +
                            "LEFT JOIN FETCH c.patient " +
                            "WHERE c.medecin.id = :medecinId " +
                            "ORDER BY c.dateConsultation DESC",
                    Consultation.class
            );
            query.setParameter("medecinId", medecinId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les consultations d'un médecin pour une date donnée
    public List<Consultation> findByMedecinAndDate(Long medecinId, LocalDate date) {
        EntityManager em = emf.createEntityManager();
        try {
            LocalDateTime debut = date.atStartOfDay();
            LocalDateTime fin = debut.plusDays(1);

            TypedQuery<Consultation> query = em.createQuery(
                    "SELECT c FROM Consultation c " +
                            "LEFT JOIN FETCH c.patient " +
                            "WHERE c.medecin.id = :medecinId " +
                            "AND c.dateConsultation BETWEEN :debut AND :fin " +
                            "ORDER BY c.dateConsultation DESC",
                    Consultation.class
            );
            query.setParameter("medecinId", medecinId);
            query.setParameter("debut", debut);
            query.setParameter("fin", fin);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les consultations d'un patient
    public List<Consultation> findByPatient(Long patientId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Consultation> query = em.createQuery(
                    "SELECT c FROM Consultation c " +
                            "LEFT JOIN FETCH c.medecin " +
                            "WHERE c.patient.id = :patientId " +
                            "ORDER BY c.dateConsultation DESC",
                    Consultation.class
            );
            query.setParameter("patientId", patientId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les consultations par statut
    public List<Consultation> findByStatut(StatutConsultation statut) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Consultation> query = em.createQuery(
                    "SELECT c FROM Consultation c " +
                            "LEFT JOIN FETCH c.patient " +
                            "LEFT JOIN FETCH c.medecin " +
                            "WHERE c.statut = :statut " +
                            "ORDER BY c.dateConsultation DESC",
                    Consultation.class
            );
            query.setParameter("statut", statut);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les consultations d'un médecin par statut
    public List<Consultation> findByMedecinAndStatut(Long medecinId, StatutConsultation statut) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Consultation> query = em.createQuery(
                    "SELECT c FROM Consultation c " +
                            "LEFT JOIN FETCH c.patient " +
                            "WHERE c.medecin.id = :medecinId " +
                            "AND c.statut = :statut " +
                            "ORDER BY c.dateConsultation DESC",
                    Consultation.class
            );
            query.setParameter("medecinId", medecinId);
            query.setParameter("statut", statut);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Mettre à jour le statut d'une consultation
    public void updateStatut(Long consultationId, StatutConsultation statut) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Consultation consultation = em.find(Consultation.class, consultationId);
            if (consultation != null) {
                consultation.setStatut(statut);
                em.merge(consultation);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la mise à jour du statut", e);
        } finally {
            em.close();
        }
    }

    // Supprimer une consultation
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Consultation consultation = em.find(Consultation.class, id);
            if (consultation != null) {
                em.remove(consultation);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression de la consultation", e);
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