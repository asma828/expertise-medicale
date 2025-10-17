<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Demandes d'Expertise - MEDICA ELITE</title>
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
            <h1 class="text-4xl font-bold mb-2 gold">📋 Mes Demandes d'Expertise</h1>
            <p class="text-gray-400">Gérer les demandes des médecins généralistes</p>
        </div>

        <!-- Filtres -->
        <div class="card-luxury p-6 mb-6">
            <div class="grid md:grid-cols-3 gap-4">
                <div>
                    <select id="statutFilter" class="input-luxury w-full" onchange="filterDemandes()">
                        <option value="">Tous les statuts</option>
                        <option value="EN_ATTENTE">En attente</option>
                        <option value="TERMINEE">Terminées</option>
                    </select>
                </div>
                <div>
                    <input type="text"
                           id="searchInput"
                           placeholder="🔍 Rechercher un patient..."
                           class="input-luxury w-full"
                           onkeyup="filterDemandes()">
                </div>
                <div class="flex items-center justify-end space-x-2">
                    <span class="text-gray-400">Total:</span>
                    <span class="text-2xl font-bold gold" id="totalCount">${demandes.size()}</span>
                    <span class="text-gray-400">demande(s)</span>
                </div>
            </div>
        </div>

        <!-- Stats -->
        <div class="grid md:grid-cols-2 gap-6 mb-6">
            <div class="card-luxury p-4">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm">En attente de réponse</p>
                        <p class="text-2xl font-bold text-orange-400">${enAttenteCount}</p>
                    </div>
                    <div class="text-3xl">⏱️</div>
                </div>
            </div>
            <div class="card-luxury p-4">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm">Terminées</p>
                        <p class="text-2xl font-bold text-green-400">${termineesCount}</p>
                    </div>
                    <div class="text-3xl">✅</div>
                </div>
            </div>
        </div>

        <!-- Liste des demandes -->
        <div class="space-y-4">
            <c:choose>
                <c:when test="${not empty demandes}">
                    <c:forEach items="${demandes}" var="demande">
                        <div class="card-luxury p-6 demande-card"
                             data-statut="${demande.statut}"
                             data-patient="${demande.consultation.patient.nom} ${demande.consultation.patient.prenom}">

                            <div class="flex justify-between items-start">
                                <div class="flex-1">
                                    <!-- Header -->
                                    <div class="flex items-center space-x-3 mb-4">
                                        <h3 class="text-2xl font-semibold">
                                            ${demande.consultation.patient.nom} ${demande.consultation.patient.prenom}
                                        </h3>
                                        <c:choose>
                                            <c:when test="${demande.statut == 'EN_ATTENTE'}">
                                                <span class="px-3 py-1 rounded-full text-xs bg-orange-900/30 text-orange-400 border border-orange-700">
                                                    ⏱️ En attente
                                                </span>
                                            </c:when>
                                            <c:when test="${demande.statut == 'TERMINEE'}">
                                                <span class="px-3 py-1 rounded-full text-xs bg-green-900/30 text-green-400 border border-green-700">
                                                    ✅ Terminée
                                                </span>
                                            </c:when>
                                        </c:choose>
                                    </div>

                                    <!-- Infos principales -->
                                    <div class="grid md:grid-cols-4 gap-4 mb-4">
                                        <div>
                                            <p class="text-gray-400 text-sm">Demandé par</p>
                                            <p class="font-semibold">Dr. ${demande.medecinDemandeur.nomComplet}</p>
                                        </div>
                                        <div>
                                            <p class="text-gray-400 text-sm">Date demande</p>
                                            <p class="font-semibold">
                                                    ${demande.dateDemande.toString().replace('T', ' ')}
                                            </p>
                                        </div>
                                        <div>
                                            <p class="text-gray-400 text-sm">Créneau</p>
                                            <p class="font-semibold text-yellow-400">${demande.creneau.heureFormatee}</p>
                                            <p class="text-xs text-gray-500">
                                                ${demande.creneau.dateFormatee}
                                            </p>
                                        </div>
                                        <div>
                                            <p class="text-gray-400 text-sm">Rémunération</p>
                                            <p class="font-semibold gold">${utilisateur.tarif} DH</p>
                                        </div>
                                    </div>

                                    <!-- Question -->
                                    <div class="bg-gray-800/40 p-4 rounded-lg mb-3">
                                        <p class="text-gray-400 text-sm mb-2">❓ Question posée:</p>
                                        <p class="text-gray-200">${demande.questionPosee}</p>
                                    </div>

                                    <!-- Données supplémentaires -->
                                    <c:if test="${not empty demande.donneesAnalyses}">
                                        <div class="bg-blue-900/10 p-4 rounded-lg border border-blue-700/30">
                                            <p class="text-blue-400 text-sm mb-2">📊 Données et analyses:</p>
                                            <p class="text-gray-200 text-sm">${demande.donneesAnalyses}</p>
                                        </div>
                                    </c:if>

                                    <!-- Réponse si terminée -->
                                    <c:if test="${demande.statut == 'TERMINEE' && not empty demande.avisMedical}">
                                        <div class="mt-4 bg-green-900/10 p-4 rounded-lg border border-green-700/30">
                                            <p class="text-green-400 text-sm mb-2">✅ Votre avis:</p>
                                            <p class="text-gray-200">${demande.avisMedical}</p>
                                            <c:if test="${not empty demande.recommandations}">
                                                <p class="text-gray-400 text-sm mt-3 mb-1">Recommandations:</p>
                                                <p class="text-gray-200">${demande.recommandations}</p>
                                            </c:if>
                                            <p class="text-xs text-gray-500 mt-3">
                                                Répondu le <fmt:formatDate value="${demande.dateReponse}" pattern="dd/MM/yyyy à HH:mm" />
                                            </p>
                                        </div>
                                    </c:if>
                                </div>

                                <!-- Action button -->
                                <div class="ml-6">
                                    <c:choose>
                                        <c:when test="${demande.statut == 'EN_ATTENTE'}">
                                            <a href="${pageContext.request.contextPath}/specialiste/expertise/${demande.id}"
                                               class="btn-luxury px-6 py-3 inline-block">
                                                Répondre
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <a href="${pageContext.request.contextPath}/specialiste/expertise/${demande.id}"
                                               class="btn-luxury px-6 py-3 inline-block opacity-70">
                                                Voir détails
                                            </a>
                                        </c:otherwise>
                                    </c:choose>
                                </div>
                            </div>
                        </div>
                    </c:forEach>

                    <div class="text-center mt-6 text-gray-400 text-sm">
                        <span id="visibleCount">${demandes.size()}</span> demande(s) affichée(s)
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="card-luxury p-12 text-center">
                        <div class="text-6xl mb-4">📋</div>
                        <h3 class="text-2xl font-semibold mb-2">Aucune Demande</h3>
                        <p class="text-gray-400">Les demandes des médecins généralistes apparaîtront ici</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <script>
        function filterDemandes() {
            const statutFilter = document.getElementById('statutFilter').value;
            const searchInput = document.getElementById('searchInput').value.toLowerCase();
            const cards = document.querySelectorAll('.demande-card');
            let visibleCount = 0;

            cards.forEach(card => {
                const statut = card.dataset.statut;
                const patient = card.dataset.patient.toLowerCase();

                const matchStatut = !statutFilter || statut === statutFilter;
                const matchSearch = !searchInput || patient.includes(searchInput);

                if (matchStatut && matchSearch) {
                    card.style.display = '';
                    visibleCount++;
                } else {
                    card.style.display = 'none';
                }
            });

            document.getElementById('visibleCount').textContent = visibleCount;
        }
    </script>

    <!-- Footer -->
    <footer class="text-center py-6 mt-12 border-t border-gray-700 text-gray-400 text-sm">
        © 2025 MEDICA ELITE — Tous droits réservés.
    </footer>

</body>
</html>