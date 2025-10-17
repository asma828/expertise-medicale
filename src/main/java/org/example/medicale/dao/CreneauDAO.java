package org.example.medicale.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.medicale.entity.Creneau;
import org.example.medicale.enums.StatutCreneau;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class CreneauDAO {

    private EntityManagerFactory emf;

    public CreneauDAO() {
        this.emf = Persistence.createEntityManagerFactory("medicale");
    }

    public CreneauDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // Créer ou mettre à jour un créneau
    public Creneau save(Creneau creneau) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (creneau.getId() == null) {
                em.persist(creneau);
            } else {
                creneau = em.merge(creneau);
            }
            em.getTransaction().commit();
            return creneau;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la sauvegarde du créneau", e);
        } finally {
            em.close();
        }
    }

    // Trouver un créneau par ID
    public Optional<Creneau> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Creneau> query = em.createQuery(
                    "SELECT c FROM Creneau c " +
                            "LEFT JOIN FETCH c.specialiste " +
                            "WHERE c.id = :id",
                    Creneau.class
            );
            query.setParameter("id", id);
            List<Creneau> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    // Trouver tous les créneaux
    public List<Creneau> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Creneau> query = em.createQuery(
                    "SELECT c FROM Creneau c ORDER BY c.dateHeure ASC",
                    Creneau.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les créneaux d'un spécialiste
    public List<Creneau> findBySpecialiste(Long specialisteId) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Creneau> query = em.createQuery(
                    "SELECT c FROM Creneau c " +
                            "WHERE c.specialiste.id = :specialisteId " +
                            "ORDER BY c.dateHeure ASC",
                    Creneau.class
            );
            query.setParameter("specialisteId", specialisteId);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les créneaux disponibles d'un spécialiste
    public List<Creneau> findDisponiblesBySpecialiste(Long specialisteId) {
        EntityManager em = emf.createEntityManager();
        try {
            LocalDateTime maintenant = LocalDateTime.now();
            TypedQuery<Creneau> query = em.createQuery(
                    "SELECT c FROM Creneau c " +
                            "WHERE c.specialiste.id = :specialisteId " +
                            "AND c.statut = :statut " +
                            "AND c.dateHeure > :maintenant " +
                            "ORDER BY c.dateHeure ASC",
                    Creneau.class
            );
            query.setParameter("specialisteId", specialisteId);
            query.setParameter("statut", StatutCreneau.DISPONIBLE);
            query.setParameter("maintenant", maintenant);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les créneaux par statut
    public List<Creneau> findByStatut(StatutCreneau statut) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Creneau> query = em.createQuery(
                    "SELECT c FROM Creneau c " +
                            "LEFT JOIN FETCH c.specialiste " +
                            "WHERE c.statut = :statut " +
                            "ORDER BY c.dateHeure ASC",
                    Creneau.class
            );
            query.setParameter("statut", statut);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Mettre à jour le statut d'un créneau
    public void updateStatut(Long creneauId, StatutCreneau statut) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Creneau creneau = em.find(Creneau.class, creneauId);
            if (creneau != null) {
                creneau.setStatut(statut);
                em.merge(creneau);
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

    // Supprimer un créneau
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Creneau creneau = em.find(Creneau.class, id);
            if (creneau != null) {
                em.remove(creneau);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression du créneau", e);
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