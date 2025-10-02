package com.svt.arif.login_rest_api.repository;

import com.svt.arif.login_rest_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    /**
     * Find user by username
     */
    Optional<User> findByUsername(String username);
    
    /**
     * Find user by email
     */
    Optional<User> findByEmail(String email);
    
    /**
     * Check if username exists
     */
    boolean existsByUsername(String username);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Find user by username or email
     */
    @Query("SELECT u FROM User u WHERE u.username = :identifier OR u.email = :identifier")
    Optional<User> findByUsernameOrEmail(@Param("identifier") String identifier);
    
    /**
     * Find enabled users
     */
    @Query("SELECT u FROM User u WHERE u.isEnabled = true")
    java.util.List<User> findEnabledUsers();
    
    /**
     * Find users by role
     */
    java.util.List<User> findByRole(User.Role role);
    
    /**
     * Find users by first name containing (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.firstName) LIKE LOWER(CONCAT('%', :firstName, '%'))")
    java.util.List<User> findByFirstNameContainingIgnoreCase(@Param("firstName") String firstName);
    
    /**
     * Find users by last name containing (case insensitive)
     */
    @Query("SELECT u FROM User u WHERE LOWER(u.lastName) LIKE LOWER(CONCAT('%', :lastName, '%'))")
    java.util.List<User> findByLastNameContainingIgnoreCase(@Param("lastName") String lastName);
    
    /**
     * Count users by role
     */
    long countByRole(User.Role role);
    
    /**
     * Count enabled users
     */
    long countByIsEnabledTrue();
}
