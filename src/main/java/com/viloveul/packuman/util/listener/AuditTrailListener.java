package com.viloveul.packuman.util.listener;

import com.viloveul.packuman.AppContextAware;
import com.viloveul.packuman.data.entity.User;
import com.viloveul.packuman.service.AuditService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.persistence.PostPersist;
import javax.persistence.PostRemove;
import javax.persistence.PostUpdate;
import javax.servlet.http.HttpServletRequest;

public class AuditTrailListener {

    private static final Logger log = LoggerFactory.getLogger(AuditTrailListener.class);

    @PostPersist
    public void onPostPersist(Object o) {
        audit(o, "INSERT");
    }

    @PostUpdate
    public void onPostUpdate(Object o) {
        audit(o, "UPDATE");
    }

    @PostRemove
    public void onPostRemove(Object o) {
        audit(o, "DELETE");
    }

    private void audit(Object o, String type) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = attributes.getRequest();
        AuditService auditService = AppContextAware.getBean(AuditService.class);

        String username = "";
        String browser = "";
        String ip = "";

        try {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            } else {
                username = principal.toString();
            }

            if (request != null) {
                ip = request.getHeader("X-FORWARDED-FOR");
                if (ip == null || "".equals(ip)) {
                    ip = request.getRemoteAddr();
                }
                browser = request.getHeader("User-Agent");

            }
        } catch (Exception e) {
            log.warn(e.getMessage());
            System.out.println(e.getMessage());
        }

        if (username.equals("") && o.getClass().equals(User.class) && type.equals("UPDATE")) {
            System.out.println("Skip AuditTrail, Username is empty");
        } else {
            auditService.log(o, type, username, browser, ip);
        }
    }
}
