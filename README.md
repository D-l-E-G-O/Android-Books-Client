# Android Books Client ![Static Badge](https://img.shields.io/badge/Statut-Termin%C3%A9-red)

**Projet de fin de 2e année de BUT Informatique à l'IUT d'Illkirch, le but de ce projet est de réaliser un client mobile Android pour l’API Books développée avec React**, en appliquant une architecture moderne, propre et maintenable.

---

## Technologies utilisées

### API (backend)
- **Langage :** TypeScript
- **Environnement** : React, Node.js + Express.js (API REST)
- **Accès aux données et modélisation :** Prisma ORM
- **Base de données :** SQLite
- **Authentification :** JWT

### Client Android
- **Langage :** Java
- **Architecture :** MVVM (Model - View - ViewModel)
- **Réseau :** Retrofit (communication avec l’API)

### Outils
- **Android Studio**
- **Git**
- **Postman** pour tester les appels à l'API

---

## Équipe

- **Nombre de développeurs :** 2
- **Durée du projet :** 3 semaines

---

## Installation et exécution

### Prérequis

- Android Studio (version récente recommandée)
- SDK Android installé
- Une API Books accessible (instance W41 personnelle ou correction minimale)

### Étapes

1. **Cloner le dépôt**
   ```bash
   git clone https://github.com/D-l-E-G-O/Android-Books-Client.git
   cd Android-Books-Client
   ```

2. **Ouvrir le projet dans Android Studio**

3. **Configurer l’URL de l’API**
   - Vérifier l’endpoint utilisé par Retrofit
   - Pointer vers votre API W41 (ou la correction minimale si nécessaire)

4. **Compiler et lancer l’application**
   - Sur émulateur Android ou appareil physique

---

## Fonctionnalités principales

### Gestion des livres

- Affichage de la liste des livres
- Affichage des détails d’un livre sélectionné (incluant les tags)
- Création d’un livre via formulaire
- Suppression d’un livre depuis sa page de description

### Gestion des auteurs

- Affichage de la liste des auteurs
- Affichage des livres d’un auteur sélectionné
- Navigation vers le détail d’un livre depuis la liste des livres d’un auteur
- Création d’un auteur via formulaire
- Suppression d’un auteur (et des livres associés)

### Bonus

- Association d’une couverture à un livre **uniquement en local** (image stockée sur le téléphone, pas sur le serveur)

---

## Interface utilisateur

L’application respecte les contraintes suivantes :

- Une **activité principale unique**
- Une **Bottom Navigation Activity** avec deux menus :
  - Liste des livres
  - Liste des auteurs
- Écran d’accueil : **liste des livres**
- Listes affichées dans des **RecyclerView**
- Clic sur un livre : ouverture d’un fragment de détail
- Clic sur un auteur : ouverture d’un fragment affichant ses livres
- Création de livres/auteurs via **FAB** sur les écrans correspondants

---

## Architecture technique

Le projet suit une architecture Android moderne :

- **MVVM** pour séparer clairement interface, logique et données
- **ViewModel** pour gérer l’état et les données liées à l’UI
- **Repository** pour centraliser l’accès aux données
- **Retrofit** pour les appels réseau vers l’API Books
