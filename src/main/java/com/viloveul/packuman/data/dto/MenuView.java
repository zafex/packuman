package com.viloveul.packuman.data.dto;

import com.viloveul.packuman.data.entity.Menu;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MenuView implements Serializable {

    private Menu menu;

    private List<MenuView> childs = new ArrayList<MenuView>();

    private boolean enabled = true;

    private int level = 0;

    private int order = 1;

    private String prefix = "";

    /*
     * CONSTRUCTOR
     */

    public MenuView(Menu menu) {
        setMenu(menu);
        setEnabled(menu.getStatus().equals(1) && menu.getDeleted().equals(Boolean.TRUE));
    }

    /*
     * GETTER SETTER
     */

    public Long getId() {
        return getMenu().getId();
    }

    public String getLabel() {
        return getMenu().getLabel();
    }

    public String getDescription() {
        return getMenu().getDescription();
    }

    public String getUrl() {
        return getMenu().getUrl();
    }

    public String getIcon() {
        return getMenu().getIcon();
    }

    public List<MenuView> getChilds() {
        return childs;
    }

    public void setChilds(List<MenuView> childs) {
        this.childs = childs;
    }

    public Menu getMenu() {
        return menu;
    }

    public void setMenu(Menu menu) {
        this.menu = menu;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public int getOrder() {
        return order;
    }

    public void setOrder(int order) {
        this.order = order;
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getMenu().getLabel() + getMenu().getType() + getMenu().getUrl());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        MenuView other = (MenuView) obj;
        return Objects.equals(getMenu().getLabel() + getMenu().getType() + getMenu().getUrl(), other.getMenu().getLabel() + other.getMenu().getType() + other.getMenu().getUrl());
    }
}
