package com.viloveul.packuman.service;

import com.viloveul.packuman.data.dto.MenuForm;
import com.viloveul.packuman.data.dto.MenuView;
import com.viloveul.packuman.data.dto.UserDetail;
import com.viloveul.packuman.data.entity.Menu;
import com.viloveul.packuman.data.entity.User;
import com.viloveul.packuman.data.repository.MenuRepository;
import com.viloveul.packuman.data.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service("menuService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class MenuServiceImpl implements MenuService {

    @Autowired
    private Validator validator;

    @Autowired
    private MenuRepository menuRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private CacheManager cacheManager;

    /*
     * MISC
     */

    @Override
    public List<String> getTypes() {
        return menuRepository.getTypes();
    }

    @Override
    public List<MenuView> getActiveMenus() {
        return getActiveMenus("main-menu");
    }

    @Override
    public List<MenuView> getActiveMenus(String type) {
        List<Menu> menus = menuRepository.findAllByTypeAndIsActive(type, true, Sort.by(Sort.Direction.ASC, "parentId").and(Sort.by(Sort.Direction.ASC, "order")));
        List<MenuView> results = new ArrayList<MenuView>();
        menus.forEach(m -> {
            if (m.getDeleted().equals(Boolean.FALSE)) {
                results.add(new MenuView(m));
            }
        });
        return results;
    }

    @Override
    public List<MenuView> getMenus() {
        return getMenus("main-menu");
    }

    @Override
    public List<MenuView> getMenus(String type) {
        List<Menu> menus = menuRepository.findAllByType(type, Sort.by(Sort.Direction.ASC, "parentId").and(Sort.by(Sort.Direction.ASC, "order")));
        List<MenuView> results = new ArrayList<MenuView>();
        menus.forEach(m -> {
            if (m.getDeleted().equals(Boolean.FALSE)) {
                results.add(new MenuView(m));
            }
        });
        return results;
    }

    @Override
    public Map<String, List<MenuView>> mapNested(List<MenuView> tmps) {
        Map<String, List<MenuView>> menus = new HashMap<String, List<MenuView>>();
        for (MenuView menu: tmps) {
            String parent = "0";
            if (menu.getMenu().getParent() != null) {
                parent = String.valueOf(menu.getMenu().getParent().getId());
            }
            List<MenuView> tmp = menus.getOrDefault(parent, new ArrayList<MenuView>());
            tmp.add(menu);
            menus.put(parent, tmp);
        }
        return menus;
    }

    @Override
    public List<MenuView> makeNested(List<MenuView> tmps) {
        Map<String, List<MenuView>> menus = mapNested(tmps);
        return parseNestedRecursive(menus, "0", 0);
    }

    private List<MenuView> parseNestedRecursive(Map<String, List<MenuView>> menus, String k, int level) {
        String prefix = "- ";
        List<MenuView> tmps = new ArrayList<MenuView>();
        for (MenuView m : menus.getOrDefault(k, new ArrayList<MenuView>())) {
            m.setLevel(level);
            m.setPrefix(new String(new char[level]).replace("\0", prefix));
            if (menus.containsKey(m.getId().toString())) {
                m.setChilds(
                        parseNestedRecursive(
                                menus,
                                m.getId().toString(),
                                level + 1
                        )
                );
            }
            tmps.add(m);
        }
        return tmps;
    }

    @Override
    public List<MenuView> getParentLists(String type, Long id) {
        List<MenuView> menus = getMenus(type).stream().filter(x -> x.getId() != id).collect(Collectors.toList());
        return makeNested(menus);
    }

    @Override
    public List<MenuView> normalizeRecursivedLists(List<MenuView> tmps) {
        List<MenuView> menus = new ArrayList<MenuView>();
        tmps.forEach(x -> {
            menus.add(x);
            if (x.getChilds().size() > 0) {
                List<MenuView> childs = normalizeRecursivedLists(x.getChilds());
                childs.forEach(y -> {
                    menus.add(y);
                });
            }
        });
        return menus;
    }

    /*
     * USER
     */

    @Override
    public List<MenuView> loadUserMenu(String id) {
        return loadUserMenu(id, "main-menu");
    }

    @Override
    public List<MenuView> loadUserMenu(String id, String type) {
        Long userID = new Long(id);
        User user = userRepository.findById(userID).get();
        return loadUserMenu(user, type);
    }

    @Override
    public List<MenuView> loadUserMenu(User user) {
        return loadUserMenu(user, "main-menu");
    }

    @Override
    @Cacheable(value = "packuman_user_menus")
    public List<MenuView> loadUserMenu(User user, String type) {
        List<MenuView> menus = getActiveMenus(type);
        UserDetail detail = new UserDetail(user);
        GrantedAuthority administrator = new SimpleGrantedAuthority("ROLE_ADMINISTRATOR");
        menus.forEach(m -> {
            if (m.getMenu().getRole() != null && m.isEnabled()) {
                if (detail.getDetails().getRoles().contains(m.getMenu().getRole()) || detail.getAuthorities().contains(administrator)) {
                    m.setEnabled(true);
                } else {
                    m.setEnabled(false);
                }
            }
        });
        return menus;
    }

    /*
     * BASIC CRUD OPERATION
     */

    @Override
    public Menu getDetail(String id) {
        return menuRepository.getOne(new Long(id));
    }

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    @CacheEvict(value = "packuman_user_menus", allEntries = true)
    public Menu create(MenuForm form) throws ValidationException {
        Set<ConstraintViolation<MenuForm>> errors = validator.validate(form);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }

        Integer size;
        if (form.getParent() == null) {
            size = menuRepository.maxByParentIdIsNull();
        } else {
            size = menuRepository.maxByParentId(form.getParent().getId());
        }

        Menu menu = new Menu(form.getLabel(), form.getUrl());
        menu.setType(form.getType());
        menu.setOrder(form.getOrder());
        menu.setParent(form.getParent());
        menu.setRole(form.getRole());
        menu.setStatus(form.getStatus());
        menu.setDeleted(false);
        menu.setIcon(form.getIcon());
        menu.setOrder(size == null ? 0 : (size + 1));

        menuRepository.save(menu);
        return menu;
    }

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    @CacheEvict(value = "packuman_user_menus", allEntries = true)
    public Menu update(Menu menu, MenuForm form) throws ValidationException {
        Set<ConstraintViolation<MenuForm>> errors = validator.validate(form);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }

        Integer size;
        if (form.getParent() == null) {
            size = menuRepository.maxByParentIdIsNull();
        } else {
            size = menuRepository.maxByParentId(form.getParent().getId());
        }

        menu.setLabel(form.getLabel());
        menu.setStatus(form.getStatus());
        menu.setDeleted(false);
        menu.setDescription(form.getDescription());
        menu.setType(form.getType());
        menu.setUrl(form.getUrl());
        menu.setRole(form.getRole());
        menu.setParent(form.getParent());
        menu.setIcon(form.getIcon());
        menu.setOrder(size == null ? 0 : (size + 1));
        menu.setUpdatedAt(new Date());

        menuRepository.saveAndFlush(menu);
        return menu;
    }

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    @CacheEvict(value = "packuman_user_menus", allEntries = true)
    public Menu delete(Menu menu) {
        menu.setDeleted(true);
        menu.setDeletedAt(new Date());
        menuRepository.saveAndFlush(menu);
        return menu;
    }

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    @CacheEvict(value = "packuman_user_menus", allEntries = true)
    public Menu delete(String id) {
        Menu menu = menuRepository.getOne(new Long(id));
        return delete(menu);
    }

    /*
     * clear cached user menus
     */

    @Override
    public void clearCachedUserMenus() {
        cacheManager.getCache("packuman_user_menus").clear();
    }
}
