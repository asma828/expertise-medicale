package org.example.medicale.servlet;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.medicale.dao.UserDAO;
import org.example.medicale.entity.User;
import org.example.medicale.util.CSRFTokenUtil;
import org.example.medicale.util.PasswordUtils;


import java.io.IOException;
import java.util.Optional;

@WebServlet(name = "AuthServlet", urlPatterns = {"/login", "/logout"})
public class AuthServlet extends HttpServlet {

    private UserDAO utilisateurDAO;

    @Override
    public void init() throws ServletException {
        utilisateurDAO = new UserDAO();
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/logout".equals(path)) {
            handleLogout(request, response);
        } else {
            // Afficher la page de login
            String csrfToken = CSRFTokenUtil.generateToken();
            HttpSession session = request.getSession();
            session.setAttribute("csrfToken", csrfToken);

            // Use forward instead of redirect
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();

        if ("/login".equals(path)) {
            handleLogin(request, response);
        }
    }

    private void handleLogin(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Vérification CSRF
        String csrfToken = request.getParameter("csrfToken");
        HttpSession session = request.getSession();
        String sessionToken = (String) session.getAttribute("csrfToken");

        if (csrfToken == null || !csrfToken.equals(sessionToken)) {
            request.setAttribute("error", "Erreur de sécurité. Veuillez réessayer.");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validation des champs
        if (email == null || email.trim().isEmpty() ||
                password == null || password.trim().isEmpty()) {
            request.setAttribute("error", "Email et mot de passe sont obligatoires");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        // Recherche de l'utilisateur
        Optional<User> utilisateurOpt = utilisateurDAO.findByEmail(email.trim());

        if (utilisateurOpt.isEmpty()) {
            request.setAttribute("error", "Email ou mot de passe incorrect");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        User utilisateur = utilisateurOpt.get();

        // Vérification du mot de passe avec BCrypt
        if (!PasswordUtils.verifyPassword(password, utilisateur.getMotDePasse())) {
            request.setAttribute("error", "Email ou mot de passe incorrect");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        // Vérification si l'utilisateur est actif
        if (!utilisateur.getActif()) {
            request.setAttribute("error", "Votre compte a été désactivé");
            request.getRequestDispatcher("/views/login.jsp").forward(request, response);
            return;
        }

        // Authentification réussie
        session.setAttribute("utilisateur", utilisateur);
        session.setAttribute("userId", utilisateur.getId());
        session.setAttribute("userRole", utilisateur.getRole().name());
        session.setAttribute("userName", utilisateur.getNomComplet());

        // Générer un nouveau token CSRF
        String newToken = CSRFTokenUtil.generateToken();
        session.setAttribute("csrfToken", newToken);

        // Redirection selon le rôle
        String redirectUrl = getRedirectUrlByRole(utilisateur);
        response.sendRedirect(request.getContextPath() + redirectUrl);
    }

    private void handleLogout(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        response.sendRedirect(request.getContextPath() + "/login");
    }

    private String getRedirectUrlByRole(User utilisateur) {
        switch (utilisateur.getRole()) {
            case INFIRMIER:
                return "/infirmier/dashboard";
            case GENERALISTE:
                return "/generaliste/dashboard";
            case SPECIALISTE:
                return "/specialiste/dashboard";
            default:
                return "/login";
        }
    }

    @Override
    public void destroy() {
        if (utilisateurDAO != null) {
            utilisateurDAO.close();
        }
    }
}
