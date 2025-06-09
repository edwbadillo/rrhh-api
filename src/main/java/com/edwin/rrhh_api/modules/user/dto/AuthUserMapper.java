package com.edwin.rrhh_api.modules.user.dto;

import com.edwin.rrhh_api.modules.user.AuthUser;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface AuthUserMapper {
    AuthUserResponse toResponse(AuthUser entity);
}
