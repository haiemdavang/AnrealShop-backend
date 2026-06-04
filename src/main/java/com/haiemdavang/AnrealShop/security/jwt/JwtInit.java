package com.haiemdavang.AnrealShop.security.jwt;

import com.haiemdavang.AnrealShop.exception.UnAuthException;
import com.haiemdavang.AnrealShop.tech.redis.service.IRedisService;
import com.haiemdavang.AnrealShop.security.userDetails.UserDetailSecu;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import java.security.Key;
import java.util.Base64;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
@Slf4j
public class JwtInit {

    @Value("${jwt.token.secret_Key}")
    private String secret_key;
    @Value("${jwt.token.expiration}")
    private Long expiration;
    @Value("${jwt.token.expiration_refresh}")
    private Long expiration_refresh;
    @Value("${jwt.token.cookie_access_name}")
    private String token_cookie_name;
    @Value("${jwt.token.cookie_refresh_name}")
    private String token_refresh_cookie_name;

    private final IRedisService redisService;
    private final String PATH_COOKIE = "/api";
    private final String PATH_COOKIE_REFRESH = "/api/auth/";

    private Key getKey() {
        return Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret_key));
    }

    public ResponseCookie generaJwtCookie(Authentication auth) {
        String jwt = generateTokenFromAuthentication(auth);
        return generateCookie(token_cookie_name, jwt, PATH_COOKIE);
    }

    public ResponseCookie generaJwtCookie(String name) {
        String jwt = gennerateTokenFromUsername(name);
        return generateCookie(token_cookie_name, jwt, PATH_COOKIE);
    }

    public ResponseCookie generaJwtRefreshCookie(String name) {
        String jwt = gennerateTokenFromUsername(name, true);
        return generateCookie(token_refresh_cookie_name, jwt, PATH_COOKIE_REFRESH);
    }
    public ResponseCookie generaJwtRefreshCookie(Authentication auth) {
        String jwt = generateTokenFromAuthentication(auth, true);
        return generateCookie(token_refresh_cookie_name, jwt, PATH_COOKIE_REFRESH);
    }



    private String generateTokenFromAuthentication(Authentication auth, boolean isRefresh) {
        Date timeExp = new Date(new Date().getTime() + (isRefresh ? expiration_refresh : expiration));
        UserDetailSecu userDetails = (UserDetailSecu) auth.getPrincipal();
        return generateToken(userDetails.getEmail(), timeExp);
    }

    private String generateTokenFromAuthentication(Authentication auth) {
        return generateTokenFromAuthentication(auth, false);
    }

    private String gennerateTokenFromUsername(String username, boolean isRefresh) {
        Date timeExp = new Date(new Date().getTime() + (isRefresh ? expiration_refresh : expiration));
        return generateToken(username, timeExp);
    }

    private String gennerateTokenFromUsername(String username) {
        return gennerateTokenFromUsername(username, false);
    }

    private String generateToken(String username, Date timeExp) {
        return Jwts.builder()
                .setIssuedAt(new Date())
                .setSubject(username)
                .setExpiration(timeExp)
                .signWith(getKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    public String getEmail(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    public Date getExpirationTime(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getKey())
                .build()
                .parseClaimsJws(token)
                .getBody()
                .getExpiration();
    }

    public void deleteToken(String token) {
        try {
            if(validateToken(token)) {
                String email = this.getEmail(token);
                Date exp = this.getExpirationTime(token);
                long timeRedis = new Date().getTime() - exp.getTime();
                if(timeRedis >= 0)
                    redisService.addValue(token, email, timeRedis, TimeUnit.MILLISECONDS);
            }
        }catch (Exception e) {
            log.warn(e.getMessage());
        }
    }

    public boolean validateToken(String token) {
        try {
            Jwts.parserBuilder()
                    .setSigningKey(getKey())
                    .build()
                    .parseClaimsJws(token);
            return true;
        }catch (ExpiredJwtException e) {
            throw new UnAuthException("JWT_EXPIRED");
        }catch (IllegalArgumentException e) {
            throw new UnAuthException("JWT_EMPTY");
        }catch (UnsupportedJwtException e) {
            throw new UnAuthException("JWT_NOT_SUPPORT");
        }catch (MalformedJwtException e) {
            throw new UnAuthException("JWT_NOT_READ");
        }catch (SignatureException e) {
            throw new UnAuthException("JWT_NOT_SIGN");
        }catch (Exception e) {
            throw new UnAuthException("SERVER_ERROR");
        }
    }


    private ResponseCookie generateCookie(String name, String value, String path) {
        return ResponseCookie.from(name, value).path(path).maxAge(7*24*60*60).httpOnly(true).sameSite("none").secure(true).build();
    }

    private String getCookieValueByName(HttpServletRequest request, String name) {
        Cookie cookie = WebUtils.getCookie(request, name);
        if(cookie != null)
            return cookie.getValue();
        return null;
    }

    public String getTokenFromCookie(HttpServletRequest request) {
        return getCookieValueByName(request, token_cookie_name);
    }

    public String getTokenRefreshFromCookie(HttpServletRequest request) {
        return getCookieValueByName(request, token_refresh_cookie_name);
    }

    private ResponseCookie cleanCookie(String name, String path) {
        return ResponseCookie.from(name, "")
                .path(path)
                .maxAge(0)
                .httpOnly(true)
                .sameSite("none")
                .secure(true)
                .build();
    }

    public ResponseCookie getCleanJwtCookie() {
        return cleanCookie(token_cookie_name, PATH_COOKIE);
    }

    public ResponseCookie getCleanJwtRefreshCookie() {
        return cleanCookie(token_refresh_cookie_name, PATH_COOKIE_REFRESH);
    }
}
