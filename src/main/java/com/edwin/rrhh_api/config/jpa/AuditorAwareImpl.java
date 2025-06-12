package com.edwin.rrhh_api.config.jpa;

import com.edwin.rrhh_api.config.security.AuthUserDetails;
import com.edwin.rrhh_api.modules.user.AuthUser;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Optional;

/**
 * Define una implementación de AuditorAware para la auditoría de entidades basada
 * en el usuario autenticado.
 */
@Component("auditorAwareImpl")
public class AuditorAwareImpl implements AuditorAware<AuthUser> {

    @Override
    public Optional<AuthUser> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal();

        if (principal instanceof AuthUserDetails authUserDetails) {
            return Optional.of(authUserDetails.getUser());
        }

        return Optional.empty();
    }
}
