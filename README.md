# Got Web - Module de Gestion des Utilisateurs

Application de gestion des rôles et permissions avec support multi-contexte, développée avec Spring Boot et Java 21.

## Table des Matières
1. [Technologies Utilisées](#technologies-utilisées)
2. [Architecture](#architecture)
3. [Fonctionnalités Principales](#fonctionnalités-principales)
4. [Installation et Configuration](#installation-et-configuration)
5. [Documentation API](#documentation-api)
6. [Sécurité](#sécurité)
7. [Cache](#cache)
8. [Gestion des Emails](#gestion-des-emails)

## Technologies Utilisées
- Java 21
- Spring Boot 3.4.1
- Spring Security avec JWT
- Spring Data JPA
- PostgreSQL 17
- Caffeine Cache
- Maven
- Swagger/OpenAPI pour la documentation
- MapStruct pour le mapping DTO
- Lombok

## Architecture
Le projet suit les principes de la Clean Architecture avec une séparation claire des responsabilités :

### Structure des Packages
```
org.got.web.gotweb/
├── config/          # Configurations Spring Boot
├── security/        # Configuration sécurité et JWT
├── user/           
│   ├── controller/  # Contrôleurs REST
│   ├── domain/      # Entités et règles métier
│   ├── dto/         # Objets de transfert de données
│   ├── mapper/      # Mappeurs DTO <-> Entité
│   ├── repository/  # Repositories JPA
│   └── service/     # Services métier
├── mail/            # Service d'envoi d'emails
└── init/            # Initialisation des données
```

### Modèle de Données
- **GotUser** : Utilisateur du système
- **Role** : Définition des rôles
- **Permission** : Permissions atomiques
- **Department** : Structure organisationnelle
- **Context** : Contexte d'application des rôles
- **UserRole** : Association utilisateur-rôle avec contexte

## Fonctionnalités Principales

### Gestion des Utilisateurs
- Création et gestion des comptes utilisateurs
- Authentification sécurisée avec JWT
- Gestion des mots de passe avec cryptage
- Réinitialisation de mot de passe par email

### Gestion des Rôles et Permissions
- Attribution de rôles aux utilisateurs
- Gestion des permissions par contexte
- Support de la hiérarchie des départements
- Validité temporelle des rôles

### Gestion des Départements
- Structure hiérarchique des départements
- Permissions par défaut par département
- Association avec les contextes

## Installation et Configuration

### Prérequis
- JDK 21
- PostgreSQL 17
- Maven

### Configuration
1. **Base de données**
   ```yaml
   spring:
     datasource:
       url: jdbc:postgresql://localhost:5432/got_web
       username: postgres
       password: postgres
   ```

2. **JWT**
   ```yaml
   spring:
     security:
       jwt:
         keystore:
           location: classpath:jwt-keystore.p12
           password: ${JWT_KEYSTORE_PASSWORD:changeit}
           alias: ${JWT_KEY_ALIAS:got-jwt-key}
   ```

3. **Cache**
   ```yaml
   cache:
     caffeine:
       initial-capacity: 100
       maximum-size: 10000
       expire-after-write: 21600000  # 6 heures
   ```

### Démarrage
1. Cloner le repository
2. Configurer la base de données PostgreSQL
3. Exécuter : `mvn clean install`
4. Lancer l'application : `mvn spring-boot:run`

## Documentation API

### Swagger UI
- Interface : [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)
- Documentation : [http://localhost:8080/api-docs](http://localhost:8080/api-docs)

### Utilisation de l'API
1. **Authentification**
   - Obtenir un token JWT via `/api/v1/auth/login`
   - Utiliser le token dans le header : `Authorization: Bearer <token>`

2. **Endpoints Principaux**
   - Users : `/api/v1/users`
   - Roles : `/api/v1/users/{id}/roles`
   - Permissions : `/api/v1/users/{id}/roles/permissions`
   - Départements : `/api/v1/departments`

## Sécurité

### Authentification
- JWT avec clés RSA
- Tokens d'accès et de rafraîchissement
- Expiration configurable des tokens

### Autorisation
- Contrôle d'accès basé sur les rôles (RBAC)
- Permissions contextuelles
- Validation des tokens JWT

## Cache
- Cache Caffeine pour les données fréquemment accédées
- Configuration paramétrable
- Statistiques de cache disponibles

## Gestion des Emails
- Service d'envoi d'emails configurable
- Templates pour les notifications
- Support des emails HTML

## Contribution
1. Fork le projet
2. Créer une branche (`git checkout -b feature/AmazingFeature`)
3. Commit les changements (`git commit -m 'Add some AmazingFeature'`)
4. Push vers la branche (`git push origin feature/AmazingFeature`)
5. Ouvrir une Pull Request

## Licence
Propriétaire - Voir le fichier LICENSE pour plus de détails.
