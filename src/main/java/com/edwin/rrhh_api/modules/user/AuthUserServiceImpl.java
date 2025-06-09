package com.edwin.rrhh_api.modules.user;

import com.edwin.rrhh_api.modules.user.dto.AuthUserMapper;
import com.edwin.rrhh_api.modules.user.dto.AuthUserResponse;
import com.edwin.rrhh_api.modules.user.exception.UserNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthUserServiceImpl implements AuthUserService {

    private final AuthUserRepository authUserRepository;
    private final AuthUserMapper authUserMapper;

    @Override
    public List<AuthUserResponse> findAll() {
        List<AuthUser> users = authUserRepository.findByRoleIn(
                List.of(AuthUser.Role.ADMIN.toString(),
                        AuthUser.Role.RH.toString()));

        if (users.isEmpty())
            return List.of();

        return users.stream().map(authUserMapper::toResponse).toList();
    }

    @Override
    public AuthUserResponse findById(UUID id) {
        AuthUser user = authUserRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        return authUserMapper.toResponse(user);
    }
}
