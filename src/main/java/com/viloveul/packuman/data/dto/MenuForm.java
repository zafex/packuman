package com.viloveul.packuman.data.dto;

import com.viloveul.packuman.data.entity.Menu;
import com.viloveul.packuman.data.entity.Role;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

public class MenuForm implements Serializable {

    private Long parentId;

    @NotEmpty
    private String label;

    private String description;

    private String type = "default";

    private String icon;

    @NotEmpty
    private String url;

    private Integer status = 1;

    private Boolean deleted = false;

    private int order;

    private Role role;

    private Menu parent;

    public MenuForm() {
        // do nothing
    }

    public MenuForm(String label, String url) {
        setLabel(label);
        setUrl(url);
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Menu getParent() {
        return parent;
    }

    public MenuView getParentView() {
        if (parent != null) {
            return new MenuView(parent);
        }
        return null;
    }

    public void setParent(Menu parent) {
        this.parent = parent;
        this.setParentId(parent.getId());
    }

    public void setParentView(MenuView parent) {
        this.parent = parent.getMenu();
    }

    public void clearRole() {
        this.role = null;
    }

    public void clearParent() {
        this.parent = null;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }
}

