<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="fr">
<head>
    <meta charset="UTF-8">
    <title>Télé-Expertise Médicale</title>
</head>
<body>
    <h1>Bienvenue sur la plateforme de télé-expertise</h1>
</body>
</html>

<!DOCTYPE html>
<html lang="fr">
<head>
  <meta charset="UTF-8" />
  <meta name="viewport" content="width=device-width, initial-scale=1.0" />
  <title>Télé-Expertise Médicale</title>
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

<body class="luxury-gradient min-h-screen text-gray-100 flex flex-col">

  <!-- Navbar -->
  <nav class="flex justify-between items-center py-5 px-10 bg-opacity-20 backdrop-blur-md fixed w-full top-0 z-50 border-b border-gray-700">
    <div class="text-2xl font-bold gold tracking-wide">MEDICA ELITE</div>
    <ul class="flex space-x-6 text-gray-200">
      <li><a href="#home" class="hover:text-yellow-400 transition">Accueil</a></li>
      <li><a href="#about" class="hover:text-yellow-400 transition">À propos</a></li>
      <li><a href="#services" class="hover:text-yellow-400 transition">Services</a></li>
      <li><a href="#contact" class="hover:text-yellow-400 transition">Contact</a></li>
    </ul>
<a href="${pageContext.request.contextPath}/login" class="btn-luxury px-6 py-2">Se connecter</a>
<a href="${pageContext.request.contextPath}/register">S'inscrire'</a>
  </nav>

  <!-- Hero Section -->
  <section id="home" class="flex flex-col justify-center items-center text-center mt-32 px-6">
    <h1 class="text-5xl md:text-6xl font-bold mb-6">
      L’Excellence Médicale <span class="gold">à Portée de Clic</span>
    </h1>
    <p class="text-gray-300 max-w-2xl mb-10">
      Bénéficiez de la télé-expertise de nos spécialistes : connectez-vous,
      partagez vos cas cliniques et obtenez un avis professionnel en toute sécurité.
    </p>
    <a href="${pageContext.request.contextPath}/login" class="btn-luxury px-8 py-3 text-lg">Commencer</a>
  </section>

  <!-- About -->
  <section id="about" class="mt-28 px-10 text-center">
    <h2 class="text-3xl font-semibold mb-6 gold">À propos</h2>
    <p class="max-w-3xl mx-auto text-gray-300">
      Notre plateforme de télé-expertise met en relation médecins généralistes et spécialistes
      pour améliorer la prise en charge des patients. Conçue avec soin et élégance, MEDICA ELITE
      offre une expérience fluide, intuitive et hautement sécurisée.
    </p>
  </section>

  <!-- Services -->
  <section id="services" class="mt-28 px-10 text-center">
    <h2 class="text-3xl font-semibold mb-10 gold">Nos Services</h2>
    <div class="grid md:grid-cols-3 gap-8 max-w-6xl mx-auto">
      <div class="bg-gray-800/40 p-8 rounded-2xl hover:bg-gray-800/70 transition shadow-lg">
        <img src="https://cdn-icons-png.flaticon.com/512/2966/2966327.png" alt="Consultation" class="w-16 mx-auto mb-4">
        <h3 class="text-xl font-semibold mb-2">Télé-consultation</h3>
        <p class="text-gray-400 text-sm">
          Échangez avec un médecin sans quitter votre cabinet ou votre domicile.
        </p>
      </div>
      <div class="bg-gray-800/40 p-8 rounded-2xl hover:bg-gray-800/70 transition shadow-lg">
        <img src="https://cdn-icons-png.flaticon.com/512/3209/3209896.png" alt="Expertise" class="w-16 mx-auto mb-4">
        <h3 class="text-xl font-semibold mb-2">Télé-expertise</h3>
        <p class="text-gray-400 text-sm">
          Obtenez un avis d’expert en quelques minutes sur vos cas complexes.
        </p>
      </div>
      <div class="bg-gray-800/40 p-8 rounded-2xl hover:bg-gray-800/70 transition shadow-lg">
        <img src="https://cdn-icons-png.flaticon.com/512/2966/2966329.png" alt="Dossiers" class="w-16 mx-auto mb-4">
        <h3 class="text-xl font-semibold mb-2">Gestion sécurisée</h3>
        <p class="text-gray-400 text-sm">
          Vos données médicales sont stockées et chiffrées selon les normes les plus strictes.
        </p>
      </div>
    </div>
  </section>

  <!-- Contact -->
  <section id="contact" class="mt-28 px-10 text-center mb-20">
    <h2 class="text-3xl font-semibold mb-6 gold">Contact</h2>
    <p class="text-gray-300 mb-8">Des questions ? Envoyez-nous un message :</p>
    <form class="max-w-md mx-auto bg-gray-800/40 p-8 rounded-2xl">
      <input type="text" placeholder="Votre nom" class="w-full mb-4 p-3 rounded-md bg-gray-900 border border-gray-700 text-gray-200" />
      <input type="email" placeholder="Votre email" class="w-full mb-4 p-3 rounded-md bg-gray-900 border border-gray-700 text-gray-200" />
      <textarea placeholder="Votre message" rows="4" class="w-full mb-4 p-3 rounded-md bg-gray-900 border border-gray-700 text-gray-200"></textarea>
      <button type="button" class="btn-luxury px-8 py-2">Envoyer</button>
    </form>
  </section>

  <!-- Footer -->
  <footer class="text-center py-6 border-t border-gray-700 text-gray-400 text-sm">
    © 2025 MEDICA ELITE — Tous droits réservés.
  </footer>

  <!-- Small animation -->
  <script>
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
      anchor.addEventListener("click", function(e) {
        e.preventDefault();
        document.querySelector(this.getAttribute("href")).scrollIntoView({
          behavior: "smooth"
        });
      });
    });
  </script>
</body>
</html>
