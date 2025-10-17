package org.example.medicale.filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.medicale.entity.User;
import org.example.medicale.enums.Role;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@WebFilter("/*")
public class AuthenticationFilter implements Filter {

    // Pages publiques accessibles sans authentification
    private static final List<String> PUBLIC_URLS = Arrays.asList(
            "/login",
            "/register",
            "/index.jsp",
            "/",
            "/css/",
            "/js/",
            "/images/",
            "/assets/"
    );

    // Pages à rediriger si l'utilisateur est déjà connecté
    private static final List<String> AUTH_PAGES = Arrays.asList(
            "/login",
            "/register"
    );

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        // Initialisation si nécessaire
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        String uri = httpRequest.getRequestURI();
        String contextPath = httpRequest.getContextPath();
        String path = uri.substring(contextPath.length());

        HttpSession session = httpRequest.getSession(false);
        User user = null;
        String userRole = null;

        if (session != null) {
            user = (User) session.getAttribute("utilisateur");
            userRole = (String) session.getAttribute("userRole");
        }

        boolean isLoggedIn = (user != null && userRole != null);

        // 1. Vérifier si c'est une ressource publique
        if (isPublicResource(path)) {
            // Si l'utilisateur est connecté et essaie d'accéder à login/register
            if (isLoggedIn && isAuthPage(path)) {
                redirectToDashboard(httpResponse, contextPath, userRole);
                return;
            }
            chain.doFilter(request, response);
            return;
        }

        // 2. Vérifier si l'utilisateur est connecté
        if (!isLoggedIn) {
            httpResponse.sendRedirect(contextPath + "/login");
            return;
        }

        // 3. Vérifier les autorisations basées sur les rôles
        if (!isAuthorized(path, userRole)) {
            // Rediriger vers le dashboard approprié
            redirectToDashboard(httpResponse, contextPath, userRole);
            return;
        }

        // 4. Autoriser l'accès
        chain.doFilter(request, response);
    }

    /**
     * Vérifie si la ressource est publique
     */
    private boolean isPublicResource(String path) {
        // Vérifier les URLs exactes
        for (String publicUrl : PUBLIC_URLS) {
            if (path.equals(publicUrl) || path.equals(publicUrl + "/")) {
                return true;
            }
            // Vérifier les préfixes pour les ressources statiques
            if (publicUrl.endsWith("/") && path.startsWith(publicUrl)) {
                return true;
            }
        }

        // Permettre l'accès à index.jsp et la racine
        if (path.isEmpty() || path.equals("/") || path.equals("/index.jsp")) {
            return true;
        }

        return false;
    }

    /**
     * Vérifie si c'est une page d'authentification
     */
    private boolean isAuthPage(String path) {
        for (String authPage : AUTH_PAGES) {
            if (path.equals(authPage)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Vérifie si l'utilisateur est autorisé à accéder au chemin demandé
     */
    private boolean isAuthorized(String path, String userRole) {
        if (userRole == null) {
            return false;
        }

        try {
            Role role = Role.valueOf(userRole);

            // Vérifier l'accès basé sur le rôle
            switch (role) {
                case INFIRMIER:
                    return path.startsWith("/infirmier/") ||
                            path.equals("/logout");

                case GENERALISTE:
                    return path.startsWith("/generaliste/") ||
                            path.equals("/logout");

                case SPECIALISTE:
                    return path.startsWith("/specialiste/") ||
                            path.equals("/logout");

                default:
                    return false;
            }
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Redirige vers le dashboard approprié selon le rôle
     */
    private void redirectToDashboard(HttpServletResponse response, String contextPath, String userRole)
            throws IOException {

        if (userRole == null) {
            response.sendRedirect(contextPath + "/login");
            return;
        }

        try {
            Role role = Role.valueOf(userRole);
            String redirectUrl;

            switch (role) {
                case INFIRMIER:
                    redirectUrl = contextPath + "/infirmier/dashboard";
                    break;
                case GENERALISTE:
                    redirectUrl = contextPath + "/generaliste/dashboard";
                    break;
                case SPECIALISTE:
                    redirectUrl = contextPath + "/specialiste/dashboard";
                    break;
                default:
                    redirectUrl = contextPath + "/login";
            }

            response.sendRedirect(redirectUrl);
        } catch (IllegalArgumentException e) {
            response.sendRedirect(contextPath + "/login");
        }
    }

    @Override
    public void destroy() {
        // Nettoyage si nécessaire
    }
}