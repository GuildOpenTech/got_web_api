# DevBook - Gestion de la Sécurité

## 1. Analyse de l'Architecture Existante

### 1.1 Package User
- [x] Analyse complète de la structure des entités
  - Architecture bien structurée avec une séparation claire des responsabilités
  - Entités principales : User, Role, Permission, Department, Context, UserRole
  - Relations bien définies avec une gestion fine des permissions

#### Points forts :
- Hiérarchie des départements bien gérée
- Gestion contextuelle des rôles
- Système de permissions granulaire
- Support pour des structures organisationnelles complexes

#### Points d'amélioration :
- [ ] Ajouter la validation des permissions au niveau du Context
- [ ] Implémenter la gestion des permissions héritées dans la hiérarchie des départements
- [ ] Optimiser les requêtes avec des chargements lazy/eager appropriés
- [ ] Ajouter des contraintes de validation métier

### 1.2 Package Security
- [x] Analyse du système d'authentification actuel
  - Utilisation de Spring Security avec JWT
  - Configuration de base bien établie

#### Points forts :
- [x] Authentification stateless avec JWT
  - Clés RSA asymétriques (4096 bits)
  - Stockage sécurisé dans un keystore PKCS12
  - Rotation des clés possible
  - Service JWT complet avec refresh tokens
  - Cache des tokens révoqués
  - Claims personnalisés pour les rôles et permissions
- Filtres de sécurité bien configurés
- Service d'authentification fonctionnel

#### Points d'amélioration :
- [x] Renforcer la sécurité du JWT
  - Implémentation des refresh tokens 
  - Configuration des durées d'expiration 
  - Gestion des clés RSA 
  - Révocation des tokens 
  - Claims personnalisés 
- [ ] Ajouter la gestion des sessions concurrentes
- [ ] Implémenter un système de blocage de compte
- [ ] Ajouter des logs de sécurité détaillés

## 2. Plan d'Amélioration

### 2.1 Sécurité Renforcée
- [x] Configuration JWT sécurisée
  - Clés RSA 4096 bits 
  - Keystore PKCS12 
  - Gestion des refresh tokens 
  - Service de gestion des tokens 
  - Filtre d'authentification JWT 
- [x] Système de révocation des tokens
  - Cache des tokens révoqués avec gestion de l'expiration
  - Révocation individuelle des tokens
  - Révocation globale par utilisateur
  - Nettoyage automatique des tokens expirés
  - Validation à deux niveaux (token + utilisateur)
- [x] Gestion complète des permissions
  - Permissions directes des UserRoles
  - Permissions héritées des Roles
  - Permissions par défaut des Departments
  - Validation stricte des permissions dans les tokens
- [x] Validation renforcée des tokens
  - Vérification du type de token (ACCESS/REFRESH)
  - Validation de l'émetteur
  - Vérification des claims obligatoires
  - Gestion améliorée des erreurs
  - Logging détaillé des opérations de sécurité
- [ ] Gestion des tentatives de connexion échouées
- [ ] Audit logging des actions sensibles

### 2.2 Optimisation des Performances
- [x] Mise en place du cache hybride
  - Cache local avec Caffeine
  - Cache distribué optionnel avec Redis
  - Configuration flexible via application.yml
  - Caches configurés :
    * `revokedTokens` : Liste noire des JWT
    * `userPermissions` : Cache des permissions
    * `userRoles` : Cache des rôles
    * `roles`, `permissions` : Données de référence
    * `departments`, `contexts` : Structure organisationnelle
- [ ] Optimisation des requêtes JPA
- [ ] Indexation appropriée des tables

### 2.3 Nouvelles Fonctionnalités
- [ ] API de gestion des rôles et permissions
- [ ] Interface d'administration des utilisateurs
- [ ] Système de notification des changements de droits

## 3. Tests et Documentation
- [ ] Tests unitaires pour les services de sécurité
- [ ] Tests d'intégration pour les workflows d'authentification
- [ ] Documentation API avec Swagger
- [ ] Guide d'utilisation des fonctionnalités de sécurité

## 4. Configuration Technique

### 4.1 Cache
```yaml
cache:
  type: local # local ou distributed
  provider: caffeine # caffeine ou redis
  
  # Configuration Caffeine
  caffeine:
    initial-capacity: 100
    maximum-size: 10000
    expire-after-write: 21600000 # 6 heures
    expire-after-access: 43200000 # 12 heures
    record-stats: true
  
  # Configuration Redis
  redis:
    enabled: false
    host: localhost
    port: 6379
    database: 0
    password: 
    ttl: 21600000 # 6 heures
    key-prefix: got:
```

### 4.2 JWT

#### Configuration YAML
```yaml
spring:
  security:
    jwt:
      keystore:
        location: classpath:jwt-keystore.p12
        password: ${JWT_KEYSTORE_PASSWORD:changeit}
        alias: ${JWT_KEY_ALIAS:got-jwt-key}
      expiration: ${JWT_EXPIRATION:86400000} # 24 heures
      refresh-token:
        expiration: ${JWT_REFRESH_EXPIRATION:604800000} # 7 jours
      issuer: ${JWT_ISSUER:got-web}
```

#### Configuration de Sécurité
La sécurité est configurée dans `SecurityConfig` avec :

1. **Authentification**
   - `CustomUserDetailsService` pour charger les utilisateurs
   - `BCryptPasswordEncoder` pour le hachage des mots de passe
   - `DaoAuthenticationProvider` pour l'authentification par base de données
   - `AuthenticationManager` pour gérer le processus d'authentification

2. **Configuration HTTP**
   - Mode stateless (pas de session)
   - CSRF désactivé pour les API REST
   - Endpoints publics : `/api/auth/**`, `/api/public/**`
   - Autres endpoints nécessitent une authentification

3. **Filtres**
   - `JwtAuthenticationFilter` pour la validation des tokens
   - Intégration avec Spring Security via `UsernamePasswordAuthenticationFilter`

4. **Annotations**
   - `@EnableWebSecurity` pour activer la sécurité web
   - `@EnableMethodSecurity` pour la sécurité au niveau des méthodes
   - Support des annotations `@PreAuthorize`, `@PostAuthorize`, etc.

#### Structure des Claims JWT
```json
{
  "jti": "UUID unique du token",
  "iss": "got-web",
  "sub": "nom d'utilisateur",
  "iat": "date d'émission",
  "exp": "date d'expiration",
  "type": "ACCESS ou REFRESH",
  "userId": "ID de l'utilisateur",
  "roles": [
    "ADMIN:1:1",    // Format: ROLE:DEPARTMENT_ID:CONTEXT_ID
    "USER:2:3"
  ],
  "permissions": [
    "READ_USER",
    "WRITE_USER",
    "DELETE_USER"
  ]
}
```

#### Composants JWT
1. **JwtTokens** (Records)
   - `TokenPair`: Paire access token / refresh token
   - `UserClaims`: Claims personnalisés (userId, roles, permissions)
   - `TokenInfo`: Informations complètes sur un token
   - `RevokedToken`: Token révoqué avec date d'expiration

2. **JwtService**
   - Génération de tokens (access + refresh)
   - Validation et parsing des tokens avec vérifications strictes :
     * Type de token (ACCESS/REFRESH)
     * Émetteur du token
     * Claims obligatoires
     * Signature et expiration
   - Révocation des tokens :
     * Révocation individuelle avec vérifications
     * Révocation globale par utilisateur
     * Gestion intelligente du cache des révocations
     * Nettoyage automatique des tokens expirés
   - Rafraîchissement des tokens
   - Logging détaillé des opérations

3. **JwtAuthenticationToken**
   - Token d'authentification Spring Security
   - Stockage des informations utilisateur
   - Conversion des rôles en GrantedAuthority

4. **JwtAuthenticationFilter**
   - Extraction du token des requêtes HTTP
   - Validation automatique du token
   - Gestion des erreurs d'authentification

### 4.3 Génération du Keystore JWT
```bash
# Configuration
KEYSTORE_FILE="src/main/resources/jwt-keystore.p12"
KEYSTORE_PASSWORD="changeit"
KEY_ALIAS="got-jwt-key"
VALIDITY=3650 # 10 ans

# Génération des clés
keytool -genkeypair \
  -alias $KEY_ALIAS \
  -keyalg RSA \
  -keysize 4096 \
  -validity $VALIDITY \
  -storetype PKCS12 \
  -keystore $KEYSTORE_FILE \
  -storepass $KEYSTORE_PASSWORD
