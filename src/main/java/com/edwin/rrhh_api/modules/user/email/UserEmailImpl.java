package com.edwin.rrhh_api.modules.user.email;

import com.edwin.rrhh_api.common.email.EmailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Implementa el servicio para el envío de emails relacionados con los usuarios.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class UserEmailImpl implements UserEmail {

    private final EmailService emailService;

    @Override
    public void sendCreatedUserEmail(UserCreatedData userCreatedData) {
        String toEmail = userCreatedData.user().getEmail();
        String subject = "Bienvenido a RRHH-API";
        String body = "Hola " + userCreatedData.user().getFullName() + ",\n\n" +
                "Gracias por registrarte en RRHH-API. Tu cuenta ha sido creada con el siguiente correo electrónico: " + toEmail + ".\n\n" +
                "Tu contraseña es: " + userCreatedData.password() + "\n\n" +
                "Debes confirmar tu correo electrónico, por favor haz clic en el siguiente enlace: " + userCreatedData.confirmationUrl() + ".\n\n" +
                "Si no has solicitado crear una cuenta, por favor ignora este este mensaje.\n\n" +
                "Gracias,\n" +
                "RRHH-API Team";
        log.info("Enviando correo de bienvenida");
        emailService.sendSimpleEmail(toEmail, subject, body);
    }

    @Override
    public void sendConfirmationEmailUpdated(EmailUpdatedData data) {
        String toEmail = data.email();
        String subject = "Correo actualizado en RRHH-API";
        String body = "Hola " + data.fullName() + ",\n\n" +
                "Se ha actualizado tu correo electrónico en RRHH-API.\n\n" +
                "Tu correo actualizado es: " + toEmail + ".\n\n" +
                "Debes confirmar tu correo electrónico, por favor, haz clic en el siguiente enlace: " + data.confirmationUrl() + ".\n\n" +
                "Gracias,\n" +
                "RRHH-API Team";
        log.info("Enviando correo de actualización de correo");
        emailService.sendSimpleEmail(toEmail, subject, body);
    }
}
