package com.viloveul.packuman.service;

import com.viloveul.packuman.data.entity.AuditTrail;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.Serializable;
import java.util.List;

@Service
public interface AuditService {

    /*
     * INDEX LIST
     */

    List<AuditTrail> getIndexList();

    List<AuditTrail> getIndexList(int size);

    List<AuditTrail> getIndexList(int size, int page);

    List<AuditTrail> getIndexList(int size, int page, Sort sort);

    List<AuditTrail> getIndexList(Serializable group, int size, int page, Sort sort);

    long count();

    long count(Serializable group);

    /*
     * BASIC CRUD OPERATION
     */

    AuditTrail getDetail(String id);

    /*
     * MISC
     */

    void log(Object o, String type, String username, String browser, String ip);
}
