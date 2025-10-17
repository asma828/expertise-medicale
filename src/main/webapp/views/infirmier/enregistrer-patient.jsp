<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Enregistrer un Patient - MEDICA ELITE</title>
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
            transition: all 0.3s ease;
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
            transition: all 0.3s ease;
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
        #patientExistantSection, #nouveauPatientSection {
            display: none;
        }
    </style>
</head>
<body class="luxury-gradient min-h-screen text-gray-100">

    <!-- Navbar -->
    <nav class="bg-opacity-20 backdrop-blur-md border-b border-gray-700 py-4 px-6">
        <div class="max-w-7xl mx-auto flex justify-between items-center">
            <div class="text-2xl font-bold gold tracking-wide">MEDICA ELITE</div>
            <div class="flex items-center space-x-6">
                <a href="${pageContext.request.contextPath}/infirmier/dashboard"
                   class="text-gray-300 hover:text-yellow-400 transition">← Retour au Dashboard</a>
                <span class="text-gray-300">👨‍⚕️ ${sessionScope.userName}</span>
            </div>
        </div>
    </nav>

    <div class="max-w-4xl mx-auto p-6">
        <!-- Header -->
        <div class="mb-8">
            <h1 class="text-4xl font-bold mb-2 gold">Enregistrer un Patient</h1>
            <p class="text-gray-400">Recherchez un patient existant ou créez-en un nouveau</p>
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

        <c:if test="${not empty success}">
            <div class="card-luxury p-4 mb-6 border-green-500">
                <div class="flex items-center space-x-3">
                    <span class="text-2xl">✅</span>
                    <p class="text-green-400">${success}</p>
                </div>
            </div>
        </c:if>

        <!-- Étape 1: Recherche Patient -->
        <div class="card-luxury p-8 mb-6">
            <h2 class="text-2xl font-semibold mb-6">🔍 Étape 1: Rechercher le Patient</h2>

            <form id="searchForm" method="get" action="${pageContext.request.contextPath}/infirmier/enregistrer-patient">
                <div class="mb-4">
                    <label class="label-luxury">Numéro de Sécurité Sociale</label>
                    <input type="text"
                           name="numeroSecuriteSociale"
                           id="numeroSecuriteSociale"
                           class="input-luxury"
                           placeholder="Ex: 123456789012345"
                           value="${param.numeroSecuriteSociale}"
                           maxlength="15">
                </div>
                <button type="submit" class="btn-luxury px-8 py-3">
                    Rechercher
                </button>
                <button type="button" onclick="showNouveauPatient()" class="btn-secondary px-8 py-3 ml-4">
                    Nouveau Patient
                </button>
            </form>
        </div>

        <!-- Patient Existant -->
        <c:if test="${not empty patient}">
            <div id="patientExistantSection" class="card-luxury p-8 mb-6">
                <h2 class="text-2xl font-semibold mb-6 text-green-400">✅ Patient Trouvé</h2>

                <div class="grid md:grid-cols-2 gap-4 mb-6 p-6 bg-gray-800/40 rounded-lg">
                    <div>
                        <p class="text-gray-400 text-sm">Nom Complet</p>
                        <p class="text-xl font-semibold">${patient.nom} ${patient.prenom}</p>
                    </div>
                    <div>
                        <p class="text-gray-400 text-sm">Date de Naissance</p>
                        <p class="text-xl font-semibold">${patient.dateNaissance}</p>
                    </div>
                    <div>
                        <p class="text-gray-400 text-sm">Téléphone</p>
                        <p class="text-xl font-semibold">${patient.telephone != null ? patient.telephone : 'Non renseigné'}</p>
                    </div>
                    <div>
                        <p class="text-gray-400 text-sm">Adresse</p>
                        <p class="text-xl font-semibold">${patient.adresse != null ? patient.adresse : 'Non renseignée'}</p>
                    </div>
                </div>

                <form method="post" action="${pageContext.request.contextPath}/infirmier/enregistrer-patient">
                    <input type="hidden" name="action" value="updateVitaux">
                    <input type="hidden" name="patientId" value="${patient.id}">
                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                    <h3 class="text-xl font-semibold mb-4 gold">📊 Nouveaux Signes Vitaux</h3>

                    <div class="grid md:grid-cols-2 gap-6">
                        <div>
                            <label class="label-luxury">Tension Systolique (mmHg) *</label>
                            <input type="number" name="tensionSystolique" class="input-luxury" required>
                        </div>
                        <div>
                            <label class="label-luxury">Tension Diastolique (mmHg) *</label>
                            <input type="number" name="tensionDiastolique" class="input-luxury" required>
                        </div>
                        <div>
                            <label class="label-luxury">Fréquence Cardiaque (bpm) *</label>
                            <input type="number" name="frequenceCardiaque" class="input-luxury" required>
                        </div>
                        <div>
                            <label class="label-luxury">Température (°C) *</label>
                            <input type="number" step="0.1" name="temperature" class="input-luxury" required>
                        </div>
                        <div>
                            <label class="label-luxury">Fréquence Respiratoire (rpm)</label>
                            <input type="number" name="frequenceRespiratoire" class="input-luxury">
                        </div>
                        <div>
                            <label class="label-luxury">Saturation O₂ (%)</label>
                            <input type="number" name="saturationOxygene" class="input-luxury" max="100">
                        </div>
                        <div>
                            <label class="label-luxury">Poids (kg)</label>
                            <input type="number" step="0.1" name="poids" class="input-luxury">
                        </div>
                        <div>
                            <label class="label-luxury">Taille (cm)</label>
                            <input type="number" step="0.1" name="taille" class="input-luxury">
                        </div>
                    </div>

                    <div class="mt-6 flex space-x-4">
                        <button type="submit" class="btn-luxury px-8 py-3">
                            Enregistrer et Ajouter à la File d'Attente
                        </button>
                        <a href="${pageContext.request.contextPath}/infirmier/dashboard"
                           class="btn-secondary px-8 py-3">
                            Annuler
                        </a>
                    </div>
                </form>
            </div>
        </c:if>

        <!-- Nouveau Patient -->
        <div id="nouveauPatientSection" class="card-luxury p-8">
            <h2 class="text-2xl font-semibold mb-6 gold">➕ Nouveau Patient</h2>

            <form method="post" action="${pageContext.request.contextPath}/infirmier/enregistrer-patient">
                <input type="hidden" name="action" value="createPatient">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <h3 class="text-xl font-semibold mb-4">📋 Informations Administratives</h3>
                <div class="grid md:grid-cols-2 gap-6 mb-6">
                    <div>
                        <label class="label-luxury">Nom *</label>
                        <input type="text" name="nom" class="input-luxury" required>
                    </div>
                    <div>
                        <label class="label-luxury">Prénom *</label>
                        <input type="text" name="prenom" class="input-luxury" required>
                    </div>
                    <div>
                        <label class="label-luxury">Date de Naissance *</label>
                        <input type="date" name="dateNaissance" class="input-luxury" required>
                    </div>
                    <div>
                        <label class="label-luxury">Numéro Sécurité Sociale *</label>
                        <input type="text" name="numeroSecuriteSociale" class="input-luxury" required maxlength="15">
                    </div>
                    <div>
                        <label class="label-luxury">Téléphone</label>
                        <input type="tel" name="telephone" class="input-luxury">
                    </div>
                    <div>
                        <label class="label-luxury">Adresse</label>
                        <input type="text" name="adresse" class="input-luxury">
                    </div>
                </div>

                <h3 class="text-xl font-semibold mb-4 gold">🏥 Informations Médicales</h3>
                <div class="grid md:grid-cols-1 gap-6 mb-6">
                    <div>
                        <label class="label-luxury">Antécédents Médicaux</label>
                        <textarea name="antecedents" class="input-luxury" rows="3" placeholder="Maladies chroniques, opérations passées..."></textarea>
                    </div>
                    <div>
                        <label class="label-luxury">Allergies</label>
                        <textarea name="allergies" class="input-luxury" rows="2" placeholder="Allergies médicamenteuses ou alimentaires..."></textarea>
                    </div>
                    <div>
                        <label class="label-luxury">Traitement en Cours</label>
                        <textarea name="traitementEnCours" class="input-luxury" rows="2" placeholder="Médicaments actuels..."></textarea>
                    </div>
                </div>

                <h3 class="text-xl font-semibold mb-4 gold">📊 Signes Vitaux</h3>
                <div class="grid md:grid-cols-2 gap-6 mb-6">
                    <div>
                        <label class="label-luxury">Tension Systolique (mmHg) *</label>
                        <input type="number" name="tensionSystolique" class="input-luxury" required>
                    </div>
                    <div>
                        <label class="label-luxury">Tension Diastolique (mmHg) *</label>
                        <input type="number" name="tensionDiastolique" class="input-luxury" required>
                    </div>
                    <div>
                        <label class="label-luxury">Fréquence Cardiaque (bpm) *</label>
                        <input type="number" name="frequenceCardiaque" class="input-luxury" required>
                    </div>
                    <div>
                        <label class="label-luxury">Température (°C) *</label>
                        <input type="number" step="0.1" name="temperature" class="input-luxury" required>
                    </div>
                    <div>
                        <label class="label-luxury">Fréquence Respiratoire (rpm)</label>
                        <input type="number" name="frequenceRespiratoire" class="input-luxury">
                    </div>
                    <div>
                        <label class="label-luxury">Saturation O₂ (%)</label>
                        <input type="number" name="saturationOxygene" class="input-luxury" max="100">
                    </div>
                    <div>
                        <label class="label-luxury">Poids (kg)</label>
                        <input type="number" step="0.1" name="poids" class="input-luxury">
                    </div>
                    <div>
                        <label class="label-luxury">Taille (cm)</label>
                        <input type="number" step="0.1" name="taille" class="input-luxury">
                    </div>
                </div>

                <div class="flex space-x-4">
                    <button type="submit" class="btn-luxury px-8 py-3">
                        Créer le Patient et Ajouter à la File
                    </button>
                    <button type="button" onclick="hideNouveauPatient()" class="btn-secondary px-8 py-3">
                        Annuler
                    </button>
                </div>
            </form>
        </div>
    </div>

    <script>
        function showNouveauPatient() {
            document.getElementById('nouveauPatientSection').style.display = 'block';
            document.getElementById('patientExistantSection').style.display = 'none';
            document.getElementById('numeroSecuriteSociale').value = '';
        }

        function hideNouveauPatient() {
            document.getElementById('nouveauPatientSection').style.display = 'none';
        }

        // Afficher la section patient existant si un patient est trouvé
        <c:if test="${not empty patient}">
            document.getElementById('patientExistantSection').style.display = 'block';
        </c:if>
    </script>

</body>
</html>