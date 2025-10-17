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
import java.util.List;

@WebServlet("/infirmier/listePatients")
public class ListePatientsServlet extends HttpServlet {

    private PatientDAO patientDAO;

    @Override
    public void init() throws ServletException {
        patientDAO = new PatientDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Récupérer tous les patients du jour avec Stream API et signes vitaux
            List<Patient> patients = patientDAO.findPatientsDuJourWithSignesVitaux();

            // Calculer les statistiques avec Stream API et Lambda
            long enAttenteCount = patients.stream()
                    .filter(p -> p.getStatut() == StatutPatient.EN_ATTENTE)
                    .count();

            long enConsultationCount = patients.stream()
                    .filter(p -> p.getStatut() == StatutPatient.EN_CONSULTATION)
                    .count();

            long termineCount = patients.stream()
                    .filter(p -> p.getStatut() == StatutPatient.TERMINE)
                    .count();

            // Passer les données à la JSP
            request.setAttribute("patients", patients);
            request.setAttribute("enAttenteCount", enAttenteCount);
            request.setAttribute("enConsultationCount", enConsultationCount);
            request.setAttribute("termineCount", termineCount);

            request.getRequestDispatcher("/views/infirmier/listePatients.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement de la liste des patients");
            request.getRequestDispatcher("/views/infirmier/listePatients.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        if (patientDAO != null) {
            patientDAO.close();
        }
    }
}