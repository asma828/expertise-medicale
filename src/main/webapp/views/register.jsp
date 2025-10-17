<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Inscription - Télé-expertise Médicale</title>
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
  </style>
</head>

<body class="luxury-gradient min-h-screen flex items-center justify-center text-gray-100 px-4">

  <div class="w-full max-w-xl bg-gray-800/40 p-8 rounded-2xl shadow-2xl backdrop-blur-md">
    <div class="text-center mb-8">
      <img src="https://cdn-icons-png.flaticon.com/512/2966/2966327.png" alt="Logo" class="w-16 mx-auto mb-3">
      <h1 class="text-3xl font-bold gold">Créer un compte</h1>
      <p class="text-gray-300 mt-2">Inscrivez-vous pour accéder à la plateforme</p>
    </div>

    <!-- Error/Success messages -->
    <c:if test="${not empty error}">
      <div class="bg-red-500/20 border border-red-400 text-red-300 px-4 py-3 rounded mb-4">
        <i class="bi bi-exclamation-triangle-fill"></i> ${error}
      </div>
    </c:if>

    <form method="POST" action="/medecine/register" class="grid gap-4">
      <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

      <div class="grid grid-cols-2 gap-4">
        <div>
          <label class="block mb-1 text-sm text-gray-200">Prénom</label>
          <input name="prenom" type="text" required
                 class="w-full px-4 py-2 rounded bg-gray-900 border border-gray-700 text-gray-100" />
        </div>
        <div>
          <label class="block mb-1 text-sm text-gray-200">Nom</label>
          <input name="nom" type="text" required
                 class="w-full px-4 py-2 rounded bg-gray-900 border border-gray-700 text-gray-100" />
        </div>
      </div>

      <div>
        <label class="block mb-1 text-sm text-gray-200">Email</label>
        <input name="email" type="email" required
               class="w-full px-4 py-2 rounded bg-gray-900 border border-gray-700 text-gray-100" />
      </div>

      <div>
        <label class="block mb-1 text-sm text-gray-200">Mot de passe</label>
        <input name="motDePasse" type="password" required
               class="w-full px-4 py-2 rounded bg-gray-900 border border-gray-700 text-gray-100" />
      </div>

      <div>
        <label class="block mb-1 text-sm text-gray-200">Confirmer le mot de passe</label>
        <input name="motDePasseConfirm" type="password" required
               class="w-full px-4 py-2 rounded bg-gray-900 border border-gray-700 text-gray-100" />
      </div>

      <div>
        <label class="block mb-1 text-sm text-gray-200">Rôle</label>
        <select name="role" required
                class="w-full px-4 py-2 rounded bg-gray-900 border border-gray-700 text-gray-100">
          <option value="" disabled selected>-- Choisir un rôle --</option>
          <option value="INFIRMIER">Infirmier</option>
          <option value="GENERALISTE">Généraliste</option>
          <option value="SPECIALISTE">Spécialiste</option>
        </select>
      </div>

      <!-- Optional: Specialité & Tarif, visible if role = SPECIALISTE (can be handled with JS later) -->
      <div id="specialiteFields" class="hidden">
        <label class="block mb-1 text-sm text-gray-200">Spécialité</label>
        <select name="specialite"
                class="w-full px-4 py-2 rounded bg-gray-900 border border-gray-700 text-gray-100">
          <option value="" disabled selected>-- Choisir une spécialité --</option>
          <option value="CARDIOLOGIE">Cardiologie</option>
          <option value="DERMATOLOGIE">Dermatologie</option>
          <option value="NEUROLOGIE">Neurologie</option>
          <!-- Ajouter plus selon votre enum -->
        </select>
      </div>

      <div id="tarifField" class="hidden">
        <label class="block mb-1 text-sm text-gray-200">Tarif (en MAD)</label>
        <input name="tarif" type="number" min="0" step="0.01"
               class="w-full px-4 py-2 rounded bg-gray-900 border border-gray-700 text-gray-100" />
      </div>

      <button type="submit" class="w-full btn-luxury py-3 text-lg mt-4">S'inscrire</button>
    </form>

    <div class="text-center text-sm text-gray-500 mt-6">
      <a href="${pageContext.request.contextPath}/login" class="hover:underline">← Retour à la connexion</a>
    </div>
  </div>

  <script>
    // JS to show specialite & tarif if role = SPECIALISTE
    document.querySelector('select[name="role"]').addEventListener('change', function () {
      const isSpecialiste = this.value === 'SPECIALISTE';
      document.getElementById('specialiteFields').classList.toggle('hidden', !isSpecialiste);
      document.getElementById('tarifField').classList.toggle('hidden', !isSpecialiste);
    });
  </script>
</body>
</html>
