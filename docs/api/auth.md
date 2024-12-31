# Authentication & Autorisation

Ce document détaille les endpoints d'authentification et les mécanismes d'autorisation de l'API.

## Endpoints d'Authentification

### Login

Authentifie un utilisateur et retourne les tokens d'accès.

```http
POST /api/auth/login
Content-Type: application/json

{
    "username": "user@example.com",
    "password": "SecurePassword123!"
}
```

#### Réponse Réussie
```json
{
    "accessToken": "eyJhbGciOiJIUzI1...",
    "refreshToken": "eyJhbGciOiJIUzI1...",
    "tokenType": "Bearer",
    "expiresIn": 3600
}
```

### Refresh Token

Obtient un nouveau access token en utilisant un refresh token valide.

```http
POST /api/auth/refresh
Authorization: Bearer <refresh_token>
```

#### Réponse Réussie
```json
{
    "accessToken": "eyJhbGciOiJIUzI1...",
    "tokenType": "Bearer",
    "expiresIn": 3600
}
```

### Logout

Invalide les tokens de l'utilisateur.

```http
POST /api/auth/logout
Authorization: Bearer <access_token>
```

## Structure des Tokens

### Access Token
- Validité : 1 heure
- Contient :
  - ID utilisateur
  - Username
  - Rôles
  - Permissions

### Refresh Token
- Validité : 24 heures
- Contient :
  - ID utilisateur
  - Token version

## Sécurité

### Rotation des Clés JWT
- Rotation automatique toutes les 24 heures
- Période de chevauchement pour transition fluide
- Invalidation automatique des anciennes clés

### Rate Limiting
- 5 tentatives de login par minute par IP
- 60 requêtes par minute pour les endpoints authentifiés

## Rôles et Permissions

### Rôles Prédéfinis
- ADMIN : Accès complet
- USER : Accès limité aux fonctionnalités de base
- MANAGER : Accès aux fonctionnalités de gestion

### Permissions
Format : `ACTION_RESOURCE`

Exemples :
- READ_USERS
- CREATE_USER
- UPDATE_USER
- DELETE_USER

## Gestion des Erreurs

### Codes d'Erreur Communs
- 401 : Non authentifié
- 403 : Non autorisé
- 429 : Trop de requêtes

### Format des Erreurs
```json
{
    "timestamp": "2024-12-31T00:00:00Z",
    "status": 401,
    "error": "Unauthorized",
    "message": "Invalid credentials",
    "path": "/api/auth/login"
}
```

## Bonnes Pratiques

1. **Stockage des Tokens**
   - Stocker dans un cookie HttpOnly pour le web
   - Utiliser le stockage sécurisé pour les applications mobiles

2. **Renouvellement des Tokens**
   - Renouveler l'access token avant expiration
   - Implémenter une logique de retry en cas d'échec

3. **Sécurité**
   - Toujours utiliser HTTPS
   - Valider les tokens côté client et serveur
   - Gérer proprement les erreurs d'authentification
