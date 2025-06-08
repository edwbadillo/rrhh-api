package com.edwin.rrhh_api.config.security;

import com.edwin.rrhh_api.modules.user.AuthUser;
import com.google.firebase.auth.FirebaseToken;
import jakarta.servlet.FilterChain;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AuthFilterTest {
    @InjectMocks
    private AuthFilter authFilter;

    @Mock
    private FirebaseTokenVerifier firebaseTokenVerifier;

    @Mock
    private AuthUserDetailsService authUserDetailsService;

    @Mock
    private FilterChain filterChain;

    private MockHttpServletRequest request;
    private MockHttpServletResponse response;

    @BeforeEach
    void setUp() {
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
    }

    @Test
    void doFilter_validToken_setsAuthentication() throws Exception {
        String token = "Bearer valid-token";
        request.addHeader("Authorization", token);

        FirebaseToken firebaseToken = mock(FirebaseToken.class);
        when(firebaseToken.getUid()).thenReturn("uid123");

        when(firebaseTokenVerifier.verify(anyString())).thenReturn(firebaseToken);

        AuthUser user = AuthUser.builder()
                .id(UUID.randomUUID())
                .firebaseUid("uid123")
                .email("admin@example.com")
                .fullName("John Doe")
                .role("ADMIN")
                .isActive(true)
                .build();

        AuthUserDetails authUserDetails = new AuthUserDetails(user);
        when(authUserDetailsService.loadUserByFirebaseUid("uid123")).thenReturn(authUserDetails);

        authFilter.doFilterInternal(request, response, filterChain);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(authentication);
        assertEquals(user.getEmail(), ((AuthUserDetails)authentication.getPrincipal()).getUsername());
    }
}
