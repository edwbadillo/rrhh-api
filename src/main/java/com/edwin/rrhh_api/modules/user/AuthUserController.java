package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.modules.user.dto.AuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
    public List<AuthUserInfo> findAll() {
        return authUserService.findAll();
    }
}
