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
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/generaliste/dashboard")
public class GeneralisteServlet extends HttpServlet {

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
        User medecin = (User) session.getAttribute("utilisateur");

        if (medecin == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Récupérer les patients en attente avec Stream API
            List<Patient> patientsEnAttente = patientDAO.findPatientsEnAttenteWithSignesVitaux().stream()
                    .filter(p -> p.getHeureArrivee().toLocalDate().equals(LocalDate.now()))
                    .limit(5) // Top 5 pour le dashboard
                    .collect(Collectors.toList());

            long patientsEnAttenteCount = patientDAO.findPatientsEnAttenteWithSignesVitaux().stream()
                    .filter(p -> p.getHeureArrivee().toLocalDate().equals(LocalDate.now()))
                    .count();

            // Récupérer les consultations du médecin
            List<Consultation> consultationsDuJour = consultationDAO.findByMedecinAndDate(
                    medecin.getId(), LocalDate.now());

            long consultationsDuJourCount = consultationsDuJour.size();

            // Consultations en attente d'avis spécialiste avec Stream API
            List<Consultation> consultationsEnAttenteAvis = consultationsDuJour.stream()
                    .filter(c -> c.getStatut() == StatutConsultation.EN_ATTENTE_AVIS_SPECIALISTE)
                    .collect(Collectors.toList());

            long consultationsEnAttenteAvisCount = consultationsEnAttenteAvis.size();

            // Consultations terminées avec Stream API
            long consultationsTermineesCount = consultationsDuJour.stream()
                    .filter(c -> c.getStatut() == StatutConsultation.TERMINEE)
                    .count();

            // Passer les données à la JSP
            request.setAttribute("patientsEnAttente", patientsEnAttente);
            request.setAttribute("patientsEnAttenteCount", patientsEnAttenteCount);
            request.setAttribute("consultationsDuJourCount", consultationsDuJourCount);
            request.setAttribute("consultationsEnAttenteAvis", consultationsEnAttenteAvis);
            request.setAttribute("consultationsEnAttenteAvisCount", consultationsEnAttenteAvisCount);
            request.setAttribute("consultationsTermineesCount", consultationsTermineesCount);

            request.getRequestDispatcher("/views/generaliste/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Erreur lors du chargement du dashboard");
            request.getRequestDispatcher("/views/generaliste/dashboard.jsp").forward(request, response);
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