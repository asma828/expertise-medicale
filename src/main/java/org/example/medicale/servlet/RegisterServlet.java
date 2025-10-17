package org.example.medicale.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import org.example.medicale.dao.UserDAO;
import org.example.medicale.entity.User;
import org.example.medicale.enums.Role;
import org.example.medicale.enums.Specialite;
import org.example.medicale.util.CSRFTokenUtil;
import org.example.medicale.util.PasswordUtils;

import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {

    private UserDAO userDAO;

    @Override
    public void init() {
        userDAO = new UserDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
//        // Générer et stocker un token CSRF
        String csrfToken = CSRFTokenUtil.generateToken();
       request.getSession().setAttribute("csrfToken", csrfToken);
        request.setAttribute("specialites", Specialite.values());

        // Afficher le formulaire d'inscription
        request.getRequestDispatcher("/views/register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // CSRF validation
        String csrfToken = request.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");

        if (csrfToken == null || !csrfToken.equals(sessionToken)) {
            request.setAttribute("error", "Erreur de sécurité. Veuillez réessayer.");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }

        // Get form parameters
        String email = request.getParameter("email");
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String motDePasse = request.getParameter("motDePasse");
        String motDePasseConfirm = request.getParameter("motDePasseConfirm");
        String roleStr = request.getParameter("role");
        String specialiteStr = request.getParameter("specialite");
        String tarifStr = request.getParameter("tarif");

        // Validate required fields
        if (email == null || email.isBlank() ||
                nom == null || nom.isBlank() ||
                prenom == null || prenom.isBlank() ||
                motDePasse == null || motDePasse.isBlank() ||
                motDePasseConfirm == null || motDePasseConfirm.isBlank() ||
                roleStr == null || roleStr.isBlank()) {

            request.setAttribute("error", "Tous les champs obligatoires doivent être remplis.");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }

        // Password confirmation
        if (!motDePasse.equals(motDePasseConfirm)) {
            request.setAttribute("error", "Les mots de passe ne correspondent pas.");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }

        // Check if email already exists
        if (userDAO.findByEmail(email).isPresent()) {
            request.setAttribute("error", "Cet email est déjà utilisé.");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }

        // Hash password
        String hashedPassword = PasswordUtils.hashPassword(motDePasse);

        // Parse role
        Role role;
        try {
            role = Role.valueOf(roleStr);
        } catch (IllegalArgumentException e) {
            request.setAttribute("error", "Rôle invalide.");
            request.getRequestDispatcher("/views/register.jsp").forward(request, response);
            return;
        }

        // Create User
        User user = new User();
        user.setEmail(email.trim());
        user.setMotDePasse(hashedPassword);
        user.setNom(nom.trim());
        user.setPrenom(prenom.trim());
        user.setRole(role);
        user.setActif(true); // default
        user.setDureeConsultation(30); // default for all roles

        // If specialist, parse and set specialty and tarif
        if (role == Role.SPECIALISTE) {
            if (specialiteStr != null && !specialiteStr.isBlank()) {
                try {
                    Specialite specialite = Specialite.valueOf(specialiteStr);
                    user.setSpecialite(specialite);
                } catch (IllegalArgumentException e) {
                    request.setAttribute("error", "Spécialité invalide.");
                    request.getRequestDispatcher("/views/register.jsp").forward(request, response);
                    return;
                }
            } else {
                request.setAttribute("error", "La spécialité est requise pour les spécialistes.");
                request.getRequestDispatcher("/views/register.jsp").forward(request, response);
                return;
            }

            if (tarifStr != null && !tarifStr.isBlank()) {
                try {
                    double tarif = Double.parseDouble(tarifStr);
                    user.setTarif(tarif);
                } catch (NumberFormatException e) {
                    request.setAttribute("error", "Le tarif doit être un nombre valide.");
                    request.getRequestDispatcher("/views/register.jsp").forward(request, response);
                    return;
                }
            } else {
                request.setAttribute("error", "Le tarif est requis pour les spécialistes.");
                request.getRequestDispatcher("/views/register.jsp").forward(request, response);
                return;
            }
        }

        // Save user
        userDAO.save(user);

        // Invalidate old token and generate new
        String newToken = CSRFTokenUtil.generateToken();
        session.setAttribute("csrfToken", newToken);

        // Redirect to login with success message (can also use a flash attribute system if available)
        request.setAttribute("success", "Inscription réussie. Vous pouvez maintenant vous connecter.");
        request.getRequestDispatcher("/views/login.jsp").forward(request, response);
    }

    @Override
    public void destroy() {
        if (userDAO != null) {
            userDAO.close();
        }
    }
}
