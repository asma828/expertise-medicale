package org.example.medicale.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.medicale.dao.UserDAO;
import org.example.medicale.entity.User;
import org.example.medicale.enums.Specialite;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/specialiste/ConfigurerProfil")
public class ConfigurerProfilServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() throws ServletException {
        userDAO = new UserDAO();
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

        request.setAttribute("utilisateur", specialiste);
        request.setAttribute("specialites", Specialite.values());

        request.getRequestDispatcher("/views/specialiste/ConfigurerProfil.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Vérification CSRF
        String csrfToken = request.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");

        if (csrfToken == null || !csrfToken.equals(sessionToken)) {
            request.setAttribute("error", "Erreur de sécurité");
            doGet(request, response);
            return;
        }

        User specialiste = (User) session.getAttribute("utilisateur");
        if (specialiste == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        // Récupérer les paramètres
        String specialiteStr = request.getParameter("specialite");
        String tarifStr = request.getParameter("tarif");
        String dureeConsultationStr = request.getParameter("dureeConsultation");

        // Validation
        if (specialiteStr == null || tarifStr == null ||
                specialiteStr.trim().isEmpty() || tarifStr.trim().isEmpty()) {
            request.setAttribute("error", "Tous les champs obligatoires doivent être remplis");
            doGet(request, response);
            return;
        }

        try {
            Specialite specialite = Specialite.valueOf(specialiteStr);
            Double tarif = Double.parseDouble(tarifStr);
            Integer dureeConsultation = dureeConsultationStr != null && !dureeConsultationStr.isEmpty()
                    ? Integer.parseInt(dureeConsultationStr)
                    : 30;

            // Validation du tarif
            if (tarif < 0) {
                request.setAttribute("error", "Le tarif ne peut pas être négatif");
                doGet(request, response);
                return;
            }

            // Mettre à jour le profil
            specialiste.setSpecialite(specialite);
            specialiste.setTarif(tarif);
            specialiste.setDureeConsultation(dureeConsultation);

            userDAO.save(specialiste);

            // Mettre à jour la session
            session.setAttribute("utilisateur", specialiste);

            session.setAttribute("successMessage", "Profil mis à jour avec succès!");
            response.sendRedirect(request.getContextPath() + "/specialiste/dashboard");

        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Spécialité invalide");
            doGet(request, response);
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Erreur lors de la mise à jour: " + e.getMessage());
            doGet(request, response);
        }
    }

    @Override
    public void destroy() {
        if (userDAO != null) {
            userDAO.close();
        }
    }
}