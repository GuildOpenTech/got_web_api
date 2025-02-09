package org.got.web.gotweb.security.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component("userSecurity")
@RequiredArgsConstructor
public class UserSecurity {

    public boolean isCurrentUser(Long userId) {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }

        var principal = authentication.getPrincipal();
        if (principal instanceof org.springframework.security.core.userdetails.User user) {
            return user.getUsername().equals(userId.toString());
        }
        return false;
    }
}
