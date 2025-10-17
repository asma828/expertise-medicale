package org.example.medicale.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.medicale.dao.CreneauDAO;
import org.example.medicale.entity.Creneau;
import org.example.medicale.entity.User;
import org.example.medicale.enums.StatutCreneau;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/specialiste/mescreneaux")
public class CreneauxServlet extends HttpServlet {

    private CreneauDAO creneauDAO;

    @Override
    public void init() throws ServletException {
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

        // Générer token CSRF
        if (session.getAttribute("csrfToken") == null) {
            String csrfToken = UUID.randomUUID().toString();
            session.setAttribute("csrfToken", csrfToken);
        }

        try {
            // Récupérer tous les créneaux du spécialiste
            List<Creneau> creneaux = creneauDAO.findBySpecialiste(specialiste.getId());

            // Grouper par date avec Stream API
            Map<LocalDate, List<Creneau>> creneauxParDate = creneaux.stream()
                    .filter(c -> c.getDateHeure().toLocalDate().isAfter(LocalDate.now().minusDays(1)))
                    .collect(Collectors.groupingBy(
                            c -> c.getDateHeure().toLocalDate(),
                            TreeMap::new,
                            Collectors.collectingAndThen(
                                    Collectors.toList(),
                                    list -> list.stream()
                                            .sorted(Comparator.comparing(Creneau::getDateHeure))
                                            .collect(Collectors.toList())
                            )
                    ));

            // Statistiques avec Stream API
            long disponiblesCount = creneaux.stream()
                    .filter(Creneau::estDisponible)
                    .count();

            long reservesCount = creneaux.stream()
                    .filter(c -> c.getStatut() == StatutCreneau.RESERVE)
                    .count();

            long passesCount = creneaux.stream()
                    .filter(Creneau::estPasse)
                    .count();

            // Date minimale pour le formulaire (aujourd'hui)
            String dateMinimale = LocalDate.now().format(DateTimeFormatter.ISO_DATE);

            // Passer les données à la JSP
            request.setAttribute("creneauxParDate", creneauxParDate);
            request.setAttribute("disponiblesCount", disponiblesCount);
            request.setAttribute("reservesCount", reservesCount);
            request.setAttribute("passesCount", passesCount);
            request.setAttribute("dateMinimale", dateMinimale);

            request.getRequestDispatcher("/views/specialiste/mescreneaux.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement des créneaux");
            request.getRequestDispatcher("/views/specialiste/mescreneaux.jsp").forward(request, response);
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
            session.setAttribute("error", "Erreur de sécurité");
            doGet(request, response);
            return;
        }

        User specialiste = (User) session.getAttribute("utilisateur");
        if (specialiste == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("ajouter".equals(action)) {
                handleAjouterCreneaux(request, response, specialiste, session);
            } else if ("supprimer".equals(action)) {
                handleSupprimerCreneau(request, response, session);
            } else {
                session.setAttribute("error", "Action non reconnue");
                doGet(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Erreur: " + e.getMessage());
            doGet(request, response);
        }
    }

    private void handleAjouterCreneaux(HttpServletRequest request, HttpServletResponse response,
                                       User specialiste, HttpSession session)
            throws ServletException, IOException {

        String dateStr = request.getParameter("date");
        String heureDebutStr = request.getParameter("heureDebut");
        String heureFinStr = request.getParameter("heureFin");
        String dureeStr = request.getParameter("duree");

        // Validation
        if (dateStr == null || heureDebutStr == null || heureFinStr == null ||
                dateStr.trim().isEmpty() || heureDebutStr.trim().isEmpty() || heureFinStr.trim().isEmpty()) {
            session.setAttribute("error", "Tous les champs sont obligatoires");
            doGet(request, response);
            return;
        }

        try {
            LocalDate date = LocalDate.parse(dateStr);
            LocalTime heureDebut = LocalTime.parse(heureDebutStr);
            LocalTime heureFin = LocalTime.parse(heureFinStr);
            int duree = dureeStr != null && !dureeStr.isEmpty() ? Integer.parseInt(dureeStr) : 30;

            // Validation de la date
            if (date.isBefore(LocalDate.now())) {
                session.setAttribute("error", "La date ne peut pas être dans le passé");
                doGet(request, response);
                return;
            }

            // Validation des heures
            if (heureFin.isBefore(heureDebut) || heureFin.equals(heureDebut)) {
                session.setAttribute("error", "L'heure de fin doit être après l'heure de début");
                doGet(request, response);
                return;
            }

            // Créer les créneaux
            LocalTime currentTime = heureDebut;
            int creneauxCrees = 0;

            while (currentTime.plusMinutes(duree).isBefore(heureFin) ||
                    currentTime.plusMinutes(duree).equals(heureFin)) {

                LocalDateTime dateHeure = LocalDateTime.of(date, currentTime);

                // Créer le créneau
                Creneau creneau = new Creneau(specialiste, dateHeure, duree);
                creneau.setStatut(StatutCreneau.DISPONIBLE);

                creneauDAO.save(creneau);
                creneauxCrees++;

                currentTime = currentTime.plusMinutes(duree);
            }

            session.setAttribute("successMessage",
                    creneauxCrees + " créneau(x) créé(s) avec succès pour le " +
                            date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            response.sendRedirect(request.getContextPath() + "/specialiste/mescreneaux");

        } catch (Exception e) {
            session.setAttribute("error", "Erreur lors de la création des créneaux: " + e.getMessage());
            doGet(request, response);
        }
    }

    private void handleSupprimerCreneau(HttpServletRequest request, HttpServletResponse response,
                                        HttpSession session)
            throws ServletException, IOException {

        String creneauIdStr = request.getParameter("creneauId");

        if (creneauIdStr == null || creneauIdStr.trim().isEmpty()) {
            session.setAttribute("error", "ID créneau manquant");
            doGet(request, response);
            return;
        }

        try {
            Long creneauId = Long.parseLong(creneauIdStr);
            Optional<Creneau> creneauOpt = creneauDAO.findById(creneauId);

            if (creneauOpt.isEmpty()) {
                session.setAttribute("error", "Créneau non trouvé");
                doGet(request, response);
                return;
            }

            Creneau creneau = creneauOpt.get();

            // Vérifier que le créneau est disponible (pas réservé)
            if (creneau.getStatut() != StatutCreneau.DISPONIBLE) {
                session.setAttribute("error", "Impossible de supprimer un créneau réservé ou passé");
                doGet(request, response);
                return;
            }

            // Supprimer le créneau
            creneauDAO.delete(creneauId);

            session.setAttribute("successMessage", "Créneau supprimé avec succès");
            response.sendRedirect(request.getContextPath() + "/specialiste/mescreneaux");

        } catch (NumberFormatException e) {
            session.setAttribute("error", "ID créneau invalide");
            doGet(request, response);
        }
    }

    @Override
    public void destroy() {
        if (creneauDAO != null) {
            creneauDAO.close();
        }
    }
}