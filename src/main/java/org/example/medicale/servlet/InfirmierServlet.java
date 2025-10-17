package org.example.medicale.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.medicale.dao.PatientDAO;
import org.example.medicale.entity.Patient;
import org.example.medicale.enums.StatutPatient;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/infirmier/dashboard")
public class InfirmierServlet extends HttpServlet {

    private PatientDAO patientDAO;

    @Override
    public void init() throws ServletException {
        patientDAO = new PatientDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Récupérer tous les patients du jour avec signes vitaux
            List<Patient> patientsAujourdhui = patientDAO.findPatientsDuJourWithSignesVitaux().stream()
                    .sorted((p1, p2) -> p2.getHeureArrivee().compareTo(p1.getHeureArrivee()))
                    .collect(Collectors.toList());

            // Statistiques avec Stream API
            long patientsCount = patientsAujourdhui.size();

            long enAttenteCount = patientsAujourdhui.stream()
                    .filter(p -> p.getStatut() == StatutPatient.EN_ATTENTE)
                    .count();

            long enConsultationCount = patientsAujourdhui.stream()
                    .filter(p -> p.getStatut() == StatutPatient.EN_CONSULTATION)
                    .count();

            // Récupérer les 5 derniers patients pour l'affichage
            List<Patient> recentPatients = patientsAujourdhui.stream()
                    .limit(5)
                    .collect(Collectors.toList());

            // Passer les données à la JSP
            request.setAttribute("patientsCount", patientsCount);
            request.setAttribute("enAttenteCount", enAttenteCount);
            request.setAttribute("enConsultationCount", enConsultationCount);
            request.setAttribute("recentPatients", recentPatients);

            request.getRequestDispatcher("/views/infirmier/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Erreur lors du chargement du dashboard");
            request.getRequestDispatcher("/views/infirmier/dashboard.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        if (patientDAO != null) {
            patientDAO.close();
        }
    }
}