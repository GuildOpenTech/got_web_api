package org.got.web.gotweb.user.mapper;

import org.got.web.gotweb.user.domain.security.Endpoint;
import org.got.web.gotweb.user.dto.endpoint.response.EndpointResponseDTO;
import org.got.web.gotweb.user.dto.endpoint.response.EndpointResponseFullDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EndpointMapper {

    EndpointResponseDTO toResponseDTO(Endpoint department);
    EndpointResponseFullDTO toResponseFullDTO(Endpoint department);
}
