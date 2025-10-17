package org.example.medicale.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.medicale.dao.ConsultationDAO;
import org.example.medicale.dao.PatientDAO;
import org.example.medicale.dao.UserDAO;
import org.example.medicale.entity.ActeTechnique;
import org.example.medicale.entity.Consultation;
import org.example.medicale.entity.Patient;
import org.example.medicale.entity.User;
import org.example.medicale.enums.StatutConsultation;
import org.example.medicale.enums.StatutPatient;
import org.example.medicale.enums.TypeActe;
import org.example.medicale.util.CSRFTokenUtil;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@WebServlet(name = "GeneralisteServlet", urlPatterns = {
        "/generaliste/dashboard",
        "/generaliste/patients/attente",
        "/generaliste/consultation/nouvelle",
        "/generaliste/consultation/creer",
        "/generaliste/consultation/detail",
        "/generaliste/consultation/ajouter-acte",
        "/generaliste/consultation/cloturer"
})
public class GeneralisteDashboardServlet extends HttpServlet {

    private PatientDAO patientDAO;
    private ConsultationDAO consultationDAO;
    private UserDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        patientDAO = new PatientDAO();
        consultationDAO = new ConsultationDAO();
        utilisateurDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAuthenticated(request, "GENERALISTE")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String path = request.getServletPath();

        try {
            switch (path) {
                case "/generaliste/dashboard":
                    showDashboard(request, response);
                    break;
                case "/generaliste/patients/attente":
                    showPatientsEnAttente(request, response);
                    break;
                case "/generaliste/consultation/nouvelle":
                    showNouvelleConsultationForm(request, response);
                    break;
                case "/generaliste/consultation/detail":
                    showConsultationDetail(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors du traitement de la requête", e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!isAuthenticated(request, "GENERALISTE")) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        if (!validateCSRF(request)) {
            request.setAttribute("error", "Erreur de sécurité. Veuillez réessayer.");
            showDashboard(request, response);
            return;
        }

        String path = request.getServletPath();

        try {
            switch (path) {
                case "/generaliste/consultation/creer":
                    creerConsultation(request, response);
                    break;
                case "/generaliste/consultation/ajouter-acte":
                    ajouterActeTechnique(request, response);
                    break;
                case "/generaliste/consultation/cloturer":
                    cloturerConsultation(request, response);
                    break;
                default:
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (Exception e) {
            throw new ServletException("Erreur lors du traitement de la requête", e);
        }
    }

    private void showDashboard(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Long medecinId = (Long) session.getAttribute("userId");

        // Récupérer les statistiques
        List<Consultation> consultationsEnCours =
                consultationDAO.findConsultationsEnCours(medecinId);
        List<Consultation> consultationsEnAttenteAvis =
                consultationDAO.findConsultationsEnAttenteAvis(medecinId);
        List<Patient> patientsEnAttente = patientDAO.findPatientsEnAttente();

        request.setAttribute("consultationsEnCours", consultationsEnCours.size());
        request.setAttribute("consultationsEnAttenteAvis", consultationsEnAttenteAvis.size());
        request.setAttribute("patientsEnAttente", patientsEnAttente.size());
        request.setAttribute("consultations", consultationsEnCours);

        request.getRequestDispatcher("/WEB-INF/views/generaliste/dashboard.jsp")
                .forward(request, response);
    }

    private void showPatientsEnAttente(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Patient> patients = patientDAO.findPatientsEnAttente();
        request.setAttribute("patients", patients);

        request.getRequestDispatcher("/WEB-INF/views/generaliste/patients-attente.jsp")
                .forward(request, response);
    }

    private void showNouvelleConsultationForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String patientIdStr = request.getParameter("patientId");

        if (patientIdStr == null || patientIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/generaliste/patients/attente");
            return;
        }

        Long patientId = Long.parseLong(patientIdStr);
        Optional<Patient> patientOpt = patientDAO.findById(patientId);

        if (patientOpt.isEmpty()) {
            request.setAttribute("error", "Patient non trouvé");
            showPatientsEnAttente(request, response);
            return;
        }

        request.setAttribute("patient", patientOpt.get());
        request.getRequestDispatcher("/WEB-INF/views/generaliste/nouvelle-consultation.jsp")
                .forward(request, response);
    }

    private void creerConsultation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            HttpSession session = request.getSession();
            Long medecinId = (Long) session.getAttribute("userId");
            Long patientId = Long.parseLong(request.getParameter("patientId"));

            Optional<User> medecinOpt = utilisateurDAO.findById(medecinId);
            Optional<Patient> patientOpt = patientDAO.findById(patientId);

            if (medecinOpt.isEmpty() || patientOpt.isEmpty()) {
                throw new Exception("Médecin ou patient non trouvé");
            }

            User medecin = medecinOpt.get();
            Patient patient = patientOpt.get();

            // Créer la consultation
            Consultation consultation = new Consultation();
            consultation.setPatient(patient);
            consultation.setMedecin(medecin);
            consultation.setMotif(request.getParameter("motif"));
            consultation.setExamenClinique(request.getParameter("examenClinique"));
            consultation.setSymptomes(request.getParameter("symptomes"));
            consultation.setObservations(request.getParameter("observations"));
            consultation.setStatut(StatutConsultation.EN_COURS);

            // Sauvegarder
            consultation = consultationDAO.save(consultation);

            // Mettre à jour le statut du patient
            patient.setStatut(StatutPatient.EN_CONSULTATION);
            patientDAO.save(patient);

            session.setAttribute("success", "Consultation créée avec succès");
            response.sendRedirect(request.getContextPath() +
                    "/generaliste/consultation/detail?id=" + consultation.getId());

        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la création: " + e.getMessage());
            showNouvelleConsultationForm(request, response);
        }
    }

    private void showConsultationDetail(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String consultationIdStr = request.getParameter("id");

        if (consultationIdStr == null || consultationIdStr.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/generaliste/dashboard");
            return;
        }

        Long consultationId = Long.parseLong(consultationIdStr);
        Optional<Consultation> consultationOpt = consultationDAO.findById(consultationId);

        if (consultationOpt.isEmpty()) {
            request.setAttribute("error", "Consultation non trouvée");
            showDashboard(request, response);
            return;
        }

        Consultation consultation = consultationOpt.get();

        // Calculer le coût total avec Lambda
        Double coutTotal = consultation.calculerCoutTotal();

        request.setAttribute("consultation", consultation);
        request.setAttribute("coutTotal", coutTotal);
        request.setAttribute("typeActes", TypeActe.values());

        request.getRequestDispatcher("/WEB-INF/views/generaliste/consultation-detail.jsp")
                .forward(request, response);
    }

    private void ajouterActeTechnique(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Long consultationId = Long.parseLong(request.getParameter("consultationId"));
            String typeActeStr = request.getParameter("typeActe");

            Optional<Consultation> consultationOpt = consultationDAO.findById(consultationId);

            if (consultationOpt.isEmpty()) {
                throw new Exception("Consultation non trouvée");
            }

            Consultation consultation = consultationOpt.get();
            TypeActe typeActe = TypeActe.valueOf(typeActeStr);

            // Créer l'acte technique
            ActeTechnique acte = new ActeTechnique(typeActe);
            consultation.ajouterActeTechnique(acte);

            // Sauvegarder
            consultationDAO.save(consultation);

            request.getSession().setAttribute("success", "Acte technique ajouté avec succès");
            response.sendRedirect(request.getContextPath() +
                    "/generaliste/consultation/detail?id=" + consultationId);

        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de l'ajout de l'acte: " + e.getMessage());
            showConsultationDetail(request, response);
        }
    }

    private void cloturerConsultation(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            Long consultationId = Long.parseLong(request.getParameter("consultationId"));

            Optional<Consultation> consultationOpt = consultationDAO.findById(consultationId);

            if (consultationOpt.isEmpty()) {
                throw new Exception("Consultation non trouvée");
            }

            Consultation consultation = consultationOpt.get();

            // Mettre à jour diagnostic et traitement
            consultation.setDiagnostic(request.getParameter("diagnostic"));
            consultation.setTraitement(request.getParameter("traitement"));
            consultation.setStatut(StatutConsultation.TERMINEE);

            // Sauvegarder
            consultationDAO.save(consultation);

            // Mettre à jour le statut du patient
            Patient patient = consultation.getPatient();
            patient.setStatut(StatutPatient.TERMINE);
            patientDAO.save(patient);

            request.getSession().setAttribute("success", "Consultation clôturée avec succès");
            response.sendRedirect(request.getContextPath() + "/generaliste/dashboard");

        } catch (Exception e) {
            request.setAttribute("error", "Erreur lors de la clôture: " + e.getMessage());
            showConsultationDetail(request, response);
        }
    }

    private boolean isAuthenticated(HttpServletRequest request, String requiredRole) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String userRole = (String) session.getAttribute("userRole");
        return userRole != null && userRole.equals(requiredRole);
    }

    private boolean validateCSRF(HttpServletRequest request) {
        String token = request.getParameter("csrfToken");
        HttpSession session = request.getSession(false);
        if (session == null) {
            return false;
        }
        String sessionToken = (String) session.getAttribute("csrfToken");
        return CSRFTokenUtil.validateToken(token, sessionToken);
    }

    @Override
    public void destroy() {
        if (patientDAO != null) patientDAO.close();
        if (consultationDAO != null) consultationDAO.close();
        if (utilisateurDAO != null) utilisateurDAO.close();
    }
}
