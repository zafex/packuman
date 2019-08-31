package com.viloveul.packuman.data.dto;

import com.viloveul.packuman.data.entity.Role;
import com.viloveul.packuman.util.validator.FieldMatch;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@FieldMatch(field = "password", match = "passconf", message = "The password fields must match")
public class UserForm implements Serializable {

    @NotEmpty
    private String username;

    @NotEmpty
    private String name;

    @NotEmpty
    private String fullname;

    @NotEmpty
    @Email
    private String email;

    private String password;

    private String passconf;

    private Integer status = 1;

    private List<Role> roles = new ArrayList<Role>();

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassconf() {
        return passconf;
    }

    public void setPassconf(String passconf) {
        this.passconf = passconf;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Role> getRoles() {
        return roles;
    }

    public void setRoles(List<Role> roles) {
        this.roles = roles;
    }
}
