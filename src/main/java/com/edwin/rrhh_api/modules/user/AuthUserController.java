package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.modules.user.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
public class AuthUserController {

    private final AuthUserService authUserService;

    /**
     * Trae todos los usuarios administradores del sistema (ADMIN y RH)
     *
     * @return List<AuthUserInfo>
     */
    @GetMapping
    public List<AuthUserResponse> findAll() {
        return authUserService.findAll();
    }

    /**
     * Busca un usuario por su id
     *
     * @param id UUID del usuario a buscar
     * @return {@link AuthUserResponse}
     */
    @GetMapping("/{id}")
    public AuthUserResponse findById(@PathVariable UUID id) {
        return authUserService.findById(id);
    }

    /**
     * Crea un nuevo usuario en firebase y posteriormente lo registra en la base de datos.
     *
     * @param request {@link CreateUserRequest}
     * @return {@link AuthUserDetailsResponse}
     */
    @PostMapping
    public AuthUserDetailsResponse createUser(@Valid @RequestBody CreateUserRequest request) {
        return authUserService.createUser(request);
    }

    /**
     * Actualiza el email de un usuario, el usuario debe verificar nuevamente su correo electrónico
     *
     * @param id      UUID del usuario a actualizar
     * @param request Nuevo email del usuario
     * @return {@link AuthUserDetailsResponse}
     */
    @PutMapping("/{id}")
    public UpdateEmailResponse updateUserEmail(@PathVariable UUID id, @Valid @RequestBody UpdateUserEmailRequest request) {
        return authUserService.updateUserEmail(id, request);
    }

    /**
     * Actualiza el estado de un usuario
     *
     * @param id      UUID del usuario a actualizar
     * @param request Nuevo email del usuario
     * @return {@link AuthUserDetailsResponse}
     */
    @PutMapping("/set-active-status/{id}")
    public SetUserActiveResponse setUserActive(@PathVariable UUID id, @Valid @RequestBody SetUserActiveRequest request) {
        return authUserService.setUserActive(id, request);
    }
}
