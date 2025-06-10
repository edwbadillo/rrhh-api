package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.common.PasswordGenerator;
import com.edwin.rrhh_api.common.exception.EmailAlreadyExistsException;
import com.edwin.rrhh_api.config.security.FirebaseService;
import com.edwin.rrhh_api.modules.user.dto.AuthUserDetailsResponse;
import com.edwin.rrhh_api.modules.user.dto.AuthUserMapper;
import com.edwin.rrhh_api.modules.user.dto.AuthUserResponse;
import com.edwin.rrhh_api.modules.user.dto.CreateUserRequest;
import com.edwin.rrhh_api.modules.user.email.UserCreatedData;
import com.edwin.rrhh_api.modules.user.email.UserEmail;
import com.edwin.rrhh_api.modules.user.exception.UserNotFoundException;
import com.google.firebase.auth.UserRecord;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

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
                .createdAt(OffsetDateTime.now())
                .updatedAt(OffsetDateTime.now())
                .build();

        AuthUser savedUser = authUserRepository.save(newUser);

        sendCreatedUserEmail(savedUser, password);

        return authUserMapper.toDetailsResponse(savedUser);

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

}
