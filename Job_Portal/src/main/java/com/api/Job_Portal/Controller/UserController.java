package com.api.Job_Portal.Controller;

import com.api.Job_Portal.Entity.User;
import com.api.Job_Portal.Service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
public class UserController {

    @Autowired
    private UserService userService;

    // Get all users (admin only) - Adjusted to /api/admin/users
    @GetMapping("/api/admin/users")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<User>> getAllUsers(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "10") int size) {
        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(new PageImpl<>(userService.listAllUsers(), pageable, userService.listAllUsers().size()));
    }

    // Get a user by ID (admin only) - Adjusted to /api/admin/users/{id}
    @GetMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    // Update user by ID (admin only) - Adjusted to /api/admin/users/{id}
    @PutMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<User> updateUser(@PathVariable Long id, @RequestBody User updatedUser) {
        User user = userService.getUserById(id); // Fetch existing user to update
        if (updatedUser.getRole() != null) {
            user.setRole(updatedUser.getRole());
        }
        user.setEmail(updatedUser.getEmail());
        user.setUsername(updatedUser.getUsername());
        if (updatedUser.getPassword() != null && !updatedUser.getPassword().isEmpty()) {
            user.setPassword(updatedUser.getPassword()); // Will be encrypted in service
        }
        return ResponseEntity.ok(userService.updateUser(id, user));
    }

    // Delete user by ID (admin only) - Adjusted to /api/admin/users/{id}
    @DeleteMapping("/api/admin/users/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // Get current authenticated user's details (any authenticated user)
    @GetMapping("/api/users/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> getCurrentUser(Principal principal) {
        return ResponseEntity.ok(userService.getCurrentUser(principal.getName()));
    }

    // Update current authenticated user's details (any authenticated user)
    @PutMapping("/api/users/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<User> updateCurrentUser(@RequestBody User user, Principal principal) {
        User currentUser = userService.getCurrentUser(principal.getName());
        currentUser.setEmail(user.getEmail());
        currentUser.setUsername(user.getUsername());
        currentUser.setPassword(user.getPassword()); // Password will be encrypted in service
        return ResponseEntity.ok(userService.updateUser(currentUser.getId(), currentUser));
    }

    // Existing methods (preserved logic)
    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN') or #id == principal?.id")
    public User getUserByIdOriginal(@PathVariable Long id, Principal principal) {
        return userService.getUserById(id);
    }

    @PutMapping("/{id}")
    @PreAuthorize("#id == principal?.id")
    public User updateUserOriginal(@PathVariable Long id, @RequestBody User user) {
        return userService.updateUser(id, user);
    }
}