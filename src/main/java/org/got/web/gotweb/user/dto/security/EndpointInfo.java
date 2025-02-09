package org.got.web.gotweb.user.dto.security;

import lombok.AllArgsConstructor;
import lombok.Data;

// Classe ou DTO pour représenter un endpoint découvert
@Data
@AllArgsConstructor
public class EndpointInfo {
    private String pattern;
    private String httpMethod;
    private String name;
    private String description;
}