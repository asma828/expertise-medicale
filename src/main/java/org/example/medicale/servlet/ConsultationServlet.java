package org.example.medicale.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.medicale.dao.ConsultationDAO;
import org.example.medicale.dao.PatientDAO;
import org.example.medicale.entity.Consultation;
import org.example.medicale.entity.Patient;
import org.example.medicale.entity.User;
import org.example.medicale.enums.StatutConsultation;
import org.example.medicale.enums.StatutPatient;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;

@WebServlet("/generaliste/creerConsultation")
public class ConsultationServlet extends HttpServlet {

    private PatientDAO patientDAO;
    private ConsultationDAO consultationDAO;

    @Override
    public void init() throws ServletException {
        patientDAO = new PatientDAO();
        consultationDAO = new ConsultationDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Générer un token CSRF
        if (session.getAttribute("csrfToken") == null) {
            String csrfToken = java.util.UUID.randomUUID().toString();
            session.setAttribute("csrfToken", csrfToken);
        }

        String patientIdStr = request.getParameter("patientId");

        if (patientIdStr == null || patientIdStr.trim().isEmpty()) {
            request.setAttribute("error", "ID patient manquant");
            request.getRequestDispatcher("/views/generaliste/creerConsultation.jsp").forward(request, response);
            return;
        }

        try {
            Long patientId = Long.parseLong(patientIdStr);
            Optional<Patient> patientOpt = patientDAO.findByIdWithSignesVitaux(patientId);

            if (patientOpt.isPresent()) {
                Patient patient = patientOpt.get();

                // Mettre à jour le statut du patient
                if (patient.getStatut() == StatutPatient.EN_ATTENTE) {
                    patient.setStatut(StatutPatient.EN_CONSULTATION);
                    patientDAO.save(patient);
                }

                request.setAttribute("patient", patient);
            } else {
                request.setAttribute("error", "Patient non trouvé");
            }

            request.getRequestDispatcher("/views/generaliste/creerConsultation.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID patient invalide");
            request.getRequestDispatcher("/views/generaliste/creerConsultation.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement du patient");
            request.getRequestDispatcher("/views/generaliste/creerConsultation.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Vérification CSRF
        String csrfToken = request.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");

        if (csrfToken == null || !csrfToken.equals(sessionToken)) {
            request.setAttribute("error", "Erreur de sécurité. Veuillez réessayer.");
            doGet(request, response);
            return;
        }

        // Récupérer le médecin connecté
        User medecin = (User) session.getAttribute("utilisateur");
        if (medecin == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Récupérer les paramètres
        String patientIdStr = request.getParameter("patientId");
        String motif = request.getParameter("motif");
        String symptomes = request.getParameter("symptomes");
        String examenClinique = request.getParameter("examenClinique");
        String observations = request.getParameter("observations");

        // Validation
        if (patientIdStr == null || motif == null || examenClinique == null ||
                motif.trim().isEmpty() || examenClinique.trim().isEmpty()) {
            request.setAttribute("error", "Les champs obligatoires doivent être remplis");
            doGet(request, response);
            return;
        }

        try {
            Long patientId = Long.parseLong(patientIdStr);
            Optional<Patient> patientOpt = patientDAO.findById(patientId);

            if (patientOpt.isEmpty()) {
                request.setAttribute("error", "Patient non trouvé");
                doGet(request, response);
                return;
            }

            Patient patient = patientOpt.get();

            // Créer la consultation
            Consultation consultation = new Consultation();
            consultation.setPatient(patient);
            consultation.setMedecin(medecin);
            consultation.setMotif(motif.trim());
            consultation.setSymptomes(symptomes != null ? symptomes.trim() : null);
            consultation.setExamenClinique(examenClinique.trim());
            consultation.setObservations(observations != null ? observations.trim() : null);
            consultation.setDateConsultation(LocalDateTime.now());
            consultation.setStatut(StatutConsultation.EN_COURS);
            consultation.setCoutConsultation(150.0); // Fixe à 150 DH

            // Sauvegarder la consultation
            consultation = consultationDAO.save(consultation);

            // Mettre à jour le statut du patient
            patient.setStatut(StatutPatient.EN_CONSULTATION);
            patientDAO.save(patient);

            // Rediriger vers le détail de la consultation
            session.setAttribute("successMessage", "Consultation créée avec succès!");
            response.sendRedirect(request.getContextPath() + "/generaliste/consultation/" + consultation.getId());

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID patient invalide");
            doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la création de la consultation: " + e.getMessage());
            doGet(request, response);
        }
    }

    @Override
    public void destroy() {
        if (patientDAO != null) {
            patientDAO.close();
        }
        if (consultationDAO != null) {
            consultationDAO.close();
        }
    }
}