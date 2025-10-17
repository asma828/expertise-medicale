package org.example.medicale.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.medicale.dao.CreneauDAO;
import org.example.medicale.dao.DemandeExpertiseDAO;
import org.example.medicale.entity.DemandeExpertise;
import org.example.medicale.entity.User;
import org.example.medicale.enums.StatutExpertise;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/specialiste/statistiques")
public class StatistiquesServlet extends HttpServlet {

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
            String periode = request.getParameter("periode");
            if (periode == null || periode.isEmpty()) {
                periode = "mois";
            }

            // Récupérer toutes les demandes
            List<DemandeExpertise> toutesLesDemandes = demandeExpertiseDAO.findBySpecialiste(specialiste.getId());

            // Filtrer selon la période
            LocalDateTime dateDebut = getDateDebut(periode);
            List<DemandeExpertise> demandesPeriode = toutesLesDemandes.stream()
                    .filter(d -> d.getDateDemande().isAfter(dateDebut))
                    .collect(Collectors.toList());

            // Calculer les statistiques
            Map<String, Object> stats = new HashMap<>();

            // Stats de base
            long totalExpertises = demandesPeriode.size();
            long expertisesTerminees = demandesPeriode.stream()
                    .filter(d -> d.getStatut() == StatutExpertise.TERMINEE)
                    .count();
            long expertisesEnAttente = demandesPeriode.stream()
                    .filter(d -> d.getStatut() == StatutExpertise.EN_ATTENTE)
                    .count();

            // Revenus
            double revenuTotal = expertisesTerminees * specialiste.getTarif();

            // Taux de réponse
            int tauxReponse = totalExpertises > 0 ?
                    (int) ((expertisesTerminees * 100.0) / totalExpertises) : 0;

            // Temps moyen de réponse (en heures)
            double tempsMoyenReponse = demandesPeriode.stream()
                    .filter(d -> d.getStatut() == StatutExpertise.TERMINEE && d.getDateReponse() != null)
                    .mapToLong(d -> Duration.between(d.getDateDemande(), d.getDateReponse()).toHours())
                    .average()
                    .orElse(0);

            // Créneaux disponibles
            long creneauxDisponibles = creneauDAO.findDisponiblesBySpecialiste(specialiste.getId()).size();

            // Revenu moyen par jour
            long joursActivite = Duration.between(dateDebut, LocalDateTime.now()).toDays();
            double revenuMoyenParJour = joursActivite > 0 ? revenuTotal / joursActivite : 0;

            // Projection mensuelle
            double projectionMensuelle = revenuMoyenParJour * 30;

            // Score de réactivité (10 si < 24h, décroissant après)
            int scoreReactivite = tempsMoyenReponse <= 24 ? 10 :
                    Math.max(1, (int) (10 - (tempsMoyenReponse - 24) / 24));

            // Top médecins demandeurs avec Stream API
            List<Map<String, Object>> topMedecins = demandesPeriode.stream()
                    .collect(Collectors.groupingBy(
                            d -> d.getMedecinDemandeur(),
                            Collectors.counting()
                    ))
                    .entrySet().stream()
                    .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
                    .limit(5)
                    .map(entry -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("nom", entry.getKey().getNomComplet());
                        map.put("email", entry.getKey().getEmail());
                        map.put("nombreDemandes", entry.getValue());
                        return map;
                    })
                    .collect(Collectors.toList());

            // Évolution mensuelle (derniers 6 mois)
            List<Map<String, Object>> evolutionMensuelle = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM yyyy", Locale.FRENCH);

            for (int i = 5; i >= 0; i--) {
                LocalDate mois = LocalDate.now().minusMonths(i);
                LocalDateTime debutMois = mois.withDayOfMonth(1).atStartOfDay();
                LocalDateTime finMois = mois.withDayOfMonth(mois.lengthOfMonth()).atTime(23, 59, 59);

                long nombreExpertises = toutesLesDemandes.stream()
                        .filter(d -> d.getStatut() == StatutExpertise.TERMINEE)
                        .filter(d -> d.getDateDemande().isAfter(debutMois) &&
                                d.getDateDemande().isBefore(finMois))
                        .count();

                double revenuMois = nombreExpertises * specialiste.getTarif();

                Map<String, Object> moisData = new HashMap<>();
                moisData.put("libelle", mois.format(formatter));
                moisData.put("nombre", nombreExpertises);
                moisData.put("revenu", (int) revenuMois);
                moisData.put("pourcentage", totalExpertises > 0 ?
                        (int) ((nombreExpertises * 100.0) / totalExpertises) : 0);

                evolutionMensuelle.add(moisData);
            }

            // Progression vs mois précédent
            long expertisesMoisActuel = demandesPeriode.stream()
                    .filter(d -> d.getDateDemande().isAfter(LocalDate.now().withDayOfMonth(1).atStartOfDay()))
                    .filter(d -> d.getStatut() == StatutExpertise.TERMINEE)
                    .count();

            long expertisesMoisPrecedent = toutesLesDemandes.stream()
                    .filter(d -> d.getDateDemande().isAfter(
                            LocalDate.now().minusMonths(1).withDayOfMonth(1).atStartOfDay()))
                    .filter(d -> d.getDateDemande().isBefore(
                            LocalDate.now().withDayOfMonth(1).atStartOfDay()))
                    .filter(d -> d.getStatut() == StatutExpertise.TERMINEE)
                    .count();

            int progression = expertisesMoisPrecedent > 0 ?
                    (int) (((expertisesMoisActuel - expertisesMoisPrecedent) * 100.0) / expertisesMoisPrecedent) : 0;

            // Rang fictif (pourrait être calculé en comparant avec d'autres spécialistes)
            int rang = 1;

            // Remplir le map stats
            stats.put("totalExpertises", totalExpertises);
            stats.put("expertisesTerminees", expertisesTerminees);
            stats.put("expertisesEnAttente", expertisesEnAttente);
            stats.put("revenuTotal", (int) revenuTotal);
            stats.put("tauxReponse", tauxReponse);
            stats.put("tempsMoyenReponse", tempsMoyenReponse);
            stats.put("creneauxDisponibles", creneauxDisponibles);
            stats.put("revenuMoyenParJour", (int) revenuMoyenParJour);
            stats.put("projectionMensuelle", (int) projectionMensuelle);
            stats.put("scoreReactivite", scoreReactivite);
            stats.put("rang", rang);
            stats.put("progression", progression);
            stats.put("topMedecins", topMedecins);
            stats.put("evolutionMensuelle", evolutionMensuelle);

            request.setAttribute("stats", stats);
            request.setAttribute("utilisateur", specialiste);

            request.getRequestDispatcher("/views/specialiste/statistiques.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement des statistiques");
            request.getRequestDispatcher("/views/specialiste/statistiques.jsp").forward(request, response);
        }
    }

    private LocalDateTime getDateDebut(String periode) {
        switch (periode) {
            case "mois":
                return LocalDate.now().withDayOfMonth(1).atStartOfDay();
            case "annee":
                return LocalDate.now().withDayOfYear(1).atStartOfDay();
            case "total":
                return LocalDateTime.of(2000, 1, 1, 0, 0);
            default:
                return LocalDate.now().withDayOfMonth(1).atStartOfDay();
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