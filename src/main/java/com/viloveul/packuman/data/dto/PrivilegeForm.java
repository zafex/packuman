package com.viloveul.packuman.data.dto;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

public class PrivilegeForm implements Serializable {

    @NotEmpty
    private String name;

    private String description;

    private Integer status = 1;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }
}
