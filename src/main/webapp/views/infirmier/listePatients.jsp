<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Liste des Patients - Infirmier</title>
    <script src="https://cdn.tailwindcss.com"></script>
</head>
<body class="bg-gray-100 text-gray-800">

    <div class="max-w-6xl mx-auto p-6">
        <h1 class="text-3xl font-bold mb-6 text-center text-blue-600">Liste des Patients du Jour</h1>

        <!-- Statistiques -->
        <div class="grid grid-cols-1 md:grid-cols-3 gap-6 mb-10">
            <div class="bg-yellow-100 p-5 rounded-2xl shadow text-center">
                <p class="text-lg font-semibold">En attente</p>
                <p class="text-3xl font-bold text-yellow-600">${enAttenteCount}</p>
            </div>
            <div class="bg-blue-100 p-5 rounded-2xl shadow text-center">
                <p class="text-lg font-semibold">En consultation</p>
                <p class="text-3xl font-bold text-blue-600">${enConsultationCount}</p>
            </div>
            <div class="bg-green-100 p-5 rounded-2xl shadow text-center">
                <p class="text-lg font-semibold">Terminé</p>
                <p class="text-3xl font-bold text-green-600">${termineCount}</p>
            </div>
        </div>

        <!-- Message d'erreur -->
        <c:if test="${not empty error}">
            <div class="bg-red-200 text-red-800 p-3 rounded-lg mb-4 text-center">
                ${error}
            </div>
        </c:if>

        <!-- Tableau des patients -->
        <div class="bg-white p-6 rounded-2xl shadow">
            <table class="min-w-full border-collapse border border-gray-300">
                <thead class="bg-blue-600 text-white">
                    <tr>
                        <th class="py-3 px-4 border">Nom</th>
                        <th class="py-3 px-4 border">Prénom</th>
                        <th class="py-3 px-4 border">Heure d'arrivée</th>
                        <th class="py-3 px-4 border">Température</th>
                        <th class="py-3 px-4 border">Tension</th>
                        <th class="py-3 px-4 border">Statut</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="patient" items="${patients}">
                        <tr class="text-center border-b hover:bg-gray-100">
                            <td class="py-3 px-4">${patient.nom}</td>
                            <td class="py-3 px-4">${patient.prenom}</td>
                            <td class="py-3 px-4">
                                ${patient.heureArrivee.toLocalTime()}
                            </td>
                            <td class="py-3 px-4">
                                <c:out value="${patient.derniersSignesVitaux != null ? patient.derniersSignesVitaux.temperature : '-'}" />
                            </td>
                            <td class="py-3 px-4">
                                <c:out value="${patient.derniersSignesVitaux != null ? patient.derniersSignesVitaux.tensionFormatee : '-'}" />
                            </td>

                            <td class="py-3 px-4">
                                <c:choose>
                                    <c:when test="${patient.statut == 'EN_ATTENTE'}">
                                        <span class="bg-yellow-200 text-yellow-800 py-1 px-3 rounded-full text-sm font-medium">En attente</span>
                                    </c:when>
                                    <c:when test="${patient.statut == 'EN_CONSULTATION'}">
                                        <span class="bg-blue-200 text-blue-800 py-1 px-3 rounded-full text-sm font-medium">En consultation</span>
                                    </c:when>
                                    <c:otherwise>
                                        <span class="bg-green-200 text-green-800 py-1 px-3 rounded-full text-sm font-medium">Terminé</span>
                                    </c:otherwise>
                                </c:choose>
                            </td>
                        </tr>
                    </c:forEach>

                    <c:if test="${empty patients}">
                        <tr>
                            <td colspan="6" class="py-4 text-center text-gray-500">Aucun patient trouvé pour aujourd'hui.</td>
                        </tr>
                    </c:if>
                </tbody>
            </table>
        </div>
    </div>

</body>
</html>
