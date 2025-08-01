package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.common.PasswordGenerator;
import com.edwin.rrhh_api.common.exception.EmailAlreadyExistsException;
import com.edwin.rrhh_api.config.security.FirebaseService;
import com.edwin.rrhh_api.modules.user.dto.*;
import com.edwin.rrhh_api.modules.user.email.EmailUpdatedData;
import com.edwin.rrhh_api.modules.user.email.UserCreatedData;
import com.edwin.rrhh_api.modules.user.email.UserEmail;
import com.edwin.rrhh_api.modules.user.exception.SetUserActiveException;
import com.edwin.rrhh_api.modules.user.exception.UserNotFoundException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final AuthUserMapper authUserMapper;
    private final FirebaseService firebaseService;
    private final UserEmail userEmail;

    @Override
    public List<AuthUserResponse> findAll() {
        List<AuthUser> users = authUserRepository.findByRoleIn(
                List.of(AuthUser.Role.ADMIN.toString(),
                        AuthUser.Role.RH.toString()));

        if (users.isEmpty())
            return List.of();

        return users.stream().map(authUserMapper::toResponse).toList();
    }

    @Override
    public AuthUserResponse findById(UUID id) {
        AuthUser user = authUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return authUserMapper.toResponse(user);
    }

    @Override
    public AuthUserDetailsResponse createUser(CreateUserRequest request) {
        if (authUserRepository.existsByEmail(request.email()))
            throw new EmailAlreadyExistsException("Email already exists", "db");

        if (firebaseService.userExistsByEmail(request.email()))
            throw new EmailAlreadyExistsException("Email already exists", "firebase");

        String password = PasswordGenerator.generateHexPassword(8);

        UserRecord userRecord = firebaseService.createUser(request.email(), request.fullName(), password);
        String firebaseUid = userRecord.getUid();

        AuthUser newUser = AuthUser.builder()
                .firebaseUid(firebaseUid)
                .email(request.email())
                .fullName(request.fullName())
                .role(AuthUser.Role.RH.toString())  // Todo usuario creado es RH
                .isActive(true)
                .build();

        AuthUser savedUser = authUserRepository.save(newUser);

        sendCreatedUserEmail(savedUser, password);

        return authUserMapper.toDetailsResponse(savedUser);

    }

    @Override
    public UpdateEmailResponse updateUserEmail(UUID id, UpdateUserEmailRequest request) {
        if (authUserRepository.existsByEmailIgnoreCaseAndIdNot(request.email(), id))
            throw new EmailAlreadyExistsException("Email already exists", "db");

        AuthUser userDB = authUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        UserRecord userFirebase = firebaseService.getUserById(userDB.getFirebaseUid());

        if (userFirebase.getEmail().equalsIgnoreCase(request.email()))
            return new UpdateEmailResponse("El correo indicado es el mismo, no se realiza ninguna actualización", false);

        firebaseService.updateUserEmail(userDB.getFirebaseUid(), request.email());
        userDB.setEmail(request.email());
        userDB = authUserRepository.save(userDB);

        sendConfirmationEmailUpdated(userDB);
        return new UpdateEmailResponse("Correo actualizado correctamente", true);
    }

    @Override
    public SetUserActiveResponse setUserActive(UUID id, SetUserActiveRequest request) {
        AuthUser userDB = authUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (userDB.isAdmin())
            throw new SetUserActiveException("Un ADMIN no puede ser desactivado/activado desde la API");

        firebaseService.setUserActive(userDB.getFirebaseUid(), request.isActive());

        if (!request.isActive())
            userDB.setDisabledAt(LocalDateTime.now());
        else
            userDB.setDisabledAt(null);

        userDB.setActive(request.isActive());
        userDB = authUserRepository.save(userDB);

        return new SetUserActiveResponse("Estado actualizado correctamente", request.isActive(), userDB.getDisabledAt());
    }

    /**
     * Envía un correo de verificación al usuario creado
     *
     * @param savedUser {@link AuthUser} creado.
     * @param password  contraseña del usuario generada
     */
    private void sendCreatedUserEmail(AuthUser savedUser, String password) {
        String confirmationUrl = firebaseService.createEmailVerificationLink(savedUser.getEmail());
        UserCreatedData userCreatedData = UserCreatedData.builder()
                .user(savedUser)
                .password(password)
                .confirmationUrl(confirmationUrl)
                .build();

        userEmail.sendCreatedUserEmail(userCreatedData);
    }

    private void sendConfirmationEmailUpdated(AuthUser user) {
        String confirmationUrl = firebaseService.createEmailVerificationLink(user.getEmail());
        EmailUpdatedData data = EmailUpdatedData.builder()
                .email(user.getEmail())
                .confirmationUrl(confirmationUrl)
                .fullName(user.getFullName())
                .build();
        userEmail.sendConfirmationEmailUpdated(data);
    }

}
