package com.haiemdavang.AnrealShop.security.jwt;

import com.haiemdavang.AnrealShop.exception.UnAuthException;
import com.haiemdavang.AnrealShop.security.userDetails.UserDetailSecu;
import com.haiemdavang.AnrealShop.security.userDetails.UserDetailSecuService;
import io.micrometer.common.lang.NonNullApi;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@NonNullApi
@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final JwtInit jwtInit;
    private final UserDetailSecuService userDetailSecuService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {
            String token = jwtInit.getTokenRefreshFromCookie(request);
            if(token == null)
                token = jwtInit.getTokenFromCookie(request);
            if((token != null) && jwtInit.validateToken(token)) {
                String email = jwtInit.getEmail(token);
                UserDetailSecu userDetails = (UserDetailSecu) userDetailSecuService.loadUserByUsername(email);
                var authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getGrantedAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                request.setAttribute("exceptionMessage", "INVALID_OR_EXPIRED_TOKEN");
            }
        }catch (UnAuthException e) {
            request.setAttribute("exceptionMessage", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }
}
