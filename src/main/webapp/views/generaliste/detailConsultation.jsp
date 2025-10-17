<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Consultation - MEDICA ELITE</title>
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
        .btn-danger {
            background: linear-gradient(90deg, #ef4444, #dc2626);
            color: white;
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
                <a href="${pageContext.request.contextPath}/generaliste/dashboard"
                   class="text-gray-300 hover:text-yellow-400 transition">← Dashboard</a>
                <span class="text-gray-300">👨‍⚕️ Dr. ${sessionScope.userName}</span>
            </div>
        </div>
    </nav>

    <div class="max-w-7xl mx-auto p-6">
        <c:if test="${not empty consultation}">
            <!-- Header avec statut -->
            <div class="flex justify-between items-start mb-8">
                <div>
                    <h1 class="text-4xl font-bold mb-2 gold">Consultation #${consultation.id}</h1>
                    <p class="text-gray-400">
                        <${consultation.dateConsultation.hour}:${consultation.dateConsultation.minute}
                    </p>
                </div>
                <div>
                    <c:choose>
                        <c:when test="${consultation.statut == 'EN_COURS'}">
                            <span class="px-4 py-2 rounded-full text-sm bg-blue-900/30 text-blue-400 border border-blue-700">
                                📝 En cours
                            </span>
                        </c:when>
                        <c:when test="${consultation.statut == 'EN_ATTENTE_AVIS_SPECIALISTE'}">
                            <span class="px-4 py-2 rounded-full text-sm bg-orange-900/30 text-orange-400 border border-orange-700">
                                🔄 En attente d'avis
                            </span>
                        </c:when>
                        <c:when test="${consultation.statut == 'TERMINEE'}">
                            <span class="px-4 py-2 rounded-full text-sm bg-green-900/30 text-green-400 border border-green-700">
                                ✅ Terminée
                            </span>
                        </c:when>
                    </c:choose>
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

            <c:if test="${not empty error}">
                <div class="card-luxury p-4 mb-6 border-red-500">
                    <div class="flex items-center space-x-3">
                        <span class="text-2xl">❌</span>
                        <p class="text-red-400">${error}</p>
                    </div>
                </div>
            </c:if>

            <div class="grid md:grid-cols-3 gap-6">
                <!-- Colonne principale -->
                <div class="md:col-span-2 space-y-6">
                    <!-- Informations Patient -->
                    <div class="card-luxury p-6">
                        <h2 class="text-2xl font-semibold mb-4 gold">👤 Patient</h2>
                        <div class="grid grid-cols-2 gap-4">
                            <div>
                                <p class="text-gray-400 text-sm">Nom Complet</p>
                                <p class="text-lg font-semibold">${consultation.patient.nom} ${consultation.patient.prenom}</p>
                            </div>
                            <div>
                                <p class="text-gray-400 text-sm">Âge</p>
                                <p class="text-lg font-semibold">
                                    ${java.time.Period.between(consultation.patient.dateNaissance, java.time.LocalDate.now()).years} ans
                                </p>
                            </div>
                        </div>
                    </div>

                    <!-- Détails Consultation -->
                    <div class="card-luxury p-6">
                        <h2 class="text-2xl font-semibold mb-4 gold">🩺 Détails de la Consultation</h2>

                        <div class="space-y-4">
                            <div>
                                <p class="text-gray-400 text-sm mb-1">Motif</p>
                                <p class="text-lg">${consultation.motif}</p>
                            </div>

                            <c:if test="${not empty consultation.symptomes}">
                                <div>
                                    <p class="text-gray-400 text-sm mb-1">Symptômes</p>
                                    <p class="text-lg">${consultation.symptomes}</p>
                                </div>
                            </c:if>

                            <div>
                                <p class="text-gray-400 text-sm mb-1">Examen Clinique</p>
                                <p class="text-lg">${consultation.examenClinique}</p>
                            </div>

                            <c:if test="${not empty consultation.observations}">
                                <div>
                                    <p class="text-gray-400 text-sm mb-1">Observations</p>
                                    <p class="text-lg">${consultation.observations}</p>
                                </div>
                            </c:if>
                        </div>

                        <!-- Formulaire diagnostic/traitement si EN_COURS -->
                        <c:if test="${consultation.statut == 'EN_COURS'}">
                            <form method="post" action="${pageContext.request.contextPath}/generaliste/consultation/${consultation.id}" class="mt-6 pt-6 border-t border-gray-700">
                                <input type="hidden" name="action" value="updateDiagnostic">
                                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                                <div class="space-y-4">
                                    <div>
                                        <label class="label-luxury">Diagnostic</label>
                                        <textarea name="diagnostic" class="input-luxury" rows="3" placeholder="Diagnostic médical...">${consultation.diagnostic}</textarea>
                                    </div>
                                    <div>
                                        <label class="label-luxury">Traitement / Ordonnance</label>
                                        <textarea name="traitement" class="input-luxury" rows="4" placeholder="Médicaments et recommandations...">${consultation.traitement}</textarea>
                                    </div>
                                    <button type="submit" class="btn-luxury px-6 py-2">
                                        Enregistrer les modifications
                                    </button>
                                </div>
                            </form>
                        </c:if>

                        <c:if test="${consultation.statut != 'EN_COURS'}">
                            <c:if test="${not empty consultation.diagnostic}">
                                <div class="mt-6 pt-6 border-t border-gray-700">
                                    <p class="text-gray-400 text-sm mb-1">Diagnostic</p>
                                    <p class="text-lg">${consultation.diagnostic}</p>
                                </div>
                            </c:if>
                            <c:if test="${not empty consultation.traitement}">
                                <div class="mt-4">
                                    <p class="text-gray-400 text-sm mb-1">Traitement</p>
                                    <p class="text-lg">${consultation.traitement}</p>
                                </div>
                            </c:if>
                        </c:if>
                    </div>

                    <!-- Actes Techniques -->
                    <div class="card-luxury p-6">
                        <div class="flex justify-between items-center mb-4">
                            <h2 class="text-2xl font-semibold gold">🔬 Actes Techniques</h2>
                            <c:if test="${consultation.statut != 'TERMINEE'}">
                                <button onclick="openModal('modalActes')" class="btn-luxury px-4 py-2">
                                    + Ajouter un acte
                                </button>
                            </c:if>
                        </div>

                        <c:choose>
                            <c:when test="${not empty consultation.actesTechniques}">
                                <div class="space-y-3">
                                    <c:forEach items="${consultation.actesTechniques}" var="acte">
                                        <div class="bg-gray-800/40 p-4 rounded-lg flex justify-between items-center">
                                            <div>
                                                <p class="font-semibold">${acte.typeActe.description}</p>
                                                <c:if test="${not empty acte.description}">
                                                    <p class="text-sm text-gray-400 mt-1">${acte.description}</p>
                                                </c:if>
                                            </div>
                                            <p class="text-xl font-bold text-yellow-400">${acte.cout} DH</p>
                                        </div>
                                    </c:forEach>
                                </div>
                            </c:when>
                            <c:otherwise>
                                <p class="text-gray-400 text-center py-8">Aucun acte technique ajouté</p>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </div>

                <!-- Colonne latérale -->
                <div class="space-y-6">
                    <!-- Coût Total -->
                    <div class="card-luxury p-6">
                        <h3 class="text-xl font-semibold mb-4 gold">💰 Coût Total</h3>

                        <div class="space-y-3 mb-4">
                            <div class="flex justify-between">
                                <span class="text-gray-400">Consultation:</span>
                                <span class="font-semibold">${consultation.coutConsultation} DH</span>
                            </div>

                            <c:if test="${not empty consultation.actesTechniques}">
                                <div class="flex justify-between">
                                    <span class="text-gray-400">Actes techniques:</span>
                                    <span class="font-semibold">
                                        ${consultation.actesTechniques.stream().map(a -> a.cout).reduce(0.0, (a,b) -> a + b)} DH
                                    </span>
                                </div>
                            </c:if>

                           <c:if test="${consultation.demandeExpertise != null && consultation.demandeExpertise.creneau != null && consultation.demandeExpertise.creneau.specialiste != null}">
                               <div class="flex justify-between">
                                   <span class="text-gray-400">Expertise:</span>
                                   <span class="font-semibold">${consultation.demandeExpertise.creneau.specialiste.tarif} DH</span>
                               </div>
                           </c:if>

                        </div>

                        <div class="pt-4 border-t border-gray-700">
                            <div class="flex justify-between items-center">
                                <span class="text-xl font-semibold">TOTAL:</span>
                                <span class="text-3xl font-bold gold">${consultation.calculerCoutTotal()} DH</span>
                            </div>
                        </div>
                    </div>

                    <!-- Actions -->
                    <div class="card-luxury p-6">
                        <h3 class="text-xl font-semibold mb-4 gold">⚡ Actions</h3>

                        <div class="space-y-3">
                            <c:if test="${consultation.statut == 'EN_COURS' && consultation.demandeExpertise == null}">
                                <a href="${pageContext.request.contextPath}/generaliste/demanderExpertise?consultationId=${consultation.id}"
                                   class="btn-secondary px-6 py-3 w-full text-center block">
                                    🔄 Demander Avis Spécialiste
                                </a>
                            </c:if>

                            <c:if test="${consultation.statut == 'EN_COURS'}">
                                <form method="post" action="${pageContext.request.contextPath}/generaliste/consultation/${consultation.id}">
                                    <input type="hidden" name="action" value="terminer">
                                    <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
                                    <button type="submit" class="btn-luxury px-6 py-3 w-full">
                                        ✅ Clôturer la Consultation
                                    </button>
                                </form>
                            </c:if>

                            <c:if test="${consultation.demandeExpertise != null}">
                                <a href="${pageContext.request.contextPath}/generaliste/expertise/${consultation.demandeExpertise.id}"
                                   class="btn-secondary px-6 py-3 w-full text-center block">
                                    📋 Voir Demande d'Expertise
                                </a>
                            </c:if>
                        </div>
                    </div>
                </div>
            </div>
        </c:if>

        <c:if test="${empty consultation}">
            <div class="card-luxury p-12 text-center">
                <div class="text-6xl mb-4">⚠️</div>
                <h3 class="text-2xl font-semibold mb-2">Consultation non trouvée</h3>
                <a href="${pageContext.request.contextPath}/generaliste/dashboard"
                   class="btn-luxury inline-block px-8 py-3 mt-4">
                    Retour au Dashboard
                </a>
            </div>
        </c:if>
    </div>

    <!-- Modal Ajouter Acte -->
    <div id="modalActes" class="modal">
        <div class="card-luxury p-8 max-w-2xl w-full mx-4">
            <h3 class="text-2xl font-semibold mb-6 gold">Ajouter un Acte Technique</h3>

            <form method="post" action="${pageContext.request.contextPath}/generaliste/consultation/${consultation.id}">
                <input type="hidden" name="action" value="ajouterActe">
                <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

                <div class="mb-6">
                    <label class="label-luxury">Type d'Acte *</label>
                    <select name="typeActe" class="input-luxury" required onchange="updateActeDescription(this)">
                        <option value="">-- Sélectionner --</option>
                        <c:forEach items="${typesActes}" var="type">
                            <option value="${type}" data-description="${type.description}" data-cout="${type.coutDefaut}">
                                ${type.description} - ${type.coutDefaut} DH
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <div class="mb-6">
                    <label class="label-luxury">Description (optionnel)</label>
                    <textarea name="description" id="acteDescription" class="input-luxury" rows="3" placeholder="Détails supplémentaires..."></textarea>
                </div>

                <div class="flex space-x-4">
                    <button type="button" onclick="closeModal('modalActes')" class="btn-secondary px-6 py-3">
                        Annuler
                    </button>
                    <button type="submit" class="btn-luxury px-6 py-3">
                        Ajouter l'acte
                    </button>
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

        function updateActeDescription(select) {
            const option = select.options[select.selectedIndex];
            const description = option.getAttribute('data-description');
            document.getElementById('acteDescription').placeholder = description;
        }

        // Fermer modal en cliquant en dehors
        document.querySelectorAll('.modal').forEach(modal => {
            modal.addEventListener('click', function(e) {
                if (e.target === this) {
                    closeModal(this.id);
                }
            });
        });
    </script>

</body>
</html>