package com.viloveul.packuman.data.dto;

import com.viloveul.packuman.data.entity.Privilege;
import com.viloveul.packuman.data.entity.Role;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class RoleForm implements Serializable {

    @NotEmpty
    private String name;

    @NotEmpty
    private String type = "role";

    private Integer status = 1;

    private String description;

    private List<Role> childs = new ArrayList<Role>();

    private List<Privilege> privileges = new ArrayList<Privilege>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(List<Privilege> privileges) {
        this.privileges = privileges;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public List<Role> getChilds() {
        return childs;
    }

    public void setChilds(List<Role> childs) {
        this.childs = childs;
    }
}
