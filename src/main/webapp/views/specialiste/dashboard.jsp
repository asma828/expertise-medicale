<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Spécialiste - MEDICA ELITE</title>
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
            transition: all 0.3s ease;
        }
        .card-luxury:hover {
            border-color: rgba(250, 204, 21, 0.4);
            transform: translateY(-4px);
            box-shadow: 0 10px 30px rgba(250, 204, 21, 0.1);
        }
    </style>
</head>
<body class="luxury-gradient min-h-screen text-gray-100">

    <!-- Navbar -->
    <nav class="bg-opacity-20 backdrop-blur-md border-b border-gray-700 py-4 px-6 sticky top-0 z-50">
        <div class="max-w-7xl mx-auto flex justify-between items-center">
            <div class="text-2xl font-bold gold tracking-wide">MEDICA ELITE</div>
            <div class="flex items-center space-x-6">
                <span class="text-gray-300">👨‍⚕️ Dr. ${sessionScope.userName}</span>
                <span class="text-sm text-gray-400">Spécialiste - ${utilisateur.specialite.label}</span>
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
            <p class="text-gray-400">Gestion des demandes d'expertise et créneaux</p>
        </div>

        <!-- Messages -->
        <c:if test="${not empty sessionScope.successMessage}">
            <div class="card-luxury p-4 mb-6 border-green-500">
                <div class="flex items-center space-x-3">
                    <span class="text-2xl">✅</span>
                    <p class="text-green-400">${sessionScope.successMessage}</p>
                </div>
            </div>
            <c:remove var="successMessage" scope="session" />
        </c:if>

        <!-- Stats Cards -->
        <div class="grid md:grid-cols-4 gap-6 mb-8">
            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">Demandes en attente</p>
                        <p class="text-3xl font-bold text-orange-400">${demandesEnAttenteCount != null ? demandesEnAttenteCount : 0}</p>
                    </div>
                    <div class="text-4xl">🔔</div>
                </div>
            </div>
            
            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">Expertises ce mois</p>
                        <p class="text-3xl font-bold text-blue-400">${expertisesMoisCount != null ? expertisesMoisCount : 0}</p>
                    </div>
                    <div class="text-4xl">📊</div>
                </div>
            </div>

            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">Créneaux disponibles</p>
                        <p class="text-3xl font-bold text-green-400">${creneauxDisponiblesCount != null ? creneauxDisponiblesCount : 0}</p>
                    </div>
                    <div class="text-4xl">📅</div>
                </div>
            </div>

            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">Revenus du mois</p>
                        <p class="text-3xl font-bold gold">${revenusMois != null ? revenusMois : 0} DH</p>
                    </div>
                    <div class="text-4xl">💰</div>
                </div>
            </div>
        </div>

        <!-- Profil Spécialiste -->
        <div class="card-luxury p-6 mb-8">
            <div class="flex justify-between items-start">
                <div>
                    <h2 class="text-2xl font-semibold mb-4 gold">👨‍⚕️ Mon Profil</h2>
                    <div class="grid md:grid-cols-3 gap-6">
                        <div>
                            <p class="text-gray-400 text-sm mb-1">Spécialité</p>
                            <p class="text-xl font-semibold">${utilisateur.specialite.label}</p>
                        </div>
                        <div>
                            <p class="text-gray-400 text-sm mb-1">Tarif Consultation</p>
                            <p class="text-xl font-semibold text-yellow-400">
                                ${utilisateur.tarif != null ? utilisateur.tarif : 'Non défini'} DH
                            </p>
                        </div>
                        <div>
                            <p class="text-gray-400 text-sm mb-1">Durée Consultation</p>
                            <p class="text-xl font-semibold">${utilisateur.dureeConsultation} minutes</p>
                        </div>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/specialiste/ConfigurerProfil"
                   class="btn-luxury px-6 py-3">
                    ⚙️ Configurer
                </a>
            </div>
        </div>

        <!-- Actions Rapides -->
        <div class="grid md:grid-cols-3 gap-6 mb-8">
            <a href="${pageContext.request.contextPath}/specialiste/demandesexpertise"
               class="card-luxury p-8 flex items-center space-x-4 cursor-pointer">
                <div class="text-5xl">📋</div>
                <div>
                    <h3 class="text-xl font-semibold mb-1">Mes Demandes</h3>
                    <p class="text-gray-400 text-sm">Voir toutes les demandes d'expertise</p>
                    <c:if test="${demandesEnAttenteCount > 0}">
                        <p class="text-orange-400 text-sm mt-2 font-semibold">
                            ${demandesEnAttenteCount} en attente
                        </p>
                    </c:if>
                </div>
            </a>

            <a href="${pageContext.request.contextPath}/specialiste/mescreneaux"
               class="card-luxury p-8 flex items-center space-x-4 cursor-pointer">
                <div class="text-5xl">📅</div>
                <div>
                    <h3 class="text-xl font-semibold mb-1">Mes Créneaux</h3>
                    <p class="text-gray-400 text-sm">Gérer ma disponibilité</p>
                </div>
            </a>

            <a href="${pageContext.request.contextPath}/specialiste/statistiques" 
               class="card-luxury p-8 flex items-center space-x-4 cursor-pointer">
                <div class="text-5xl">📈</div>
                <div>
                    <h3 class="text-xl font-semibold mb-1">Statistiques</h3>
                    <p class="text-gray-400 text-sm">Voir mes performances</p>
                </div>
            </a>
        </div>

        <!-- Demandes récentes en attente -->
        <div class="card-luxury p-6">
            <div class="flex justify-between items-center mb-6">
                <h2 class="text-2xl font-semibold gold">🔔 Nouvelles Demandes d'Expertise</h2>
                <a href="${pageContext.request.contextPath}/specialiste/demandesexpertise"
                   class="text-yellow-400 hover:text-yellow-300 transition text-sm">
                    Voir toutes →
                </a>
            </div>
            
            <c:choose>
                <c:when test="${not empty demandesRecentes}">
                    <div class="space-y-4">
                        <c:forEach items="${demandesRecentes}" var="demande">
                            <div class="bg-gray-800/40 p-6 rounded-lg hover:bg-gray-800/60 transition">
                                <div class="flex justify-between items-start">
                                    <div class="flex-1">
                                        <div class="flex items-center space-x-3 mb-3">
                                            <h3 class="text-xl font-semibold">
                                                ${demande.consultation.patient.nom} ${demande.consultation.patient.prenom}
                                            </h3>
                                            <span class="px-3 py-1 rounded-full text-xs bg-orange-900/30 text-orange-400 border border-orange-700">
                                                En attente
                                            </span>
                                        </div>

                                        <div class="grid md:grid-cols-3 gap-4 mb-3">
                                            <div>
                                                <span class="text-gray-400 text-sm">Demandé par:</span>
                                                <p class="font-medium">Dr. ${demande.medecinDemandeur.nomComplet}</p>
                                            </div>
                                            <div>
                                                <span class="text-gray-400 text-sm">Date:</span>
                                                <p class="font-medium">
                                                    <fmt:formatDate value="${demande.dateDemande}" pattern="dd/MM/yyyy HH:mm" />
                                                </p>
                                            </div>
                                            <div>
                                                <span class="text-gray-400 text-sm">Créneau:</span>
                                                <p class="font-medium text-yellow-400">${demande.creneau.heureFormatee}</p>
                                            </div>
                                        </div>

                                        <div class="bg-gray-900/50 p-4 rounded-lg">
                                            <p class="text-gray-400 text-sm mb-1">Question posée:</p>
                                            <p class="text-gray-200">${demande.questionPosee}</p>
                                        </div>
                                    </div>

                                    <a href="${pageContext.request.contextPath}/specialiste/expertise/${demande.id}" 
                                       class="btn-luxury px-6 py-3 ml-4">
                                        Consulter
                                    </a>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="text-center py-12 text-gray-400">
                        <div class="text-6xl mb-4">✨</div>
                        <p>Aucune nouvelle demande d'expertise</p>
                        <p class="text-sm mt-2">Les demandes des médecins généralistes apparaîtront ici</p>
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