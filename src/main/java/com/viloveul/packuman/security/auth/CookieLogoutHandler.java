package com.viloveul.packuman.security.auth;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
public class CookieLogoutHandler implements LogoutHandler {

    @Autowired
    private Environment environment;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        String cookieName = environment.getRequiredProperty("viloveul.packuman.cookie.name", String.class);
        String cookiePath = environment.getProperty("viloveul.packuman.cookie.path", String.class, "/");
        String cookieDomain = environment.getProperty("viloveul.packuman.cookie.domain", String.class, request.getServerName());
        Boolean cookieSecure = environment.getProperty("viloveul.packuman.cookie.secure", Boolean.class, false);

        Cookie cookie = new Cookie(cookieName, null);
        cookie.setPath(cookiePath);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(0);
        cookie.setDomain(cookieDomain);
        cookie.setSecure(cookieSecure);

        response.addCookie(cookie);
    }
}

