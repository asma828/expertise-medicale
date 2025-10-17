package org.example.medicale.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.medicale.dao.CreneauDAO;
import org.example.medicale.dao.DemandeExpertiseDAO;
import org.example.medicale.entity.Creneau;
import org.example.medicale.entity.DemandeExpertise;
import org.example.medicale.entity.User;
import org.example.medicale.enums.StatutExpertise;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/specialiste/dashboard")
public class SpecialisteServlet extends HttpServlet {

    private DemandeExpertiseDAO demandeExpertiseDAO;
    private CreneauDAO creneauDAO;

    @Override
    public void init() throws ServletException {
        demandeExpertiseDAO = new DemandeExpertiseDAO();
        creneauDAO = new CreneauDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        User specialiste = (User) session.getAttribute("utilisateur");

        if (specialiste == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            // Récupérer toutes les demandes du spécialiste
            List<DemandeExpertise> toutesLesDemandes = demandeExpertiseDAO.findBySpecialiste(specialiste.getId());

            // Demandes en attente avec Stream API
            List<DemandeExpertise> demandesEnAttente = toutesLesDemandes.stream()
                    .filter(d -> d.getStatut() == StatutExpertise.EN_ATTENTE)
                    .sorted((d1, d2) -> d2.getDateDemande().compareTo(d1.getDateDemande()))
                    .collect(Collectors.toList());

            long demandesEnAttenteCount = demandesEnAttente.size();

            // Les 5 dernières demandes en attente pour le dashboard
            List<DemandeExpertise> demandesRecentes = demandesEnAttente.stream()
                    .limit(5)
                    .collect(Collectors.toList());

            // Expertises du mois avec Stream API
            LocalDateTime debutMois = LocalDate.now().withDayOfMonth(1).atStartOfDay();
            long expertisesMoisCount = toutesLesDemandes.stream()
                    .filter(d -> d.getDateDemande().isAfter(debutMois))
                    .filter(d -> d.getStatut() == StatutExpertise.TERMINEE)
                    .count();

            // Créneaux disponibles avec Stream API
            List<Creneau> creneauxDisponibles = creneauDAO.findDisponiblesBySpecialiste(specialiste.getId());
            long creneauxDisponiblesCount = creneauxDisponibles.size();

            // Calcul des revenus du mois avec Stream API et Lambda
            double revenusMois = toutesLesDemandes.stream()
                    .filter(d -> d.getDateDemande().isAfter(debutMois))
                    .filter(d -> d.getStatut() == StatutExpertise.TERMINEE)
                    .map(d -> specialiste.getTarif())
                    .reduce(0.0, Double::sum);

            // Passer les données à la JSP
            request.setAttribute("utilisateur", specialiste);
            request.setAttribute("demandesEnAttenteCount", demandesEnAttenteCount);
            request.setAttribute("demandesRecentes", demandesRecentes);
            request.setAttribute("expertisesMoisCount", expertisesMoisCount);
            request.setAttribute("creneauxDisponiblesCount", creneauxDisponiblesCount);
            request.setAttribute("revenusMois", revenusMois);

            request.getRequestDispatcher("/views/specialiste/dashboard.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Erreur lors du chargement du dashboard");
            request.getRequestDispatcher("/views/specialiste/dashboard.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        if (demandeExpertiseDAO != null) {
            demandeExpertiseDAO.close();
        }
        if (creneauDAO != null) {
            creneauDAO.close();
        }
    }
}