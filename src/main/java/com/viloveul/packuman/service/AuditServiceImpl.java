package com.viloveul.packuman.service;

import com.viloveul.packuman.data.entity.AuditTrail;
import com.viloveul.packuman.data.entity.AuditTrailDetail;
import com.viloveul.packuman.data.repository.AuditTrailRepository;
import com.viloveul.packuman.util.specification.SearchSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Column;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service("auditService")
@Scope(value = "singleton", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class AuditServiceImpl implements AuditService {

    private static final Logger log = LoggerFactory.getLogger(AuditServiceImpl.class);

    @Autowired
    private AuditTrailRepository auditTrailRepository;

    @Override
    public List<AuditTrail> getIndexList() {
        long c = auditTrailRepository.count();
        return getIndexList((int) c);
    }

    @Override
    public List<AuditTrail> getIndexList(int size) {
        return getIndexList(size, 0);
    }

    @Override
    public List<AuditTrail> getIndexList(int size, int page) {
        return getIndexList(size, page, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public List<AuditTrail> getIndexList(int size, int page, Sort sort) {
        return auditTrailRepository.findAll(PageRequest.of(page, size, sort)).getContent();
    }

    @Override
    public List<AuditTrail> getIndexList(Serializable audit, int size, int page, Sort sort) {
        return auditTrailRepository.findAll(new SearchSpecification<AuditTrail>(audit), PageRequest.of(page, size, sort)).getContent();
    }

    @Override
    public long count() {
        return auditTrailRepository.count();
    }

    @Override
    public long count(Serializable audit) {
        return auditTrailRepository.count(new SearchSpecification<AuditTrail>(audit));
    }

    /*
     * BASIC CRUD OPERATION
     */

    @Override
    public AuditTrail getDetail(String id) {
        return auditTrailRepository.findById(new Long(id)).get();
    }

    /*
     * MISC
     */

    @Async
    @Override
    @Transactional(noRollbackFor = Exception.class)
    public void log(Object o, String type, String username, String browser, String ip) {
        Long entityID = null;

        try {
            Method method = o.getClass().getMethod("getId");
            Object val = method.invoke(o);
            entityID = (Long) val;
        } catch (Exception e) {
            log.info(e.getMessage());
            System.out.println(e.getMessage());
        }

        AuditTrail audit = new AuditTrail(username, type, entityID, o.getClass().getCanonicalName());
        audit.setBrowser(browser);
        audit.setIp(ip);

        Set<AuditTrailDetail> details = new HashSet<AuditTrailDetail>();

        Field[] fields = o.getClass().getDeclaredFields();
        for (Field field : fields) {
            Column col = field.getAnnotation(Column.class);
            if (col != null) {
                boolean isFieldAccessible = field.isAccessible();
                if (!isFieldAccessible) {
                    field.setAccessible(true);
                }
                try {
                    AuditTrailDetail detail = new AuditTrailDetail(col.name(), field.get(o).toString());
                    detail.setAuditTrail(audit);
                    details.add(detail);
                } catch (Exception e) {
                    // do nothing
                    System.out.println(e.getMessage());
                    log.warn(e.getMessage());
                }
                if (!isFieldAccessible) {
                    field.setAccessible(false);
                }
            }
        }
        audit.setDetails(details);
        auditTrailRepository.save(audit);
    }
}
