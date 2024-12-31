# Sécurité de l'Application

Ce document détaille l'architecture de sécurité de GOT Web ERP.

## Table des Matières
- [Authentication](#authentication)
- [Autorisation](#autorisation)
- [Sécurité des Mots de Passe](#sécurité-des-mots-de-passe)
- [Sécurité JWT](#sécurité-jwt)
- [Protection contre les Attaques](#protection-contre-les-attaques)
- [Logs de Sécurité](#logs-de-sécurité)

## Authentication

### Vue d'ensemble
L'authentification est un élément crucial de notre application, permettant de vérifier l'identité des utilisateurs et de protéger les ressources sensibles. Nous utilisons JWT (JSON Web Tokens) pour sa flexibilité et son passage à l'échelle dans les architectures distribuées.

### Processus d'Authentication
1. **Login** : L'utilisateur fournit ses identifiants
2. **Validation** : Le serveur vérifie les identifiants
3. **Tokens** : Deux tokens sont générés :
   - Access Token (validité : 1 heure)
   - Refresh Token (validité : 24 heures)

### Pourquoi JWT ?
- **Sans état** : Pas besoin de stocker les sessions côté serveur
- **Scalable** : Parfait pour les architectures microservices
- **Sécurisé** : Signatures cryptographiques pour garantir l'intégrité
- **Flexible** : Peut contenir des claims personnalisés

### Exemple de Requête

```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "user@example.com",
    "password": "SecurePassword123!"
}
```

## Autorisation

### Vue d'ensemble
Notre système d'autorisation utilise une approche basée sur les rôles et les permissions, offrant un contrôle granulaire sur les accès aux ressources.

### Implémentation
- **Rôles prédéfinis** : ADMIN, USER, MANAGER
- **Permissions granulaires** : READ_USERS, CREATE_USER, etc.
- **Annotations de sécurité** sur les endpoints

### Avantages
- Séparation claire des responsabilités
- Facilité de maintenance et d'audit
- Flexibilité pour ajouter de nouveaux rôles/permissions

## Sécurité des Mots de Passe

### Pourquoi une Politique Stricte ?
La sécurité des mots de passe est la première ligne de défense contre les accès non autorisés. Une politique stricte :
- Réduit les risques d'attaques par force brute
- Encourage les utilisateurs à créer des mots de passe mémorisables mais sécurisés
- Respecte les normes de sécurité modernes (NIST, OWASP)

### Règles de Validation
- **Longueur** : 8-32 caractères
  - Minimum 8 : Assez long pour résister aux attaques
  - Maximum 32 : Évite les attaques par déni de service
- **Complexité requise** :
  - Majuscule : Augmente l'entropie
  - Minuscule : Facilite la mémorisation
  - Chiffre : Ajoute de la complexité
  - Caractère spécial : Renforce la sécurité

### Implémentation Technique
```java
@StrongPassword
public String password;
```

Configuration dans `application.yml` :
```yaml
security:
  password:
    min-length: 8
    max-length: 32
    require-special: true
    require-digit: true
    require-uppercase: true
    require-lowercase: true
```

## Sécurité JWT

### Rotation des Clés : Pourquoi est-ce Important ?
La rotation des clés JWT est une pratique de sécurité essentielle qui :
- **Limite l'impact des fuites** : Si une clé est compromise, son utilisation est limitée dans le temps
- **Respecte le principe de sécurité en profondeur** : Ajoute une couche de protection supplémentaire
- **Facilite la récupération** : En cas de compromission, les nouvelles clés sont automatiquement déployées
- **Suit les bonnes pratiques** : Recommandé par OWASP et les standards de sécurité

### Comment Fonctionne la Rotation ?
1. **Génération périodique** : Une nouvelle clé est créée toutes les 24 heures
2. **Période de chevauchement** : 1 heure où l'ancienne et la nouvelle clé sont valides
   - Évite les interruptions de service
   - Permet une transition en douceur
3. **Nettoyage automatique** : Les anciennes clés sont supprimées après expiration

### Impact sur les Utilisateurs
- **Transparent** : Les utilisateurs ne remarquent pas le changement
- **Non perturbant** : Les sessions existantes restent valides
- **Sécurisé** : Les tokens invalides sont automatiquement rejetés

### Configuration
```yaml
security:
  jwt:
    key-validity-seconds: 86400  # 24 heures
    key-overlap-seconds: 3600    # 1 heure
    key-rotation-check-ms: 300000  # 5 minutes
```

## Protection contre les Attaques

### Rate Limiting
#### Objectif
Protéger l'application contre :
- Les attaques par force brute
- Les attaques par déni de service (DoS)
- Le scraping agressif

#### Implémentation
- Limite par IP et par endpoint
- Fenêtres glissantes pour plus de précision
- Réponses 429 (Too Many Requests) appropriées

### CORS (Cross-Origin Resource Sharing)
#### Importance
- Protège contre les attaques XSS et CSRF
- Contrôle précis des domaines autorisés
- Sécurise les communications cross-origin

#### Configuration
Configuration sécurisée avec :
- Origines autorisées spécifiques
- Méthodes HTTP limitées
- Headers autorisés contrôlés
- Credentials autorisés de manière sélective

### CSRF (Cross-Site Request Forgery)

#### Vue d'ensemble
La protection CSRF est une mesure de sécurité cruciale qui empêche les attaques où un site malveillant pourrait forcer le navigateur d'un utilisateur authentifié à effectuer des actions non désirées sur notre application.

#### Comment ça fonctionne ?
1. **Génération du Token**
   - Un token CSRF unique est généré pour chaque session
   - Le token est stocké dans un cookie sécurisé `XSRF-TOKEN`
   - Le cookie est HttpOnly et Secure (en HTTPS)

2. **Validation des Requêtes**
   - Chaque requête modifiante (POST, PUT, DELETE) doit inclure le token
   - Le token doit être envoyé dans l'en-tête `X-XSRF-TOKEN`
   - Les requêtes sans token valide sont rejetées

3. **Exceptions**
   - Les endpoints d'authentification sont exemptés
   - Les endpoints de documentation (Swagger) sont exemptés
   - Les requêtes GET sont exemptées

#### Implémentation
```java
// Configuration Spring Security
.csrf(csrf -> csrf
    .csrfTokenRepository(csrfTokenRepository)
    .ignoringRequestMatchers("/api/auth/**", "/api-docs/**", "/swagger-ui/**"))
```

#### Exemple d'Utilisation (Frontend)
```javascript
// Récupération automatique du token depuis le cookie
const csrfToken = document.cookie
  .split('; ')
  .find(row => row.startsWith('XSRF-TOKEN'))
  .split('=')[1];

// Inclusion dans les requêtes
fetch('/api/resource', {
  method: 'POST',
  headers: {
    'X-XSRF-TOKEN': csrfToken
  },
  body: JSON.stringify(data)
});
```

## Logs de Sécurité

### Vue d'ensemble
Notre système de logging de sécurité suit une approche minimaliste et sécurisée, ne traçant que les événements critiques tout en protégeant les données sensibles.

### Événements Tracés
- **Échecs d'authentification**
- **Activités suspectes**
- **Verrouillages de compte**
- **Révocations de session**

### Protection des Données
- Masquage systématique des identifiants utilisateurs
- Messages d'erreur génériques
- Aucune donnée sensible dans les logs
- Niveau de détail minimal mais suffisant pour l'audit

### Format des Logs
Chaque événement de sécurité contient :
```json
{
  "timestamp": "2024-12-31T02:04:40+01:00",
  "event": "AUTH_FAILURE",
  "user_id": "jo***",
  "ip_address": "192.168.1.1",
  "type": "authentication",
  "reason": "invalid_credentials"
}
```

### Bonnes Pratiques
- Logs centralisés au niveau WARN pour les événements de sécurité
- Rotation des logs configurée
- Accès restreint aux fichiers de logs
- Pas de données personnelles ou sensibles

### Rétention
- Conservation des logs de sécurité : 12 mois
- Rotation quotidienne des fichiers
- Compression des archives

## Bonnes Pratiques

### 1. Validation des Entrées
- Validation stricte côté serveur
- Échappement des caractères spéciaux
- Protection contre les injections SQL et XSS

### 2. Logging de Sécurité
- Journalisation des événements critiques
- Audit des modifications sensibles
- Alertes en temps réel pour les activités suspectes

### 3. Gestion des Sessions
- Approche stateless avec JWT
- Expiration automatique
- Révocation possible en cas de compromission

## Roadmap Sécurité

### Court Terme
- [x] Protection CSRF
- [ ] Amélioration des logs de sécurité

### Moyen Terme
- [ ] Système d'audit complet
- [ ] Détection d'intrusion

### Long Terme
- [ ] Authentification 2FA
- [ ] Intégration SSO

## Authentification

### JWT (JSON Web Tokens)

#### Configuration
- **Access Token** : Token de courte durée pour l'accès aux ressources
- **Refresh Token** : Token de longue durée pour obtenir un nouveau access token
- **Rotation des Clés** : Changement automatique des clés de signature

#### Implémentation
```java
// Création d'un access token
Claims claims = Jwts.claims()
    .setSubject(username)
    .put("type", "ACCESS");
```

#### Sécurité
- Validation complète des tokens
- Gestion des erreurs détaillée
- Blacklist des tokens révoqués
- Protection contre les attaques courantes

### Validation des Entrées

#### Utilisateurs
- Validation du nom d'utilisateur (3-50 caractères)
- Validation de l'email (format standard)
- Politique de mot de passe forte

#### Messages d'Erreur
Messages d'erreur explicites en français pour une meilleure expérience utilisateur :
```java
@NotBlank(message = "Le nom d'utilisateur est obligatoire")
@Size(min = 3, max = 50, message = "Le nom d'utilisateur doit contenir entre 3 et 50 caractères")
String username;
```

## Protection contre les Attaques

### CSRF (Cross-Site Request Forgery)

#### Vue d'ensemble
Protection contre les attaques CSRF via tokens synchronisés.

#### Implémentation
1. **Génération du Token**
   - Token unique par session
   - Stockage dans cookie non-HttpOnly
   - Validation côté serveur

2. **Configuration**
   ```java
   .csrf(csrf -> csrf
       .csrfTokenRepository(customCsrfTokenRepository)
       .ignoringRequestMatchers("/api/auth/**"))
   ```

3. **Endpoints Exclus**
   - `/api/auth/**` (authentification)
   - `/api-docs/**` (documentation)
   - `/swagger-ui/**` (interface Swagger)
   - `/actuator/**` (monitoring)

4. **Headers**
   - `X-XSRF-TOKEN` : Token CSRF
   - `X-CSRF-HEADER` : Nom de l'en-tête attendu
   - `X-CSRF-PARAM` : Nom du paramètre attendu

### CORS (Cross-Origin Resource Sharing)

Configuration détaillée pour sécuriser les requêtes cross-origin :
```java
CorsConfiguration config = new CorsConfiguration();
config.setAllowedOrigins(Arrays.asList(
    "http://localhost:3000",
    "http://localhost:8080",
    "https://got-web-erp.com"
));
config.setAllowedMethods(Arrays.asList(
    "GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"
));
```

## Bonnes Pratiques

### 1. Validation des Entrées
- Validation côté serveur systématique
- Messages d'erreur explicites
- Sanitization des données

### 2. Gestion des Erreurs
- Logging sécurisé
- Messages d'erreur appropriés
- Pas d'information sensible exposée

### 3. Authentification
- Tokens JWT avec rotation des clés
- Validation complète des tokens
- Gestion des sessions via tokens

## Roadmap Sécurité

### Court Terme
- [x] Protection CSRF
- [ ] Amélioration des logs de sécurité
- [ ] Audit des actions utilisateurs

### Moyen Terme
- [ ] Authentification 2FA
- [ ] Rate limiting par IP
- [ ] Monitoring de sécurité

### Long Terme
- [ ] Intégration SSO
- [ ] Analyse de sécurité automatisée
- [ ] Conformité RGPD complète
