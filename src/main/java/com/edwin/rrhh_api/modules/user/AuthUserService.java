package com.edwin.rrhh_api.modules.user;


import com.edwin.rrhh_api.modules.user.dto.AuthUserDetailsResponse;
import com.edwin.rrhh_api.modules.user.dto.AuthUserResponse;
import com.edwin.rrhh_api.modules.user.dto.CreateUserRequest;

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

    /**
     * Crea un nuevo usuario en firebase y posteriormente lo registra en la base de datos,
     * todos los usuarios registrados con este método son usuarios RH
     * para el registro de usuarios ADMIN se debe realizar manualmente en Firebase
     * y crear el registro en la base de datos de la aplicación.
     *
     * @param request {@link CreateUserRequest}
     * @return {@link AuthUserDetailsResponse}
     */
    AuthUserDetailsResponse createUser(CreateUserRequest request);
}
