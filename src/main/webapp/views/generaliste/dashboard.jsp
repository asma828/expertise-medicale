<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard Généraliste - MEDICA ELITE</title>
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
                <span class="text-sm text-gray-400">Médecin Généraliste</span>
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
            <p class="text-gray-400">Gestion des consultations et télé-expertise</p>
        </div>

        <!-- Stats Cards -->
        <div class="grid md:grid-cols-4 gap-6 mb-8">
            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">Patients en attente</p>
                        <p class="text-3xl font-bold text-yellow-400">${patientsEnAttenteCount != null ? patientsEnAttenteCount : 0}</p>
                    </div>
                    <div class="text-4xl">⏱️</div>
                </div>
            </div>
            
            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">Consultations du jour</p>
                        <p class="text-3xl font-bold text-blue-400">${consultationsDuJourCount != null ? consultationsDuJourCount : 0}</p>
                    </div>
                    <div class="text-4xl">📋</div>
                </div>
            </div>

            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">En attente avis</p>
                        <p class="text-3xl font-bold text-orange-400">${consultationsEnAttenteAvisCount != null ? consultationsEnAttenteAvisCount : 0}</p>
                    </div>
                    <div class="text-4xl">🔄</div>
                </div>
            </div>

            <div class="card-luxury p-6">
                <div class="flex items-center justify-between">
                    <div>
                        <p class="text-gray-400 text-sm mb-1">Terminées</p>
                        <p class="text-3xl font-bold text-green-400">${consultationsTermineesCount != null ? consultationsTermineesCount : 0}</p>
                    </div>
                    <div class="text-4xl">✅</div>
                </div>
            </div>
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

        <!-- Actions Rapides -->
        <div class="grid md:grid-cols-2 gap-6 mb-8">
            <a href="${pageContext.request.contextPath}/generaliste/patientsenattente"
               class="card-luxury p-8 flex items-center space-x-4 cursor-pointer">
                <div class="text-5xl">👥</div>
                <div>
                    <h3 class="text-xl font-semibold mb-1">File d'Attente</h3>
                    <p class="text-gray-400 text-sm">Voir les patients en attente de consultation</p>
                    <c:if test="${patientsEnAttenteCount > 0}">
                        <p class="text-yellow-400 text-sm mt-2 font-semibold">
                            ${patientsEnAttenteCount} patient(s) en attente
                        </p>
                    </c:if>
                </div>
            </a>

            <a href="${pageContext.request.contextPath}/generaliste/mes-consultations" 
               class="card-luxury p-8 flex items-center space-x-4 cursor-pointer">
                <div class="text-5xl">📝</div>
                <div>
                    <h3 class="text-xl font-semibold mb-1">Mes Consultations</h3>
                    <p class="text-gray-400 text-sm">Voir toutes mes consultations en cours</p>
                </div>
            </a>
        </div>

        <!-- Prochains patients en attente -->
        <div class="card-luxury p-6">
            <div class="flex justify-between items-center mb-6">
                <h2 class="text-2xl font-semibold gold">👥 Prochains Patients en Attente</h2>
                <a href="${pageContext.request.contextPath}/generaliste/patientsenattente"
                   class="text-yellow-400 hover:text-yellow-300 transition text-sm">
                    Voir tous →
                </a>
            </div>
            
            <c:choose>
                <c:when test="${not empty patientsEnAttente}">
                    <div class="space-y-4">
                        <c:forEach items="${patientsEnAttente}" var="patient" varStatus="status">
                            <div class="bg-gray-800/40 p-6 rounded-lg hover:bg-gray-800/60 transition">
                                <div class="flex justify-between items-start">
                                    <div class="flex-1">
                                        <div class="flex items-center space-x-3 mb-2">
                                            <span class="text-2xl font-bold gold">#${status.index + 1}</span>
                                            <h3 class="text-xl font-semibold">${patient.nom} ${patient.prenom}</h3>
                                            <span class="px-3 py-1 rounded-full text-xs bg-yellow-900/30 text-yellow-400 border border-yellow-700">
                                                En attente
                                            </span>
                                        </div>
                                        
                                        <div class="grid md:grid-cols-3 gap-4 text-sm mt-4">
                                            <div>
                                                <span class="text-gray-400">Arrivée:</span>
                                                <span class="font-medium ml-2">
                                                    ${patient.heureArrivee.hour}:${patient.heureArrivee.minute}
                                                </span>
                                            </div>
                                            <div>
                                                <span class="text-gray-400">Âge:</span>
                                                <span class="font-medium ml-2">
                                                    ${java.time.Period.between(patient.dateNaissance, java.time.LocalDate.now()).years} ans
                                                </span>
                                            </div>
                                            <div>
                                                <span class="text-gray-400">N° SS:</span>
                                                <span class="font-medium ml-2">${patient.numeroSecuriteSociale}</span>
                                            </div>
                                        </div>

                                        <c:if test="${patient.derniersSignesVitaux != null}">
                                            <div class="mt-4 p-3 bg-gray-900/50 rounded-lg">
                                                <p class="text-xs text-gray-400 mb-2">Signes vitaux:</p>
                                                <div class="grid grid-cols-4 gap-3 text-sm">
                                                    <div>
                                                        <span class="text-gray-400">TA:</span>
                                                        <span class="font-medium ml-1">${patient.derniersSignesVitaux.tensionFormatee}</span>
                                                    </div>
                                                    <div>
                                                        <span class="text-gray-400">FC:</span>
                                                        <span class="font-medium ml-1">${patient.derniersSignesVitaux.frequenceCardiaque} bpm</span>
                                                    </div>
                                                    <div>
                                                        <span class="text-gray-400">T°:</span>
                                                        <span class="font-medium ml-1">${patient.derniersSignesVitaux.temperature}°C</span>
                                                    </div>
                                                    <c:if test="${patient.derniersSignesVitaux.saturationOxygene != null}">
                                                        <div>
                                                            <span class="text-gray-400">SpO₂:</span>
                                                            <span class="font-medium ml-1">${patient.derniersSignesVitaux.saturationOxygene}%</span>
                                                        </div>
                                                    </c:if>
                                                </div>
                                            </div>
                                        </c:if>
                                    </div>

                                    <a href="${pageContext.request.contextPath}/generaliste/creerConsultation?patientId=${patient.id}"
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
                        <p>Aucun patient en attente</p>
                        <p class="text-sm mt-2">Les patients ajoutés par l'infirmier apparaîtront ici</p>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>

        <!-- Consultations en attente d'avis -->
        <c:if test="${not empty consultationsEnAttenteAvis}">
            <div class="card-luxury p-6 mt-8">
                <h2 class="text-2xl font-semibold mb-6 text-orange-400">🔄 Consultations en Attente d'Avis Spécialiste</h2>
                
                <div class="space-y-4">
                    <c:forEach items="${consultationsEnAttenteAvis}" var="consultation">
                        <div class="bg-gray-800/40 p-6 rounded-lg">
                            <div class="flex justify-between items-start">
                                <div>
                                    <h3 class="text-lg font-semibold mb-2">
                                        ${consultation.patient.nom} ${consultation.patient.prenom}
                                    </h3>
                                    <p class="text-gray-400 text-sm mb-2">
                                        Consultation du <fmt:formatDate value="${consultation.dateConsultation}" pattern="dd/MM/yyyy à HH:mm" />
                                    </p>
                                    <p class="text-gray-300 text-sm">
                                        <strong>Motif:</strong> ${consultation.motif}
                                    </p>
                                </div>
                                <a href="${pageContext.request.contextPath}/generaliste/consultation/${consultation.id}" 
                                   class="btn-luxury px-6 py-2">
                                    Voir détails
                                </a>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </c:if>
    </div>

    <!-- Footer -->
    <footer class="text-center py-6 mt-12 border-t border-gray-700 text-gray-400 text-sm">
        © 2025 MEDICA ELITE — Tous droits réservés.
    </footer>

</body>
</html>