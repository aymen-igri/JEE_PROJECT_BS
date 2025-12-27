package com.backend.backend.security;

import com.backend.backend.entity.User.User;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

public class CustomUserDetails implements UserDetails {
    private final User user;
    private final String userType;

    public CustomUserDetails(User user) {
        this.user = user;
        this.userType = extractUserType(user);
    }

    private String extractUserType(User user) {
        jakarta.persistence.DiscriminatorValue annotation =
                user.getClass().getAnnotation(jakarta.persistence.DiscriminatorValue.class);

        if (annotation != null) {
            return annotation.value();
        }
        return user.getClass().getSimpleName().toUpperCase();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority("ROLE_" + userType));
    }

    @Override
    public String getPassword() {

        return user.getPassword();
    }

    @Override
    public String getUsername() {
        // Return email as username
        return user.getEmail(); // Make sure your User entity has this method
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    public User getUser() {
        return user;
    }
}