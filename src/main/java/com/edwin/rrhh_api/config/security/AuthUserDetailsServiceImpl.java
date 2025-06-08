package com.edwin.rrhh_api.config.security;

import com.edwin.rrhh_api.modules.user.AuthUser;
import com.edwin.rrhh_api.modules.user.AuthUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Implementa el servicio de consulta de un usuario para ser autenticado, consulta en la base de datos el usuario
 * correspondiente al ID del usuario Firebase.
 */
@Service
@RequiredArgsConstructor
public class AuthUserDetailsServiceImpl implements AuthUserDetailsService {

    private final AuthUserRepository repository;

    @Override
    public AuthUserDetails loadUserByFirebaseUid(String firebaseUid) {
        AuthUser user = repository.findByFirebaseUid(firebaseUid).orElseThrow(() -> new UsernameNotFoundException("User not found"));
        if (!user.isActive()) throw new UsernameNotFoundException("User not active");
        return new AuthUserDetails(user);
    }
}
