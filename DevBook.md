# DevBook - Module de Gestion des Utilisateurs GOT ERP

## Technologies
- Java 21
- Spring Boot (dernière version)
- Maven
- PostgreSQL 17

## Plan de Développement

### 1. Configuration Initiale et Structure du Projet
- [x] Configuration Maven et dépendances
- [x] Configuration de base (application.yml)
- [x] Structure Clean Architecture
- [x] Mise en place de la base de données

### 2. Modèle de Données
- [x] Création des entités de base
  - [x] GotUser
  - [x] Role
  - [x] Permission
  - [x] Department
  - [x] Context
  - [x] UserRole
- [x] Configuration des relations JPA
- [x] Création des repositories

### 3. Use Cases et Règles Métier
- [x] Définition des interfaces des use cases
  - [x] Gestion des utilisateurs
  - [x] Gestion des rôles
  - [x] Gestion des départements
- [x] Définition des DTOs
- [x] Définition des exceptions métier
- [ ] Implémentation des use cases
  - [x] CreateUserUseCase
  - [ ] UpdateUserUseCase
  - [ ] ToggleUserStatusUseCase
  - [ ] SearchUsersUseCase
  - [ ] AssignRoleUseCase
  - [ ] UpdateRolePermissionsUseCase
  - [ ] ManageDepartmentUseCase
  - [ ] ConfigureDepartmentPermissionsUseCase

### 4. Services et Implémentation
- [x] Services de sécurité
  - [x] Hachage des mots de passe
  - [x] Gestion des tokens
  - [x] Validation des mots de passe
- [x] Service d'emails
  - [x] Templates d'emails
  - [x] Envoi des notifications
  - [x] Gestion des vérifications
- [ ] Services métier
  - [ ] Validation et gestion des erreurs
  - [ ] Tests unitaires

### 5. API REST et Documentation
- [ ] Contrôleurs REST
- [ ] Documentation OpenAPI/Swagger
- [ ] Tests d'intégration

### 6. Sécurité et Performance
- [ ] Configuration Spring Security
- [ ] Gestion des tokens JWT
- [ ] Cache des permissions
- [ ] Optimisation des requêtes
- [ ] Tests de charge

### 7. Documentation et Finalisation
- [ ] Documentation technique
- [ ] Guide d'utilisation
- [ ] Documentation des API
- [ ] Revue de code finale
