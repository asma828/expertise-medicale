package org.example.medicale.servlet;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.example.medicale.dao.PatientDAO;
import org.example.medicale.dao.SigneVitalDAO;
import org.example.medicale.entity.Patient;
import org.example.medicale.entity.SigneVital;
import org.example.medicale.entity.User;
import org.example.medicale.enums.StatutPatient;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@WebServlet("/infirmier/enregistrer-patient")
public class EnregistrerPatientServlet extends HttpServlet {

    private PatientDAO patientDAO;
    private SigneVitalDAO signeVitalDAO;

    @Override
    public void init() throws ServletException {
        patientDAO = new PatientDAO();
        signeVitalDAO = new SigneVitalDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Génerer un token CSRF
        HttpSession session = request.getSession();
        if (session.getAttribute("csrfToken") == null) {
            String csrfToken = java.util.UUID.randomUUID().toString();
            session.setAttribute("csrfToken", csrfToken);
        }

        String numeroSecuriteSociale = request.getParameter("numeroSecuriteSociale");

        if (numeroSecuriteSociale != null && !numeroSecuriteSociale.trim().isEmpty()) {
            // Rechercher le patient
            Optional<Patient> patientOpt = patientDAO.findByNumeroSecuriteSociale(numeroSecuriteSociale.trim());

            if (patientOpt.isPresent()) {
                request.setAttribute("patient", patientOpt.get());
            } else {
                request.setAttribute("error", "Aucun patient trouvé avec ce numéro de sécurité sociale.");
            }
        }

        request.getRequestDispatcher("/views/infirmier/enregistrer-patient.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();

        // Vérification CSRF
        String csrfToken = request.getParameter("csrfToken");
        String sessionToken = (String) session.getAttribute("csrfToken");

        if (csrfToken == null || !csrfToken.equals(sessionToken)) {
            request.setAttribute("error", "Erreur de sécurité. Veuillez réessayer.");
            request.getRequestDispatcher("/views/infirmier/enregistrer-patient.jsp").forward(request, response);
            return;
        }

        String action = request.getParameter("action");

        try {
            if ("createPatient".equals(action)) {
                handleCreatePatient(request, response, session);
            } else if ("updateVitaux".equals(action)) {
                handleUpdateVitaux(request, response, session);
            } else {
                request.setAttribute("error", "Action non reconnue.");
                request.getRequestDispatcher("/views/infirmier/enregistrer-patient.jsp").forward(request, response);
            }
        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("error", "Une erreur est survenue: " + e.getMessage());
            request.getRequestDispatcher("/views/infirmier/enregistrer-patient.jsp").forward(request, response);
        }
    }

    private void handleCreatePatient(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        // Récupérer les informations du patient
        String nom = request.getParameter("nom");
        String prenom = request.getParameter("prenom");
        String dateNaissanceStr = request.getParameter("dateNaissance");
        String numeroSecuriteSociale = request.getParameter("numeroSecuriteSociale");
        String telephone = request.getParameter("telephone");
        String adresse = request.getParameter("adresse");
        String antecedents = request.getParameter("antecedents");
        String allergies = request.getParameter("allergies");
        String traitementEnCours = request.getParameter("traitementEnCours");

        // Validation
        if (nom == null || prenom == null || dateNaissanceStr == null || numeroSecuriteSociale == null ||
                nom.trim().isEmpty() || prenom.trim().isEmpty() || numeroSecuriteSociale.trim().isEmpty()) {
            request.setAttribute("error", "Les champs obligatoires doivent être remplis.");
            request.getRequestDispatcher("/views/infirmier/enregistrer-patient.jsp").forward(request, response);
            return;
        }

        // Vérifier si le numéro existe déjà
        if (patientDAO.findByNumeroSecuriteSociale(numeroSecuriteSociale.trim()).isPresent()) {
            request.setAttribute("error", "Ce numéro de sécurité sociale existe déjà.");
            request.getRequestDispatcher("/views/infirmier/enregistrer-patient.jsp").forward(request, response);
            return;
        }

        // Créer le patient
        Patient patient = new Patient();
        patient.setNom(nom.trim());
        patient.setPrenom(prenom.trim());
        patient.setDateNaissance(LocalDate.parse(dateNaissanceStr));
        patient.setNumeroSecuriteSociale(numeroSecuriteSociale.trim());
        patient.setTelephone(telephone != null && !telephone.trim().isEmpty() ? telephone.trim() : null);
        patient.setAdresse(adresse != null && !adresse.trim().isEmpty() ? adresse.trim() : null);
        patient.setAntecedents(antecedents);
        patient.setAllergies(allergies);
        patient.setTraitementEnCours(traitementEnCours);
        patient.setHeureArrivee(LocalDateTime.now());
        patient.setStatut(StatutPatient.EN_ATTENTE);

        // Sauvegarder le patient
        patient = patientDAO.save(patient);

        // Créer les signes vitaux
        SigneVital signeVital = createSigneVitalFromRequest(request, patient, session);
        signeVitalDAO.save(signeVital);

        // Rediriger avec message de succès
        session.setAttribute("successMessage", "Patient créé et ajouté à la file d'attente avec succès!");
        response.sendRedirect(request.getContextPath() + "/infirmier/dashboard");
    }

    private void handleUpdateVitaux(HttpServletRequest request, HttpServletResponse response, HttpSession session)
            throws ServletException, IOException {

        String patientIdStr = request.getParameter("patientId");

        if (patientIdStr == null || patientIdStr.trim().isEmpty()) {
            request.setAttribute("error", "ID patient manquant.");
            request.getRequestDispatcher("/views/infirmier/enregistrer-patient.jsp").forward(request, response);
            return;
        }

        Long patientId = Long.parseLong(patientIdStr);
        Optional<Patient> patientOpt = patientDAO.findById(patientId);

        if (patientOpt.isEmpty()) {
            request.setAttribute("error", "Patient non trouvé.");
            request.getRequestDispatcher("/views/infirmier/enregistrer-patient.jsp").forward(request, response);
            return;
        }

        Patient patient = patientOpt.get();

        // Créer les nouveaux signes vitaux
        SigneVital signeVital = createSigneVitalFromRequest(request, patient, session);
        signeVitalDAO.save(signeVital);

        // Mettre à jour le statut du patient
        patient.setStatut(StatutPatient.EN_ATTENTE);
        patient.setHeureArrivee(LocalDateTime.now());
        patientDAO.save(patient);

        // Rediriger avec message de succès
        session.setAttribute("successMessage", "Signes vitaux enregistrés! Patient ajouté à la file d'attente.");
        response.sendRedirect(request.getContextPath() + "/infirmier/dashboard");
    }

    private SigneVital createSigneVitalFromRequest(HttpServletRequest request, Patient patient, HttpSession session) {
        SigneVital signeVital = new SigneVital();
        signeVital.setPatient(patient);
        signeVital.setDateMesure(LocalDateTime.now());

        // Récupérer l'infirmier connecté
        User infirmier = (User) session.getAttribute("utilisateur");
        signeVital.setInfirmier(infirmier);

        // Signes vitaux obligatoires
        signeVital.setTensionSystolique(Integer.parseInt(request.getParameter("tensionSystolique")));
        signeVital.setTensionDiastolique(Integer.parseInt(request.getParameter("tensionDiastolique")));
        signeVital.setFrequenceCardiaque(Integer.parseInt(request.getParameter("frequenceCardiaque")));
        signeVital.setTemperature(Double.parseDouble(request.getParameter("temperature")));

        // Signes vitaux optionnels
        String frequenceRespiratoire = request.getParameter("frequenceRespiratoire");
        if (frequenceRespiratoire != null && !frequenceRespiratoire.trim().isEmpty()) {
            signeVital.setFrequenceRespiratoire(Integer.parseInt(frequenceRespiratoire));
        }

        String saturationOxygene = request.getParameter("saturationOxygene");
        if (saturationOxygene != null && !saturationOxygene.trim().isEmpty()) {
            signeVital.setSaturationOxygene(Integer.parseInt(saturationOxygene));
        }

        String poids = request.getParameter("poids");
        if (poids != null && !poids.trim().isEmpty()) {
            signeVital.setPoids(Double.parseDouble(poids));
        }

        String taille = request.getParameter("taille");
        if (taille != null && !taille.trim().isEmpty()) {
            signeVital.setTaille(Double.parseDouble(taille));
        }

        return signeVital;
    }

    @Override
    public void destroy() {
        if (patientDAO != null) {
            patientDAO.close();
        }
        if (signeVitalDAO != null) {
            signeVitalDAO.close();
        }
    }
}