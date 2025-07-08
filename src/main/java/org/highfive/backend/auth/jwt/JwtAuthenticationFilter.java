package org.highfive.backend.auth.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.auth.exception.AuthErrorCode;
import org.highfive.backend.auth.service.TokenService;
import org.highfive.backend.global.exception.BusinessException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final TokenService tokenService;

    @Override
    protected void doFilterInternal(final HttpServletRequest request, final HttpServletResponse response, final FilterChain filterChain) throws ServletException, IOException {

        try {
            final String token = tokenService.resolveToken(request);

            if(token == null) {
                throw new BusinessException(AuthErrorCode.TOKEN_ERROR);
            }

            if(tokenService.isBlackListToken(token)) {
                throw new BusinessException(AuthErrorCode.TOKEN_ERROR);
            }

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

    private static void errorResponse(final HttpServletResponse response) throws IOException {
        final AuthErrorCode authErrorCode = AuthErrorCode.TOKEN_ERROR;
        response.setStatus(authErrorCode.getHttpStatus().value());
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(authErrorCode.getMessage());
    }
}
