package org.got.erp.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {

    @Value("${app.swagger.version}")
    private String version;

    @Value("${app.name}")
    private String appName;

    @Value("${app.description}")
    private String appDescription;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title(appName)
                        .version(version)
                        .description(appDescription + "\n\n" +
                                "## Authentication\n" +
                                "Cette API utilise JWT pour l'authentification. Pour accéder aux endpoints protégés :\n" +
                                "1. Obtenez un token via `/api/auth/login`\n" +
                                "2. Incluez le token dans le header : `Authorization: Bearer <token>`\n\n" +
                                "## Rate Limiting\n" +
                                "Les appels API sont limités à :\n" +
                                "- 60 requêtes/minute pour les endpoints authentifiés\n" +
                                "- 5 tentatives/minute pour les endpoints d'authentification\n\n" +
                                "## Réponses d'Erreur\n" +
                                "- 400 : Requête invalide\n" +
                                "- 401 : Non authentifié\n" +
                                "- 403 : Non autorisé\n" +
                                "- 429 : Trop de requêtes")
                        .contact(new Contact()
                                .name("GOT Web ERP Team")
                                .email("support@got-web-erp.com")
                                .url("https://got-web-erp.com"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://got-web-erp.com/license")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8080")
                                .description("Serveur de développement"),
                        new Server()
                                .url("https://api.got-web-erp.com")
                                .description("Serveur de production")))
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .name("bearerAuth")
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("Utilisez le token JWT obtenu via /api/auth/login")));
    }
}
