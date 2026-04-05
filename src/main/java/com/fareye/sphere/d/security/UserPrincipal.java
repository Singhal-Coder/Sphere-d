package com.fareye.sphere.d.security;

import com.fareye.sphere.d.entities.User;
import com.fareye.sphere.d.entities.enums.Department;
import com.fareye.sphere.d.entities.enums.Role;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Getter
public class UserPrincipal implements UserDetails {

    private final Long userId;
    private final String formattedUserId;
    private final String email;
    private final String password;
    private final Role role;
    private final Department department;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(
            Long userId,
            String formattedUserId,
            String email,
            String password,
            Role role,
            Department department) {
        this.userId = userId;
        this.formattedUserId = formattedUserId;
        this.email = email;
        this.password = password;
        this.role = role;
        this.department = department;
        this.authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
    }

    public static UserPrincipal from(User user, String formattedUserId) {
        return new UserPrincipal(
                user.getUserId(),
                formattedUserId,
                user.getEmail(),
                user.getPassword(),
                user.getRole(),
                user.getDepartment());
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
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
}
