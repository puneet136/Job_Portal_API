package com.api.Job_Portal.Controller;

import com.api.Job_Portal.Entity.Role;
import com.api.Job_Portal.Entity.User;
import com.api.Job_Portal.Repository.RoleRepository;
import com.api.Job_Portal.Repository.UserRepository;
import com.api.Job_Portal.config.JwtUtil;
import com.api.Job_Portal.dto.LoginDTO;
import com.api.Job_Portal.dto.RegisterDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserRepository userRepo;
    @Autowired
    private RoleRepository roleRepo;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private AuthenticationManager authenticationManager;

    private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> register(
            @Valid @RequestBody @NotNull RegisterDTO dto) {
        try {
            logger.info("Registering user with email: {}, role: {}", dto.getEmail(), dto.getRole());
            if (userRepo.findByEmail(dto.getEmail()).isPresent()) {
                logger.warn("Email already exists: {}", dto.getEmail());
                return ResponseEntity.status(HttpStatus.CONFLICT)
                        .body(new AuthResponse("Email already exists", null));
            }

            logger.info("Looking up role: {}", dto.getRole());
            Role role = roleRepo.findByName(dto.getRole())
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST, "Role not found: " + dto.getRole()));
            logger.info("Found role with ID: {}", role.getId());

            User user = new User();
            user.setUsername(dto.getUsername());
            user.setEmail(dto.getEmail());
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
            user.setRole(role);
            logger.info("Preparing to save user with role ID: {}", role.getId());

            userRepo.save(user);
            logger.info("User saved successfully with ID: {}", user.getId());

            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(new AuthResponse("User registered successfully", null));
        } catch (Exception e) {
            logger.error("Registration failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse("Registration failed: " + e.getMessage(), null));
        }
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(
            @Valid @RequestBody @NotNull LoginDTO dto) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
            );

            User user = userRepo.findByEmail(dto.getEmail())
                    .orElseThrow(() -> new UserNotFoundException("User not found"));

            // Use authentication.getPrincipal() for consistent UserDetails
            UserDetails userDetails = (UserDetails) authentication.getPrincipal();
            String token = jwtUtil.generateToken(userDetails);

            return ResponseEntity.ok(new AuthResponse("Login successful", token));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new AuthResponse("Invalid credentials", null));
        } catch (Exception e) {
            logger.error("Login failed: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new AuthResponse("Login failed: " + e.getMessage(), null));
        }
    }

    // Unified response class
    public static class AuthResponse {
        private String message;
        private String token;

        public AuthResponse(String message, String token) {
            this.message = message;
            this.token = token;
        }

        public String getMessage() { return message; }
        public void setMessage(String message) { this.message = message; }
        public String getToken() { return token; }
        public void setToken(String token) { this.token = token; }
    }

    // Custom exceptions
    public static class RoleNotFoundException extends RuntimeException {
        public RoleNotFoundException(String message) { super(message); }
    }

    public static class UserNotFoundException extends RuntimeException {
        public UserNotFoundException(String message) { super(message); }
    }
}