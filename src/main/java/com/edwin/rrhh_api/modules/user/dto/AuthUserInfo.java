package com.edwin.rrhh_api.modules.user.dto;

public record AuthUserInfo(
        String id,
        String email,
        String fullName,
        String role,
        Boolean isActive
) {
}
