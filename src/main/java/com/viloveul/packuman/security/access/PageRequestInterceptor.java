package com.viloveul.packuman.security.access;

import com.viloveul.packuman.data.dto.UserDetail;
import com.viloveul.packuman.data.entity.Privilege;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.resource.ResourceHttpRequestHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Component
public class PageRequestInterceptor implements HandlerInterceptor {

    @Autowired
    private Environment environment;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception  {
        if (!(handler instanceof ResourceHttpRequestHandler)) {
            List<String> alloweds = Arrays.asList("/", "/login", "/logout", "/error", "/home", "/profile");
            String servletPath = request.getServletPath();
            if (servletPath != null && !servletPath.equals("") && !alloweds.contains(servletPath)) {
                Authentication authentication = (Authentication) request.getUserPrincipal();
                if (authentication != null && authentication.isAuthenticated()) {
                    if (request.isUserInRole("ROLE_ADMINISTRATOR")) {
                        return true;
                    } else {
                        UserDetail.Details details = (UserDetail.Details) authentication.getDetails();
                        List<Privilege> privileges = details.getPrivileges();
                        String kcontext = environment.getProperty("viloveul.packuman.context", String.class, "app");
                        String context = String.format("%s:%s", kcontext, servletPath);
                        Privilege privilege = privileges.stream().filter(x -> x.getName().equals(context)).findFirst().orElse(null);
                        if (privilege != null) {
                            return true;
                        }
                    }
                }
                response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied");
                return false;
            }
        }
        return true;
    }
}
