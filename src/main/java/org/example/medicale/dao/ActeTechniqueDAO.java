package org.example.medicale.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.medicale.entity.ActeTechnique;

import java.util.List;
import java.util.Optional;

public class ActeTechniqueDAO {

    private EntityManagerFactory emf;

    public ActeTechniqueDAO() {
        this.emf = Persistence.createEntityManagerFactory("medicale");
    }

    public ActeTechniqueDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // Créer ou mettre à jour un acte technique
    public ActeTechnique save(ActeTechnique acteTechnique) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (acteTechnique.getId() == null) {
                em.persist(acteTechnique);
            } else {
                acteTechnique = em.merge(acteTechnique);
            }
            em.getTransaction().commit();
            return acteTechnique;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la sauvegarde de l'acte technique", e);
        } finally {
            em.close();
        }
    }

    // Trouver un acte technique par ID
    public Optional<ActeTechnique> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            ActeTechnique acte = em.find(ActeTechnique.class, id);
            return Optional.ofNullable(acte);
        } finally {
            em.close();
        }
    }

    // Trouver tous les actes techniques
    public List<ActeTechnique> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ActeTechnique> query = em.createQuery(
                    "SELECT a FROM ActeTechnique a",
                    ActeTechnique.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les actes techniques d'une consultation
    public List<ActeTechnique> findByConsultation(Long consultationId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<ActeTechnique> query = em.createQuery(
                    "SELECT a FROM ActeTechnique a WHERE a.consultation.id = :consultationId",
                    ActeTechnique.class
            );
            query.setParameter("consultationId", consultationId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Supprimer un acte technique
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            ActeTechnique acte = em.find(ActeTechnique.class, id);
            if (acte != null) {
                em.remove(acte);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression de l'acte technique", e);
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