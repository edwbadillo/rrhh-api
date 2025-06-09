package com.edwin.rrhh_api.modules.user;


import com.edwin.rrhh_api.modules.user.dto.AuthUserResponse;

import java.util.List;

public interface AuthUserService {

    /**
     * Trae todos los usuarios administradores del sistema (ADMIN y RH)
     *
     * @return List<AuthUserInfo>
     */
    List<AuthUserResponse> findAll();
}
