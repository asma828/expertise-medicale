<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Nouvelle Consultation - MEDICA ELITE</title>
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
        .btn-secondary:hover {
            background: rgba(51, 65, 85, 0.8);
            border-color: #facc15;
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
                <a href="${pageContext.request.contextPath}/generaliste/patients-attente"
                   class="text-gray-300 hover:text-yellow-400 transition">← Retour à la File</a>
                <span class="text-gray-300">👨‍⚕️ Dr. ${sessionScope.userName}</span>
            </div>
        </div>
    </nav>

    <div class="max-w-6xl mx-auto p-6">
        <!-- Header -->
        <div class="mb-8">
            <h1 class="text-4xl font-bold mb-2 gold">📝 Nouvelle Consultation</h1>
            <p class="text-gray-400">Créer une consultation pour le patient</p>
        </div>

        <!-- Messages -->
        <c:if test="${not empty error}">
            <div class="card-luxury p-4 mb-6 border-red-500">
                <div class="flex items-center space-x-3">
                    <span class="text-2xl">❌</span>
                    <p class="text-red-400">${error}</p>
                </div>
            </div>
        </c:if>

        <!-- Informations Patient -->
        <c:if test="${not empty patient}">
            <div class="card-luxury p-6 mb-6">
                <h2 class="text-2xl font-semibold mb-4 gold">👤 Informations Patient</h2>

                <div class="grid md:grid-cols-3 gap-6">
                    <div class="bg-gray-800/40 p-4 rounded-lg">
                        <p class="text-gray-400 text-sm mb-1">Nom Complet</p>
                        <p class="text-xl font-semibold">${patient.nom} ${patient.prenom}</p>
                    </div>
                    <div class="bg-gray-800/40 p-4 rounded-lg">
                        <p class="text-gray-400 text-sm mb-1">Âge</p>
                        <p class="text-xl font-semibold">
                            ${java.time.Period.between(patient.dateNaissance, java.time.LocalDate.now()).years} ans
                        </p>
                    </div>
                    <div class="bg-gray-800/40 p-4 rounded-lg">
                        <p class="text-gray-400 text-sm mb-1">N° Sécurité Sociale</p>
                        <p class="text-lg font-semibold">${patient.numeroSecuriteSociale}</p>
                    </div>
                </div>

                <!-- Signes vitaux -->
                <c:if test="${patient.derniersSignesVitaux != null}">
                    <div class="mt-6">
                        <h3 class="text-lg font-semibold mb-3 text-yellow-400">📊 Derniers Signes Vitaux</h3>
                        <div class="grid md:grid-cols-4 gap-4">
                            <div class="bg-gray-800/40 p-3 rounded-lg text-center">
                                <p class="text-gray-400 text-xs mb-1">TA</p>
                                <p class="text-xl font-bold">${patient.derniersSignesVitaux.tensionFormatee}</p>
                            </div>
                            <div class="bg-gray-800/40 p-3 rounded-lg text-center">
                                <p class="text-gray-400 text-xs mb-1">FC</p>
                                <p class="text-xl font-bold">${patient.derniersSignesVitaux.frequenceCardiaque} bpm</p>
                            </div>
                            <div class="bg-gray-800/40 p-3 rounded-lg text-center">
                                <p class="text-gray-400 text-xs mb-1">T°</p>
                                <p class="text-xl font-bold">${patient.derniersSignesVitaux.temperature}°C</p>
                            </div>
                            <c:if test="${patient.derniersSignesVitaux.saturationOxygene != null}">
                                <div class="bg-gray-800/40 p-3 rounded-lg text-center">
                                    <p class="text-gray-400 text-xs mb-1">SpO₂</p>
                                    <p class="text-xl font-bold">${patient.derniersSignesVitaux.saturationOxygene}%</p>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </c:if>

                <!-- Alertes médicales -->
                <c:if test="${not empty patient.antecedents or not empty patient.allergies or not empty patient.traitementEnCours}">
                    <div class="mt-6 bg-red-900/20 border border-red-700/40 p-4 rounded-lg">
                        <p class="text-red-400 font-semibold mb-3">⚠️ Informations Médicales Importantes</p>
                        <div class="grid md:grid-cols-3 gap-4 text-sm">
                            <c:if test="${not empty patient.antecedents}">
                                <div>
                                    <p class="text-gray-400 mb-1">Antécédents:</p>
                                    <p class="text-gray-200">${patient.antecedents}</p>
                                </div>
                            </c:if>
                            <c:if test="${not empty patient.allergies}">
                                <div>
                                    <p class="text-red-400 mb-1">Allergies:</p>
                                    <p class="text-red-300 font-semibold">${patient.allergies}</p>
                                </div>
                            </c:if>
                            <c:if test="${not empty patient.traitementEnCours}">
                                <div>
                                    <p class="text-gray-400 mb-1">Traitement en cours:</p>
                                    <p class="text-gray-200">${patient.traitementEnCours}</p>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </c:if>
            </div>

            <!-- Formulaire Consultation -->
            <form method="post" action="${pageContext.request.contextPath}/generaliste/creer-consultation">
                <input type="hidden" name="patientId" value="${patient.id}">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="card-luxury p-6 mb-6">
                    <h2 class="text-2xl font-semibold mb-6 gold">🩺 Informations de Consultation</h2>

                    <div class="space-y-6">
                        <div>
                            <label class="label-luxury">Motif de Consultation *</label>
                            <input type="text"
                                   name="motif"
                                   class="input-luxury"
                                   placeholder="Ex: Douleurs thoraciques, fièvre persistante..."
                                   required>
                        </div>

                        <div>
                            <label class="label-luxury">Symptômes</label>
                            <textarea name="symptomes"
                                      class="input-luxury"
                                      rows="3"
                                      placeholder="Décrivez les symptômes rapportés par le patient..."></textarea>
                        </div>

                        <div>
                            <label class="label-luxury">Examen Clinique *</label>
                            <textarea name="examenClinique"
                                      class="input-luxury"
                                      rows="4"
                                      placeholder="Résultats de l'examen physique (auscultation, palpation, inspection...)..."
                                      required></textarea>
                        </div>

                        <div>
                            <label class="label-luxury">Observations</label>
                            <textarea name="observations"
                                      class="input-luxury"
                                      rows="3"
                                      placeholder="Autres observations importantes..."></textarea>
                        </div>
                    </div>
                </div>

                <!-- Boutons d'action -->
                <div class="flex justify-between items-center">
                    <a href="${pageContext.request.contextPath}/generaliste/patients-attente"
                       class="btn-secondary px-8 py-3">
                        Annuler
                    </a>

                    <div class="space-x-4">
                        <button type="submit"
                                name="action"
                                value="save"
                                class="btn-luxury px-8 py-3">
                            Enregistrer la Consultation
                        </button>
                    </div>
                </div>
            </form>
        </c:if>

        <c:if test="${empty patient}">
            <div class="card-luxury p-12 text-center">
                <div class="text-6xl mb-4">⚠️</div>
                <h3 class="text-2xl font-semibold mb-2">Patient non trouvé</h3>
                <p class="text-gray-400 mb-6">Le patient sélectionné n'existe pas ou n'est plus disponible</p>
                <a href="${pageContext.request.contextPath}/generaliste/patients-attente"
                   class="btn-luxury inline-block px-8 py-3">
                    Retour à la File d'Attente
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