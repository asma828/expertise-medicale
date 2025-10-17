package org.example.medicale.dao;

import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import jakarta.persistence.TypedQuery;
import org.example.medicale.entity.Patient;
import org.example.medicale.enums.StatutPatient;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class PatientDAO {

    private EntityManagerFactory emf;

    public PatientDAO() {
        this.emf = Persistence.createEntityManagerFactory("medicale");
    }

    public PatientDAO(EntityManagerFactory emf) {
        this.emf = emf;
    }

    // Créer un patient
    public Patient save(Patient patient) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            if (patient.getId() == null) {
                em.persist(patient);
            } else {
                patient = em.merge(patient);
            }
            em.getTransaction().commit();
            return patient;
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la sauvegarde du patient", e);
        } finally {
            em.close();
        }
    }

    // Trouver un patient par ID
    public Optional<Patient> findById(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            Patient patient = em.find(Patient.class, id);
            return Optional.ofNullable(patient);
        } finally {
            em.close();
        }
    }

    // Trouver un patient par numéro de sécurité sociale
    public Optional<Patient> findByNumeroSecuriteSociale(String numero) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Patient> query = em.createQuery(
                    "SELECT p FROM Patient p WHERE p.numeroSecuriteSociale = :numero",
                    Patient.class
            );
            query.setParameter("numero", numero);
            List<Patient> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    // Trouver tous les patients
    public List<Patient> findAll() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Patient> query = em.createQuery(
                    "SELECT p FROM Patient p ORDER BY p.heureArrivee ASC",
                    Patient.class
            );
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Trouver les patients du jour avec Stream API
    public List<Patient> findPatientsDuJour() {
        EntityManager em = emf.createEntityManager();
        try {
            LocalDateTime debutJour = LocalDate.now().atStartOfDay();
            LocalDateTime finJour = debutJour.plusDays(1);

            TypedQuery<Patient> query = em.createQuery(
                    "SELECT p FROM Patient p WHERE p.heureArrivee BETWEEN :debut AND :fin",
                    Patient.class
            );
            query.setParameter("debut", debutJour);
            query.setParameter("fin", finJour);

            List<Patient> patients = query.getResultList();

            // Utilisation Stream API pour trier par heure d'arrivée
            return patients.stream()
                    .sorted((p1, p2) -> p1.getHeureArrivee().compareTo(p2.getHeureArrivee()))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    // Trouver les patients en attente
    public List<Patient> findPatientsEnAttente() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Patient> query = em.createQuery(
                    "SELECT p FROM Patient p WHERE p.statut = :statut ORDER BY p.heureArrivee ASC",
                    Patient.class
            );
            query.setParameter("statut", StatutPatient.EN_ATTENTE);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Mettre à jour le statut d'un patient
    public void updateStatut(Long patientId, StatutPatient statut) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Patient patient = em.find(Patient.class, patientId);
            if (patient != null) {
                patient.setStatut(statut);
                em.merge(patient);
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

    // Rechercher des patients par nom ou prénom
    public List<Patient> searchByNomPrenom(String recherche) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Patient> query = em.createQuery(
                    "SELECT p FROM Patient p WHERE LOWER(p.nom) LIKE LOWER(:recherche) " +
                            "OR LOWER(p.prenom) LIKE LOWER(:recherche)",
                    Patient.class
            );
            query.setParameter("recherche", "%" + recherche + "%");
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Supprimer un patient
    public void delete(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            em.getTransaction().begin();
            Patient patient = em.find(Patient.class, id);
            if (patient != null) {
                em.remove(patient);
            }
            em.getTransaction().commit();
        } catch (Exception e) {
            if (em.getTransaction().isActive()) {
                em.getTransaction().rollback();
            }
            throw new RuntimeException("Erreur lors de la suppression du patient", e);
        } finally {
            em.close();
        }
    }

    // Trouver les patients du jour AVEC les signes vitaux (résout LazyInitializationException)
    public List<Patient> findPatientsDuJourWithSignesVitaux() {
        EntityManager em = emf.createEntityManager();
        try {
            LocalDateTime debutJour = LocalDate.now().atStartOfDay();
            LocalDateTime finJour = debutJour.plusDays(1);

            // Utiliser JOIN FETCH pour charger les signes vitaux en même temps
            TypedQuery<Patient> query = em.createQuery(
                    "SELECT DISTINCT p FROM Patient p " +
                            "LEFT JOIN FETCH p.signesVitaux " +
                            "WHERE p.heureArrivee BETWEEN :debut AND :fin",
                    Patient.class
            );
            query.setParameter("debut", debutJour);
            query.setParameter("fin", finJour);

            List<Patient> patients = query.getResultList();

            // Utilisation Stream API pour trier par heure d'arrivée
            return patients.stream()
                    .sorted((p1, p2) -> p1.getHeureArrivee().compareTo(p2.getHeureArrivee()))
                    .collect(Collectors.toList());
        } finally {
            em.close();
        }
    }

    // Trouver les patients en attente AVEC les signes vitaux (résout LazyInitializationException)
    public List<Patient> findPatientsEnAttenteWithSignesVitaux() {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Patient> query = em.createQuery(
                    "SELECT DISTINCT p FROM Patient p " +
                            "LEFT JOIN FETCH p.signesVitaux " +
                            "WHERE p.statut = :statut " +
                            "ORDER BY p.heureArrivee ASC",
                    Patient.class
            );
            query.setParameter("statut", StatutPatient.EN_ATTENTE);
            return query.getResultList();
        } finally {
            em.close();
        }
    }

    // Add this method to your PatientDAO class

    public Optional<Patient> findByIdWithSignesVitaux(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Patient> query = em.createQuery(
                    "SELECT p FROM Patient p " +
                            "LEFT JOIN FETCH p.signesVitaux " +
                            "WHERE p.id = :id",
                    Patient.class
            );
            query.setParameter("id", id);
            List<Patient> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
        } finally {
            em.close();
        }
    }

    public Optional<Patient> findByIdWithAllRelations(Long id) {
        EntityManager em = emf.createEntityManager();
        try {
            TypedQuery<Patient> query = em.createQuery(
                    "SELECT DISTINCT p FROM Patient p " +
                            "LEFT JOIN FETCH p.signesVitaux " +
                            "LEFT JOIN FETCH p.consultations " +
                            "WHERE p.id = :id",
                    Patient.class
            );
            query.setParameter("id", id);
            List<Patient> results = query.getResultList();
            return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
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
