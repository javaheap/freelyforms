package com.utbm.da50.freelyform.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.mongodb.lang.NonNull;
import com.utbm.da50.freelyform.dto.user.UserSimpleResponse;
import com.utbm.da50.freelyform.enums.UserRole;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Document
@NoArgsConstructor
@AllArgsConstructor
@Data
public class User implements UserDetails {

    @Id
    String id;

    private String firstName;

    private String lastName;

    @Indexed(unique = true)
    @NonNull
    private String email;

    @JsonIgnore
    @NonNull
    private String password;

    private HashSet<UserRole> role;


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        if (role == null || role.isEmpty()) {
            throw new IllegalStateException("User has no roles assigned");
        }
        return List.of(new SimpleGrantedAuthority(role.toString()));
    }


    @Override
    public String getUsername() {
        return email;
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


    // For DTO conversion
    public UserSimpleResponse toUserSimpleResponse() {
        return new UserSimpleResponse(
                id,
                firstName,
                lastName,
                email,
                role
        );
    }

}
