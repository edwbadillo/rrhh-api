package com.edwin.rrhh_api.config.security;

/**
 * Interfaz para el servicio de consulta de un usuario para ser autenticado.
 */
public interface AuthUserDetailsService {
    AuthUserDetails loadUserByFirebaseUid(String firebaseUid);
}
