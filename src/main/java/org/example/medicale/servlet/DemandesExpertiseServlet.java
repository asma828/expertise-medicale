package org.example.medicale.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.medicale.dao.DemandeExpertiseDAO;
import org.example.medicale.entity.DemandeExpertise;
import org.example.medicale.entity.User;
import org.example.medicale.enums.StatutExpertise;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@WebServlet("/specialiste/demandesexpertise")
public class DemandesExpertiseServlet extends HttpServlet {

    private DemandeExpertiseDAO demandeExpertiseDAO;

    @Override
    public void init() throws ServletException {
        demandeExpertiseDAO = new DemandeExpertiseDAO();
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
            // Récupérer toutes les demandes avec Stream API
            List<DemandeExpertise> demandes = demandeExpertiseDAO.findBySpecialiste(specialiste.getId()).stream()
                    .sorted((d1, d2) -> {
                        // Trier: EN_ATTENTE d'abord, puis par date décroissante
                        if (d1.getStatut() == StatutExpertise.EN_ATTENTE && d2.getStatut() != StatutExpertise.EN_ATTENTE) {
                            return -1;
                        }
                        if (d1.getStatut() != StatutExpertise.EN_ATTENTE && d2.getStatut() == StatutExpertise.EN_ATTENTE) {
                            return 1;
                        }
                        return d2.getDateDemande().compareTo(d1.getDateDemande());
                    })
                    .collect(Collectors.toList());

            // Statistiques avec Stream API
            long enAttenteCount = demandes.stream()
                    .filter(d -> d.getStatut() == StatutExpertise.EN_ATTENTE)
                    .count();

            long termineesCount = demandes.stream()
                    .filter(d -> d.getStatut() == StatutExpertise.TERMINEE)
                    .count();

            // Passer les données à la JSP
            request.setAttribute("demandes", demandes);
            request.setAttribute("enAttenteCount", enAttenteCount);
            request.setAttribute("termineesCount", termineesCount);
            request.setAttribute("utilisateur", specialiste);

            request.getRequestDispatcher("/views/specialiste/demandesexpertise.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement des demandes");
            request.getRequestDispatcher("/views/specialiste/demandesexpertise.jsp").forward(request, response);
        }
    }

    @Override
    public void destroy() {
        if (demandeExpertiseDAO != null) {
            demandeExpertiseDAO.close();
        }
    }
}