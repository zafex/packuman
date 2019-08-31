package com.viloveul.packuman.service;

import com.viloveul.packuman.data.dto.MenuForm;
import com.viloveul.packuman.data.dto.MenuView;
import com.viloveul.packuman.data.entity.Menu;
import com.viloveul.packuman.data.entity.User;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.util.List;
import java.util.Map;

@Service
public interface MenuService {

    /*
     * USER
     */

    List<MenuView> loadUserMenu(String id);

    List<MenuView> loadUserMenu(User user);

    List<MenuView> loadUserMenu(String id, String type);

    List<MenuView> loadUserMenu(User user, String type);

    /*
     * MISC
     */

    List<MenuView> getActiveMenus(String type);

    List<MenuView> getActiveMenus();

    List<MenuView> getMenus(String type);

    List<MenuView> getMenus();

    Map<String, List<MenuView>> mapNested(List<MenuView> tmps);

    List<MenuView> makeNested(List<MenuView> tmps);

    List<String> getTypes();

    List<MenuView> getParentLists(String type, Long id);

    List<MenuView> normalizeRecursivedLists(List<MenuView> tmps);

    /*
     * BASIC CRUD OPERATION
     */

    Menu create(MenuForm form) throws ValidationException;

    Menu update(Menu menu, MenuForm form) throws ValidationException;

    Menu delete(Menu menu);

    Menu delete(String id);

    Menu getDetail(String id);

    /*
     * clear cached user menus
     */

    void clearCachedUserMenus();
}
