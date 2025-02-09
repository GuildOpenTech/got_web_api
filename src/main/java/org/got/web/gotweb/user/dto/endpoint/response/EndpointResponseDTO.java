package org.got.web.gotweb.user.dto.endpoint.response;

public record EndpointResponseDTO(
        Long id,
        String name,
        String description,
        String pattern,
        String httpMethod,
        String status
) { }
