package org.example.medicale.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.medicale.dao.PatientDAO;
import org.example.medicale.entity.Patient;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/generaliste/patientsenattente")
public class PatientsAttenteServlet extends HttpServlet {

    private PatientDAO patientDAO;

    @Override
    public void init() throws ServletException {
        patientDAO = new PatientDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        try {
            // Récupérer tous les patients en attente du jour avec Stream API
            List<Patient> patients = patientDAO.findPatientsEnAttenteWithSignesVitaux().stream()
                    .filter(p -> p.getHeureArrivee().toLocalDate().equals(LocalDate.now()))
                    .sorted((p1, p2) -> p1.getHeureArrivee().compareTo(p2.getHeureArrivee()))
                    .collect(Collectors.toList());

            // Calculer le temps d'attente moyen avec Stream API et Lambda
            long tempsAttenteMoyen = 0;
            if (!patients.isEmpty()) {
                LocalDateTime maintenant = LocalDateTime.now();
                tempsAttenteMoyen = patients.stream()
                        .map(p -> Duration.between(p.getHeureArrivee(), maintenant).toMinutes())
                        .collect(Collectors.averagingLong(Long::longValue))
                        .longValue();
            }

            // Passer les données à la JSP
            request.setAttribute("patients", patients);
            request.setAttribute("tempsAttenteMoyen", tempsAttenteMoyen);

            request.getRequestDispatcher("/views/generaliste/patientsenattente.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement de la file d'attente");
            request.getRequestDispatcher("/views/generaliste/patientsenattente.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        if (patientDAO != null) {
            patientDAO.close();
        }
    }
}