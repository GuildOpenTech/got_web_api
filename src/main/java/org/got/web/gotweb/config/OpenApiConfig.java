package org.got.web.gotweb.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeIn;
import io.swagger.v3.oas.annotations.enums.SecuritySchemeType;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.security.SecurityScheme;
import io.swagger.v3.oas.annotations.servers.Server;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration de la documentation OpenAPI (Swagger) pour l'application.
 * Cette classe configure l'interface Swagger UI et définit les informations globales de l'API.
 */
@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Got Web ERP API",
                version = "1.0",
                description = "API de gestion des rôles et permissions avec support multi-contexte",
                contact = @Contact(
                        name = "Got Web Team",
                        email = "contact@gotweb.org"
                )
        ),
        servers = {
                @Server(
                        description = "Local ENV",
                        url = "http://localhost:8080"
                )
        },
        security = {
                @SecurityRequirement(
                        name = "bearerAuth"
                )
        }
)
@SecurityScheme(
        name = "bearerAuth",
        description = "JWT auth description",
        scheme = "bearer",
        type = SecuritySchemeType.HTTP,
        bearerFormat = "JWT",
        in = SecuritySchemeIn.HEADER
)
public class OpenApiConfig {
    // La configuration est gérée par les annotations
}
