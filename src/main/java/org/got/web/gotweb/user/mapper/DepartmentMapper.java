package org.got.web.gotweb.user.mapper;

import org.got.web.gotweb.user.domain.Department;
import org.got.web.gotweb.user.dto.department.response.DepartmentResponseDTO;
import org.got.web.gotweb.user.dto.department.response.DepartmentSimpleResponseDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring", uses = {PermissionMapper.class, ContextMapper.class})
public interface DepartmentMapper {

    DepartmentResponseDTO toResponseDTO(Department department);
    DepartmentSimpleResponseDTO toSimpleResponseDTO(Department department);
}
