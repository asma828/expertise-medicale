<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Demander une Expertise - MEDICA ELITE</title>
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
        .specialiste-card {
            background: rgba(51, 65, 85, 0.4);
            border: 2px solid transparent;
            transition: all 0.3s ease;
            cursor: pointer;
        }
        .specialiste-card:hover {
            border-color: rgba(250, 204, 21, 0.5);
            background: rgba(51, 65, 85, 0.6);
        }
        .specialiste-card.selected {
            border-color: #facc15;
            background: rgba(250, 204, 21, 0.1);
        }
        .creneau-slot {
            background: rgba(51, 65, 85, 0.4);
            border: 2px solid transparent;
            transition: all 0.3s ease;
            cursor: pointer;
        }
        .creneau-slot.disponible:hover {
            border-color: #4ade80;
            background: rgba(74, 222, 128, 0.1);
        }
        .creneau-slot.selected {
            border-color: #facc15;
            background: rgba(250, 204, 21, 0.2);
        }
        .creneau-slot.indisponible {
            opacity: 0.5;
            cursor: not-allowed;
        }
        #etape2, #etape3 {
            display: none;
        }
    </style>
</head>
<body class="luxury-gradient min-h-screen text-gray-100">

    <!-- Navbar -->
    <nav class="bg-opacity-20 backdrop-blur-md border-b border-gray-700 py-4 px-6 sticky top-0 z-50">
        <div class="max-w-7xl mx-auto flex justify-between items-center">
            <div class="text-2xl font-bold gold tracking-wide">MEDICA ELITE</div>
            <div class="flex items-center space-x-6">
                <a href="${pageContext.request.contextPath}/generaliste/consultation/${consultation.id}"
                   class="text-gray-300 hover:text-yellow-400 transition">← Retour à la Consultation</a>
                <span class="text-gray-300">👨‍⚕️ Dr. ${sessionScope.userName}</span>
            </div>
        </div>
    </nav>

    <div class="max-w-7xl mx-auto p-6">
        <!-- Header -->
        <div class="mb-8">
            <h1 class="text-4xl font-bold mb-2 gold">🔄 Demander une Télé-Expertise</h1>
            <p class="text-gray-400">Processus en 3 étapes</p>
        </div>

        <!-- Progress Steps -->
        <div class="card-luxury p-6 mb-8">
            <div class="flex justify-between items-center">
                <div class="flex items-center space-x-2" id="step1-indicator">
                    <div class="w-10 h-10 rounded-full bg-yellow-400 flex items-center justify-center text-gray-900 font-bold">1</div>
                    <span class="font-semibold">Spécialité</span>
                </div>
                <div class="flex-1 h-1 bg-gray-700 mx-4"></div>
                <div class="flex items-center space-x-2 opacity-50" id="step2-indicator">
                    <div class="w-10 h-10 rounded-full bg-gray-700 flex items-center justify-center font-bold">2</div>
                    <span>Spécialiste</span>
                </div>
                <div class="flex-1 h-1 bg-gray-700 mx-4"></div>
                <div class="flex items-center space-x-2 opacity-50" id="step3-indicator">
                    <div class="w-10 h-10 rounded-full bg-gray-700 flex items-center justify-center font-bold">3</div>
                    <span>Détails</span>
                </div>
            </div>
        </div>

        <!-- Messages -->
        <c:if test="${not empty error}">
            <div class="card-luxury p-4 mb-6 border-red-500">
                <p class="text-red-400">${error}</p>
            </div>
        </c:if>

        <!-- Info Consultation -->
        <div class="card-luxury p-6 mb-6">
            <h3 class="text-xl font-semibold mb-3 gold">📋 Consultation</h3>
            <div class="grid md:grid-cols-3 gap-4">
                <div>
                    <p class="text-gray-400 text-sm">Patient</p>
                    <p class="font-semibold">${consultation.patient.nom} ${consultation.patient.prenom}</p>
                </div>
                <div>
                    <p class="text-gray-400 text-sm">Motif</p>
                    <p class="font-semibold">${consultation.motif}</p>
                </div>
                <div>
                    <p class="text-gray-400 text-sm">Date</p>
                    <p class="font-semibold">
                        <fmt:formatDate value="${consultation.dateConsultation}" pattern="dd/MM/yyyy HH:mm" />
                    </p>
                </div>
            </div>
        </div>

        <form id="expertiseForm" method="post" action="${pageContext.request.contextPath}/generaliste/demander-expertise">
            <input type="hidden" name="consultationId" value="${consultation.id}">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">
            <input type="hidden" name="specialisteId" id="specialisteId">
            <input type="hidden" name="creneauId" id="creneauId">

            <!-- Étape 1: Choisir Spécialité -->
            <div id="etape1" class="card-luxury p-8">
                <h2 class="text-2xl font-semibold mb-6 gold">Étape 1: Choisir la Spécialité Requise</h2>

                <div class="grid md:grid-cols-3 gap-4">
                    <c:forEach items="${specialites}" var="spec">
                        <div class="specialiste-card p-6 rounded-lg text-center" onclick="selectSpecialite('${spec}')">
                            <div class="text-4xl mb-3">🏥</div>
                            <h3 class="font-semibold text-lg">${spec.label}</h3>
                        </div>
                    </c:forEach>
                </div>
            </div>

            <!-- Étape 2: Choisir Spécialiste -->
            <div id="etape2" class="card-luxury p-8">
                <div class="flex justify-between items-center mb-6">
                    <h2 class="text-2xl font-semibold gold">Étape 2: Choisir un Spécialiste</h2>
                    <button type="button" onclick="retourEtape1()" class="btn-secondary px-4 py-2">
                        ← Retour
                    </button>
                </div>

                <div id="specialistesListe" class="space-y-4">
                    <!-- Liste des spécialistes chargée dynamiquement -->
                </div>
            </div>

            <!-- Étape 3: Créneaux et Détails -->
            <div id="etape3" class="space-y-6">
                <!-- Créneaux disponibles -->
                <div class="card-luxury p-8">
                    <div class="flex justify-between items-center mb-6">
                        <h2 class="text-2xl font-semibold gold">Créneaux Disponibles</h2>
                        <button type="button" onclick="retourEtape2()" class="btn-secondary px-4 py-2">
                            ← Retour
                        </button>
                    </div>

                    <div id="creneauxListe" class="grid md:grid-cols-4 gap-3">
                        <!-- Créneaux chargés dynamiquement -->
                    </div>
                </div>

                <!-- Détails de la demande -->
                <div class="card-luxury p-8">
                    <h2 class="text-2xl font-semibold mb-6 gold">Détails de la Demande</h2>

                    <div class="space-y-6">
                        <div>
                            <label class="label-luxury">Question pour le Spécialiste *</label>
                            <textarea name="questionPosee"
                                      class="input-luxury"
                                      rows="4"
                                      placeholder="Décrivez la question ou le problème pour lequel vous sollicitez l'avis du spécialiste..."
                                      required></textarea>
                        </div>

                        <div>
                            <label class="label-luxury">Données et Analyses</label>
                            <textarea name="donneesAnalyses"
                                      class="input-luxury"
                                      rows="4"
                                      placeholder="Résultats d'examens, analyses biologiques, imagerie médicale..."></textarea>
                        </div>

                        <div>
                            <label class="label-luxury">Niveau de Priorité *</label>
                            <select name="priorite" class="input-luxury" required>
                                <option value="">-- Sélectionner --</option>
                                <option value="URGENTE">🔴 Urgente</option>
                                <option value="NORMALE">🟡 Normale</option>
                                <option value="NON_URGENTE">🟢 Non urgente</option>
                            </select>
                        </div>

                        <div class="flex space-x-4">
                            <button type="submit" class="btn-luxury px-8 py-3">
                                ✅ Confirmer la Demande
                            </button>
                            <a href="${pageContext.request.contextPath}/generaliste/consultation/${consultation.id}"
                               class="btn-secondary px-8 py-3">
                                Annuler
                            </a>
                        </div>
                    </div>
                </div>
            </div>
        </form>
    </div>

    <script>
        let specialistes = ${specialistesJson != null ? specialistesJson : '[]'};
        let creneaux = {};

        function selectSpecialite(specialite) {
            // Afficher les spécialistes de cette spécialité
            fetch('${pageContext.request.contextPath}/generaliste/demander-expertise?action=getSpecialistes&specialite=' + specialite)
                .then(response => response.json())
                .then(data => {
                    specialistes = data;
                    afficherSpecialistes();

                    // Passer à l'étape 2
                    document.getElementById('etape1').style.display = 'none';
                    document.getElementById('etape2').style.display = 'block';
                    updateStepIndicator(2);
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    alert('Erreur lors du chargement des spécialistes');
                });
        }

        function afficherSpecialistes() {
            const liste = document.getElementById('specialistesListe');

            if (specialistes.length === 0) {
                liste.innerHTML = '<p class="text-center text-gray-400 py-8">Aucun spécialiste disponible pour cette spécialité</p>';
                return;
            }

            liste.innerHTML = specialistes.map(spec => `
                <div class="specialiste-card p-6 rounded-lg" onclick="selectSpecialiste(${spec.id})">
                    <div class="flex justify-between items-start">
                        <div class="flex-1">
                            <h3 class="text-xl font-semibold mb-2">Dr. ${spec.nom} ${spec.prenom}</h3>
                            <p class="text-gray-400 text-sm mb-2">${spec.specialite}</p>
                            <div class="flex items-center space-x-4 mt-3">
                                <div>
                                    <span class="text-gray-400 text-sm">Tarif:</span>
                                    <span class="text-yellow-400 font-bold ml-2">${spec.tarif} DH</span>
                                </div>
                                <div>
                                    <span class="text-gray-400 text-sm">Durée:</span>
                                    <span class="font-semibold ml-2">${spec.dureeConsultation} min</span>
                                </div>
                            </div>
                        </div>
                        <div class="text-3xl">👨‍⚕️</div>
                    </div>
                </div>
            `).join('');
        }

        function selectSpecialiste(specialisteId) {
            document.getElementById('specialisteId').value = specialisteId;

            // Charger les créneaux du spécialiste
            fetch('${pageContext.request.contextPath}/generaliste/demander-expertise?action=getCreneaux&specialisteId=' + specialisteId)
                .then(response => response.json())
                .then(data => {
                    creneaux = data;
                    afficherCreneaux();

                    // Passer à l'étape 3
                    document.getElementById('etape2').style.display = 'none';
                    document.getElementById('etape3').style.display = 'block';
                    updateStepIndicator(3);
                })
                .catch(error => {
                    console.error('Erreur:', error);
                    alert('Erreur lors du chargement des créneaux');
                });
        }

        function afficherCreneaux() {
            const liste = document.getElementById('creneauxListe');

            if (creneaux.length === 0) {
                liste.innerHTML = '<p class="col-span-4 text-center text-gray-400 py-8">Aucun créneau disponible</p>';
                return;
            }

            liste.innerHTML = creneaux.map(cren => {
                const disponible = cren.statut === 'DISPONIBLE';
                const classes = disponible ? 'creneau-slot disponible' : 'creneau-slot indisponible';
                const onclick = disponible ? `onclick="selectCreneau(${cren.id})"` : '';

                return `
                    <div class="${classes} p-4 rounded-lg text-center" ${onclick}>
                        <div class="text-2xl mb-2">${disponible ? '✅' : '❌'}</div>
                        <p class="font-semibold">${cren.heureFormatee}</p>
                        <p class="text-xs text-gray-400 mt-1">${cren.dateFormatee}</p>
                        <p class="text-xs mt-2 ${disponible ? 'text-green-400' : 'text-red-400'}">
                            ${disponible ? 'Disponible' : cren.statut}
                        </p>
                    </div>
                `;
            }).join('');
        }

        function selectCreneau(creneauId) {
            // Désélectionner tous les créneaux
            document.querySelectorAll('.creneau-slot').forEach(slot => {
                slot.classList.remove('selected');
            });

            // Sélectionner le créneau cliqué
            event.currentTarget.classList.add('selected');
            document.getElementById('creneauId').value = creneauId;
        }

        function retourEtape1() {
            document.getElementById('etape2').style.display = 'none';
            document.getElementById('etape1').style.display = 'block';
            updateStepIndicator(1);
        }

        function retourEtape2() {
            document.getElementById('etape3').style.display = 'none';
            document.getElementById('etape2').style.display = 'block';
            updateStepIndicator(2);
        }

        function updateStepIndicator(step) {
            for (let i = 1; i <= 3; i++) {
                const indicator = document.getElementById('step' + i + '-indicator');
                if (i <= step) {
                    indicator.classList.remove('opacity-50');
                    indicator.querySelector('div').classList.remove('bg-gray-700');
                    indicator.querySelector('div').classList.add('bg-yellow-400');
                } else {
                    indicator.classList.add('opacity-50');
                    indicator.querySelector('div').classList.remove('bg-yellow-400');
                    indicator.querySelector('div').classList.add('bg-gray-700');
                }
            }
        }

        // Validation du formulaire
        document.getElementById('expertiseForm').addEventListener('submit', function(e) {
            const specialisteId = document.getElementById('specialisteId').value;
            const creneauId = document.getElementById('creneauId').value;

            if (!specialisteId) {
                e.preventDefault();
                alert('Veuillez sélectionner un spécialiste');
                return;
            }

            if (!creneauId) {
                e.preventDefault();
                alert('Veuillez sélectionner un créneau');
                return;
            }
        });
    </script>

    <!-- Footer -->
    <footer class="text-center py-6 mt-12 border-t border-gray-700 text-gray-400 text-sm">
        © 2025 MEDICA ELITE — Tous droits réservés.
    </footer>

</body>
</html>