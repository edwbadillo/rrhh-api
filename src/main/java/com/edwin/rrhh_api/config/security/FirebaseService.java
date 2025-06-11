package com.edwin.rrhh_api.config.security;

import com.edwin.rrhh_api.common.exception.EmailAlreadyExistsException;
import com.edwin.rrhh_api.modules.user.exception.UserNotFoundException;
import com.google.firebase.auth.AuthErrorCode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Define funciones de utilidad relacionadas con la instancia {@link FirebaseAuth}.
 */
@Service
@RequiredArgsConstructor
public class FirebaseService {

    private final FirebaseAuth firebaseAuth;

    /**
     * Verifica la existencia de un usuario en Firebase por su email.
     *
     * @param email email del usuario a verificar
     * @return true si el usuario existe, false en caso contrario
     */
    public boolean userExistsByEmail(String email) {
        try {
            firebaseAuth.getUserByEmail(email);
            return true;
        } catch (FirebaseAuthException e) {
            if (e.getAuthErrorCode() == AuthErrorCode.USER_NOT_FOUND)
                return false;

            throw new RuntimeException(e);
        }
    }

    /**
     * Crea un nuevo usuario en Firebase.
     *
     * @param email    email del usuario a crear
     * @param password contraseña del usuario a crear
     * @return el usuario creado
     */
    public UserRecord createUser(String email, String name, String password) {
        UserRecord.CreateRequest request = new UserRecord.CreateRequest()
                .setEmail(email)
                .setPassword(password)
                .setDisplayName(name);
        try {
            return firebaseAuth.createUser(request);
        } catch (FirebaseAuthException e) {
            if (e.getAuthErrorCode() == AuthErrorCode.EMAIL_ALREADY_EXISTS)
                throw new EmailAlreadyExistsException("Email already exists", "firebase");

            throw new RuntimeException("Error creando usuario", e);
        }
    }

    /**
     * Genera un enlace de verificación de correo electrónica para un usuario en Firebase.
     *
     * @param email email del usuario
     * @return el enlace de verificación
     */
    public String createEmailVerificationLink(String email) {
        try {
            return firebaseAuth.generateEmailVerificationLink(email);
        } catch (FirebaseAuthException e) {
            throw new RuntimeException("Error generando enlace de verificación", e);
        }
    }

    /**
     * Obtiene un usuario por su id.
     *
     * @param id id del usuario
     * @return el usuario
     */
    public UserRecord getUserById(String id) {
        try {
            return firebaseAuth.getUser(id);
        } catch (FirebaseAuthException e) {
            if (e.getAuthErrorCode() == AuthErrorCode.USER_NOT_FOUND) {
                throw new UserNotFoundException("User not found");
            }
            throw new RuntimeException("Error obteniendo usuario", e);
        }
    }

    /**
     * Actualiza el correo electrónico de un usuario en Firebase.
     *
     * @param firebaseUid ID del usuario en Firebase
     * @param newEmail    nuevo correo electrónico
     * @return el usuario actualizado
     */
    public UserRecord updateUserEmail(String firebaseUid, String newEmail) {
        try {
            UserRecord.UpdateRequest request = new UserRecord.UpdateRequest(firebaseUid)
                    .setEmail(newEmail);

            return firebaseAuth.updateUser(request);

        } catch (FirebaseAuthException e) {
            if (e.getAuthErrorCode() == AuthErrorCode.USER_NOT_FOUND) {
                throw new UserNotFoundException("User not found");
            }
            if (e.getAuthErrorCode() == AuthErrorCode.EMAIL_ALREADY_EXISTS) {
                throw new EmailAlreadyExistsException("Email already exists", "firebase");
            }
            throw new RuntimeException("Error actualizando correo electrónico", e);
        }
    }


}
