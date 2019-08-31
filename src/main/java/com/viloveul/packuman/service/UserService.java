package com.viloveul.packuman.service;

import com.viloveul.packuman.data.dto.UserForm;
import com.viloveul.packuman.data.entity.Role;
import com.viloveul.packuman.data.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import javax.validation.ValidationException;
import java.io.Serializable;
import java.util.List;

@Service
public interface UserService extends UserDetailsService {

    /*
     * INDEX LIST
     */

    List<User> getIndexList();

    List<User> getIndexList(int size);

    List<User> getIndexList(int size, int page);

    List<User> getIndexList(int size, int page, Sort sort);

    List<User> getIndexList(Serializable user, int size, int page, Sort sort);

    long count();

    long count(Serializable user);

    /*
     * BASIC CRUD OPERATION
     */

    User getDetail(String id);

    User getDetailByUsername(String username);

    User create(UserForm form) throws ValidationException, Exception;

    User update(User user, UserForm form) throws ValidationException;

    /*
     * USER-ROLE
     */

    void attachRole(User user, Role role);

    void detachRole(User user, Role role);

    /*
     * clear cached user details
     */

    void clearCachedUserDetails();

    void clearCachedUserDetails(String username);
}
