package com.edwin.rrhh_api.config.security;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import org.springframework.stereotype.Component;

/**
 * Define funciones para verificar tokens de autenticación con Firebase
 */
@Component
public class FirebaseTokenVerifier {

    public FirebaseToken verify(String idToken) throws Exception {
        return FirebaseAuth.getInstance().verifyIdToken(idToken);
    }
}
