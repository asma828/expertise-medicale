<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Configurer Mon Profil - MEDICA ELITE</title>
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
    </style>
</head>
<body class="luxury-gradient min-h-screen text-gray-100">

    <!-- Navbar -->
    <nav class="bg-opacity-20 backdrop-blur-md border-b border-gray-700 py-4 px-6 sticky top-0 z-50">
        <div class="max-w-7xl mx-auto flex justify-between items-center">
            <div class="text-2xl font-bold gold tracking-wide">MEDICA ELITE</div>
            <div class="flex items-center space-x-6">
                <a href="${pageContext.request.contextPath}/specialiste/dashboard"
                   class="text-gray-300 hover:text-yellow-400 transition">← Retour au Dashboard</a>
                <span class="text-gray-300">👨‍⚕️ Dr. ${sessionScope.userName}</span>
            </div>
        </div>
    </nav>

    <div class="max-w-4xl mx-auto p-6">
        <!-- Header -->
        <div class="mb-8">
            <h1 class="text-4xl font-bold mb-2 gold">⚙️ Configurer Mon Profil</h1>
            <p class="text-gray-400">Définir votre spécialité et tarif de consultation</p>
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

        <!-- Formulaire -->
        <form method="post" action="${pageContext.request.contextPath}/specialiste/ConfigurerProfil">
            <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

            <div class="card-luxury p-8 mb-6">
                <h2 class="text-2xl font-semibold mb-6 gold">Informations Professionnelles</h2>

                <div class="space-y-6">
                    <!-- Spécialité -->
                    <div>
                        <label class="label-luxury">Spécialité Médicale *</label>
                        <select name="specialite" class="input-luxury" required>
                            <option value="">-- Sélectionner --</option>
                            <c:forEach items="${specialites}" var="spec">
                                <option value="${spec}" ${utilisateur.specialite == spec ? 'selected' : ''}>
                                    ${spec.label}
                                </option>
                            </c:forEach>
                        </select>
                        <p class="text-gray-400 text-sm mt-2">
                            Cette information permettra aux médecins généralistes de vous trouver
                        </p>
                    </div>

                    <!-- Tarif -->
                    <div>
                        <label class="label-luxury">Tarif de Consultation (DH) *</label>
                        <input type="number"
                               name="tarif"
                               step="0.01"
                               min="0"
                               class="input-luxury"
                               value="${utilisateur.tarif != null ? utilisateur.tarif : ''}"
                               placeholder="Ex: 500.00"
                               required>
                        <p class="text-gray-400 text-sm mt-2">
                            Tarif facturé pour une télé-expertise
                        </p>
                    </div>

                    <!-- Durée consultation -->
                    <div>
                        <label class="label-luxury">Durée Moyenne de Consultation (minutes)</label>
                        <select name="dureeConsultation" class="input-luxury">
                            <option value="15" ${utilisateur.dureeConsultation == 15 ? 'selected' : ''}>15 minutes</option>
                            <option value="30" ${utilisateur.dureeConsultation == 30 ? 'selected' : ''}>30 minutes</option>
                            <option value="45" ${utilisateur.dureeConsultation == 45 ? 'selected' : ''}>45 minutes</option>
                            <option value="60" ${utilisateur.dureeConsultation == 60 ? 'selected' : ''}>60 minutes</option>
                        </select>
                        <p class="text-gray-400 text-sm mt-2">
                            Durée utilisée pour générer vos créneaux automatiquement
                        </p>
                    </div>
                </div>
            </div>

            <!-- Résumé -->
            <c:if test="${utilisateur.specialite != null && utilisateur.tarif != null}">
                <div class="card-luxury p-6 mb-6 border-yellow-500">
                    <h3 class="text-lg font-semibold mb-4 text-yellow-400">📋 Profil Actuel</h3>
                    <div class="grid md:grid-cols-3 gap-4">
                        <div>
                            <p class="text-gray-400 text-sm">Spécialité</p>
                            <p class="text-lg font-semibold">${utilisateur.specialite.label}</p>
                        </div>
                        <div>
                            <p class="text-gray-400 text-sm">Tarif</p>
                            <p class="text-lg font-semibold gold">${utilisateur.tarif} DH</p>
                        </div>
                        <div>
                            <p class="text-gray-400 text-sm">Durée</p>
                            <p class="text-lg font-semibold">${utilisateur.dureeConsultation} min</p>
                        </div>
                    </div>
                </div>
            </c:if>

            <!-- Buttons -->
            <div class="flex space-x-4">
                <button type="submit" class="btn-luxury px-8 py-3">
                    💾 Enregistrer les Modifications
                </button>
                <a href="${pageContext.request.contextPath}/specialiste/dashboard"
                   class="btn-secondary px-8 py-3">
                    Annuler
                </a>
            </div>
        </form>

        <!-- Info -->
        <div class="card-luxury p-6 mt-8 bg-blue-900/10 border-blue-700/30">
            <h3 class="text-lg font-semibold mb-3 text-blue-400">💡 Informations Importantes</h3>
            <ul class="space-y-2 text-gray-300 text-sm">
                <li>• Votre spécialité détermine les demandes que vous recevrez</li>
                <li>• Votre tarif sera affiché aux médecins généralistes lors de leur recherche</li>
                <li>• La durée de consultation influence la génération automatique de vos créneaux</li>
                <li>• Ces informations peuvent être modifiées à tout moment</li>
            </ul>
        </div>
    </div>

    <!-- Footer -->
    <footer class="text-center py-6 mt-12 border-t border-gray-700 text-gray-400 text-sm">
        © 2025 MEDICA ELITE — Tous droits réservés.
    </footer>

</body>
</html>