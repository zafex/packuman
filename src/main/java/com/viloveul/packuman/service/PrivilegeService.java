package com.viloveul.packuman.service;

import com.viloveul.packuman.data.dto.PrivilegeForm;
import com.viloveul.packuman.data.entity.Privilege;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.io.Serializable;
import java.util.List;

@Service
public interface PrivilegeService {

    List<Privilege> getAll();

    /*
     * INDEX LIST
     */

    List<Privilege> getIndexList();

    List<Privilege> getIndexList(int size);

    List<Privilege> getIndexList(int size, int page);

    List<Privilege> getIndexList(int size, int page, Sort sort);

    List<Privilege> getIndexList(Serializable privilege, int size, int page, Sort sort);

    long count();

    long count(Serializable privilege);

    /*
     * BASIC CRUD OPERATION
     */

    Privilege update(Privilege privilege, PrivilegeForm form) throws ValidationException;

    Privilege create(PrivilegeForm form) throws ValidationException;

    Privilege delete(Privilege privilege);

    Privilege delete(String id);

    Privilege getDetail(String id);

    Privilege getDetailByName(String name);
}
