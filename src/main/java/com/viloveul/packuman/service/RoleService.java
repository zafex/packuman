package com.viloveul.packuman.service;

import com.viloveul.packuman.data.dto.RoleForm;
import com.viloveul.packuman.data.entity.Privilege;
import com.viloveul.packuman.data.entity.Role;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.io.Serializable;
import java.util.List;

@Service
public interface RoleService {

    List<Role> getAll();

    List<Role> getAll(String type);

    /*
     * INDEX LIST
     */

    List<Role> getIndexList();

    List<Role> getIndexList(int size);

    List<Role> getIndexList(int size, int page);

    List<Role> getIndexList(int size, int page, Sort sort);

    List<Role> getIndexList(Serializable role, int size, int page, Sort sort);

    long count();

    long count(Serializable role);

    /*
     * BASIC CRUD OPERATION
     */

    Role getDetail(String id);

    Role getDetailByName(String name);

    Role create(RoleForm form) throws ValidationException;

    Role update(Role role, RoleForm form) throws ValidationException;

    Role delete(Role role);

    Role delete(String id);

    /*
     * ROLE-NESTED
     */

    void attachChild(Role role, Role child);

    void detachChild(Role role, Role child);

    /*
     * ROLE-PRIVILEGE
     */

    void attachPrivilege(Role role, Privilege privilege);

    void detachPrivilege(Role role, Privilege privilege);
}
