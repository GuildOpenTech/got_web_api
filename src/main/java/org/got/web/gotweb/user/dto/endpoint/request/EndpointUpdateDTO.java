package org.got.web.gotweb.user.dto.endpoint.request;

import org.got.web.gotweb.user.domain.security.EndpointStatus;

public record EndpointUpdateDTO(
    String name,
    String description,
    EndpointStatus status
) {

}
