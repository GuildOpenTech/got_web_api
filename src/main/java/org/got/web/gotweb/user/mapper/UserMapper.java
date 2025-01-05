//package org.got.web.gotweb.user.mapper;
//
//import org.got.web.gotweb.user.domain.GotUser;
//import org.got.web.gotweb.user.dto.request.UserCreateDTO;
//import org.got.web.gotweb.user.dto.request.UserUpdateDTO;
//import org.got.web.gotweb.user.dto.response.UserResponseDTO;
//import org.mapstruct.BeanMapping;
//import org.mapstruct.Mapper;
//import org.mapstruct.Mapping;
//import org.mapstruct.MappingTarget;
//import org.mapstruct.NullValuePropertyMappingStrategy;
//import org.mapstruct.ReportingPolicy;
//
//import java.util.List;
//
//@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
//public interface UserMapper {
//
//    @Mapping(target = "id", ignore = true)
//    @Mapping(target = "enabled", constant = "true")
//    @Mapping(target = "emailVerified", constant = "false")
//    @Mapping(target = "userRoles", ignore = true)
//    @Mapping(target = "createdAt", ignore = true)
//    @Mapping(target = "updatedAt", ignore = true)
//    @Mapping(target = "lastLoginAt", ignore = true)
//    @Mapping(target = "emailVerificationToken", ignore = true)
//    @Mapping(target = "emailVerificationTokenExpiresAt", ignore = true)
//    @Mapping(target = "resetPasswordToken", ignore = true)
//    @Mapping(target = "resetPasswordTokenExpiresAt", ignore = true)
//    @Mapping(target = "failedLoginAttempts", ignore = true)
//    @Mapping(target = "lastFailedLoginAttempt", ignore = true)
//    @Mapping(target = "lockedUntil", ignore = true)
//    GotUser toEntity(UserCreateDTO dto);
//
//    @Mapping(target = "roles", expression = "java(user.getRoles().stream().map(role -> role.getName()).collect(java.util.stream.Collectors.toSet()))")
//    @Mapping(target = "departments", expression = "java(user.getDepartments().stream().map(dept -> dept.getName()).collect(java.util.stream.Collectors.toSet()))")
//    @Mapping(target = "contexts", expression = "java(user.getContexts().stream().map(ctx -> ctx.getName()).collect(java.util.stream.Collectors.toSet()))")
//    UserResponseDTO toDto(GotUser user);
//
//    List<UserResponseDTO> toDtoList(List<GotUser> users);
//
//    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
//    void updateEntityFromDto(UserUpdateDTO dto, @MappingTarget GotUser user);
//}
