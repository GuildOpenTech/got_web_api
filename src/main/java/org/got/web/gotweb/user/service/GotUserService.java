package org.got.web.gotweb.user.service;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.got.web.gotweb.exception.UserException;
import org.got.web.gotweb.mail.service.EmailService;
import org.got.web.gotweb.security.CryptoService;
import org.got.web.gotweb.user.criteria.UserSearchCriteria;
import org.got.web.gotweb.user.domain.GotUser;
import org.got.web.gotweb.user.dto.request.UserCreateDTO;
import org.got.web.gotweb.user.dto.request.UserUpdateDTO;
import org.got.web.gotweb.user.dto.response.UserResponseDTO;
import org.got.web.gotweb.user.mapper.GotUserMapper;
import org.got.web.gotweb.user.repository.GotUserRepository;
import org.got.web.gotweb.user.specification.UserSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Validated
@RequiredArgsConstructor
@Slf4j
public class GotUserService {
    private final GotUserRepository userRepository;
    private final GotUserMapper userMapper;
    private final CryptoService cryptoService;
    private final EmailService emailService;

    @Transactional
    public GotUser createUser(@Valid UserCreateDTO createUserDto) {
        log.info("Création d'un nouvel utilisateur avec le nom d'utilisateur: {}", createUserDto.username());

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
                .enabled(true)
                .emailVerified(false)
                .createdAt(LocalDateTime.now())
                .failedLoginAttempts(0)
                .build();

        user = userRepository.save(user);
        log.info("Utilisateur créé avec succès, ID: {}", user.getId());

        // Envoi de l'email de vérification
        String verificationToken = cryptoService.generateEmailVerificationToken();
        user.setEmailVerificationToken(verificationToken);
        user.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
        userRepository.save(user);

        emailService.sendVerificationEmail(user, verificationToken);
        return user;
    }

    @Transactional(readOnly = true)
    public List<GotUser> getAllUsers() {
        return userRepository.findAll();
    }

    @Transactional(readOnly = true)
    public GotUser getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserException.UserNotFoundException(id));
    }

    @Transactional
    public GotUser updateUser(Long id, @Valid UserUpdateDTO updateUserDto) {
        GotUser user = userRepository.findById(id)
                .orElseThrow(() -> new UserException.UserNotFoundException(id));

        if (updateUserDto.username() != null &&
            !user.getUsername().equals(updateUserDto.username()) &&
            userRepository.existsByUsername(updateUserDto.username())) {
            throw new UserException.UserAlreadyExistsException("nom d'utilisateur", updateUserDto.username());
        }

        if (updateUserDto.email() != null &&
            !user.getEmail().equals(updateUserDto.email()) &&
            userRepository.existsByEmail(updateUserDto.email())) {
            throw new UserException.UserAlreadyExistsException("email", updateUserDto.email());
        }

        // Mise à jour des champs
        if (updateUserDto.username() != null) {
            user.setUsername(updateUserDto.username());
        }
        if (updateUserDto.email() != null) {
            user.setEmail(updateUserDto.email());
            user.setEmailVerified(false);
            String verificationToken = cryptoService.generateEmailVerificationToken();
            user.setEmailVerificationToken(verificationToken);
            user.setEmailVerificationTokenExpiresAt(LocalDateTime.now().plusHours(24));
            emailService.sendVerificationEmail(user, verificationToken);
        }
//        TODO: Gérer la mise à jour du mot de passe
//        if (updateUserDto.password() != null) {
//            user.setPassword(cryptoService.hashPassword(updateUserDto.password()));
//            emailService.sendPasswordChangeNotification(user);
//        }
        if (updateUserDto.firstName() != null) {
            user.setFirstName(updateUserDto.firstName());
        }
        if (updateUserDto.lastName() != null) {
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
    public Page<UserResponseDTO> searchUsers(UserSearchCriteria criteria, Pageable pageable) {
        return userRepository.findAll(UserSpecification.createSpecification(criteria), pageable)
                .map(userMapper::toResponseDTO);
    }

    public GotUser verifyEmail(String token) {
        GotUser user = userRepository.findByEmailVerificationToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de vérification invalide"));

        if (user.getEmailVerificationTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token de vérification expiré");
        }

        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiresAt(null);

        return userRepository.save(user);
    }

    public GotUser resetPassword(String token, String newPassword) {
        GotUser user = userRepository.findByResetPasswordToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Token de réinitialisation invalide"));

        if (user.getResetPasswordTokenExpiresAt().isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token de réinitialisation expiré");
        }

        user.setPassword(cryptoService.hashPassword(newPassword));
        user.setResetPasswordToken(null);
        user.setResetPasswordTokenExpiresAt(null);
        emailService.sendPasswordChangeNotification(user);

        return userRepository.save(user);
    }

    public void requestPasswordReset(String email) {
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

        user.setEnabled(enabled);
        if (!enabled) {
            // Invalider les sessions actives si nécessaire
            log.info("Désactivation du compte utilisateur: {}", userId);
        } else {
            log.info("Activation du compte utilisateur: {}", userId);
        }

        return userRepository.save(user);
    }
}
