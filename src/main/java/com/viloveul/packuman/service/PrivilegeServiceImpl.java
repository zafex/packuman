package com.viloveul.packuman.service;

import com.viloveul.packuman.data.dto.PrivilegeForm;
import com.viloveul.packuman.data.entity.Privilege;
import com.viloveul.packuman.data.repository.PrivilegeRepository;
import com.viloveul.packuman.util.specification.SearchSpecification;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Set;

@Service("privilegeService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class PrivilegeServiceImpl implements PrivilegeService {

    @Autowired
    private Validator validator;

    @Autowired
    private PrivilegeRepository privilegeRepository;

    @Override
    @Cacheable(value = "packuman_privileges")
    public List<Privilege> getAll() {
        return privilegeRepository.findAll();
    }

    @Override
    public List<Privilege> getIndexList() {
        long c = privilegeRepository.count();
        return getIndexList((int) c);
    }

    @Override
    public List<Privilege> getIndexList(int size) {
        return getIndexList(size, 0);
    }

    @Override
    public List<Privilege> getIndexList(int size, int page) {
        return getIndexList(size, page, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public List<Privilege> getIndexList(int size, int page, Sort sort) {
        return privilegeRepository.findAll(PageRequest.of(page, size, sort)).getContent();
    }

    @Override
    public List<Privilege> getIndexList(Serializable privilege, int size, int page, Sort sort) {
        return privilegeRepository.findAll(new SearchSpecification<Privilege>(privilege), PageRequest.of(page, size, sort)).getContent();
    }

    @Override
    public long count() {
        return privilegeRepository.count();
    }

    @Override
    public long count(Serializable privilege) {
        return privilegeRepository.count(new SearchSpecification<Privilege>(privilege));
    }

    /*
     * BASIC CRUD OPERATION
     */

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    @CacheEvict(value = "packuman_privileges", allEntries = true)
    public Privilege create(PrivilegeForm form) throws ValidationException {
        Set<ConstraintViolation<PrivilegeForm>> errors = validator.validate(form);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }
        Privilege privilege = new Privilege(form.getName());
        privilege.setDescription(form.getDescription());
        privilegeRepository.saveAndFlush(privilege);

        return privilege;
    }

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    @CacheEvict(value = {"packuman_privileges", "packuman_user_details", "packuman_user_menus"}, allEntries = true)
    public Privilege update(Privilege privilege, PrivilegeForm form) throws ValidationException {
        Set<ConstraintViolation<PrivilegeForm>> errors = validator.validate(form);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }
        privilege.setName(form.getName());
        privilege.setStatus(form.getStatus());
        privilege.setDescription(form.getDescription());
        privilege.setUpdatedAt(new Date());
        privilege.setDeleted(false);
        privilegeRepository.saveAndFlush(privilege);
        return privilege;
    }

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    @CacheEvict(value = {"packuman_privileges", "packuman_user_details", "packuman_user_menus"}, allEntries = true)
    public Privilege delete(Privilege privilege) {
        privilege.setDeleted(true);
        privilege.setDeletedAt(new Date());
        privilegeRepository.saveAndFlush(privilege);
        return privilege;
    }

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    @CacheEvict(value = {"packuman_privileges", "packuman_user_details", "packuman_user_menus"}, allEntries = true)
    public Privilege delete(String id) {
        Privilege privilege = privilegeRepository.getOne(new Long(id));
        return delete(privilege);
    }

    @Override
    public Privilege getDetail(String id) {
        return privilegeRepository.getOne(new Long(id));
    }

    @Override
    public Privilege getDetailByName(String name) {
        return privilegeRepository.findByName(name).get();
    }
}
