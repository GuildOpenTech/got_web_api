package org.got.web.gotweb.user.mapper;

import org.got.web.gotweb.user.domain.Context;
import org.got.web.gotweb.user.dto.context.response.ContextResponseDTO;
import org.mapstruct.Mapper;

import java.util.Set;

@Mapper(componentModel = "spring")
public interface ContextMapper {

    //@Mapping(target = "department", ignore = true)
    ContextResponseDTO toResponseDTO(Context context);
    Set<ContextResponseDTO> toResponseDTO(Set<Context> contexts);
}
