package org.example.medicale.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.medicale.entity.DemandeExpertise;
import org.example.medicale.enums.StatutExpertise;

import java.util.List;
import java.util.Optional;

public class DemandeExpertiseDAO {

    private EntityManagerFactory emf;

    public DemandeExpertiseDAO() {
        this.emf = Persistence.createEntityManagerFactory("medicale");
    }

    public DemandeExpertiseDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // Créer ou mettre à jour une demande d'expertise
    public DemandeExpertise save(DemandeExpertise demande) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (demande.getId() == null) {
                em.persist(demande);
            } else {
                demande = em.merge(demande);
            }
            em.getTransaction().commit();
            return demande;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la sauvegarde de la demande d'expertise", e);
        } finally {
            em.close();
        }
    }

    // Trouver une demande par ID avec toutes ses relations
    public Optional<DemandeExpertise> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<DemandeExpertise> query = em.createQuery(
                    "SELECT d FROM DemandeExpertise d " +
                            "LEFT JOIN FETCH d.consultation c " +
                            "LEFT JOIN FETCH c.patient " +
                            "LEFT JOIN FETCH d.specialiste " +
                            "LEFT JOIN FETCH d.medecinDemandeur " +
                            "LEFT JOIN FETCH d.creneau " +
                            "WHERE d.id = :id",
                    DemandeExpertise.class
            );
            query.setParameter("id", id);
            List<DemandeExpertise> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    // Trouver toutes les demandes
    public List<DemandeExpertise> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<DemandeExpertise> query = em.createQuery(
                    "SELECT d FROM DemandeExpertise d ORDER BY d.dateDemande DESC",
                    DemandeExpertise.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les demandes d'un spécialiste
    public List<DemandeExpertise> findBySpecialiste(Long specialisteId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<DemandeExpertise> query = em.createQuery(
                    "SELECT d FROM DemandeExpertise d " +
                            "LEFT JOIN FETCH d.consultation c " +
                            "LEFT JOIN FETCH c.patient " +
                            "LEFT JOIN FETCH d.medecinDemandeur " +
                            "WHERE d.specialiste.id = :specialisteId " +
                            "ORDER BY d.dateDemande DESC",
                    DemandeExpertise.class
            );
            query.setParameter("specialisteId", specialisteId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les demandes d'un médecin demandeur
    public List<DemandeExpertise> findByMedecinDemandeur(Long medecinId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<DemandeExpertise> query = em.createQuery(
                    "SELECT d FROM DemandeExpertise d " +
                            "LEFT JOIN FETCH d.consultation c " +
                            "LEFT JOIN FETCH c.patient " +
                            "LEFT JOIN FETCH d.specialiste " +
                            "WHERE d.medecinDemandeur.id = :medecinId " +
                            "ORDER BY d.dateDemande DESC",
                    DemandeExpertise.class
            );
            query.setParameter("medecinId", medecinId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les demandes par statut
    public List<DemandeExpertise> findByStatut(StatutExpertise statut) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<DemandeExpertise> query = em.createQuery(
                    "SELECT d FROM DemandeExpertise d " +
                            "LEFT JOIN FETCH d.consultation c " +
                            "LEFT JOIN FETCH c.patient " +
                            "LEFT JOIN FETCH d.specialiste " +
                            "LEFT JOIN FETCH d.medecinDemandeur " +
                            "WHERE d.statut = :statut " +
                            "ORDER BY d.dateDemande DESC",
                    DemandeExpertise.class
            );
            query.setParameter("statut", statut);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les demandes d'un spécialiste par statut
    public List<DemandeExpertise> findBySpecialisteAndStatut(Long specialisteId, StatutExpertise statut) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<DemandeExpertise> query = em.createQuery(
                    "SELECT d FROM DemandeExpertise d " +
                            "LEFT JOIN FETCH d.consultation c " +
                            "LEFT JOIN FETCH c.patient " +
                            "LEFT JOIN FETCH d.medecinDemandeur " +
                            "WHERE d.specialiste.id = :specialisteId " +
                            "AND d.statut = :statut " +
                            "ORDER BY d.dateDemande DESC",
                    DemandeExpertise.class
            );
            query.setParameter("specialisteId", specialisteId);
            query.setParameter("statut", statut);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Mettre à jour le statut d'une demande
    public void updateStatut(Long demandeId, StatutExpertise statut) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            DemandeExpertise demande = em.find(DemandeExpertise.class, demandeId);
            if (demande != null) {
                demande.setStatut(statut);
                em.merge(demande);
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

    // Supprimer une demande
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            DemandeExpertise demande = em.find(DemandeExpertise.class, id);
            if (demande != null) {
                em.remove(demande);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression de la demande", e);
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