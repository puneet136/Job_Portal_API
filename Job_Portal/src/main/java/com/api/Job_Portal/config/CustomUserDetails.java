package com.api.Job_Portal.config;

import com.api.Job_Portal.Entity.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;


public class CustomUserDetails implements UserDetails {

    private final User user;

    private static final Logger logger = LoggerFactory.getLogger(CustomUserDetails.class);

    public CustomUserDetails(User user) {
        if (user == null) {
            logger.error("Attempted to create CustomUserDetails with null user");
            throw new IllegalArgumentException("User cannot be null");
        }
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        String roleName = user.getRole() != null ? user.getRole().getName() : "ROLE_USER"; // Fallback to default role
        return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + roleName));
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getEmail();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // Implement custom logic if needed
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // Implement custom logic if needed
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // Implement custom logic if needed
    }

    @Override
    public boolean isEnabled() {
        return true; // Implement custom logic if needed (e.g., based on user status)
    }
}