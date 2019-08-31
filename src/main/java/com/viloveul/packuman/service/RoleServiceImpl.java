package com.viloveul.packuman.service;

import com.viloveul.packuman.data.dto.RoleForm;
import com.viloveul.packuman.data.entity.Privilege;
import com.viloveul.packuman.data.entity.Role;
import com.viloveul.packuman.data.repository.RoleRepository;
import com.viloveul.packuman.data.repository.PrivilegeRepository;
import com.viloveul.packuman.util.specification.SearchSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("roleService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class RoleServiceImpl implements RoleService {

    @Autowired
    private Validator validator;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    /*
     * GET ALL
     */

    @Override
    public List<Role> getAll() {
        return roleRepository.findAll();
    }

    @Override
    public List<Role> getAll(String type) {
        return roleRepository.findAllByType(type);
    }

    @Override
    public List<Role> getIndexList() {
        long c = roleRepository.count();
        return getIndexList((int) c);
    }

    @Override
    public List<Role> getIndexList(int size) {
        return getIndexList(size, 0);
    }

    @Override
    public List<Role> getIndexList(int size, int page) {
        return getIndexList(size, page, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public List<Role> getIndexList(int size, int page, Sort sort) {
        return roleRepository.findAll(PageRequest.of(page, size, sort)).getContent();
    }

    @Override
    public List<Role> getIndexList(Serializable role, int size, int page, Sort sort) {
        return roleRepository.findAll(new SearchSpecification<Role>(role), PageRequest.of(page, size, sort)).getContent();
    }

    @Override
    public long count() {
        return roleRepository.count();
    }

    @Override
    public long count(Serializable role) {
        return roleRepository.count(new SearchSpecification<Role>(role));
    }

    /*
     * BASIC CRUD OPERATION
     */

    @Override
    public Role getDetail(String id) {
        return roleRepository.findById(new Long(id)).get();
    }

    @Override
    public Role getDetailByName(String name) {
        return roleRepository.findByName(name).get();
    }

    @Override
    @Transactional
    @Secured({"ROLE_ADMINISTRATOR"})
    public Role create(RoleForm form) throws ValidationException {
        Set<ConstraintViolation<RoleForm>> errors = validator.validate(form);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }
        Role role = new Role(form.getName(), form.getType());

        role.setPrivileges(new HashSet<Privilege>(form.getPrivileges()));

        if (form.getType().equals("group")) {
            form.getChilds().forEach(child -> {
                attachChild(role, child);
            });
        }

        roleRepository.saveAndFlush(role);

        return role;
    }

    @Override
    @Transactional
    @Secured({"ROLE_ADMINISTRATOR"})
    @CacheEvict(value = {"packuman_user_details"}, allEntries = true)
    public Role update(Role role, RoleForm form) throws ValidationException {
        Set<ConstraintViolation<RoleForm>> errors = validator.validate(form);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }
        role.setName(form.getName());
        role.setDescription(form.getDescription());
        role.setStatus(form.getStatus());
        role.setChilds(new HashSet<Role>());
        role.setPrivileges(new HashSet<Privilege>(form.getPrivileges()));
        role.setUpdatedAt(new Date());
        role.setDeleted(false);

        if (form.getType().equals("group")) {
            form.getChilds().forEach(child -> {
                attachChild(role, child);
            });
        }

        roleRepository.saveAndFlush(role);

        return role;
    }

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    @CacheEvict(value = {"packuman_user_details", "packuman_user_menus"}, allEntries = true)
    public Role delete(Role role) {
        role.setDeleted(true);
        role.setDeletedAt(new Date());
        roleRepository.saveAndFlush(role);
        return role;
    }

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    @CacheEvict(value = {"packuman_user_details", "packuman_user_menus"}, allEntries = true)
    public Role delete(String id) {
        Role role = roleRepository.getOne(new Long(id));
        return delete(role);
    }

    /*
     * ROLE-NESTED
     */

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    public void attachChild(Role role, Role child) {
        role.getChilds().add(child);
        child.getParents().add(role);
    }

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    public void detachChild(Role role, Role child) {
        role.getChilds().remove(child);
        child.getParents().remove(role);
    }

    /*
     * ROLE-PRIVILEGE
     */

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    public void attachPrivilege(Role role, Privilege privilege) {
        role.getPrivileges().add(privilege);
        privilege.getRoles().add(role);
    }

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    public void detachPrivilege(Role role, Privilege privilege) {
        role.getPrivileges().remove(privilege);
        privilege.getRoles().remove(role);
    }
}
