package org.highfive.backend.auth.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.auth.exception.AuthErrorCode;
import org.highfive.backend.auth.service.TokenService;
import org.highfive.backend.global.dto.Response;
import org.highfive.backend.global.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final String AUTH_LOGIN = "/auth/login";
    private final String SWAGGER = "/swagger-ui";
    private final String V3 = "/v3";
    private final String USER_INFO = "/user/info";

    private final ObjectMapper objectMapper;
    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {

        final String requestURI = request.getRequestURI();
        if (requestURI.startsWith(AUTH_LOGIN) || requestURI.startsWith(SWAGGER) || requestURI.startsWith(V3) || requestURI.equals(USER_INFO)) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            final String token = tokenService.resolveToken(request);
            tokenService.validateToken(token);
            Authentication authentication = tokenService.getAuthentication(token);
            SecurityContextHolder.getContext().setAuthentication(authentication);

        } catch (JwtException | IllegalArgumentException | BusinessException e) {
            log.error("JWT 인증 실패 : {}", e.getMessage(), e);
            errorResponse(response);
            return;
        }

        filterChain.doFilter(request, response);
    }

    private void errorResponse(final HttpServletResponse response) throws IOException {
        final AuthErrorCode authErrorCode = AuthErrorCode.TOKEN_ERROR;
        response.setStatus(authErrorCode.getHttpStatus().value());

        Response<Object> errorResponse = new Response<>(
                authErrorCode.getCode(),
                null,
                authErrorCode.getMessage()
        );

        String json = objectMapper.writeValueAsString(errorResponse);
        response.getWriter().write(json);
    }
}
