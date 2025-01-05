package org.got.web.gotweb.user.mapper;

import org.got.web.gotweb.user.domain.Context;
import org.got.web.gotweb.user.domain.Department;
import org.got.web.gotweb.user.domain.GotUser;
import org.got.web.gotweb.user.domain.Role;
import org.got.web.gotweb.user.dto.response.UserResponseDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.List;
import java.util.Set;

@Mapper(componentModel = "spring")
public interface GotUserMapper {

    @Mapping(source = "roles", target = "roles", qualifiedByName = "rolesToStrings")
    @Mapping(source = "contexts", target = "contexts", qualifiedByName = "contextsToStrings")
    @Mapping(source = "departments", target = "departments", qualifiedByName = "departmentsToStrings")
    UserResponseDTO toResponseDTO(GotUser gotUser);

    List<UserResponseDTO> toResponseDTOs(List<GotUser> gotUsers);

    @Named("rolesToStrings")
    default Set<String> rolesToStrings(Set<Role> roles) {
        return MapperUtils.mapRolesToStrings(roles);
    }

    @Named("contextsToStrings")
    default Set<String> contextsToStrings(Set<Context> contexts) {
        return MapperUtils.mapContextsToStrings(contexts);
    }

    @Named("departmentsToStrings")
    default Set<String> departmentsToStrings(Set<Department> departments) {
        return MapperUtils.mapDepartmentsToStrings(departments);
    }
}
