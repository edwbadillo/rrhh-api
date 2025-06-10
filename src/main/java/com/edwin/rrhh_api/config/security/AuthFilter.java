package com.edwin.rrhh_api.config.security;

import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * Filtro para la autenticación de usuarios. Verifica el token de autenticación
 * con Firebase y carga el usuario correspondiente en el contexto de seguridad
 */
@Component
@RequiredArgsConstructor
public class AuthFilter extends OncePerRequestFilter {

    private final FirebaseTokenVerifier firebaseTokenVerifier;
    private final AuthUserDetailsService authUserDetailsService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authorizationHeader = request.getHeader("Authorization");
        if (Objects.nonNull(authorizationHeader) && authorizationHeader.startsWith("Bearer ")) {
            String idToken = authorizationHeader.substring("Bearer ".length());
            try {
                FirebaseToken firebaseToken = firebaseTokenVerifier.verify(idToken);
                String firebaseUid = firebaseToken.getUid();
                // TODO: Validar que el correo esté verificado en firebase
                AuthUserDetails user = authUserDetailsService.loadUserByFirebaseUid(firebaseUid);
                if (Objects.nonNull(user)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    user, null, user.getAuthorities()
                            );
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception e) {
                SecurityContextHolder.clearContext();
                throw new ServletException(e);
            }
        }
        filterChain.doFilter(request, response);
    }
}
