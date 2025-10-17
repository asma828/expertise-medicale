package org.example.medicale.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.medicale.dao.ActeTechniqueDAO;
import org.example.medicale.dao.ConsultationDAO;
import org.example.medicale.dao.PatientDAO;
import org.example.medicale.entity.ActeTechnique;
import org.example.medicale.entity.Consultation;
import org.example.medicale.enums.StatutConsultation;
import org.example.medicale.enums.StatutPatient;
import org.example.medicale.enums.TypeActe;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/generaliste/consultation/*")
public class DetailConsultationServlet extends HttpServlet {

    private ConsultationDAO consultationDAO;
    private ActeTechniqueDAO acteTechniqueDAO;
    private PatientDAO patientDAO;

    @Override
    public void init() throws ServletException {
        consultationDAO = new ConsultationDAO();
        acteTechniqueDAO = new ActeTechniqueDAO();
        patientDAO = new PatientDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Générer token CSRF
        if (session.getAttribute("csrfToken") == null) {
            String csrfToken = java.util.UUID.randomUUID().toString();
            session.setAttribute("csrfToken", csrfToken);
        }

        // Extraire l'ID de la consultation de l'URL
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/generaliste/dashboard");
            return;
        }

        try {
            Long consultationId = Long.parseLong(pathInfo.substring(1));
            Optional<Consultation> consultationOpt = consultationDAO.findById(consultationId);

            if (consultationOpt.isPresent()) {
                Consultation consultation = consultationOpt.get();
                request.setAttribute("consultation", consultation);
                request.setAttribute("typesActes", TypeActe.values());
            } else {
                request.setAttribute("error", "Consultation non trouvée");
            }

            request.getRequestDispatcher("/views/generaliste/detailConsultation.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID consultation invalide");
            request.getRequestDispatcher("/views/generaliste/detailConsultation.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement de la consultation");
            request.getRequestDispatcher("/views/generaliste/detailConsultation.jsp").forward(request, response);
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
            session.setAttribute("error", "Erreur de sécurité. Veuillez réessayer.");
            doGet(request, response);
            return;
        }

        String action = request.getParameter("action");
        String pathInfo = request.getPathInfo();

        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/generaliste/dashboard");
            return;
        }

        try {
            Long consultationId = Long.parseLong(pathInfo.substring(1));

            switch (action) {
                case "updateDiagnostic":
                    handleUpdateDiagnostic(request, response, consultationId, session);
                    break;
                case "ajouterActe":
                    handleAjouterActe(request, response, consultationId, session);
                    break;
                case "terminer":
                    handleTerminerConsultation(request, response, consultationId, session);
                    break;
                default:
                    session.setAttribute("error", "Action non reconnue");
                    doGet(request, response);
            }

        } catch (NumberFormatException e) {
            session.setAttribute("error", "ID consultation invalide");
            doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Erreur: " + e.getMessage());
            doGet(request, response);
        }
    }

    private void handleUpdateDiagnostic(HttpServletRequest request, HttpServletResponse response,
                                        Long consultationId, HttpSession session)
            throws ServletException, IOException {

        String diagnostic = request.getParameter("diagnostic");
        String traitement = request.getParameter("traitement");

        Optional<Consultation> consultationOpt = consultationDAO.findById(consultationId);

        if (consultationOpt.isEmpty()) {
            session.setAttribute("error", "Consultation non trouvée");
            doGet(request, response);
            return;
        }

        Consultation consultation = consultationOpt.get();

        if (consultation.getStatut() != StatutConsultation.EN_COURS) {
            session.setAttribute("error", "Cette consultation ne peut plus être modifiée");
            doGet(request, response);
            return;
        }

        consultation.setDiagnostic(diagnostic != null && !diagnostic.trim().isEmpty() ? diagnostic.trim() : null);
        consultation.setTraitement(traitement != null && !traitement.trim().isEmpty() ? traitement.trim() : null);

        consultationDAO.save(consultation);

        session.setAttribute("successMessage", "Diagnostic et traitement mis à jour avec succès!");
        response.sendRedirect(request.getContextPath() + "/generaliste/consultation/" + consultationId);
    }

    private void handleAjouterActe(HttpServletRequest request, HttpServletResponse response,
                                   Long consultationId, HttpSession session)
            throws ServletException, IOException {

        String typeActeStr = request.getParameter("typeActe");
        String description = request.getParameter("description");

        if (typeActeStr == null || typeActeStr.trim().isEmpty()) {
            session.setAttribute("error", "Type d'acte manquant");
            doGet(request, response);
            return;
        }

        Optional<Consultation> consultationOpt = consultationDAO.findById(consultationId);

        if (consultationOpt.isEmpty()) {
            session.setAttribute("error", "Consultation non trouvée");
            doGet(request, response);
            return;
        }

        Consultation consultation = consultationOpt.get();

        if (consultation.getStatut() == StatutConsultation.TERMINEE) {
            session.setAttribute("error", "Impossible d'ajouter un acte à une consultation terminée");
            doGet(request, response);
            return;
        }

        try {
            TypeActe typeActe = TypeActe.valueOf(typeActeStr);

            ActeTechnique acte = new ActeTechnique(typeActe);
            acte.setConsultation(consultation);
            if (description != null && !description.trim().isEmpty()) {
                acte.setDescription(description.trim());
            }

            acteTechniqueDAO.save(acte);

            session.setAttribute("successMessage", "Acte technique ajouté avec succès!");
            response.sendRedirect(request.getContextPath() + "/generaliste/consultation/" + consultationId);

        } catch (IllegalArgumentException e) {
            session.setAttribute("error", "Type d'acte invalide");
            doGet(request, response);
        }
    }

    private void handleTerminerConsultation(HttpServletRequest request, HttpServletResponse response,
                                            Long consultationId, HttpSession session)
            throws ServletException, IOException {

        Optional<Consultation> consultationOpt = consultationDAO.findById(consultationId);

        if (consultationOpt.isEmpty()) {
            session.setAttribute("error", "Consultation non trouvée");
            doGet(request, response);
            return;
        }

        Consultation consultation = consultationOpt.get();

        // Vérifier que la consultation peut être terminée
        if (consultation.getStatut() == StatutConsultation.EN_ATTENTE_AVIS_SPECIALISTE) {
            session.setAttribute("error", "Impossible de clôturer une consultation en attente d'avis spécialiste");
            doGet(request, response);
            return;
        }

        if (consultation.getStatut() == StatutConsultation.TERMINEE) {
            session.setAttribute("error", "Cette consultation est déjà terminée");
            doGet(request, response);
            return;
        }

        // Vérifier que le diagnostic est renseigné
        if (consultation.getDiagnostic() == null || consultation.getDiagnostic().trim().isEmpty()) {
            session.setAttribute("error", "Veuillez renseigner le diagnostic avant de clôturer la consultation");
            doGet(request, response);
            return;
        }

        // Terminer la consultation
        consultation.setStatut(StatutConsultation.TERMINEE);
        consultationDAO.save(consultation);

        // Mettre à jour le statut du patient
        consultation.getPatient().setStatut(StatutPatient.TERMINE);
        patientDAO.save(consultation.getPatient());

        session.setAttribute("successMessage", "Consultation clôturée avec succès! Coût total: " +
                consultation.calculerCoutTotal() + " DH");
        response.sendRedirect(request.getContextPath() + "/generaliste/dashboard");
    }

    @Override
    public void destroy() {
        if (consultationDAO != null) {
            consultationDAO.close();
        }
        if (acteTechniqueDAO != null) {
            acteTechniqueDAO.close();
        }
        if (patientDAO != null) {
            patientDAO.close();
        }
    }
}