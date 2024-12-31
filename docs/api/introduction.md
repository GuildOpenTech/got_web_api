# Introduction aux APIs GOT Web ERP

## Vue d'Ensemble

L'API GOT Web ERP est conçue selon les principes REST et utilise JSON comme format d'échange de données. Elle fournit un ensemble complet d'endpoints pour gérer tous les aspects de l'ERP.

## Documentation Interactive

### Swagger UI
- **Environnement de Développement** : http://localhost:8080/swagger-ui.html
- **Production** : https://api.got-web-erp.com/swagger-ui.html

### OpenAPI Specification
- **Format JSON** : `/v3/api-docs`
- **Format YAML** : `/v3/api-docs.yaml`

## Authentification

### Mécanisme
L'API utilise JWT (JSON Web Tokens) pour l'authentification. Ce choix offre plusieurs avantages :
- **Stateless** : Pas de session à gérer côté serveur
- **Scalable** : Parfait pour les architectures distribuées
- **Sécurisé** : Signatures cryptographiques
- **Auto-contenu** : Contient les informations nécessaires

### Processus
1. **Obtention du Token**
   ```http
   POST /api/auth/login
   Content-Type: application/json
   
   {
     "username": "user@example.com",
     "password": "SecurePass123!"
   }
   ```

2. **Utilisation du Token**
   ```http
   GET /api/users
   Authorization: Bearer eyJhbGciOiJIUzI1NiIs...
   ```

## Gestion des Erreurs

### Format Standard
```json
{
  "timestamp": "2024-12-31T00:00:00Z",
  "status": 400,
  "error": "Bad Request",
  "message": "Description détaillée de l'erreur",
  "path": "/api/resource"
}
```

### Codes HTTP
- **2xx** : Succès
  - 200 : OK
  - 201 : Created
  - 204 : No Content
- **4xx** : Erreurs Client
  - 400 : Bad Request
  - 401 : Unauthorized
  - 403 : Forbidden
  - 404 : Not Found
  - 429 : Too Many Requests
- **5xx** : Erreurs Serveur
  - 500 : Internal Server Error
  - 503 : Service Unavailable

## Bonnes Pratiques

### Pagination
Pour les endpoints retournant des listes :
```http
GET /api/users?page=0&size=20&sort=createdAt,desc
```

### Filtrage
Utilisez les query parameters :
```http
GET /api/users?role=ADMIN&status=ACTIVE
```

### Versioning
- Version actuelle : v1
- Format : Header `Accept: application/vnd.got-erp.v1+json`

## Rate Limiting

### Limites par Défaut
- **Endpoints Authentifiés** : 60 requêtes/minute
- **Authentication** : 5 tentatives/minute

### Headers de Réponse
```http
X-RateLimit-Limit: 60
X-RateLimit-Remaining: 58
X-RateLimit-Reset: 1640995200
```

## Sécurité

### CORS
Origines autorisées :
- `http://localhost:3000` (développement)
- `https://got-web-erp.com` (production)

### Protection
- Validation des entrées
- Rate limiting
- Rotation des clés JWT
- Politique de mots de passe forte

## Support

### Contact
- Email : support@got-web-erp.com
- Documentation : https://got-web-erp.com/docs
- Status API : https://status.got-web-erp.com

### Environnements
- **Développement** : http://localhost:8080
- **Production** : https://api.got-web-erp.com
