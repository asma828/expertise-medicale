package org.example.medicale.dao;


import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.medicale.entity.User;
import org.example.medicale.enums.Role;
import org.example.medicale.enums.Specialite;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class UserDAO {

    private EntityManagerFactory emf;

    public UserDAO() {
        this.emf = Persistence.createEntityManagerFactory("teleexpertise-pu");
    }

    public UserDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // Créer ou mettre à jour un utilisateur
    public User save(User utilisateur) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (utilisateur.getId() == null) {
                em.persist(utilisateur);
            } else {
                utilisateur = em.merge(utilisateur);
            }
            em.getTransaction().commit();
            return utilisateur;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la sauvegarde de l'utilisateur", e);
        } finally {
            em.close();
        }
    }

    // Trouver un utilisateur par ID
    public Optional<User> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            User utilisateur = em.find(User.class, id);
            return Optional.ofNullable(utilisateur);
        } finally {
            em.close();
        }
    }

    // Trouver un utilisateur par email
    public Optional<User> findByEmail(String email) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.email = :email",
                    User.class
            );
            query.setParameter("email", email);
            List<User> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    // Trouver tous les utilisateurs
    public List<User> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM Utilisateur u ORDER BY u.nom ASC",
                    User.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les utilisateurs par rôle
    public List<User> findByRole(Role role) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.role = :role ORDER BY u.nom ASC",
                    User.class
            );
            query.setParameter("role", role);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les spécialistes par spécialité avec Stream API
    public List<User> findSpecialistesBySpecialite(Specialite specialite) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.role = :role " +
                            "AND u.specialite = :specialite AND u.actif = true",
                    User.class
            );
            query.setParameter("role", Role.SPECIALISTE);
            query.setParameter("specialite", specialite);

            // Utilisation Stream API pour trier par tarif
            return query.getResultList().stream()
                    .sorted(Comparator.comparing(User::getTarif))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    // Trouver les spécialistes disponibles avec Stream API (filtre + tri)
    public List<User> findSpecialistesDisponibles(Specialite specialite, Double tarifMax) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.role = :role " +
                            "AND u.specialite = :specialite AND u.actif = true",
                    User.class
            );
            query.setParameter("role", Role.SPECIALISTE);
            query.setParameter("specialite", specialite);

            // Utilisation Stream API : filtrer par tarif et trier
            return query.getResultList().stream()
                    .filter(u -> u.getTarif() != null && u.getTarif() <= tarifMax)
                    .sorted(Comparator.comparing(User::getTarif))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    // Trouver tous les spécialistes actifs
    public List<User> findAllSpecialistes() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.role = :role AND u.actif = true " +
                            "ORDER BY u.nom ASC",
                    User.class
            );
            query.setParameter("role", Role.SPECIALISTE);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver tous les médecins généralistes
    public List<User> findAllGeneralistes() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.role = :role AND u.actif = true " +
                            "ORDER BY u.nom ASC",
                    User.class
            );
            query.setParameter("role", Role.GENERALISTE);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver tous les infirmiers
    public List<User> findAllInfirmiers() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<User> query = em.createQuery(
                    "SELECT u FROM Utilisateur u WHERE u.role = :role AND u.actif = true " +
                            "ORDER BY u.nom ASC",
                    User.class
            );
            query.setParameter("role", Role.INFIRMIER);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Supprimer un utilisateur
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            User utilisateur = em.find(User.class, id);
            if (utilisateur != null) {
                em.remove(utilisateur);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression de l'utilisateur", e);
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