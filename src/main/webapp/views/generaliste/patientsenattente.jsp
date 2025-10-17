<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>File d'Attente - MEDICA ELITE</title>
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
        .card-luxury {
            background: rgba(30, 41, 59, 0.6);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(250, 204, 21, 0.2);
            border-radius: 1rem;
        }
        .patient-card {
            background: rgba(51, 65, 85, 0.4);
            border: 1px solid rgba(250, 204, 21, 0.2);
            border-radius: 1rem;
            transition: all 0.3s ease;
        }
        .patient-card:hover {
            border-color: rgba(250, 204, 21, 0.4);
            background: rgba(51, 65, 85, 0.6);
            transform: translateY(-2px);
        }
    </style>
</head>
<body class="luxury-gradient min-h-screen text-gray-100">

    <!-- Navbar -->
    <nav class="bg-opacity-20 backdrop-blur-md border-b border-gray-700 py-4 px-6 sticky top-0 z-50">
        <div class="max-w-7xl mx-auto flex justify-between items-center">
            <div class="text-2xl font-bold gold tracking-wide">MEDICA ELITE</div>
            <div class="flex items-center space-x-6">
                <a href="${pageContext.request.contextPath}/generaliste/dashboard"
                   class="text-gray-300 hover:text-yellow-400 transition">← Retour au Dashboard</a>
                <span class="text-gray-300">👨‍⚕️ Dr. ${sessionScope.userName}</span>
            </div>
        </div>
    </nav>

    <div class="max-w-7xl mx-auto p-6">
        <!-- Header -->
        <div class="mb-8">
            <h1 class="text-4xl font-bold mb-2 gold">👥 File d'Attente</h1>
            <p class="text-gray-400">Patients en attente de consultation</p>
        </div>

        <!-- Stats -->
        <div class="grid md:grid-cols-3 gap-6 mb-8">
            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">Total en attente</p>
                        <p class="text-3xl font-bold text-yellow-400">${patients.size()}</p>
                    </div>
                    <div class="text-4xl">⏱️</div>
                </div>
            </div>

            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">Temps d'attente moyen</p>
                        <p class="text-3xl font-bold text-blue-400">${tempsAttenteMoyen != null ? tempsAttenteMoyen : 0} min</p>
                    </div>
                    <div class="text-4xl">⏰</div>
                </div>
            </div>

            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">Premier arrivé à</p>
                        <p class="text-2xl font-bold text-green-400">
                            <c:if test="${not empty patients}">
                               ${patient.heureArrivee.hour}:${patient.heureArrivee.minute}
                            </c:if>
                            <c:if test="${empty patients}">--:--</c:if>
                        </p>
                    </div>
                    <div class="text-4xl">🕐</div>
                </div>
            </div>
        </div>

        <!-- Liste des patients -->
        <div class="card-luxury p-6">
            <c:choose>
                <c:when test="${not empty patients}">
                    <div class="space-y-4">
                        <c:forEach items="${patients}" var="patient" varStatus="status">
                            <div class="patient-card p-6">
                                <div class="flex items-start justify-between">
                                    <div class="flex items-start space-x-4 flex-1">
                                        <!-- Numéro dans la file -->
                                        <div class="flex-shrink-0">
                                            <div class="w-16 h-16 rounded-full bg-gradient-to-br from-yellow-400 to-yellow-600 flex items-center justify-center">
                                                <span class="text-2xl font-bold text-gray-900">#${status.index + 1}</span>
                                            </div>
                                        </div>

                                        <!-- Informations patient -->
                                        <div class="flex-1">
                                            <div class="flex items-center space-x-3 mb-3">
                                                <h3 class="text-2xl font-semibold">${patient.nom} ${patient.prenom}</h3>
                                                <span class="px-3 py-1 rounded-full text-xs bg-yellow-900/30 text-yellow-400 border border-yellow-700">
                                                    En attente
                                                </span>
                                            </div>

                                            <!-- Informations de base -->
                                            <div class="grid md:grid-cols-4 gap-4 mb-4">
                                                <div>
                                                    <p class="text-xs text-gray-400 mb-1">Arrivée</p>
                                                    <p class="font-semibold text-yellow-400">
                                                        ${patient.heureArrivee.hour}:${patient.heureArrivee.minute}
                                                    </p>
                                                    <p class="text-xs text-gray-500">
                                                        Il y a ${java.time.Duration.between(patient.heureArrivee.toLocalTime(), java.time.LocalTime.now()).toMinutes()} min
                                                    </p>
                                                </div>
                                                <div>
                                                    <p class="text-xs text-gray-400 mb-1">Âge</p>
                                                    <p class="font-semibold">
                                                        ${java.time.Period.between(patient.dateNaissance, java.time.LocalDate.now()).years} ans
                                                    </p>
                                                </div>
                                                <div>
                                                    <p class="text-xs text-gray-400 mb-1">N° Sécurité Sociale</p>
                                                    <p class="font-semibold text-sm">${patient.numeroSecuriteSociale}</p>
                                                </div>
                                                <div>
                                                    <p class="text-xs text-gray-400 mb-1">Téléphone</p>
                                                    <p class="font-semibold">${patient.telephone != null ? patient.telephone : 'Non renseigné'}</p>
                                                </div>
                                            </div>

                                            <!-- Signes vitaux -->
                                            <c:if test="${patient.derniersSignesVitaux != null}">
                                                <div class="bg-gray-900/50 p-4 rounded-lg mb-4">
                                                    <p class="text-xs text-gray-400 mb-3 font-semibold">📊 Signes Vitaux</p>
                                                    <div class="grid grid-cols-2 md:grid-cols-4 gap-4">
                                                        <div class="bg-gray-800/50 p-3 rounded-lg">
                                                            <p class="text-xs text-gray-400 mb-1">Tension Artérielle</p>
                                                            <p class="text-lg font-bold">${patient.derniersSignesVitaux.tensionFormatee}</p>
                                                            <p class="text-xs text-gray-500">mmHg</p>
                                                        </div>
                                                        <div class="bg-gray-800/50 p-3 rounded-lg">
                                                            <p class="text-xs text-gray-400 mb-1">Fréquence Cardiaque</p>
                                                            <p class="text-lg font-bold">${patient.derniersSignesVitaux.frequenceCardiaque}</p>
                                                            <p class="text-xs text-gray-500">bpm</p>
                                                        </div>
                                                        <div class="bg-gray-800/50 p-3 rounded-lg">
                                                            <p class="text-xs text-gray-400 mb-1">Température</p>
                                                            <p class="text-lg font-bold">${patient.derniersSignesVitaux.temperature}</p>
                                                            <p class="text-xs text-gray-500">°C</p>
                                                        </div>
                                                        <c:if test="${patient.derniersSignesVitaux.saturationOxygene != null}">
                                                            <div class="bg-gray-800/50 p-3 rounded-lg">
                                                                <p class="text-xs text-gray-400 mb-1">SpO₂</p>
                                                                <p class="text-lg font-bold">${patient.derniersSignesVitaux.saturationOxygene}</p>
                                                                <p class="text-xs text-gray-500">%</p>
                                                            </div>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </c:if>

                                            <!-- Antécédents -->
                                            <c:if test="${not empty patient.antecedents or not empty patient.allergies or not empty patient.traitementEnCours}">
                                                <div class="bg-red-900/10 border border-red-700/30 p-4 rounded-lg">
                                                    <p class="text-xs text-red-400 mb-2 font-semibold">⚠️ Informations Médicales Importantes</p>
                                                    <div class="grid md:grid-cols-3 gap-4 text-sm">
                                                        <c:if test="${not empty patient.antecedents}">
                                                            <div>
                                                                <p class="text-gray-400 text-xs mb-1">Antécédents:</p>
                                                                <p class="text-gray-200">${patient.antecedents}</p>
                                                            </div>
                                                        </c:if>
                                                        <c:if test="${not empty patient.allergies}">
                                                            <div>
                                                                <p class="text-red-400 text-xs mb-1">Allergies:</p>
                                                                <p class="text-red-300 font-semibold">${patient.allergies}</p>
                                                            </div>
                                                        </c:if>
                                                        <c:if test="${not empty patient.traitementEnCours}">
                                                            <div>
                                                                <p class="text-gray-400 text-xs mb-1">Traitement en cours:</p>
                                                                <p class="text-gray-200">${patient.traitementEnCours}</p>
                                                            </div>
                                                        </c:if>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>

                                    <!-- Bouton d'action -->
                                    <div class="ml-6 flex-shrink-0">
                                        <a href="${pageContext.request.contextPath}/generaliste/creerConsultation?patientId=${patient.id}"
                                           class="btn-luxury px-8 py-4 text-lg inline-block">
                                            Consulter Maintenant
                                        </a>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-16 text-gray-400">
                        <div class="text-8xl mb-6">✨</div>
                        <h3 class="text-2xl font-semibold mb-2">Aucun Patient en Attente</h3>
                        <p class="text-gray-500">Les patients ajoutés par l'infirmier apparaîtront ici</p>
                        <a href="${pageContext.request.contextPath}/generaliste/dashboard"
                           class="btn-luxury inline-block px-8 py-3 mt-6">
                            Retour au Dashboard
                        </a>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Footer -->
    <footer class="text-center py-6 mt-12 border-t border-gray-700 text-gray-400 text-sm">
        © 2025 MEDICA ELITE — Tous droits réservés.
    </footer>

</body>
</html>