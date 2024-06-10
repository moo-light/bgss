package com.server.blockchainserver.utils;

import com.server.blockchainserver.advices.Constants;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class AuthenticationHelper {
    private static final String ROLE_GUEST = "ROLE_ANONYMOUS";

    public static final Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    public static boolean hasRoles(Collection<? extends GrantedAuthority> authorities,
                                   String[]... roles) {
        // Create a set to store unique role names
        Set<String> roleSet = new HashSet<>();
        for (String[] roleArray : roles) {
            for (String role : roleArray) {
                roleSet.add(role);
            }
        }

        // Iterate over authorities and check if any authority matches the roles
        for (GrantedAuthority authority : authorities) {
            if (roleSet.contains(authority.getAuthority())) {
                return true;
            }
        }
        return false;
    }

    public static boolean hasRoles(Authentication authentication, String... roles) {
        return hasRoles(authentication.getAuthorities(), roles);
    }

    // get principal as Map
    public static Map<String, Object> getPrincipal(Authentication authentication)
            throws IllegalAccessException {
        Object principal = authentication.getPrincipal();
        if (principal instanceof String)
            throw new IllegalAccessException("This method need @PreAuthorize");
        return ObjectHelper.convertObjectToHashMap(principal);
    }

    // getcurrent user id in authentication
    public static boolean isSameUserId(Authentication authentication,Long userId) throws IllegalAccessException {
        if (authentication == null) throw new IllegalArgumentException("No authentication found");
        Object authId = getPrincipal(authentication).get("id");
        return userId.equals(authId);
    }

    public static boolean isCustomer(Authentication authentication) {
        return hasRoles(authentication, Constants.ROLE_CUSTOMER);
    }

    public static boolean isAdmin(Authentication authentication) {
        return hasRoles(authentication, Constants.ROLE_ADMIN);
    }

    public static boolean isStaff(Authentication authentication) {
        return hasRoles(authentication, Constants.ROLE_STAFF);
    }
    public static boolean isAdminOrStaff(Authentication authentication) {
        return hasRoles(authentication, Constants.ROLE_ADMIN,Constants.ROLE_STAFF);
    }

    public static boolean isGuestOrCustomer(Authentication authentication) {
        return hasRoles(authentication, ROLE_GUEST, Constants.ROLE_CUSTOMER);
    }
}
