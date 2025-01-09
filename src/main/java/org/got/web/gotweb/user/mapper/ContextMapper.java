package org.got.web.gotweb.user.mapper;

import org.got.web.gotweb.user.domain.Context;
import org.got.web.gotweb.user.dto.response.ContextResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface ContextMapper {
    ContextMapper INSTANCE = Mappers.getMapper(ContextMapper.class);

    ContextResponseDTO toResponseDTO(Context context);

    Context mapToContext(ContextResponseDTO userContext);
}
