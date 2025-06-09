package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.modules.user.dto.AuthUserResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
