/* (C)2025 */
package com.o5d.mockify.service;

import com.o5d.mockify.enums.ERole;
import com.o5d.mockify.model.Role;
import com.o5d.mockify.model.User;
import com.o5d.mockify.repository.RoleRepository;
import com.o5d.mockify.repository.UserRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    public User saveUser(User user, String[] roleNames) {
        Set<Role> resolvedRoles =
                Arrays.stream(roleNames)
                        .map(name -> ERole.valueOf(name.toUpperCase()))
                        .map(
                                erole ->
                                        roleRepository
                                                .findByName(erole)
                                                .orElseThrow(
                                                        () ->
                                                                new RuntimeException(
                                                                        "Role not found: "
                                                                                + erole)))
                        .collect(Collectors.toSet());

        user.setRoles(resolvedRoles);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        return userRepository.save(user);
    }

    public Optional<User> deleteUser(Long id) {
        return userRepository
                .findById(id)
                .map(
                        user -> {
                            userRepository.delete(user);
                            return user;
                        });
    }
}
