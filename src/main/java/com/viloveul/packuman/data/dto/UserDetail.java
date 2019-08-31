package com.viloveul.packuman.data.dto;

import com.viloveul.packuman.data.entity.Privilege;
import com.viloveul.packuman.data.entity.Role;
import com.viloveul.packuman.data.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class UserDetail implements UserDetails {

    private User user;

    private List<Privilege> privileges = new ArrayList<Privilege>();

    private List<Role> roles = new ArrayList<Role>();

    private Collection<GrantedAuthority> authorities = new LinkedHashSet<GrantedAuthority>();

    /*
     * CLASS CONSTRUCTOR
     */

    public UserDetail(User user) {
        this.user = user;
        this.authorities.add(new SimpleGrantedAuthority("ROLE_USER"));
        this.parseGrantedAuthorities(user.getRoles());
    }

    public Details getDetails() {
        return new Details();
    }

    private void parseGrantedAuthorities(Set<Role> relations) {
        relations.forEach(role -> {
            if (role.getStatus().equals(1) && role.getDeleted().equals(Boolean.FALSE)) {
                if (!roles.contains(role)) {
                    roles.add(role);

                    if (role.getType().equals("role")) {
                        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + role.getName().toUpperCase());
                        if (!authorities.contains(authority)) {
                            authorities.add(authority);
                        }
                    }

                    if (role.getChilds().size() > 0) {
                        parseGrantedAuthorities(role.getChilds());
                    }

                    if (role.getPrivileges().size() > 0) {
                        role.getPrivileges().forEach(p -> {
                            if (p.getStatus().equals(1) && p.getDeleted().equals(Boolean.FALSE)) {
                                if (!privileges.contains(p)) {
                                    privileges.add(p);
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.getStatus().equals(1);
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.getStatus().equals(1);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getUsername());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        UserDetail other = (UserDetail) obj;
        return Objects.equals(getUsername(), other.getUsername());
    }

    /*
     * Inner class for authentication details
     * see CookieFilter line 70
     */

    public class Details {

        public User getUser() {
            return user;
        }

        public List<Role> getRoles() {
            return roles;
        }

        public List<Privilege> getPrivileges() {
            return privileges;
        }
    }
}
