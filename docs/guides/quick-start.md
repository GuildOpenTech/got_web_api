# Guide de Démarrage Rapide

Ce guide vous aidera à démarrer rapidement avec le projet GOT Web ERP.

## Prérequis

- Java 21 ou supérieur
- Maven 3.8+
- PostgreSQL 17+
- IDE recommandé : IntelliJ IDEA ou VS Code
- Extension recommandée pour la documentation : mermaid, markdown

## Installation

1. **Cloner le projet**
   ```bash
   git clone [URL_DU_REPO]
   cd got_web_api
   ```

2. **Configuration de la base de données**
   - Créer une base de données PostgreSQL
   - Configurer les informations de connexion dans `application.yml`

3. **Configurer l'environnement**
   ```
   # A FAIRE
   ```

## Démarrage

1. **Compiler le projet**
   ```bash
   mvn clean install
   ```

2. **Lancer l'application**
   ```bash
   mvn spring-boot:run
   ```

L'application sera accessible à `http://localhost:8080`

## Premiers Pas

1. **Accéder à la Documentation API**
   - Swagger UI : `http://localhost:8080/swagger-ui.html`
   - OpenAPI JSON : `http://localhost:8080/v3/api-docs`

2. **Créer un Compte Admin**
   ```http
   POST http://localhost:8080/api/auth/register
   Content-Type: application/json

   {
     "username": "admin",
     "email": "admin@example.com",
     "password": "SecurePassword123!",
     "roles": ["ADMIN"]
   }
   ```

3. **Se Connecter**
   ```http
   POST http://localhost:8080/api/auth/login
   Content-Type: application/json

   {
     "username": "admin",
     "password": "SecurePassword123!"
   }
   ```

## Structure du Projet

```
got_web_api/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── org/got/erp/
│   │   └── resources/
│   └── test/
├── docs/
└── README.md
```

## Développement

### Conventions de Code
- Suivre les conventions Java standard
- Utiliser les annotations Lombok
- Utiliser les mappers MapStruct
- Documenter les APIs avec OpenAPI

### Tests
```bash
# Exécuter tous les tests
mvn test

# Tests d'intégration uniquement
mvn verify -P integration-tests
```

## Support

Pour obtenir de l'aide :
1. Consulter la [documentation complète](../README.md)
2. Ouvrir une issue sur GitHub
3. Contacter l'équipe de développement
