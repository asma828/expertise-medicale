<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Expertise Médicale - MEDICA ELITE</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="icon" href="https://cdn-icons-png.flaticon.com/512/2966/2966327.png" />
    <style>
        .luxury-gradient {
            background: linear-gradient(135deg, #0f172a, #1e293b, #334155);
        }
        .gold { color: #facc15; }
        .btn-luxury {
            background: linear-gradient(90deg, #facc15, #fde68a);
            color: #1e293b;
            font-weight: 600;
            border-radius: 9999px;
            transition: transform 0.3s ease, box-shadow 0.3s ease;
        }
        .btn-luxury:hover {
            transform: translateY(-2px);
            box-shadow: 0 10px 20px rgba(250, 204, 21, 0.3);
        }
        .btn-secondary {
            background: rgba(51, 65, 85, 0.6);
            color: #e2e8f0;
            border: 1px solid rgba(250, 204, 21, 0.3);
            font-weight: 600;
            border-radius: 9999px;
        }
        .card-luxury {
            background: rgba(30, 41, 59, 0.6);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(250, 204, 21, 0.2);
            border-radius: 1rem;
        }
        .input-luxury {
            background: rgba(15, 23, 42, 0.8);
            border: 1px solid rgba(250, 204, 21, 0.3);
            color: #e2e8f0;
            border-radius: 0.5rem;
            padding: 0.75rem;
            width: 100%;
        }
        .input-luxury:focus {
            outline: none;
            border-color: #facc15;
            box-shadow: 0 0 0 3px rgba(250, 204, 21, 0.1);
        }
        .label-luxury {
            color: #cbd5e1;
            font-weight: 500;
            margin-bottom: 0.5rem;
            display: block;
        }
    </style>
</head>
<body class="luxury-gradient min-h-screen text-gray-100">

    <!-- Navbar -->
    <nav class="bg-opacity-20 backdrop-blur-md border-b border-gray-700 py-4 px-6 sticky top-0 z-50">
        <div class="max-w-7xl mx-auto flex justify-between items-center">
            <div class="text-2xl font-bold gold tracking-wide">MEDICA ELITE</div>
            <div class="flex items-center space-x-6">
                <a href="${pageContext.request.contextPath}/specialiste/demandes-expertise"
                   class="text-gray-300 hover:text-yellow-400 transition">← Retour aux Demandes</a>
                <span class="text-gray-300">👨‍⚕️ Dr. ${sessionScope.userName}</span>
            </div>
        </div>
    </nav>

    <div class="max-w-7xl mx-auto p-6">
        <c:if test="${not empty demande}">
            <!-- Header -->
            <div class="flex justify-between items-start mb-8">
                <div>
                    <h1 class="text-4xl font-bold mb-2 gold">Demande d'Expertise #${demande.id}</h1>
                    <p class="text-gray-400">
                        Demandée le ${demande.dateDemandeFormatee}
                    </p>
                </div>
                <c:choose>
                    <c:when test="${demande.statut == 'EN_ATTENTE'}">
                        <span class="px-4 py-2 rounded-full text-sm bg-orange-900/30 text-orange-400 border border-orange-700">
                            ⏱️ En attente
                        </span>
                    </c:when>
                    <c:when test="${demande.statut == 'TERMINEE'}">
                        <span class="px-4 py-2 rounded-full text-sm bg-green-900/30 text-green-400 border border-green-700">
                            ✅ Terminée
                        </span>
                    </c:when>
                </c:choose>
            </div>

            <!-- Messages -->
            <c:if test="${not empty error}">
                <div class="card-luxury p-4 mb-6 border-red-500">
                    <p class="text-red-400">${error}</p>
                </div>
            </c:if>

            <div class="grid md:grid-cols-3 gap-6">
                <!-- Colonne principale -->
                <div class="md:col-span-2 space-y-6">
                    <!-- Informations Patient -->
                    <div class="card-luxury p-6">
                        <h2 class="text-2xl font-semibold mb-4 gold">👤 Patient</h2>
                        <div class="grid grid-cols-2 gap-4 mb-4">
                            <div>
                                <p class="text-gray-400 text-sm">Nom Complet</p>
                                <p class="text-xl font-semibold">
                                    ${demande.consultation.patient.nom} ${demande.consultation.patient.prenom}
                                </p>
                            </div>
                            <div>
                                <p class="text-gray-400 text-sm">Âge</p>
                                <p class="text-xl font-semibold">
                                    ${java.time.Period.between(demande.consultation.patient.dateNaissance, java.time.LocalDate.now()).years} ans
                                </p>
                            </div>
                        </div>

                        <!-- Signes vitaux -->
                        <c:if test="${demande.consultation.patient.derniersSignesVitaux != null}">
                            <div class="bg-gray-800/40 p-4 rounded-lg">
                                <h3 class="text-sm font-semibold mb-3 text-yellow-400">📊 Signes Vitaux</h3>
                                <div class="grid grid-cols-4 gap-3">
                                    <div class="text-center">
                                        <p class="text-xs text-gray-400">TA</p>
                                        <p class="font-bold">${demande.consultation.patient.derniersSignesVitaux.tensionFormatee}</p>
                                    </div>
                                    <div class="text-center">
                                        <p class="text-xs text-gray-400">FC</p>
                                        <p class="font-bold">${demande.consultation.patient.derniersSignesVitaux.frequenceCardiaque} bpm</p>
                                    </div>
                                    <div class="text-center">
                                        <p class="text-xs text-gray-400">T°</p>
                                        <p class="font-bold">${demande.consultation.patient.derniersSignesVitaux.temperature}°C</p>
                                    </div>
                                    <c:if test="${demande.consultation.patient.derniersSignesVitaux.saturationOxygene != null}">
                                        <div class="text-center">
                                            <p class="text-xs text-gray-400">SpO₂</p>
                                            <p class="font-bold">${demande.consultation.patient.derniersSignesVitaux.saturationOxygene}%</p>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </c:if>

                        <!-- Antécédents médicaux -->
                        <c:if test="${not empty demande.consultation.patient.antecedents or not empty demande.consultation.patient.allergies}">
                            <div class="mt-4 bg-red-900/10 border border-red-700/30 p-4 rounded-lg">
                                <p class="text-red-400 font-semibold mb-3 text-sm">⚠️ Informations Importantes</p>
                                <div class="space-y-2 text-sm">
                                    <c:if test="${not empty demande.consultation.patient.antecedents}">
                                        <div>
                                            <span class="text-gray-400">Antécédents:</span>
                                            <p class="text-gray-200">${demande.consultation.patient.antecedents}</p>
                                        </div>
                                    </c:if>
                                    <c:if test="${not empty demande.consultation.patient.allergies}">
                                        <div>
                                            <span class="text-red-400">Allergies:</span>
                                            <p class="text-red-300 font-semibold">${demande.consultation.patient.allergies}</p>
                                        </div>
                                    </c:if>
                                </div>
                            </div>
                        </c:if>
                    </div>

                    <!-- Détails Consultation -->
                    <div class="card-luxury p-6">
                        <h2 class="text-2xl font-semibold mb-4 gold">🩺 Consultation du Généraliste</h2>

                        <div class="space-y-4">
                            <div>
                                <p class="text-gray-400 text-sm mb-1">Motif de consultation</p>
                                <p class="text-lg">${demande.consultation.motif}</p>
                            </div>

                            <c:if test="${not empty demande.consultation.symptomes}">
                                <div>
                                    <p class="text-gray-400 text-sm mb-1">Symptômes</p>
                                    <p class="text-lg">${demande.consultation.symptomes}</p>
                                </div>
                            </c:if>

                            <div>
                                <p class="text-gray-400 text-sm mb-1">Examen Clinique</p>
                                <p class="text-lg">${demande.consultation.examenClinique}</p>
                            </div>

                            <c:if test="${not empty demande.consultation.observations}">
                                <div>
                                    <p class="text-gray-400 text-sm mb-1">Observations</p>
                                    <p class="text-lg">${demande.consultation.observations}</p>
                                </div>
                            </c:if>
                        </div>
                    </div>

                    <!-- Question posée -->
                    <div class="card-luxury p-6 border-yellow-500">
                        <h2 class="text-2xl font-semibold mb-4 gold">❓ Question du Médecin Généraliste</h2>
                        <p class="text-lg mb-4">${demande.questionPosee}</p>

                        <c:if test="${not empty demande.donneesAnalyses}">
                            <div class="mt-4 bg-blue-900/10 p-4 rounded-lg border border-blue-700/30">
                                <p class="text-blue-400 text-sm mb-2">📊 Données et Analyses fournies:</p>
                                <p class="text-gray-200">${demande.donneesAnalyses}</p>
                            </div>
                        </c:if>
                    </div>

                    <!-- Formulaire de réponse -->
                    <c:if test="${demande.statut == 'EN_ATTENTE'}">
                        <form method="post" action="${pageContext.request.contextPath}/specialiste/expertise/${demande.id}">
                            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                            <div class="card-luxury p-6">
                                <h2 class="text-2xl font-semibold mb-6 gold">✍️ Votre Avis d'Expert</h2>

                                <div class="space-y-6">
                                    <div>
                                        <label class="label-luxury">Avis Médical *</label>
                                        <textarea name="avisMedical"
                                                  class="input-luxury"
                                                  rows="6"
                                                  placeholder="Votre diagnostic et analyse du cas..."
                                                  required></textarea>
                                        <p class="text-gray-400 text-xs mt-2">
                                            Donnez votre avis professionnel sur le cas présenté
                                        </p>
                                    </div>

                                    <div>
                                        <label class="label-luxury">Recommandations *</label>
                                        <textarea name="recommandations"
                                                  class="input-luxury"
                                                  rows="6"
                                                  placeholder="Traitement recommandé, examens complémentaires, conduite à tenir..."
                                                  required></textarea>
                                        <p class="text-gray-400 text-xs mt-2">
                                            Recommandations thérapeutiques et conduite à tenir
                                        </p>
                                    </div>

                                    <div class="flex space-x-4">
                                        <button type="submit" class="btn-luxury px-8 py-3">
                                            ✅ Envoyer l'Avis
                                        </button>
                                        <a href="${pageContext.request.contextPath}/specialiste/demandes-expertise"
                                           class="btn-secondary px-8 py-3">
                                            Annuler
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </form>
                    </c:if>

                    <!-- Réponse déjà donnée -->
                    <c:if test="${demande.statut == 'TERMINEE'}">
                        <div class="card-luxury p-6 border-green-500">
                            <h2 class="text-2xl font-semibold mb-4 text-green-400">✅ Votre Avis (Envoyé)</h2>

                            <div class="space-y-4">
                                <div class="bg-gray-800/40 p-4 rounded-lg">
                                    <p class="text-gray-400 text-sm mb-2">Avis Médical:</p>
                                    <p class="text-gray-200">${demande.avisMedical}</p>
                                </div>

                                <div class="bg-gray-800/40 p-4 rounded-lg">
                                    <p class="text-gray-400 text-sm mb-2">Recommandations:</p>
                                    <p class="text-gray-200">${demande.recommandations}</p>
                                </div>

                                <p class="text-sm text-gray-500">
                                    Répondu le ${demande.dateReponseFormatee}
                                    ${demande.creneau.dateReponse.format(java.time.format.DateTimeFormatter.ofPattern('EEEE dd MMMM yyyy').withLocale(java.util.Locale.FRENCH))}
                                </p>
                            </div>
                        </div>
                    </c:if>
                </div>

                <!-- Colonne latérale -->
                <div class="space-y-6">
                    <!-- Informations demandeur -->
                    <div class="card-luxury p-6">
                        <h3 class="text-xl font-semibold mb-4 gold">👨‍⚕️ Médecin Demandeur</h3>
                        <div class="space-y-3">
                            <div>
                                <p class="text-gray-400 text-sm">Nom</p>
                                <p class="font-semibold">Dr. ${demande.medecinDemandeur.nomComplet}</p>
                            </div>
                            <div>
                                <p class="text-gray-400 text-sm">Email</p>
                                <p class="text-sm">${demande.medecinDemandeur.email}</p>
                            </div>
                        </div>
                    </div>

                    <!-- Créneau -->
                    <div class="card-luxury p-6">
                        <h3 class="text-xl font-semibold mb-4 gold">📅 Créneau</h3>
                        <div class="text-center bg-gray-800/40 p-4 rounded-lg">
                            <p class="text-2xl font-bold text-yellow-400 mb-2">
                                ${demande.creneau.heureFormatee}
                            </p>
                            <p class="text-gray-400 text-sm">
                                ${demande.creneau.dateCompleteFr}
                            </p>
                        </div>
                    </div>

                    <!-- Rémunération -->
                    <div class="card-luxury p-6">
                        <h3 class="text-xl font-semibold mb-4 gold">💰 Rémunération</h3>
                        <div class="text-center">
                            <p class="text-4xl font-bold gold">${utilisateur.tarif} DH</p>
                            <p class="text-gray-400 text-sm mt-2">Tarif de télé-expertise</p>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <c:if test="${empty demande}">
            <div class="card-luxury p-12 text-center">
                <div class="text-6xl mb-4">⚠️</div>
                <h3 class="text-2xl font-semibold mb-2">Demande non trouvée</h3>
                <a href="${pageContext.request.contextPath}/specialiste/demandes-expertise"
                   class="btn-luxury inline-block px-8 py-3 mt-4">
                    Retour aux Demandes
                </a>
            </div>
        </c:if>
    </div>

    <!-- Footer -->
    <footer class="text-center py-6 mt-12 border-t border-gray-700 text-gray-400 text-sm">
        © 2025 MEDICA ELITE — Tous droits réservés.
    </footer>

</body>
</html>