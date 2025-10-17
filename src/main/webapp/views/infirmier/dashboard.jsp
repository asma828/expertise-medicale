<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Infirmier - MEDICA ELITE</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="icon" href="https://cdn-icons-png.flaticon.com/512/2966/2966327.png" />
    <style>
        .luxury-gradient {
            background: linear-gradient(135deg, #0f172a, #1e293b, #334155);
        }
        .gold {
            color: #facc15;
        }
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
            transition: all 0.3s ease;
        }
        .card-luxury:hover {
            border-color: rgba(250, 204, 21, 0.4);
            transform: translateY(-4px);
            box-shadow: 0 10px 30px rgba(250, 204, 21, 0.1);
        }
        .input-luxury {
            background: rgba(15, 23, 42, 0.8);
            border: 1px solid rgba(250, 204, 21, 0.3);
            color: #e2e8f0;
            border-radius: 0.5rem;
            padding: 0.75rem;
            transition: all 0.3s ease;
        }
        .input-luxury:focus {
            outline: none;
            border-color: #facc15;
            box-shadow: 0 0 0 3px rgba(250, 204, 21, 0.1);
        }
    </style>
</head>
<body class="luxury-gradient min-h-screen text-gray-100">

    <!-- Navbar -->
    <nav class="bg-opacity-20 backdrop-blur-md border-b border-gray-700 py-4 px-6 sticky top-0 z-50">
        <div class="max-w-7xl mx-auto flex justify-between items-center">
            <div class="text-2xl font-bold gold tracking-wide">MEDICA ELITE</div>
            <div class="flex items-center space-x-6">
                <span class="text-gray-300">👨‍⚕️ ${sessionScope.userName}</span>
                <span class="text-sm text-gray-400">Infirmier(ère)</span>
                <a href="${pageContext.request.contextPath}/logout" class="text-red-400 hover:text-red-300 transition">
                    Déconnexion
                </a>
            </div>
        </div>
    </nav>

    <div class="max-w-7xl mx-auto p-6">
        <!-- Header -->
        <div class="mb-8">
            <h1 class="text-4xl font-bold mb-2">Tableau de Bord</h1>
            <p class="text-gray-400">Gestion de l'accueil des patients</p>
        </div>

        <!-- Stats Cards -->
        <div class="grid md:grid-cols-3 gap-6 mb-8">
            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">Patients du jour</p>
                        <p class="text-3xl font-bold gold">${patientsCount != null ? patientsCount : 0}</p>
                    </div>
                    <div class="text-4xl">👥</div>
                </div>
            </div>
            
            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">En attente</p>
                        <p class="text-3xl font-bold text-yellow-400">${enAttenteCount != null ? enAttenteCount : 0}</p>
                    </div>
                    <div class="text-4xl">⏱️</div>
                </div>
            </div>

            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">En consultation</p>
                        <p class="text-3xl font-bold text-green-400">${enConsultationCount != null ? enConsultationCount : 0}</p>
                    </div>
                    <div class="text-4xl">🏥</div>
                </div>
            </div>
        </div>

        <!-- Actions Rapides -->
        <div class="grid md:grid-cols-2 gap-6 mb-8">
            <a href="${pageContext.request.contextPath}/infirmier/enregistrer-patient" 
               class="card-luxury p-8 flex items-center space-x-4 hover:scale-105 transition cursor-pointer">
                <div class="text-5xl">➕</div>
                <div>
                    <h3 class="text-xl font-semibold mb-1">Enregistrer un Patient</h3>
                    <p class="text-gray-400 text-sm">Nouveau patient ou mise à jour signes vitaux</p>
                </div>
            </a>

            <a href="${pageContext.request.contextPath}/infirmier/listePatients
"
               class="card-luxury p-8 flex items-center space-x-4 hover:scale-105 transition cursor-pointer">
                <div class="text-5xl">📋</div>
                <div>
                    <h3 class="text-xl font-semibold mb-1">Liste des Patients</h3>
                    <p class="text-gray-400 text-sm">Voir tous les patients enregistrés aujourd'hui</p>
                </div>
            </a>
        </div>

        <!-- Messages d'alerte -->
        <c:if test="${not empty successMessage}">
            <div class="card-luxury p-4 mb-6 border-green-500">
                <div class="flex items-center space-x-3">
                    <span class="text-2xl">✅</span>
                    <p class="text-green-400">${successMessage}</p>
                </div>
            </div>
        </c:if>

        <c:if test="${not empty errorMessage}">
            <div class="card-luxury p-4 mb-6 border-red-500">
                <div class="flex items-center space-x-3">
                    <span class="text-2xl">❌</span>
                    <p class="text-red-400">${errorMessage}</p>
                </div>
            </div>
        </c:if>

        <!-- Derniers patients enregistrés -->
        <div class="card-luxury p-6">
            <h2 class="text-2xl font-semibold mb-6 gold">Derniers Patients Enregistrés</h2>
            
            <c:choose>
                <c:when test="${not empty recentPatients}">
                    <div class="overflow-x-auto">
                        <table class="w-full">
                            <thead>
                                <tr class="border-b border-gray-700">
                                    <th class="text-left py-3 px-4 text-gray-400 font-medium">Patient</th>
                                    <th class="text-left py-3 px-4 text-gray-400 font-medium">N° Sécurité Sociale</th>
                                    <th class="text-left py-3 px-4 text-gray-400 font-medium">Heure d'arrivée</th>
                                    <th class="text-left py-3 px-4 text-gray-400 font-medium">Signes Vitaux</th>
                                    <th class="text-left py-3 px-4 text-gray-400 font-medium">Statut</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach items="${recentPatients}" var="patient" varStatus="status">
                                    <tr class="border-b border-gray-800 hover:bg-gray-800/30 transition">
                                        <td class="py-4 px-4">
                                            <div class="font-semibold">${patient.nom} ${patient.prenom}</div>
                                            <div class="text-sm text-gray-400">
                                                ${patient.dateNaissance.dayOfMonth}/${patient.dateNaissance.monthValue}/${patient.dateNaissance.year}
                                            </div>
                                        </td>
                                        <td class="py-4 px-4 text-gray-300">${patient.numeroSecuriteSociale}</td>
                                        <td class="py-4 px-4 text-gray-300">
                                            ${patient.heureArrivee.hour}:${patient.heureArrivee.minute}
                                        </td>
                                        <td class="py-4 px-4">
                                             <c:if test="${patient.derniersSignesVitaux != null}">
                                                <div class="text-sm">
                                                    <span class="text-gray-400">TA:</span> ${patient.derniersSignesVitaux.tensionFormatee} |
                                                    <span class="text-gray-400">FC:</span> ${patient.derniersSignesVitaux.frequenceCardiaque} bpm
                                                </div>
                                            </c:if>
                                            <c:if test="${patient.derniersSignesVitaux == null}">
                                                <span class="text-gray-500">Non mesuré</span>
                                            </c:if>
                                        </td>
                                        <td class="py-4 px-4">
                                            <c:choose>
                                                <c:when test="${patient.statut == 'EN_ATTENTE'}">
                                                    <span class="px-3 py-1 rounded-full text-xs bg-yellow-900/30 text-yellow-400 border border-yellow-700">
                                                        En attente
                                                    </span>
                                                </c:when>
                                                <c:when test="${patient.statut == 'EN_CONSULTATION'}">
                                                    <span class="px-3 py-1 rounded-full text-xs bg-blue-900/30 text-blue-400 border border-blue-700">
                                                        En consultation
                                                    </span>
                                                </c:when>
                                                <c:when test="${patient.statut == 'TERMINE'}">
                                                    <span class="px-3 py-1 rounded-full text-xs bg-green-900/30 text-green-400 border border-green-700">
                                                        Terminé
                                                    </span>
                                                </c:when>
                                            </c:choose>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-12 text-gray-400">
                        <div class="text-6xl mb-4">📋</div>
                        <p>Aucun patient enregistré aujourd'hui</p>
                        <a href="${pageContext.request.contextPath}/infirmier/enregistrer-patient" 
                           class="btn-luxury inline-block px-6 py-2 mt-4">
                            Enregistrer un patient
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