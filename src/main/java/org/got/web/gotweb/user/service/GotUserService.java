package org.got.web.gotweb.user.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.got.web.gotweb.common.annotations.ToLowerCase;
import org.got.web.gotweb.exception.ContextException;
import org.got.web.gotweb.exception.DepartmentException;
import org.got.web.gotweb.exception.PermissionException;
import org.got.web.gotweb.exception.RoleException;
import org.got.web.gotweb.exception.TokenException;
import org.got.web.gotweb.exception.UserException;
import org.got.web.gotweb.exception.UserRoleException;
import org.got.web.gotweb.mail.service.EmailService;
import org.got.web.gotweb.security.service.CryptoService;
import org.got.web.gotweb.user.criteria.UserSearchCriteria;
import org.got.web.gotweb.user.domain.Context;
import org.got.web.gotweb.user.domain.Department;
import org.got.web.gotweb.user.domain.GotUser;
import org.got.web.gotweb.user.domain.Permission;
import org.got.web.gotweb.user.domain.Role;
import org.got.web.gotweb.user.domain.UserRole;
import org.got.web.gotweb.user.dto.user.request.ResetPasswordDto;
import org.got.web.gotweb.user.dto.user.request.UserCreateDTO;
import org.got.web.gotweb.user.dto.user.request.UserRoleAssignToUserDTO;
import org.got.web.gotweb.user.dto.user.request.UserRolePermissionsDTO;
import org.got.web.gotweb.user.dto.user.request.UserRoleRemoveDTO;
import org.got.web.gotweb.user.dto.user.request.UserRoleUpdateValidityDTO;
import org.got.web.gotweb.user.dto.user.request.UserUpdateDTO;
import org.got.web.gotweb.user.dto.user.request.UserUpdatePasswordDTO;
import org.got.web.gotweb.user.dto.user.response.UserResponseFullDTO;
import org.got.web.gotweb.user.dto.user.response.UserRoleResponseDTO;
import org.got.web.gotweb.user.mapper.GotUserMapper;
import org.got.web.gotweb.user.mapper.UserRoleMapper;
import org.got.web.gotweb.user.repository.ContextRepository;
import org.got.web.gotweb.user.repository.DepartmentRepository;
import org.got.web.gotweb.user.repository.GotUserRepository;
import org.got.web.gotweb.user.repository.PermissionRepository;
import org.got.web.gotweb.user.repository.RoleRepository;
import org.got.web.gotweb.user.repository.UserRoleRepository;
import org.got.web.gotweb.user.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Optional;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class GotUserService {
    private final GotUserRepository userRepository;
    private final GotUserMapper userMapper;
    private final UserRoleMapper userRoleMapper;
    private final CryptoService cryptoService;
    private final EmailService emailService;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final ContextRepository contextRepository;
    private final PermissionRepository permissionRepository;

    @Transactional
    public GotUser createUser(@Valid UserCreateDTO createUserDto) {
        log.info("Création d'un nouvel utilisateur avec le username: {}", createUserDto.username());

        // Vérification de l'unicité
        if (userRepository.existsByUsername(createUserDto.username())) {
            throw new UserException.UserAlreadyExistsException("nom d'utilisateur", createUserDto.username());
        }
        if (userRepository.existsByEmail(createUserDto.email())) {
            throw new UserException.UserAlreadyExistsException("email", createUserDto.email());
        }

        // Création de l'utilisateur
        GotUser user = GotUser.builder()
                .username(createUserDto.username())
                .email(createUserDto.email())
                .password(cryptoService.hashPassword(createUserDto.password()))
                .firstName(createUserDto.firstName())
                .lastName(createUserDto.lastName())
                .enabled(createUserDto.enabled())
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .failedLoginAttempts(0)
                .userRoles(new HashSet<>())
                .build();

        user = userRepository.save(user);
        log.info("Utilisateur créé avec succès, username: {}", user.getUsername());

        // Envoi de l'email de vérification
        String verificationToken = cryptoService.generateEmailVerificationToken();
        user.setEmailVerificationToken(verificationToken);
        user.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        emailService.sendVerificationEmail(user, verificationToken);
        return user;
    }

    @Transactional
    public GotUser updateUser(Long id, @Valid UserUpdateDTO updateUserDto) {
        GotUser user = validateUsernameUserId(updateUserDto.username(), id);

        if (StringUtils.isNotBlank(updateUserDto.username()) &&
            !user.getUsername().equalsIgnoreCase(updateUserDto.username())) {
            if (userRepository.existsByUsername(updateUserDto.username())) {
                throw new UserException.UserAlreadyExistsException("username", updateUserDto.username());
            }
            user.setUsername(updateUserDto.username());
        }

        if (StringUtils.isNotBlank(updateUserDto.email())
                && !user.getEmail().equalsIgnoreCase(updateUserDto.email())) {
            if(userRepository.existsByEmail(updateUserDto.email())){
                throw new UserException.UserAlreadyExistsException("email", updateUserDto.email());
            }
            user.setEmail(updateUserDto.email().toLowerCase());
            user.setEmailVerified(false);
            String verificationToken = cryptoService.generateEmailVerificationToken();
            user.setEmailVerificationToken(verificationToken);
            user.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
            emailService.sendVerificationEmail(user, verificationToken);
        }

        if (StringUtils.isNotBlank(updateUserDto.firstName())) {
            user.setFirstName(updateUserDto.firstName());
        }
        if (StringUtils.isNotBlank(updateUserDto.lastName())) {
            user.setLastName(updateUserDto.lastName());
        }
        if (updateUserDto.enabled() != null) {
            user.setEnabled(updateUserDto.enabled());
        }

        return userRepository.save(user);
    }

    @Transactional
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new UserException.UserNotFoundException(id);
        }
        userRepository.deleteById(id);
        log.info("Utilisateur supprimé avec succès, ID: {}", id);
    }

    @Transactional(readOnly = true)
    public Page<GotUser> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable);
    }

    @Transactional(readOnly = true)
    public GotUser getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException.UserNotFoundException(id));
    }

    @Transactional(readOnly = true)
    public GotUser getUserByUsername(@NotBlank @ToLowerCase String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() -> new UserException.UserNotFoundException(username));
    }

    @Transactional(readOnly = true)
    public GotUser getUserByEmail(@NotBlank @ToLowerCase String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException(email));
    }

    @Transactional(readOnly = true)
    public Page<UserResponseFullDTO> searchUsers(UserSearchCriteria criteria, Pageable pageable) {
        return userRepository.findAll(UserSpecification.createSpecification(criteria), pageable)
                .map(userMapper::toResponseFullDTO);
    }

    public GotUser verifyEmail(@NotBlank String token) {
        GotUser user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new TokenException.InvalidTokenException(token, TokenException.VERIFICATION_EMAIL, null));

        if (user.getEmailVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenException.TokenExpiredException(token, TokenException.VERIFICATION_EMAIL, null);
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);

        return userRepository.save(user);
    }

    public GotUser changePassword(Long userId, @Valid UserUpdatePasswordDTO updatePasswordDto) {
        GotUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException(userId));

        if (!cryptoService.checkPassword(updatePasswordDto.currentPassword(), user.getPassword())) {
            throw new UserException.InvalidUserDataException("L'ancien mot de passe est incorrect");
        }

        user.setPassword(cryptoService.hashPassword(updatePasswordDto.newPassword()));

        try {
            emailService.sendPasswordChangeNotification(user);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de notification de changement de mot de passe pour l'utilisateur avec l'email {}:", user.getEmail(), e);
            //TODO : Implémenter un système de notification avec Retry et Dead Letter Queue
        }

        return userRepository.save(user);
    }

    public GotUser resetPassword(ResetPasswordDto dto) {
        GotUser user = userRepository.findByResetPasswordToken(dto.token())
                .orElseThrow(() -> new TokenException.InvalidTokenException(dto.token(), TokenException.RESET_PASSWORD, null));

        if (user.getResetPasswordTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenException.TokenExpiredException(dto.token(), TokenException.RESET_PASSWORD, null);
        }

        user.setPassword(cryptoService.hashPassword(dto.newPassword()));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiresAt(null);
        GotUser savedUser = userRepository.save(user);

        try {
            emailService.sendPasswordChangeNotification(user);
        } catch (Exception e) {
            log.error("Erreur lors de l'envoi de l'email de notification de changement de mot de passe pour l'utilisateur avec l'email {}:", user.getEmail(), e);
            //TODO : Implémenter un système de notification avec Retry et Dead Letter Queue
        }

        return savedUser;
    }

    public void verifyPasswordResetToken(String token) {
        Optional<GotUser> user = userRepository.findByResetPasswordToken(token);
        boolean exist = user.isPresent() && user.get().getResetPasswordTokenExpiresAt().isAfter(LocalDateTime.now());
        if (!exist) {
            throw new TokenException.InvalidTokenException(token, TokenException.RESET_PASSWORD, null);
        }
    }

    @Transactional
    public void requestPasswordReset(@NotBlank String email) {
        GotUser user = userRepository.findByEmail(email)
                .orElseThrow(() -> new UserException.UserNotFoundException(email));

        String resetToken = cryptoService.generatePasswordResetToken();
        user.setResetPasswordToken(resetToken);
        user.setResetPasswordTokenExpiresAt(LocalDateTime.now().plusHours(1));
        userRepository.save(user);

        emailService.sendPasswordResetEmail(user, resetToken);
    }

    @Transactional
    public GotUser toggleUserStatus(Long userId, boolean enabled) {
        GotUser user = getUserById(userId);
        if(user.isEnabled() == enabled) {
            return user;
        }

        user.setEnabled(enabled);
        if (!enabled) {
            // Invalider les sessions actives si nécessaire
            log.info("Désactivation du compte utilisateur: {}", userId);
        } else {
            log.info("Activation du compte utilisateur: {}", userId);
        }

        return userRepository.save(user);
    }

    @Transactional
    public UserRoleResponseDTO assignUserRoleToUser(Long userId, UserRoleAssignToUserDTO dto) {
        return userRoleMapper.toResponseDTO(assignUserRoleToUserEntity(userId, dto));
    }
    /**
     * Assigne un nouveau rôle à un utilisateur dans un contexte spécifique.
     */
    @Transactional
    public UserRole assignUserRoleToUserEntity(Long userId, UserRoleAssignToUserDTO dto) {
        // Vérification de l'existence de l'utilisateur
        GotUser user = validateUsernameUserId(dto.username(), userId);
        userRoleRepository.findByUserRoleDetails(userId, dto.roleId(), dto.departmentId(), dto.contextId())
                .ifPresent(userRole -> {throw new UserRoleException.DuplicateRoleException(user.getUsername());});

            // Récupération des entités nécessaires
            Role role = roleRepository.findById(dto.roleId())
                    .orElseThrow(() -> new RoleException.RoleNotFoundException(dto.roleId()));
            // Vérification si le rôle peut être multiple
            if (!role.getAllowsMultiple()) {
                boolean hasRole = user.getUserRoles().stream()
                        .anyMatch(ur -> ur.getRole().getId().equals(dto.roleId()));
                if (hasRole) {
                    throw new RoleException.DuplicateMultipleRolesException(user.getUsername(), role.getName());
                }
            }

            Department department = departmentRepository.findById(dto.departmentId())
                    .orElseThrow(() -> new DepartmentException.DepartmentNotFoundException(dto.departmentId()));
            Context context = contextRepository.findById(dto.contextId())
                    .orElseThrow(() -> new ContextException.ContextNotFoundException(dto.contextId()));

            boolean isContextInDepartment = department.getContexts().stream()
                    .anyMatch(c -> c.getId().equals(context.getId()));

            if (!isContextInDepartment) {
                throw new ContextException.InvalidContextOperationException(
                        String.format("Le contexte %s n'appartient pas au département %s", context.getName(), department.getName()));
            }

            // Création du nouveau UserRole
            UserRole userRole = UserRole.builder()
                    .gotUser(user)
                    .role(role)
                    .department(department)
                    .context(context)
                    .validFrom(dto.validFrom())
                    .validTo(dto.validTo())
                    .permissions(new HashSet<>())
                    .build();

        // Ajout des permissions spécifiques si fournies
        if (dto.permissionIds() != null) {
            for (Long permissionId : dto.permissionIds()) {
                Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new PermissionException.PermissionNotFoundException(permissionId));
                // Utilise la nouvelle méthode pour ajouter uniquement les permissions non héritées
                if (userRole.addDirectPermission(permission)) {
                    log.debug("Permission directe ajoutée à l'utilisateur {}: {}", 
                        user.getUsername(), permission.getName());
                } else {
                    log.debug("Permission {} déjà présente via Role ou Department pour l'utilisateur {}", 
                        permission.getName(), user.getUsername());
                }
            }
        }

        // Sauvegarde du UserRole
        userRole = userRoleRepository.save(userRole);
        return userRole;
    }

    @Transactional
    public UserRoleResponseDTO updateUserRoleValidity(Long userId, UserRoleUpdateValidityDTO dto) {
        return userRoleMapper.toResponseDTO(updateUserRoleValidityEntity(userId, dto));
    }

    @Transactional
    public UserRole updateUserRoleValidityEntity(Long userId, UserRoleUpdateValidityDTO dto) {
        validateUsernameUserId(dto.username(), userId);

        UserRole userRole = userRoleRepository.findByUserRoleDetails(
                userId, dto.roleId(), dto.departmentId(), dto.contextId())
            .orElseThrow(() -> new UserRoleException.UserRoleNotFoundException(
                    String.format("[User:%s-Role:%s-Department:%s-Context%s]", dto.username(), dto.roleId(), dto.departmentId(), dto.contextId())));
        
        // Vérification de la logique de validité
        if (dto.validFrom() != null && dto.validTo() != null && dto.validFrom().isAfter(dto.validTo())) {
            throw new UserRoleException.InvalidValidityPeriodException();
        }
        userRole.setValidFrom(dto.validFrom());
        userRole.setValidTo(dto.validTo());
        return userRoleRepository.save(userRole);
    }

    @Transactional
    public void removeUserRole(Long userId, UserRoleRemoveDTO dto) {
        validateUsernameUserId(dto.username(), userId);

        UserRole userRole = userRoleRepository.findByUserRoleDetails(
                userId, dto.roleId(), dto.departmentId(), dto.contextId())
            .orElseThrow(() -> new UserRoleException.UserRoleNotFoundException(
                    String.format("[User:%s-Role:%s-Department:%s-Context%s]", userId, dto.roleId(), dto.departmentId(), dto.contextId())));
        userRoleRepository.deleteById(userRole.getId());
    }

    @Transactional
    public UserRoleResponseDTO assignPermissionToUserRole(Long userId, UserRolePermissionsDTO dto) {
        return userRoleMapper.toResponseDTO(assignPermissionToUserRoleEntity(userId, dto));
    }

    @Transactional
    public UserRole assignPermissionToUserRoleEntity(Long userId, UserRolePermissionsDTO dto) {
        validateUsernameUserId(dto.username(), userId);

        UserRole userRole = userRoleRepository.findByUserRoleDetails(
                userId, dto.roleId(), dto.departmentId(), dto.contextId())
            .orElseThrow(() -> new UserRoleException.UserRoleNotFoundException(
                    String.format("[User:%s-Role:%s-Department:%s-Context:%s]", userId, dto.roleId(), dto.departmentId(), dto.contextId())));
        
        for (Long permissionId : dto.permissionIds()) {
            Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new PermissionException.PermissionNotFoundException(permissionId));
            if (!userRole.hasPermission(permission.getName())) {
                userRole.getPermissions().add(permission);
            }
        }
        return userRoleRepository.save(userRole);
    }

    @Transactional
    public UserRoleResponseDTO removePermissionFromUserRole(Long userId, UserRolePermissionsDTO dto) {
        return userRoleMapper.toResponseDTO(removePermissionFromUserRoleEntity(userId, dto));
    }

    @Transactional
    public UserRole removePermissionFromUserRoleEntity(Long userId, UserRolePermissionsDTO dto) {
        validateUsernameUserId(dto.username(), userId);

        UserRole userRole = userRoleRepository.findByUserRoleDetails(
                userId, dto.roleId(), dto.departmentId(), dto.contextId())
            .orElseThrow(() -> new UserRoleException.UserRoleNotFoundException(
                    String.format("[User:%s-Role:%s-Department:%s-Context%s]", userId, dto.roleId(), dto.departmentId(), dto.contextId())));

        for (Long permissionId : dto.permissionIds()) {
            Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new PermissionException.PermissionNotFoundException(permissionId));
            userRole.getPermissions().remove(permission);
        }
        return userRoleRepository.save(userRole);
    }

    @Transactional(readOnly = true)
    protected GotUser validateUsernameUserId(String username, Long userId) {
        GotUser user = getUserByUsername(username);
        if (!userId.equals(user.getId())) {
            throw new UserException.InvalidUserDataException("L'id de l'utilisateur ne correspond pas au username");
        }
        return user;
    }

    @Transactional(readOnly = true)
    public boolean existsByEmailVerificationToken(String token) {
        return userRepository.existsByEmailVerificationToken(token);
    }

    @Transactional(readOnly = true)
    public boolean existsByResetPasswordToken(String token) {
        return userRepository.existsByResetPasswordToken(token);
    }
}
