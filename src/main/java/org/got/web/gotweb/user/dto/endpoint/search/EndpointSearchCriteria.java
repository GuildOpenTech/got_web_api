package org.got.web.gotweb.user.dto.endpoint.search;

public record EndpointSearchCriteria(
    String name,
    String pattern,
    String httpMethod,
    String description,
    String status
) { }
