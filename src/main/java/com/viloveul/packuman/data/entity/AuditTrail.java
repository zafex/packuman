package com.viloveul.packuman.data.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tbl_audit_trail", indexes = {
        @Index(name = "username", columnList = "username"),
        @Index(name = "type", columnList = "type"),
        @Index(name = "ip", columnList = "ip"),
        @Index(name = "object_id", columnList = "object_id"),
        @Index(name = "object_class", columnList = "object_class")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "event"})
public class AuditTrail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "username", updatable = false)
    private String username;

    @Column(name = "type", updatable = false, nullable = false)
    private String type;

    @Column(name = "ip", updatable = false)
    private String ip;

    @Column(name = "browser", updatable = false)
    private String browser;

    @Column(name = "object_id", updatable = false)
    private Long objectId;

    @Column(name = "object_class", updatable = false)
    private String objectClass;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;

    @OneToMany(mappedBy = "auditTrail", cascade = CascadeType.ALL)
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<AuditTrailDetail> details = new HashSet<AuditTrailDetail>();

    public AuditTrail() {
        // do nothing
    }

    public AuditTrail(String username, String type, Long objectId, String objectClass) {
        setUsername(username);
        setType(type);
        setObjectId(objectId);
        setObjectClass(objectClass);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public Long getObjectId() {
        return objectId;
    }

    public void setObjectId(Long objectId) {
        this.objectId = objectId;
    }

    public String getObjectClass() {
        return objectClass;
    }

    public void setObjectClass(String objectClass) {
        this.objectClass = objectClass;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Set<AuditTrailDetail> getDetails() {
        return details;
    }

    public void setDetails(Set<AuditTrailDetail> details) {
        this.details = details;
    }
}
