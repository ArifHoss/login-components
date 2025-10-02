package com.svt.arif.login_rest_api.service;

import com.svt.arif.login_rest_api.dto.UserRegistrationDto;
import com.svt.arif.login_rest_api.dto.UserResponseDto;
import com.svt.arif.login_rest_api.dto.UserUpdateDto;
import com.svt.arif.login_rest_api.entity.User;
import com.svt.arif.login_rest_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userRepository.findByUsernameOrEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
    }
    
    /**
     * Register a new user
     */
    public UserResponseDto registerUser(UserRegistrationDto registrationDto) {
        // Check if username already exists
        if (userRepository.existsByUsername(registrationDto.getUsername())) {
            throw new RuntimeException("Username already exists: " + registrationDto.getUsername());
        }
        
        // Check if email already exists
        if (userRepository.existsByEmail(registrationDto.getEmail())) {
            throw new RuntimeException("Email already exists: " + registrationDto.getEmail());
        }
        
        // Create new user
        User user = new User();
        user.setUsername(registrationDto.getUsername());
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFirstName(registrationDto.getFirstName());
        user.setLastName(registrationDto.getLastName());
        user.setRole(User.Role.USER);
        user.setIsEnabled(true);
        user.setIsAccountNonLocked(true);
        user.setIsAccountNonExpired(true);
        user.setIsCredentialsNonExpired(true);
        
        User savedUser = userRepository.save(user);
        return UserResponseDto.fromUser(savedUser);
    }
    
    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public UserResponseDto getUserById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        return UserResponseDto.fromUser(user);
    }
    
    /**
     * Get user by username
     */
    @Transactional(readOnly = true)
    public UserResponseDto getUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found with username: " + username));
        return UserResponseDto.fromUser(user);
    }
    
    /**
     * Get all users
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserResponseDto::fromUser)
                .collect(Collectors.toList());
    }
    
    /**
     * Update user
     */
    public UserResponseDto updateUser(Long id, UserUpdateDto updateDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        // Update fields if provided
        if (updateDto.getUsername() != null && !updateDto.getUsername().isEmpty()) {
            if (userRepository.existsByUsername(updateDto.getUsername()) && 
                !user.getUsername().equals(updateDto.getUsername())) {
                throw new RuntimeException("Username already exists: " + updateDto.getUsername());
            }
            user.setUsername(updateDto.getUsername());
        }
        
        if (updateDto.getEmail() != null && !updateDto.getEmail().isEmpty()) {
            if (userRepository.existsByEmail(updateDto.getEmail()) && 
                !user.getEmail().equals(updateDto.getEmail())) {
                throw new RuntimeException("Email already exists: " + updateDto.getEmail());
            }
            user.setEmail(updateDto.getEmail());
        }
        
        if (updateDto.getFirstName() != null) {
            user.setFirstName(updateDto.getFirstName());
        }
        
        if (updateDto.getLastName() != null) {
            user.setLastName(updateDto.getLastName());
        }
        
        if (updateDto.getIsEnabled() != null) {
            user.setIsEnabled(updateDto.getIsEnabled());
        }
        
        User updatedUser = userRepository.save(user);
        return UserResponseDto.fromUser(updatedUser);
    }
    
    /**
     * Delete user
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User not found with id: " + id);
        }
        userRepository.deleteById(id);
    }
    
    /**
     * Enable/Disable user
     */
    public UserResponseDto toggleUserStatus(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("User not found with id: " + id));
        
        user.setIsEnabled(!user.getIsEnabled());
        User updatedUser = userRepository.save(user);
        return UserResponseDto.fromUser(updatedUser);
    }
    
    /**
     * Update last login time
     */
    public void updateLastLogin(String username) {
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            user.setLastLogin(LocalDateTime.now());
            userRepository.save(user);
        }
    }
    
    /**
     * Check if username exists
     */
    @Transactional(readOnly = true)
    public boolean usernameExists(String username) {
        return userRepository.existsByUsername(username);
    }
    
    /**
     * Check if email exists
     */
    @Transactional(readOnly = true)
    public boolean emailExists(String email) {
        return userRepository.existsByEmail(email);
    }
    
    /**
     * Get users by role
     */
    @Transactional(readOnly = true)
    public List<UserResponseDto> getUsersByRole(User.Role role) {
        return userRepository.findByRole(role).stream()
                .map(UserResponseDto::fromUser)
                .collect(Collectors.toList());
    }
    
    /**
     * Get enabled users count
     */
    @Transactional(readOnly = true)
    public long getEnabledUsersCount() {
        return userRepository.countByIsEnabledTrue();
    }
}
