package com.edwin.rrhh_api.modules.user;


import com.edwin.rrhh_api.modules.user.dto.*;

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
     * para el registro de usuarios ADMIN se debe realizar los ajustes de rol en
     * la base de datos.
     *
     * @param request {@link CreateUserRequest}
     * @return {@link AuthUserDetailsResponse}
     */
    AuthUserDetailsResponse createUser(CreateUserRequest request);

    /**
     * Actualiza el email de un usuario, el usuario debe verificar nuevamente su correo electrónico
     *
     * @param id      UUID del usuario a actualizar
     * @param request Nuevo email del usuario
     * @return {@link UpdateEmailResponse}
     */
    UpdateEmailResponse updateUserEmail(UUID id, UpdateUserEmailRequest request);

    /**
     * Actualiza el estado de un usuario
     *
     * @param id      UUID del usuario a actualizar
     * @param request Nuevo email del usuario
     * @return {@link SetUserActiveResponse}
     */
    SetUserActiveResponse setUserActive(UUID id, SetUserActiveRequest request);
}
