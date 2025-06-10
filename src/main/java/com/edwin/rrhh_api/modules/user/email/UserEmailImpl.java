package com.edwin.rrhh_api.modules.user.email;

import com.edwin.rrhh_api.common.email.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * Implementa el servicio para el envío de emails relacionados con los usuarios.
 */
@Service
@RequiredArgsConstructor
public class UserEmailImpl implements UserEmail {

    private final EmailService emailService;

    @Override
    public void sendCreatedUserEmail(UserCreatedData userCreatedData) {
        String toEmail = userCreatedData.user().getEmail();
        String subject = "Bienvenido a RRHH-API";
        String body = "Hola " + userCreatedData.user().getFullName() + ",\n\n" +
                "Gracias por registrarte en RRHH-API. Tu cuenta ha sido creada con el siguiente correo electrónico: " + toEmail + ".\n\n" +
                "Tu contraseña es: " + userCreatedData.password() + ".\n\n" +
                "Debes confirmar tu correo electrónico, por favor haz clic en el siguiente enlace: " + userCreatedData.confirmationUrl() + ".\n\n" +
                "Si no has solicitado crear una cuenta, por favor ignora este este mensaje.\n\n" +
                "Gracias,\n" +
                "RRHH-API Team";
        emailService.sendSimpleEmail(toEmail, subject, body);
    }
}
