<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Mes Créneaux - MEDICA ELITE</title>
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
        .label-luxury {
            color: #cbd5e1;
            font-weight: 500;
            margin-bottom: 0.5rem;
            display: block;
        }
        .creneau-disponible {
            background: rgba(74, 222, 128, 0.1);
            border: 1px solid rgba(74, 222, 128, 0.3);
        }
        .creneau-reserve {
            background: rgba(251, 191, 36, 0.1);
            border: 1px solid rgba(251, 191, 36, 0.3);
        }
        .creneau-passe {
            background: rgba(107, 114, 128, 0.1);
            border: 1px solid rgba(107, 114, 128, 0.3);
            opacity: 0.6;
        }
        .modal {
            display: none;
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0, 0, 0, 0.8);
            z-index: 1000;
            align-items: center;
            justify-content: center;
        }
        .modal.active {
            display: flex;
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
            <h1 class="text-4xl font-bold mb-2 gold">📅 Mes Créneaux de Disponibilité</h1>
            <p class="text-gray-400">Gérer votre planning de télé-expertise</p>
        </div>

        <!-- Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="card-luxury p-4 mb-6 border-green-500">
                <p class="text-green-400">${sessionScope.successMessage}</p>
            </div>
            <c:remove var="successMessage" scope="session" />
        </c:if>

        <c:if test="${not empty error}">
            <div class="card-luxury p-4 mb-6 border-red-500">
                <p class="text-red-400">${error}</p>
            </div>
        </c:if>

        <!-- Stats -->
        <div class="grid md:grid-cols-3 gap-6 mb-8">
            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">Disponibles</p>
                        <p class="text-3xl font-bold text-green-400">${disponiblesCount}</p>
                    </div>
                    <div class="text-4xl">✅</div>
                </div>
            </div>
            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">Réservés</p>
                        <p class="text-3xl font-bold text-yellow-400">${reservesCount}</p>
                    </div>
                    <div class="text-4xl">📌</div>
                </div>
            </div>
            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">Passés</p>
                        <p class="text-3xl font-bold text-gray-400">${passesCount}</p>
                    </div>
                    <div class="text-4xl">⏱️</div>
                </div>
            </div>
        </div>

        <!-- Bouton ajouter -->
        <div class="mb-6 flex justify-end">
            <button onclick="openModal('modalAjouterCreneau')" class="btn-luxury px-6 py-3">
                ➕ Ajouter des Créneaux
            </button>
        </div>

        <!-- Liste des créneaux groupés par date -->
        <div class="space-y-6">
            <c:choose>
                <c:when test="${not empty creneauxParDate}">
                    <c:forEach items="${creneauxParDate}" var="entry">
                        <div class="card-luxury p-6">
                            <h3 class="text-xl font-semibold mb-4 gold">
                                📅 ${entry.key.toString()}
                            </h3>

                            <div class="grid md:grid-cols-4 gap-4">
                                <c:forEach items="${entry.value}" var="creneau">
                                    <div class="p-4 rounded-lg
                                        ${creneau.statut == 'DISPONIBLE' ? 'creneau-disponible' : ''}
                                        ${creneau.statut == 'RESERVE' ? 'creneau-reserve' : ''}
                                        ${creneau.statut == 'PASSE' ? 'creneau-passe' : ''}">

                                        <div class="flex justify-between items-start mb-2">
                                            <div>
                                                <p class="font-bold text-lg">${creneau.heureFormatee}</p>
                                                <p class="text-xs text-gray-400">${creneau.duree} minutes</p>
                                            </div>
                                            <c:choose>
                                                <c:when test="${creneau.statut == 'DISPONIBLE'}">
                                                    <span class="text-2xl">✅</span>
                                                </c:when>
                                                <c:when test="${creneau.statut == 'RESERVE'}">
                                                    <span class="text-2xl">📌</span>
                                                </c:when>
                                                <c:when test="${creneau.statut == 'PASSE'}">
                                                    <span class="text-2xl">⏱️</span>
                                                </c:when>
                                            </c:choose>
                                        </div>

                                        <c:choose>
                                            <c:when test="${creneau.statut == 'DISPONIBLE'}">
                                                <p class="text-xs text-green-400 mb-2">Disponible</p>
                                                <form method="post" action="${pageContext.request.contextPath}/specialiste/mescreneaux" class="inline">
                                                    <input type="hidden" name="action" value="supprimer">
                                                    <input type="hidden" name="creneauId" value="${creneau.id}">
                                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                                    <button type="submit" class="text-red-400 text-xs hover:text-red-300">
                                                        🗑️ Supprimer
                                                    </button>
                                                </form>
                                            </c:when>
                                            <c:when test="${creneau.statut == 'RESERVE'}">
                                                <p class="text-xs text-yellow-400">Réservé</p>
                                            </c:when>
                                            <c:when test="${creneau.statut == 'PASSE'}">
                                                <p class="text-xs text-gray-500">Passé</p>
                                            </c:when>
                                        </c:choose>
                                    </div>
                                </c:forEach>
                            </div>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <div class="card-luxury p-12 text-center">
                        <div class="text-6xl mb-4">📅</div>
                        <h3 class="text-2xl font-semibold mb-2">Aucun Créneau</h3>
                        <p class="text-gray-400 mb-6">Commencez par ajouter des créneaux de disponibilité</p>
                        <button onclick="openModal('modalAjouterCreneau')" class="btn-luxury px-8 py-3">
                            Ajouter mes premiers créneaux
                        </button>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </div>

    <!-- Modal Ajouter Créneaux -->
    <div id="modalAjouterCreneau" class="modal">
        <div class="card-luxury p-8 max-w-2xl w-full mx-4">
            <h3 class="text-2xl font-semibold mb-6 gold">Ajouter des Créneaux</h3>

            <form method="post" action="${pageContext.request.contextPath}/specialiste/mescreneaux">
                <input type="hidden" name="action" value="ajouter">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="space-y-6">
                    <div>
                        <label class="label-luxury">Date *</label>
                        <input type="date"
                               name="date"
                               class="input-luxury"
                               min="${dateMinimale}"
                               required>
                    </div>

                    <div class="grid grid-cols-2 gap-4">
                        <div>
                            <label class="label-luxury">Heure de début *</label>
                            <input type="time" name="heureDebut" class="input-luxury" required>
                        </div>
                        <div>
                            <label class="label-luxury">Heure de fin *</label>
                            <input type="time" name="heureFin" class="input-luxury" required>
                        </div>
                    </div>

                    <div>
                        <label class="label-luxury">Durée par créneau</label>
                        <select name="duree" class="input-luxury">
                            <option value="15">15 minutes</option>
                            <option value="30" selected>30 minutes</option>
                            <option value="45">45 minutes</option>
                            <option value="60">60 minutes</option>
                        </select>
                        <p class="text-gray-400 text-xs mt-2">
                            Le système créera automatiquement des créneaux de cette durée dans la plage horaire choisie
                        </p>
                    </div>

                    <div class="bg-blue-900/10 p-4 rounded-lg border border-blue-700/30">
                        <p class="text-blue-400 text-sm">💡 Exemple:</p>
                        <p class="text-gray-300 text-sm mt-2">
                            De 09:00 à 12:00 avec créneaux de 30 min = 6 créneaux créés
                            <br>(09:00-09:30, 09:30-10:00, 10:00-10:30, etc.)
                        </p>
                    </div>

                    <div class="flex space-x-4">
                        <button type="submit" class="btn-luxury px-6 py-3">
                            ✅ Créer les Créneaux
                        </button>
                        <button type="button" onclick="closeModal('modalAjouterCreneau')" class="btn-secondary px-6 py-3">
                            Annuler
                        </button>
                    </div>
                </div>
            </form>
        </div>
    </div>

    <script>
        function openModal(id) {
            document.getElementById(id).classList.add('active');
        }

        function closeModal(id) {
            document.getElementById(id).classList.remove('active');
        }

        // Fermer modal en cliquant dehors
        document.querySelectorAll('.modal').forEach(modal => {
            modal.addEventListener('click', function(e) {
                if (e.target === this) {
                    closeModal(this.id);
                }
            });
        });
    </script>

    <!-- Footer -->
    <footer class="text-center py-6 mt-12 border-t border-gray-700 text-gray-400 text-sm">
        © 2025 MEDICA ELITE — Tous droits réservés.
    </footer>

</body>
</html>