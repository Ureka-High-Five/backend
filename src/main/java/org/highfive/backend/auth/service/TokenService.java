package org.highfive.backend.auth.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.highfive.backend.auth.repository.redis.TokenRedisRepository;
import org.highfive.backend.global.exception.BusinessException;
import org.highfive.backend.user.entity.User;
import org.highfive.backend.user.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import static org.highfive.backend.auth.exception.AuthErrorCode.TOKEN_ERROR;
import static org.highfive.backend.user.exception.UserErrorCode.USER_NOT_FOUND_ERROR;

@Slf4j
@Component
@RequiredArgsConstructor
public class TokenService {

    private final String ROLES = "roles";
    private final String AUTHORIZATION = "Authorization";
    private final String BEARER = "Bearer ";
    private final int BEARER_START_INDEX = 7;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.accessToken-expiration}")
    private Long accessTokenExpiration;

    @Value("${jwt.refreshToken-expiration}")
    private Long refreshTokenExpiration;

    private final UserRepository userRepository;
    private final TokenRedisRepository tokenRedisRepository;

    private Key key;

    @PostConstruct
    public void initKey() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateAccessToken(final String kakaoUserId, final List<String> roles) {
        return createToken(kakaoUserId, roles, accessTokenExpiration);
    }

    public String generateRefreshToken(final String kakaoUserId, final List<String> roles) {

        final String refreshToken = createToken(kakaoUserId, roles, refreshTokenExpiration);
        tokenRedisRepository.save(kakaoUserId, refreshToken);
        return refreshToken;
    }

    private String createToken(final String kakaoUserId, final List<String> roles, final long expireTime) {

        return Jwts.builder()
                .setSubject(kakaoUserId)
                .claim(ROLES, roles)
                .setIssuedAt(Date.from(Instant.now()))
                .setExpiration(new Date(System.currentTimeMillis() + expireTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public boolean isBlackListToken(final String token) {
        return tokenRedisRepository.isBlackListToken(token);
    }

    public boolean validateToken(final String token) {

        Jwts.parserBuilder().setSigningKey(key).build().parseClaimsJws(token);
        return true;
    }

    public Authentication getAuthentication(final String token) {

        final Claims claims = parseClaims(token);
        final String kakaoUserId = claims.getSubject();
        final List<String> roles = claims.get(ROLES, List.class);

        List<GrantedAuthority> authorities = new ArrayList<>();

        if(roles != null) {
            authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        }

        final User user = userRepository.findByKakaoUserId(kakaoUserId).orElseThrow(() -> new BusinessException(USER_NOT_FOUND_ERROR));
        return new UsernamePasswordAuthenticationToken(user, null, authorities);
    }

    public String resolveToken(final HttpServletRequest request) {

        final String bearer = request.getHeader(AUTHORIZATION);

        if(bearer != null && bearer.startsWith(BEARER)) {
            return bearer.substring(BEARER_START_INDEX);
        }

        return null;
    }

    public long getRemainingTime(final String token) {

        Claims claims = parseClaims(token);
        Date expiration = claims.getExpiration();
        long now = System.currentTimeMillis();

        return expiration.getTime() - now;
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (JwtException | IllegalArgumentException e) {
            log.error("JWT 인증 실패 : {}", e.getMessage(), e);
            throw new BusinessException(TOKEN_ERROR);
        }
    }
}
