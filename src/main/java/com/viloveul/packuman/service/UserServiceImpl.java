package com.viloveul.packuman.service;

import com.viloveul.packuman.data.dto.UserDetail;
import com.viloveul.packuman.data.dto.UserForm;
import com.viloveul.packuman.data.entity.Role;
import com.viloveul.packuman.data.entity.User;
import com.viloveul.packuman.data.repository.UserRepository;

import com.viloveul.packuman.util.specification.SearchSpecification;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import javax.validation.Validator;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("userService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class UserServiceImpl implements UserService {

    @Autowired
    private Validator validator;

    @Autowired
    private Environment env;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private CacheManager cacheManager;

    @Autowired
    private HttpServletRequest request;

    @Override
    @Cacheable(value = "packuman_user_details", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username).get();

        return new UserDetail(user);
    }

    @Override
    public List<User> getIndexList() {
        long c = userRepository.count();
        return getIndexList((int) c);
    }

    @Override
    public List<User> getIndexList(int size) {
        return getIndexList(size, 0);
    }

    @Override
    public List<User> getIndexList(int size, int page) {
        return getIndexList(size, page, Sort.by(Sort.Direction.DESC, "createdAt"));
    }

    @Override
    public List<User> getIndexList(int size, int page, Sort sort) {
        return userRepository.findAll(PageRequest.of(page, size, sort)).getContent();
    }

    @Override
    public List<User> getIndexList(Serializable user, int size, int page, Sort sort) {
        return userRepository.findAll(new SearchSpecification<User>(user), PageRequest.of(page, size, sort)).getContent();
    }

    @Override
    public long count() {
        return userRepository.count();
    }

    @Override
    public long count(Serializable user) {
        return userRepository.count(new SearchSpecification<User>(user));
    }

    @Override
    public User getDetail(String id) {
        return userRepository.getOne(new Long(id));
    }

    @Override
    public User getDetailByUsername(String username) {
        return userRepository.findByUsername(username).get();
    }

    @Override
    @Transactional
    @Secured({"ROLE_ADMINISTRATOR"})
    public User create(UserForm form) throws ValidationException, Exception {
        Set<ConstraintViolation<UserForm>> errors = validator.validate(form);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }

        User user = new User();
        user.setUsername(form.getUsername());
        user.setDeleted(false);
        user.setStatus(1);
        user.setEmail(form.getEmail());
        user.setName(form.getName());
        user.setFullname(form.getFullname());
        user.setRoles(new HashSet<Role>());

        if (form.getRoles().size() > 0) {
            form.getRoles().forEach(role -> {
                attachRole(user, role);
            });
        }

        return userRepository.saveAndFlush(user);
    }

    @Override
    @Transactional
    @CacheEvict(value = "packuman_user_details", key = "#user.username")
    @PreAuthorize("#user.username == authentication.principal or hasRole('ROLE_ADMINISTRATOR')")
    public User update(User user, UserForm form) throws ValidationException {
        Set<ConstraintViolation<UserForm>> errors = validator.validate(form);
        if (!errors.isEmpty()) {
            throw new ConstraintViolationException(errors);
        }

        user.setName(form.getName());
        user.setEmail(form.getEmail());
        user.setFullname(form.getFullname());
        user.setStatus(form.getStatus());
        user.setDeleted(false);
        user.setUpdatedAt(new Date());

        if (request.isUserInRole("ROLE_ADMINISTRATOR")) {
            user.setRoles(new HashSet<Role>());
            form.getRoles().forEach(role -> {
                attachRole(user, role);
            });
        }

        return userRepository.saveAndFlush(user);
    }

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    @CacheEvict(value = "packuman_user_details", key = "#user.username")
    public void attachRole(User user, Role role) {
        user.getRoles().add(role);
        role.getUsers().add(user);
    }

    @Override
    @Secured({"ROLE_ADMINISTRATOR"})
    @CacheEvict(value = "packuman_user_details", key = "#user.username")
    public void detachRole(User user, Role role) {
        user.getRoles().remove(role);
        role.getUsers().remove(user);
    }

    @Override
    @CacheEvict(value = "packuman_user_details", allEntries = true)
    public void clearCachedUserDetails() {
        System.out.println("Delete cache");
    }

    @Override
    @CacheEvict(value = "packuman_user_details", key = "#username")
    public void clearCachedUserDetails(String username) {
        System.out.println("Delete cache user_details: " + username);
    }
}