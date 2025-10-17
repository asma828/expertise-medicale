<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Connexion - Télé-expertise Médicale</title>
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

<body class="luxury-gradient min-h-screen flex items-center justify-center text-gray-100">

  <div class="w-full max-w-md bg-gray-800/40 p-8 rounded-2xl shadow-2xl backdrop-blur-md">
    <div class="text-center mb-8">
      <img src="https://cdn-icons-png.flaticon.com/512/2966/2966327.png" alt="Logo" class="w-16 mx-auto mb-3">
      <h1 class="text-3xl font-bold gold">Connexion</h1>
      <p class="text-gray-300 mt-2">Connectez-vous à votre compte</p>
    </div>

    <!-- Messages d'alerte -->
    <c:if test="${not empty error}">
      <div class="bg-red-500/20 border border-red-400 text-red-300 px-4 py-3 rounded mb-4">
        <i class="bi bi-exclamation-triangle-fill"></i> ${error}
      </div>
    </c:if>

    <c:if test="${not empty success}">
      <div class="bg-green-500/20 border border-green-400 text-green-300 px-4 py-3 rounded mb-4">
        <i class="bi bi-check-circle-fill"></i> ${success}
      </div>
    </c:if>

    <!-- Formulaire de connexion -->
    <form method="POST" action="${pageContext.request.contextPath}/login" class="space-y-5">
      <input type="hidden" name="csrfToken" value="${sessionScope.csrfToken}">

      <div>
        <label for="email" class="block mb-1 text-sm font-medium text-gray-200">
          <i class="bi bi-envelope-fill"></i> Email
        </label>
        <input type="email" id="email" name="email" required autofocus
          placeholder="utilisateur@hopital.ma"
          class="w-full px-4 py-3 rounded-lg bg-gray-900 border border-gray-700 text-gray-100 focus:outline-none focus:ring-2 focus:ring-yellow-500" />
      </div>

      <div>
        <label for="password" class="block mb-1 text-sm font-medium text-gray-200">
          <i class="bi bi-lock-fill"></i> Mot de passe
        </label>
        <input type="password" id="password" name="password" required
          placeholder="••••••••"
          class="w-full px-4 py-3 rounded-lg bg-gray-900 border border-gray-700 text-gray-100 focus:outline-none focus:ring-2 focus:ring-yellow-500" />
      </div>

      <button type="submit" class="w-full btn-luxury py-3 text-lg flex justify-center items-center gap-2">
        <i class="bi bi-box-arrow-in-right"></i> Se connecter
      </button>
    </form>



    <div class="text-center text-sm text-gray-500 mt-6">
      <a href="${pageContext.request.contextPath}/" class="hover:underline">← Retour à l'accueil</a>
    </div>
  </div>

</body>
</html>
