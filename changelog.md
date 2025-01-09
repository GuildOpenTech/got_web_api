# Changelog - Module de Gestion des Utilisateurs GOT ERP

## 2025-01-02
- Initialisation du projet
  - Création de la structure du projet avec Spring Boot
  - Configuration des dépendances Maven
  - Mise en place du DevBook pour le suivi du développement
  - Configuration initiale de Git

## 2025-01-02
### Configuration Initiale
- Configuration du projet Maven avec les dépendances nécessaires :
  - Spring Boot 3.4.1
  - Spring Security
  - Spring Data JPA
  - PostgreSQL
  - JWT pour l'authentification
  - Validation
  - Lombok
  - SpringDoc OpenAPI
  - H2 Database (pour les tests)

### Structure du Projet
- Création de la configuration de base (`application.yml`)
  - Configuration de la base de données PostgreSQL
  - Configuration JWT
  - Configuration Swagger/OpenAPI
  - Configuration des logs

### Modèles de Données (Clean Architecture)
- Réorganisation selon Clean Architecture
  - Séparation en couches (domain, application, infrastructure, presentation)
  - Migration des entités vers le package domain.model
  - Configuration des repositories JPA dans domain.repository

- Création/Mise à jour des entités de base :
  - `GotUser` : Gestion des utilisateurs avec sécurité renforcée
  - `Role` : Gestion des rôles avec permissions
  - `Permission` : Gestion des permissions avec types
  - `PermissionType` : Types de permissions supportées
  - `Department` : Structure organisationnelle
  - `Context` : Contexte d'application des rôles
  - `UserRole` : Association utilisateur-rôle avec contexte
  - `ContextType` : Types de contextes supportés

- Implémentation des relations complexes :
  - Hiérarchie des départements
  - Rôles multiples par utilisateur
  - Permissions contextuelles
  - Validité temporelle des rôles

### Interfaces Repository
- Création des interfaces repository dans la couche domaine :
  - `GotUserRepository` : Gestion des utilisateurs
  - `RoleRepository` : Gestion des rôles
  - `DepartmentRepository` : Gestion des départements
  - `ContextRepository` : Gestion des contextes
  - `PermissionRepository` : Gestion des permissions
  - `UserRoleRepository` : Gestion des associations utilisateur-rôle

### Repositories JPA
- Création des repositories JPA avec Spring Data :
  - `GotUserRepository` : Gestion des utilisateurs
  - `RoleRepository` : Gestion des rôles
  - `DepartmentRepository` : Gestion des départements
  - `ContextRepository` : Gestion des contextes
  - `PermissionRepository` : Gestion des permissions
  - `UserRoleRepository` : Gestion des associations utilisateur-rôle
- Implémentation des méthodes de recherche personnalisées
- Configuration des requêtes JPQL pour les cas complexes

## 2025-01-03
### Use Cases et Règles Métier
- Définition des interfaces des use cases avec règles de gestion détaillées :
  - Gestion des utilisateurs :
    - Création et mise à jour des utilisateurs
    - Activation/désactivation des comptes
    - Recherche et filtrage
  - Gestion des rôles :
    - Attribution des rôles
    - Gestion des permissions
    - Vérification des accès
  - Gestion des départements :
    - Configuration des départements
    - Gestion des permissions départementales

- Création des DTOs pour la validation et le transfert de données :
  - `CreateUserDto`
  - `UpdateUserDto`
  - `UserSearchCriteria`

- Mise en place du système d'exceptions métier :
  - Exceptions de base avec `BusinessException`
  - Exceptions spécifiques pour utilisateurs, rôles et départements
  - Messages d'erreur localisés en français

### Services et Implémentation
- Implémentation des services de sécurité :
  - Service de hachage des mots de passe avec BCrypt
  - Génération sécurisée des tokens
  - Validation des mots de passe

- Mise en place du service d'emails :
  - Configuration de JavaMailSender
  - Templates Thymeleaf pour les emails
  - Gestion des notifications :
    - Vérification d'email
    - Réinitialisation de mot de passe
    - Notification de changement de mot de passe

- Implémentation du CreateUserUseCase :
  - Validation des données utilisateur
  - Vérification de l'unicité (username/email)
  - Hachage sécurisé du mot de passe
  - Création du compte désactivé
  - Envoi de l'email de vérification
