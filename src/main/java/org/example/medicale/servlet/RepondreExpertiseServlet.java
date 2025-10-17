package org.example.medicale.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.medicale.dao.ConsultationDAO;
import org.example.medicale.dao.DemandeExpertiseDAO;
import org.example.medicale.entity.DemandeExpertise;
import org.example.medicale.entity.User;
import org.example.medicale.enums.StatutConsultation;
import org.example.medicale.enums.StatutExpertise;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@WebServlet("/specialiste/expertise/*")
public class RepondreExpertiseServlet extends HttpServlet {

    private DemandeExpertiseDAO demandeExpertiseDAO;
    private ConsultationDAO consultationDAO;

    @Override
    public void init() throws ServletException {
        demandeExpertiseDAO = new DemandeExpertiseDAO();
        consultationDAO = new ConsultationDAO();
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

        // Extraire l'ID de la demande de l'URL
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/specialiste/demandesexpertise");
            return;
        }

        try {
            Long demandeId = Long.parseLong(pathInfo.substring(1));
            Optional<DemandeExpertise> demandeOpt = demandeExpertiseDAO.findById(demandeId);

            if (demandeOpt.isPresent()) {
                DemandeExpertise demande = demandeOpt.get();

                // Vérifier que c'est bien le spécialiste concerné
                if (!demande.getSpecialiste().getId().equals(specialiste.getId())) {
                    session.setAttribute("error", "Vous n'êtes pas autorisé à consulter cette demande");
                    response.sendRedirect(request.getContextPath() + "/specialiste/demandesexpertise");
                    return;
                }

                request.setAttribute("demande", demande);
                request.setAttribute("utilisateur", specialiste);
            } else {
                request.setAttribute("error", "Demande non trouvée");
            }

            request.getRequestDispatcher("/views/specialiste/repondreexpertise.jsp").forward(request, response);

        } catch (NumberFormatException e) {
            request.setAttribute("error", "ID demande invalide");
            request.getRequestDispatcher("/views/specialiste/repondreexpertise.jsp").forward(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors du chargement de la demande");
            request.getRequestDispatcher("/views/specialiste/repondreexpertise.jsp").forward(request, response);
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

        // Extraire l'ID de la demande
        String pathInfo = request.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            response.sendRedirect(request.getContextPath() + "/specialiste/demandesexpertise");
            return;
        }

        try {
            Long demandeId = Long.parseLong(pathInfo.substring(1));
            Optional<DemandeExpertise> demandeOpt = demandeExpertiseDAO.findById(demandeId);

            if (demandeOpt.isEmpty()) {
                session.setAttribute("error", "Demande non trouvée");
                response.sendRedirect(request.getContextPath() + "/specialiste/demandesexpertise");
                return;
            }

            DemandeExpertise demande = demandeOpt.get();

            // Vérifier que c'est bien le spécialiste concerné
            if (!demande.getSpecialiste().getId().equals(specialiste.getId())) {
                session.setAttribute("error", "Vous n'êtes pas autorisé à répondre à cette demande");
                response.sendRedirect(request.getContextPath() + "/specialiste/demandesexpertise");
                return;
            }

            // Vérifier que la demande est en attente
            if (demande.getStatut() != StatutExpertise.EN_ATTENTE) {
                session.setAttribute("error", "Cette demande a déjà été traitée");
                response.sendRedirect(request.getContextPath() + "/specialiste/expertise/" + demandeId);
                return;
            }

            // Récupérer les paramètres
            String avisMedical = request.getParameter("avisMedical");
            String recommandations = request.getParameter("recommandations");

            // Validation
            if (avisMedical == null || recommandations == null ||
                    avisMedical.trim().isEmpty() || recommandations.trim().isEmpty()) {
                request.setAttribute("error", "Tous les champs sont obligatoires");
                doGet(request, response);
                return;
            }

            // Enregistrer la réponse
            demande.setAvisMedical(avisMedical.trim());
            demande.setRecommandations(recommandations.trim());
            demande.setStatut(StatutExpertise.TERMINEE);
            demande.setDateReponse(LocalDateTime.now());

            demandeExpertiseDAO.save(demande);

            // Mettre à jour le statut de la consultation (retour à EN_COURS)
            demande.getConsultation().setStatut(StatutConsultation.EN_COURS);
            consultationDAO.save(demande.getConsultation());

            session.setAttribute("successMessage", "Votre avis a été envoyé avec succès! Rémunération: " +
                    specialiste.getTarif() + " DH");
            response.sendRedirect(request.getContextPath() + "/specialiste/demandesexpertise");

        } catch (NumberFormatException e) {
            session.setAttribute("error", "ID demande invalide");
            response.sendRedirect(request.getContextPath() + "/specialiste/demandesexpertise");
        } catch (Exception e) {
            e.printStackTrace();
            session.setAttribute("error", "Erreur lors de l'envoi de la réponse: " + e.getMessage());
            doGet(request, response);
        }
    }

    @Override
    public void destroy() {
        if (demandeExpertiseDAO != null) {
            demandeExpertiseDAO.close();
        }
        if (consultationDAO != null) {
            consultationDAO.close();
        }
    }
}