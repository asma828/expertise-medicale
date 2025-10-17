<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Statistiques - MEDICA ELITE</title>
    <script src="https://cdn.tailwindcss.com"></script>
    <link rel="icon" href="https://cdn-icons-png.flaticon.com/512/2966/2966327.png" />
    <style>
        .luxury-gradient {
            background: linear-gradient(135deg, #0f172a, #1e293b, #334155);
        }
        .gold { color: #facc15; }
        .card-luxury {
            background: rgba(30, 41, 59, 0.6);
            backdrop-filter: blur(10px);
            border: 1px solid rgba(250, 204, 21, 0.2);
            border-radius: 1rem;
        }
    </style>
</head>
<body class="luxury-gradient min-h-screen text-gray-100">

    <!-- Navbar -->
    <nav class="bg-opacity-20 backdrop-blur-md border-b border-gray-700 py-4 px-6 sticky top-0 z-50">
        <div class="max-w-7xl mx-auto flex justify-between items-center">
            <div class="text-2xl font-bold gold tracking-wide">MEDICA ELITE</div>
            <div class="flex items-center space-x-6">
                <a href="${pageContext.request.contextPath}/specialiste/dashboard"
                   class="text-gray-300 hover:text-yellow-400 transition">← Dashboard</a>
                <span class="text-gray-300">👨‍⚕️ Dr. ${sessionScope.userName}</span>
            </div>
        </div>
    </nav>

    <div class="max-w-7xl mx-auto p-6">
        <!-- Header -->
        <div class="mb-8">
            <h1 class="text-4xl font-bold mb-2 gold">📈 Mes Statistiques</h1>
            <p class="text-gray-400">Vue d'ensemble de votre activité de télé-expertise</p>
        </div>

        <!-- Période -->
        <div class="card-luxury p-6 mb-8">
            <div class="flex items-center space-x-4">
                <span class="text-gray-400">📅 Période:</span>
                <div class="flex space-x-4">
                    <a href="?periode=mois"
                       class="px-4 py-2 rounded-full ${param.periode == 'mois' || empty param.periode ? 'bg-yellow-400 text-gray-900' : 'bg-gray-700 text-gray-300'}">
                        Ce mois
                    </a>
                    <a href="?periode=annee"
                       class="px-4 py-2 rounded-full ${param.periode == 'annee' ? 'bg-yellow-400 text-gray-900' : 'bg-gray-700 text-gray-300'}">
                        Cette année
                    </a>
                    <a href="?periode=total"
                       class="px-4 py-2 rounded-full ${param.periode == 'total' ? 'bg-yellow-400 text-gray-900' : 'bg-gray-700 text-gray-300'}">
                        Total
                    </a>
                </div>
            </div>
        </div>

        <!-- Stats principales -->
        <div class="grid md:grid-cols-4 gap-6 mb-8">
            <div class="card-luxury p-6">
                <div class="text-center">
                    <div class="text-4xl mb-3">📋</div>
                    <p class="text-gray-400 text-sm mb-2">Expertises Totales</p>
                    <p class="text-4xl font-bold gold">${stats.totalExpertises}</p>
                </div>
            </div>

            <div class="card-luxury p-6">
                <div class="text-center">
                    <div class="text-4xl mb-3">✅</div>
                    <p class="text-gray-400 text-sm mb-2">Terminées</p>
                    <p class="text-4xl font-bold text-green-400">${stats.expertisesTerminees}</p>
                </div>
            </div>

            <div class="card-luxury p-6">
                <div class="text-center">
                    <div class="text-4xl mb-3">⏱️</div>
                    <p class="text-gray-400 text-sm mb-2">En Attente</p>
                    <p class="text-4xl font-bold text-orange-400">${stats.expertisesEnAttente}</p>
                </div>
            </div>

            <div class="card-luxury p-6">
                <div class="text-center">
                    <div class="text-4xl mb-3">💰</div>
                    <p class="text-gray-400 text-sm mb-2">Revenus</p>
                    <p class="text-4xl font-bold gold">${stats.revenuTotal} DH</p>
                </div>
            </div>
        </div>

        <!-- Détails revenus -->
        <div class="grid md:grid-cols-2 gap-6 mb-8">
            <div class="card-luxury p-6">
                <h3 class="text-2xl font-semibold mb-6 gold">💵 Détails Financiers</h3>

                <div class="space-y-4">
                    <div class="flex justify-between items-center p-4 bg-gray-800/40 rounded-lg">
                        <div>
                            <p class="text-gray-400 text-sm">Tarif par Expertise</p>
                            <p class="text-xl font-semibold">${utilisateur.tarif} DH</p>
                        </div>
                        <div class="text-3xl">💳</div>
                    </div>

                    <div class="flex justify-between items-center p-4 bg-gray-800/40 rounded-lg">
                        <div>
                            <p class="text-gray-400 text-sm">Revenu Moyen/Jour</p>
                            <p class="text-xl font-semibold">${stats.revenuMoyenParJour} DH</p>
                        </div>
                        <div class="text-3xl">📊</div>
                    </div>

                    <div class="flex justify-between items-center p-4 bg-gray-800/40 rounded-lg">
                        <div>
                            <p class="text-gray-400 text-sm">Projection Mensuelle</p>
                            <p class="text-xl font-semibold">${stats.projectionMensuelle} DH</p>
                        </div>
                        <div class="text-3xl">🎯</div>
                    </div>
                </div>
            </div>

            <div class="card-luxury p-6">
                <h3 class="text-2xl font-semibold mb-6 gold">⏱️ Activité</h3>

                <div class="space-y-4">
                    <div class="flex justify-between items-center p-4 bg-gray-800/40 rounded-lg">
                        <div>
                            <p class="text-gray-400 text-sm">Taux de Réponse</p>
                            <p class="text-xl font-semibold">${stats.tauxReponse}%</p>
                        </div>
                        <div class="text-3xl">📈</div>
                    </div>

                    <div class="flex justify-between items-center p-4 bg-gray-800/40 rounded-lg">
                        <div>
                            <p class="text-gray-400 text-sm">Temps Moyen de Réponse</p>
                            <p class="text-xl font-semibold">${stats.tempsMoyenReponse} heures</p>
                        </div>
                        <div class="text-3xl">⏰</div>
                    </div>

                    <div class="flex justify-between items-center p-4 bg-gray-800/40 rounded-lg">
                        <div>
                            <p class="text-gray-400 text-sm">Créneaux Disponibles</p>
                            <p class="text-xl font-semibold">${stats.creneauxDisponibles}</p>
                        </div>
                        <div class="text-3xl">📅</div>
                    </div>
                </div>
            </div>
        </div>

        <!-- Top médecins demandeurs -->
        <div class="card-luxury p-6 mb-8">
            <h3 class="text-2xl font-semibold mb-6 gold">👨‍⚕️ Médecins Généralistes Fréquents</h3>

            <c:choose>
                <c:when test="${not empty stats.topMedecins}">
                    <div class="space-y-3">
                        <c:forEach items="${stats.topMedecins}" var="medecin" varStatus="status">
                            <div class="flex items-center justify-between p-4 bg-gray-800/40 rounded-lg">
                                <div class="flex items-center space-x-4">
                                    <div class="w-10 h-10 rounded-full bg-gradient-to-br from-yellow-400 to-yellow-600 flex items-center justify-center text-gray-900 font-bold">
                                        ${status.index + 1}
                                    </div>
                                    <div>
                                        <p class="font-semibold">Dr. ${medecin.nom}</p>
                                        <p class="text-sm text-gray-400">${medecin.email}</p>
                                    </div>
                                </div>
                                <div class="text-right">
                                    <p class="text-2xl font-bold gold">${medecin.nombreDemandes}</p>
                                    <p class="text-xs text-gray-400">demandes</p>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="text-center text-gray-400 py-8">Aucune donnée disponible</p>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Évolution mensuelle -->
        <div class="card-luxury p-6">
            <h3 class="text-2xl font-semibold mb-6 gold">📊 Évolution des Expertises</h3>

            <c:choose>
                <c:when test="${not empty stats.evolutionMensuelle}">
                    <div class="space-y-3">
                        <c:forEach items="${stats.evolutionMensuelle}" var="mois">
                            <div class="flex items-center space-x-4">
                                <div class="w-32 text-gray-400 text-sm">${mois.libelle}</div>
                                <div class="flex-1 bg-gray-800 rounded-full h-8 relative overflow-hidden">
                                    <div class="bg-gradient-to-r from-yellow-400 to-yellow-600 h-full rounded-full transition-all duration-500"
                                         style="width: ${mois.pourcentage}%"></div>
                                    <div class="absolute inset-0 flex items-center justify-center text-sm font-semibold">
                                        ${mois.nombre} expertise(s)
                                    </div>
                                </div>
                                <div class="w-20 text-right font-bold gold">${mois.revenu} DH</div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <p class="text-center text-gray-400 py-8">Aucune donnée disponible</p>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Informations complémentaires -->
        <div class="grid md:grid-cols-3 gap-6 mt-8">
            <div class="card-luxury p-6 text-center">
                <div class="text-4xl mb-3">🌟</div>
                <p class="text-gray-400 text-sm mb-2">Score de Réactivité</p>
                <p class="text-3xl font-bold gold">${stats.scoreReactivite}/10</p>
                <p class="text-xs text-gray-500 mt-2">Basé sur le temps de réponse</p>
            </div>

            <div class="card-luxury p-6 text-center">
                <div class="text-4xl mb-3">🎖️</div>
                <p class="text-gray-400 text-sm mb-2">Rang</p>
                <p class="text-3xl font-bold gold">#${stats.rang}</p>
                <p class="text-xs text-gray-500 mt-2">Parmi les spécialistes ${utilisateur.specialite.label}</p>
            </div>

            <div class="card-luxury p-6 text-center">
                <div class="text-4xl mb-3">📈</div>
                <p class="text-gray-400 text-sm mb-2">Progression</p>
                <p class="text-3xl font-bold ${stats.progression >= 0 ? 'text-green-400' : 'text-red-400'}">
                    ${stats.progression >= 0 ? '+' : ''}${stats.progression}%
                </p>
                <p class="text-xs text-gray-500 mt-2">vs mois précédent</p>
            </div>
        </div>
    </div>

    <!-- Footer -->
    <footer class="text-center py-6 mt-12 border-t border-gray-700 text-gray-400 text-sm">
        © 2025 MEDICA ELITE — Tous droits réservés.
    </footer>

</body>
</html>