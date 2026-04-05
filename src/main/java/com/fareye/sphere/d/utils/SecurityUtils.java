package com.fareye.sphere.d.utils;

import com.fareye.sphere.d.entities.enums.Department;
import com.fareye.sphere.d.entities.enums.Role;
import com.fareye.sphere.d.security.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {

    public Role getCurrentUserRole() {
        return currentPrincipal().getRole();
    }

    public String getCurrentUserId() {
        return currentPrincipal().getFormattedUserId();
    }

    public Department getCurrentUserDepartment() {
        Department department = currentPrincipal().getDepartment();
        if (department == null) {
            throw new IllegalStateException("Authenticated user has no department assigned");
        }
        return department;
    }

    public Long getCurrentUserIdLong() {
        return currentPrincipal().getUserId();
    }

    private UserPrincipal currentPrincipal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user");
        }
        Object principal = authentication.getPrincipal();
        if (principal instanceof UserPrincipal userPrincipal) {
            return userPrincipal;
        }
        throw new IllegalStateException("Unexpected principal type: " + principal.getClass());
    }
}
