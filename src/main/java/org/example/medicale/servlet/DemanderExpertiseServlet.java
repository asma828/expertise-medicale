package org.example.medicale.servlet;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.medicale.dao.ConsultationDAO;
import org.example.medicale.dao.CreneauDAO;
import org.example.medicale.dao.DemandeExpertiseDAO;
import org.example.medicale.dao.UserDAO;
import org.example.medicale.entity.Consultation;
import org.example.medicale.entity.Creneau;
import org.example.medicale.entity.DemandeExpertise;
import org.example.medicale.entity.User;
import org.example.medicale.enums.Role;
import org.example.medicale.enums.Specialite;
import org.example.medicale.enums.StatutConsultation;
import org.example.medicale.enums.StatutCreneau;
import org.example.medicale.enums.StatutExpertise;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@WebServlet("/generaliste/demanderExpertise")
public class DemanderExpertiseServlet extends HttpServlet {

    private ConsultationDAO consultationDAO;
    private UserDAO userDAO;
    private CreneauDAO creneauDAO;
    private DemandeExpertiseDAO demandeExpertiseDAO;
    private Gson gson;

    @Override
    public void init() throws ServletException {
        consultationDAO = new ConsultationDAO();
        userDAO = new UserDAO();
        creneauDAO = new CreneauDAO();
        demandeExpertiseDAO = new DemandeExpertiseDAO();
        gson = new Gson();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String action = request.getParameter("action");

        // Requêtes AJAX
        if ("getSpecialistes".equals(action)) {
            handleGetSpecialistes(request, response);
            return;
        }

        if ("getCreneaux".equals(action)) {
            handleGetCreneaux(request, response);
            return;
        }

        // Page principale
        HttpSession session = request.getSession();

        // Générer token CSRF
        if (session.getAttribute("csrfToken") == null) {
            String csrfToken = UUID.randomUUID().toString();
            session.setAttribute("csrfToken", csrfToken);
        }

        String consultationIdStr = request.getParameter("consultationId");

        System.out.println("=== DEBUG DemanderExpertise ===");
        System.out.println("consultationId parameter: " + consultationIdStr);

        if (consultationIdStr == null || consultationIdStr.trim().isEmpty()) {
            System.out.println("ERROR: consultationId is null or empty");
            session.setAttribute("error", "ID consultation manquant");
            response.sendRedirect(request.getContextPath() + "/generaliste/dashboard");
            return;
        }

        try {
            Long consultationId = Long.parseLong(consultationIdStr);
            System.out.println("Parsed consultationId: " + consultationId);

            Optional<Consultation> consultationOpt = consultationDAO.findById(consultationId);
            System.out.println("Consultation found: " + consultationOpt.isPresent());

            if (consultationOpt.isEmpty()) {
                System.out.println("ERROR: Consultation not found");
                session.setAttribute("error", "Consultation non trouvée");
                response.sendRedirect(request.getContextPath() + "/generaliste/dashboard");
                return;
            }

            Consultation consultation = consultationOpt.get();
            System.out.println("Consultation ID: " + consultation.getId());
            System.out.println("DemandeExpertise: " + consultation.getDemandeExpertise());

            // Vérifier que la consultation n'a pas déjà une demande d'expertise
            if (consultation.getDemandeExpertise() != null) {
                System.out.println("ERROR: Already has expertise request");
                session.setAttribute("error", "Cette consultation a déjà une demande d'expertise en cours");
                response.sendRedirect(request.getContextPath() + "/generaliste/consultation/" + consultationId);
                return;
            }

            System.out.println("Setting consultation attribute");
            request.setAttribute("consultation", consultation);
            request.setAttribute("specialites", Specialite.values());

            System.out.println("Forwarding to JSP...");
            request.getRequestDispatcher("/views/generaliste/demanderExpertise.jsp").forward(request, response);
            System.out.println("Forward completed");

        } catch (NumberFormatException e) {
            System.out.println("ERROR: NumberFormatException - " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "ID consultation invalide");
            response.sendRedirect(request.getContextPath() + "/generaliste/dashboard");
        } catch (Exception e) {
            System.out.println("ERROR: Exception - " + e.getMessage());
            e.printStackTrace();
            session.setAttribute("error", "Erreur: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/generaliste/dashboard");
        }
    }

    private void handleGetSpecialistes(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String specialiteStr = request.getParameter("specialite");

        if (specialiteStr == null || specialiteStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Specialite specialite = Specialite.valueOf(specialiteStr);

            // Récupérer tous les spécialistes avec Stream API
            List<User> specialistes = userDAO.findByRole(Role.SPECIALISTE).stream()
                    .filter(u -> u.getSpecialite() == specialite)
                    .filter(u -> u.getActif())
                    .sorted(Comparator.comparing(User::getTarif)) // Trier par tarif croissant
                    .collect(Collectors.toList());

            // Convertir en JSON
            List<Map<String, Object>> specialistesJson = specialistes.stream()
                    .map(s -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", s.getId());
                        map.put("nom", s.getNom());
                        map.put("prenom", s.getPrenom());
                        map.put("specialite", s.getSpecialite().getLabel());
                        map.put("tarif", s.getTarif());
                        map.put("dureeConsultation", s.getDureeConsultation());
                        return map;
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(specialistesJson));

        } catch (IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private void handleGetCreneaux(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        String specialisteIdStr = request.getParameter("specialisteId");

        if (specialisteIdStr == null || specialisteIdStr.trim().isEmpty()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            Long specialisteId = Long.parseLong(specialisteIdStr);

            // Récupérer les créneaux futurs avec Stream API
            List<Creneau> creneaux = creneauDAO.findBySpecialiste(specialisteId).stream()
                    .filter(c -> c.getDateHeure().isAfter(LocalDateTime.now()))
                    .sorted(Comparator.comparing(Creneau::getDateHeure))
                    .limit(20) // Limiter à 20 créneaux
                    .collect(Collectors.toList());

            // Convertir en JSON
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            List<Map<String, Object>> creneauxJson = creneaux.stream()
                    .map(c -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", c.getId());
                        map.put("heureFormatee", c.getHeureFormatee());
                        map.put("dateFormatee", c.getDateHeure().format(dateFormatter));
                        map.put("statut", c.getStatut().toString());
                        map.put("disponible", c.estDisponible());
                        return map;
                    })
                    .collect(Collectors.toList());

            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(gson.toJson(creneauxJson));

        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
            response.sendRedirect(request.getContextPath() + "/generaliste/dashboard");
            return;
        }

        // Récupérer le médecin connecté
        User medecin = (User) session.getAttribute("utilisateur");
        if (medecin == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Récupérer les paramètres
        String consultationIdStr = request.getParameter("consultationId");
        String specialisteIdStr = request.getParameter("specialisteId");
        String creneauIdStr = request.getParameter("creneauId");
        String questionPosee = request.getParameter("questionPosee");
        String donneesAnalyses = request.getParameter("donneesAnalyses");
        String prioriteStr = request.getParameter("priorite");

        // Validation
        if (consultationIdStr == null || specialisteIdStr == null || creneauIdStr == null ||
                questionPosee == null || prioriteStr == null ||
                questionPosee.trim().isEmpty()) {
            session.setAttribute("error", "Tous les champs obligatoires doivent être remplis");
            response.sendRedirect(request.getContextPath() + "/generaliste/demanderExpertise?consultationId=" + consultationIdStr);
            return;
        }

        try {
            Long consultationId = Long.parseLong(consultationIdStr);
            Long specialisteId = Long.parseLong(specialisteIdStr);
            Long creneauId = Long.parseLong(creneauIdStr);

            // Récupérer les entités
            Optional<Consultation> consultationOpt = consultationDAO.findById(consultationId);
            Optional<User> specialisteOpt = userDAO.findById(specialisteId);
            Optional<Creneau> creneauOpt = creneauDAO.findById(creneauId);

            if (consultationOpt.isEmpty() || specialisteOpt.isEmpty() || creneauOpt.isEmpty()) {
                session.setAttribute("error", "Données invalides");
                response.sendRedirect(request.getContextPath() + "/generaliste/dashboard");
                return;
            }

            Consultation consultation = consultationOpt.get();
            User specialiste = specialisteOpt.get();
            Creneau creneau = creneauOpt.get();

            // Vérifier que le créneau est disponible
            if (!creneau.estDisponible()) {
                session.setAttribute("error", "Ce créneau n'est plus disponible");
                response.sendRedirect(request.getContextPath() + "/generaliste/demanderExpertise?consultationId=" + consultationId);
                return;
            }

            // Créer la demande d'expertise
            DemandeExpertise demande = new DemandeExpertise();
            demande.setConsultation(consultation);
            demande.setMedecinDemandeur(medecin);
            demande.setSpecialiste(specialiste);
            demande.setCreneau(creneau);
            demande.setQuestionPosee(questionPosee.trim());
            demande.setDonneesAnalyses(donneesAnalyses != null ? donneesAnalyses.trim() : null);
            demande.setStatut(StatutExpertise.EN_ATTENTE);
            demande.setDateDemande(LocalDateTime.now());

            // Sauvegarder
            demandeExpertiseDAO.save(demande);

            // Mettre à jour le statut de la consultation
            consultation.setStatut(StatutConsultation.EN_ATTENTE_AVIS_SPECIALISTE);
            consultationDAO.save(consultation);

            // Réserver le créneau
            creneau.setStatut(StatutCreneau.RESERVE);
            creneauDAO.save(creneau);

            session.setAttribute("successMessage", "Demande d'expertise créée avec succès! Le spécialiste a été notifié.");
            response.sendRedirect(request.getContextPath() + "/generaliste/consultation/" + consultationId);

        } catch (NumberFormatException e) {
            session.setAttribute("error", "Données invalides");
            response.sendRedirect(request.getContextPath() + "/generaliste/dashboard");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Erreur: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/generaliste/dashboard");
        }
    }

    @Override
    public void destroy() {
        if (consultationDAO != null) consultationDAO.close();
        if (userDAO != null) userDAO.close();
        if (creneauDAO != null) creneauDAO.close();
        if (demandeExpertiseDAO != null) demandeExpertiseDAO.close();
    }
}