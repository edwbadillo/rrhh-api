package com.edwin.rrhh_api.modules.user;


import com.edwin.rrhh_api.modules.user.dto.AuthUserResponse;

import java.util.List;
import java.util.UUID;

public interface AuthUserService {

    /**
     * Trae todos los usuarios administradores del sistema (ADMIN y RH)
     *
     * @return Una lista de objetos {@link AuthUserResponse}
     */
    List<AuthUserResponse> findAll();

    /**
     * Busca un usuario por su id
     *
     * @param id UUID del usuario a buscar
     * @return {@link AuthUserResponse}
     */
    AuthUserResponse findById(UUID id);
}
