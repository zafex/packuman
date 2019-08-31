package com.viloveul.packuman.security.auth;

import com.viloveul.packuman.data.dto.UserDetail;
import com.viloveul.packuman.security.crypto.CryptoManager;
import com.viloveul.packuman.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.DefaultRedirectStrategy;
import org.springframework.security.web.RedirectStrategy;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
public class CookieLoginHandler implements AuthenticationSuccessHandler {

    private static final DateTimeFormatter COOKIE_DATE_FORMATTER = DateTimeFormatter.ofPattern("EEE, dd-MMM-yyyy HH:mm:ss z");

    @Autowired
    private Environment environment;

    @Autowired
    private UserService userService;

    @Autowired
    private CryptoManager cryptoManager;

    private RedirectStrategy redirectStrategy = new DefaultRedirectStrategy();

    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        UserDetail userDetails = (UserDetail) authentication.getPrincipal();

        userService.clearCachedUserDetails(userDetails.getUsername());

        String cookieName = environment.getRequiredProperty("viloveul.packuman.cookie.name", String.class);
        Long cookieMaxAge = environment.getProperty("viloveul.packuman.cookie.age", Long.class);
        String cookiePath = environment.getProperty("viloveul.packuman.cookie.path", String.class, "/");
        String cookieDomain = environment.getProperty("viloveul.packuman.cookie.domain", String.class, request.getServerName());
        Boolean cookieSecure = environment.getProperty("viloveul.packuman.cookie.secure", Boolean.class, false);

        String cookieValue = userDetails.getUsername() + ":";

        Duration duration;

        if (cookieMaxAge != null) {
            duration = Duration.ofHours(cookieMaxAge);
            cookieValue += Instant.now().plus(duration).getEpochSecond();
        } else {
            // default max age of 4h
            duration = Duration.ofHours(4);
            cookieValue += Instant.now().plus(duration).getEpochSecond();
        }

        Cookie cookie = new Cookie(cookieName, cryptoManager.encrypt(cookieValue));

        int maxAgeInSeconds = (int) duration.getSeconds();

        if (maxAgeInSeconds > -1) {
            cookie.setMaxAge(maxAgeInSeconds);
        }

        cookie.setPath(cookiePath);
        cookie.setHttpOnly(true);
        cookie.setDomain(cookieDomain);
        cookie.setSecure(cookieSecure);

        response.addCookie(cookie);
        response.sendRedirect(
                ServletUriComponentsBuilder.fromCurrentContextPath().path("/").build().toUriString()
        );
    }
}

