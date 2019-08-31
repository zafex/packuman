package com.viloveul.packuman.security.auth;

import com.viloveul.packuman.data.dto.UserDetail;
import com.viloveul.packuman.security.crypto.CryptoManager;
import com.viloveul.packuman.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.AccountStatusUserDetailsChecker;
import org.springframework.security.authentication.CredentialsExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsChecker;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;

@Component
public class CookieFilter extends GenericFilterBean {

    private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();

    @Autowired
    private UserService userService;

    @Autowired
    private CryptoManager cryptoManager;

    @Autowired
    private Environment environment;

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        HttpServletResponse httpServletResponse = (HttpServletResponse) servletResponse;

        String cookieName = environment.getRequiredProperty("viloveul.packuman.cookie.name", String.class);

        Cookie[] cookies = httpServletRequest.getCookies();
        if (cookies != null) {
            Cookie cookie = Arrays.stream(cookies).filter(c -> c.getName().equals(cookieName)).findFirst().orElse(null);
            if (cookie != null) {
                String decryptedCookieValue = cryptoManager.decrypt(cookie.getValue());
                if (decryptedCookieValue != null) {
                    int colonPos = decryptedCookieValue.indexOf(':');
                    String username = decryptedCookieValue.substring(0, colonPos);
                    int expiresAtEpochSeconds = Integer.valueOf(decryptedCookieValue.substring(colonPos + 1));

                    if (Instant.now().getEpochSecond() < expiresAtEpochSeconds) {
                        try {
                            UserDetails userDetails = userService.loadUserByUsername(username);
                            userDetailsChecker.check(userDetails);

                            UserDetail userDetailDto = (UserDetail) userDetails;

                            AbstractAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(username, userDetails.getPassword(), userDetails.getAuthorities());

                            authentication.setDetails(userDetailDto.getDetails());

                            SecurityContextHolder.getContext().setAuthentication(authentication);

                        } catch (LockedException | DisabledException | AccountExpiredException | CredentialsExpiredException e) {
                            // do nothing
                            userService.clearCachedUserDetails(username);
                        }
                    } else {
                        userService.clearCachedUserDetails(username);
                    }
                }
            } else {
                if (httpServletRequest.isRequestedSessionIdValid()) {
                    httpServletRequest.changeSessionId();
                }
            }
        }

        filterChain.doFilter(httpServletRequest, httpServletResponse);
    }
}

