package org.got.web.gotweb.user.service;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.got.web.gotweb.exception.ContextException;
import org.got.web.gotweb.exception.DepartmentException;
import org.got.web.gotweb.exception.RoleException;
import org.got.web.gotweb.exception.TokenException;
import org.got.web.gotweb.exception.UserException;
import org.got.web.gotweb.exception.UserRoleException;
import org.got.web.gotweb.mail.service.EmailService;
import org.got.web.gotweb.security.CryptoService;
import org.got.web.gotweb.user.criteria.UserSearchCriteria;
import org.got.web.gotweb.user.domain.Context;
import org.got.web.gotweb.user.domain.Department;
import org.got.web.gotweb.user.domain.GotUser;
import org.got.web.gotweb.user.domain.Permission;
import org.got.web.gotweb.user.domain.Role;
import org.got.web.gotweb.user.domain.UserRole;
import org.got.web.gotweb.user.dto.request.user.request.UserCreateDTO;
import org.got.web.gotweb.user.dto.request.user.request.UserRoleAssignDTO;
import org.got.web.gotweb.user.dto.request.user.request.UserUpdateDTO;
import org.got.web.gotweb.user.dto.request.user.request.UserUpdatePasswordDTO;
import org.got.web.gotweb.user.dto.request.user.response.UserResponseFullDTO;
import org.got.web.gotweb.user.mapper.GotUserMapper;
import org.got.web.gotweb.user.repository.ContextRepository;
import org.got.web.gotweb.user.repository.DepartmentRepository;
import org.got.web.gotweb.user.repository.GotUserRepository;
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
import java.util.Set;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class GotUserService {
    private final GotUserRepository userRepository;
    private final GotUserMapper userMapper;
    private final CryptoService cryptoService;
    private final EmailService emailService;
    private final UserRoleRepository userRoleRepository;
    private final RoleRepository roleRepository;
    private final DepartmentRepository departmentRepository;
    private final ContextRepository contextRepository;

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
//                .emailVerified(false)
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
        GotUser user = userRepository.findById(id)
                .orElseThrow(() -> new UserException.UserNotFoundException(id));

        if (StringUtils.isNotBlank(updateUserDto.username()) &&
            !user.getUsername().equals(updateUserDto.username()) &&
            userRepository.existsByUsername(updateUserDto.username())) {
            throw new UserException.UserAlreadyExistsException("username", updateUserDto.username());
        }

        if (StringUtils.isNotBlank(updateUserDto.email()) &&
            !user.getEmail().equals(updateUserDto.email()) &&
            userRepository.existsByEmail(updateUserDto.email())) {
            throw new UserException.UserAlreadyExistsException("email", updateUserDto.email());
        }

        // Mise à jour des champs
        if (StringUtils.isNotBlank(updateUserDto.username())) {
            user.setUsername(updateUserDto.username());
        }

        if (StringUtils.isNotBlank(updateUserDto.email())) {
            user.setEmail(updateUserDto.email());
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
    public Page<UserResponseFullDTO> searchUsers(UserSearchCriteria criteria, Pageable pageable) {
        return userRepository.findAll(UserSpecification.createSpecification(criteria), pageable)
                .map(userMapper::toResponseFullDTO);
    }

    public GotUser updateUserRole(Long userId, Set<UserRole> userRoles) {
        GotUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException(userId));

        //TODO: Vérifier les rôles

        return userRepository.save(user);
    }

    public GotUser verifyEmail(@NotBlank String token) {
        GotUser user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new TokenException.InvalidTokenException(token, TokenException.VERIFICATION_EMAIL));

        if (user.getEmailVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenException.TokenExpiredException(token, TokenException.VERIFICATION_EMAIL);
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);

        return userRepository.save(user);
    }

//    public GotUser verifyEmail(@NotBlank String email, @NotBlank String token) {
//        GotUser user = userRepository.findByEmail(email)
//                .orElseThrow(() -> new UserException.UserNotFoundException("email", email));
//
//        if(user.isEmailVerified()) {
//            throw new UserException("L'email est déjà vérifié");
//        }
//
//        if (user.getEmailVerificationTokenExpiresAt() == null || user.getEmailVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
//            throw new TokenException.TokenExpiredException(token, TokenException.VERIFICATION_EMAIL);
//        }
//
//        user.setEmailVerified(true);
//        user.setEmailVerificationToken(null);
//        user.setEmailVerificationTokenExpiresAt(null);
//
//        return userRepository.save(user);
//    }


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

    public GotUser resetPassword(@NotBlank String token, @NotBlank String newPassword) {
        GotUser user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new TokenException.InvalidTokenException(token, TokenException.RESET_PASSWORD));

        if (user.getResetPasswordTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new TokenException.TokenExpiredException(token, TokenException.RESET_PASSWORD);
        }

        user.setPassword(cryptoService.hashPassword(newPassword));
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
        GotUser user = userRepository.findById(userId)
                .orElseThrow(() -> new UserException.UserNotFoundException(userId));
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

    /**
     * Assigne un nouveau rôle à un utilisateur dans un contexte spécifique.
     */
    @Transactional
    public UserRole assignUserRole(UserRoleAssignDTO dto) {
        // Vérification de l'existence de l'utilisateur
        GotUser user = userRepository.findById(dto.userId())
                .orElseThrow(() -> new UserException.UserNotFoundException(dto.userId()));

        // Récupération des entités nécessaires
        Role role = roleRepository.findById(dto.roleId())
                .orElseThrow(() -> new RoleException.RoleNotFoundException(dto.roleId()));
        Department department = departmentRepository.findById(dto.departmentId())
                .orElseThrow(() -> new DepartmentException.DepartmentNotFoundException(dto.departmentId()));
        Context context = null;
        if (dto.contextId() != null) {
            context = contextRepository.findById(dto.contextId())
                    .orElseThrow(() -> new ContextException.ContextNotFoundException(dto.contextId()));
        }

        // Vérification si le rôle peut être multiple
        if (!role.isAllowsMultiple()) {
            boolean hasRole = user.getUserRoles().stream()
                    .anyMatch(ur -> ur.getRole().getId().equals(dto.roleId()));
            if (hasRole) {
                throw new UserRoleException.DuplicateRoleException(user.getUsername(), role.getName());
            }
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
        if (dto.permissions() != null) {
            userRole.getPermissions().addAll(dto.permissions());
        }

        // Validation et fusion des permissions
        userRole.validatePermissions();

        // Sauvegarde du UserRole
        userRole = userRoleRepository.save(userRole);
        
        // Mise à jour de la collection de rôles de l'utilisateur
        user.getUserRoles().add(userRole);
        userRepository.save(user);

        return userRole;
    }

    /**
     * Met à jour un UserRole existant.
     * Cette méthode permet de :
     * 1. Modifier les dates de validité
     * 2. Modifier le contexte
     * 3. Modifier les permissions spécifiques
     *
     * @param userRoleId ID du UserRole à mettre à jour
     * @param contextId Nouveau contexte (optionnel)
     * @param validFrom Nouvelle date de début de validité (optionnel)
     * @param validTo Nouvelle date de fin de validité (optionnel)
     * @param specificPermissions Nouvelles permissions spécifiques (optionnel)
     * @return Le UserRole mis à jour
     */
    @Transactional
    public UserRole updateUserRole(Long userRoleId, Long contextId,
                                 LocalDateTime validFrom, LocalDateTime validTo,
                                 Set<Permission> specificPermissions) {
        UserRole userRole = userRoleRepository.findById(userRoleId)
                .orElseThrow(() -> new UserRoleException.UserRoleNotFoundException(userRoleId));

        // Mise à jour du contexte si fourni
        if (contextId != null) {
            Context context = contextRepository.findById(contextId)
                    .orElseThrow(() -> new ContextException.ContextNotFoundException(contextId));
            userRole.setContext(context);
        }

        // Mise à jour des dates de validité
        userRole.setValidFrom(validFrom);
        userRole.setValidTo(validTo);

        // Mise à jour des permissions spécifiques si fournies
        if (specificPermissions != null) {
            userRole.getPermissions().clear();
            userRole.getPermissions().addAll(specificPermissions);
        }

        // Revalidation des permissions
        userRole.validatePermissions();

        return userRoleRepository.save(userRole);
    }

    /**
     * Supprime un UserRole d'un utilisateur.
     * Cette méthode vérifie :
     * 1. Si le UserRole existe
     * 2. Si l'utilisateur a le droit de supprimer ce rôle
     *
     * @param userRoleId ID du UserRole à supprimer
     */
    @Transactional
    public void removeUserRole(Long userRoleId) {
        UserRole userRole = userRoleRepository.findById(userRoleId)
                .orElseThrow(() -> new UserRoleException.UserRoleNotFoundException(userRoleId));

        // Suppression de la référence dans l'utilisateur
        GotUser user = userRole.getGotUser();
        user.getUserRoles().remove(userRole);
        userRepository.save(user);

        // Suppression du UserRole
        userRoleRepository.delete(userRole);
    }
}
