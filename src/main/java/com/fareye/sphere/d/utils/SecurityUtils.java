package com.fareye.sphere.d.utils;

import com.fareye.sphere.d.entities.enums.Role;
import org.springframework.stereotype.Component;

@Component
public class SecurityUtils {
    public Role getCurrentUserRole() { return Role.ADMIN; } 
    public String getCurrentUserId() { return "FEI-000001"; }
}