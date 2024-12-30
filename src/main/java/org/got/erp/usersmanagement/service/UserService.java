package org.got.erp.usersmanagement.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.got.erp.exception.EmailAlreadyExistsException;
import org.got.erp.exception.ResourceNotFoundException;
import org.got.erp.exception.RoleNotFoundException;
import org.got.erp.exception.UsernameAlreadyExistsException;
import org.got.erp.usersmanagement.dto.UserDTO;
import org.got.erp.usersmanagement.entity.Role;
import org.got.erp.usersmanagement.entity.User;
import org.got.erp.usersmanagement.mapper.UserMapper;
import org.got.erp.usersmanagement.repository.RoleRepository;
import org.got.erp.usersmanagement.repository.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;
    private final ACLService aclService;
    private final UserMapper userMapper;

    @Transactional(readOnly = true)
    public Page<UserDTO.UserResponse> getAllUsers(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public UserDTO.UserResponse getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));
    }

    public UserDTO.UserResponse createUser(UserDTO.UserRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new UsernameAlreadyExistsException(request.username());
        }

        if (userRepository.existsByEmail(request.email())) {
            throw new EmailAlreadyExistsException(request.email());
        }

        User user = new User();
        user.setUsername(request.username());
        user.setEmail(request.email());
        user.setPassword(passwordEncoder.encode(request.password()));

        Set<Role> roles = request.roles().stream()
                .map(roleName -> roleRepository.findByName(roleName)
                        .orElseThrow(() -> new RoleNotFoundException(roleName)))
                .collect(Collectors.toSet());

        user.setRoles(roles);
        User savedUser = userRepository.save(user);

        // Create default ACL entries for the user
        aclService.createDefaultAclEntries(savedUser);

        return userMapper.toResponse(savedUser);
    }

    public UserDTO.UserResponse updateUser(Long id, UserDTO.UserUpdate request) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        request.email().ifPresent(email -> {
            if (!email.equals(user.getEmail()) && userRepository.existsByEmail(email)) {
                throw new EmailAlreadyExistsException(email);
            }
            user.setEmail(email);
        });

        request.password().ifPresent(password ->
                user.setPassword(passwordEncoder.encode(password)));

        return userMapper.toResponse(userRepository.save(user));
    }

    @Transactional
    public void deleteUser(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + id));

        // Supprimer d'abord les entrées ACL associées
        aclService.deleteAclEntriesForUser(user);
        userRepository.delete(user);
    }
}
