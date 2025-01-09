package org.got.web.gotweb.user.mapper;

import org.got.web.gotweb.user.domain.Department;
import org.got.web.gotweb.user.dto.response.DepartmentResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {
    DepartmentMapper INSTANCE = Mappers.getMapper(DepartmentMapper.class);

    Department mapToDepartment(DepartmentResponseDTO departmentResponseDTO);
    DepartmentResponseDTO toResponseDTO(Department department);
}
