package org.got.web.gotweb.user.dto.endpoint.response;

import org.got.web.gotweb.user.domain.security.AccessConditionGroup;

import java.util.Set;

public record EndpointResponseFullDTO(
        Long id,
        String name,
        String description,
        String pattern,
        String httpMethod,
        String status,
        Set<AccessConditionGroup> conditionGroups
) { }
