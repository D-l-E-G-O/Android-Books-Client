# Rapport de Projet - ABC (Android Books Client)

## 1. Architecture Technique

L'application repose sur une architecture **MVVM (Model-View-ViewModel)** rigoureuse, garantissant une séparation des responsabilités :

*   **Repository (`LibraryRepository`)** : Unique source de vérité pour les données. Il orchestre les appels API via Retrofit et gère les transformations de données.
*   **ViewModels** : 
    *   `AuthorViewModel` : Gère l'état des auteurs.
    *   `BookViewModel` : Gère la liste des livres et la persistance locale des couvertures.
    *   `TagViewModel` : Gère la liste des tags disponibles.
*   **Navigation** : Utilisation du composant **Navigation**. Toutes les transitions sont centralisées dans `nav_graph.xml`, facilitant la gestion du backstack.

---

## 2. Fonctionnalités Opérationnelles

### Gestion des Livres
*   **Affichage** : Liste complète avec noms des auteurs.
*   **Détails** : Vue complète incluant auteur, année et tags.
*   **Ajout** : Formulaire dynamique permettant la sélection d'un auteur et de multiples tags. L'ajout des tags est géré de manière asynchrone après la création du livre.
*   **Suppression** : Fonctionnelle avec boîte de dialogue de confirmation.

### Gestion des Auteurs
*   **Affichage** : Liste des auteurs.
*   **Détails** : Récupération de la liste des livres de l'auteur sélectionné.
*   **Ajout/Suppression** : Formulaires dédiés et suppression en cascade (locale) des livres associés.

### Bonus : Couvertures de livres
*   Possibilité d'associer une image de la galerie à chaque livre.
*   **Persistance locale** : Les URIs des images sont gérées localement pour survivre durant la session.

---

## 3. Difficultés et Solutions

### Synchronisation asynchrone lors de l'ajout
**Difficulté** : La création d'un livre complet est fragmentée en plusieurs étapes (création du livre, puis ajout individuel de chaque tag). Assurer un rafraîchissement unique et cohérent de l'interface après la résolution de toutes ces requêtes asynchrones était complexe.
**Solution** : Utilisation de callbacks imbriqués et centralisation du rafraîchissement final via `fetchBooks` dans le `LibraryRepository`.

### Liaison des entités (Auteurs/Livres)
**Difficulté** : L'API fournit les auteurs et les livres via des endpoints distincts. Pour afficher des informations complètes (ex: le nom de l'auteur dans la liste des livres), il faut réconcilier ces données manuellement.
**Solution** : Implémentation de `loadDataFromJsons` qui parcourt les deux flux de données pour reconstruire dynamiquement les relations en mémoire (injection des objets Author dans les Books) avant de notifier l'interface utilisateur.

### Navigation persistante
**Difficulté** : Le comportement par défaut du composant Navigation conservait l'état profond (détails) lors d'un changement d'onglet, ce qui nuisait à l'ergonomie.
**Solution** : Customisation des listeners de navigation dans `MainActivity` pour forcer systématiquement un retour à la racine de la section consultée.
