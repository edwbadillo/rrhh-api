package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.modules.user.dto.AuthUserInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserRepository authUserRepository;

    @Override
    public List<AuthUserInfo> findAll() {
        List<AuthUser> users = authUserRepository.findByRoleIn(List.of(AuthUser.Role.ADMIN.toString(), AuthUser.Role.RH.toString()));
        if (users.isEmpty())
            return List.of();
        return users.stream().map(user -> new AuthUserInfo(user.getId().toString(), user.getEmail(), user.getFullName(), user.getRole(), user.isActive())).toList();
    }
}
