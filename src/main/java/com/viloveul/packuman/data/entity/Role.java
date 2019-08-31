package com.viloveul.packuman.data.entity;

import com.viloveul.packuman.util.listener.AuditTrailListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(
        name = "tbl_role",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "type", "deleted_at"})
        },
        indexes = {
                @Index(name = "name", columnList = "name"),
                @Index(name = "type", columnList = "type"),
                @Index(name = "status", columnList = "status"),
                @Index(name = "deleted", columnList = "deleted"),
                @Index(name = "created_at", columnList = "created_at")
        }
)
@JsonIgnoreProperties({"hibernateLazyInitializer", "event"})
@EntityListeners({AuditTrailListener.class})
public class Role implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @NotEmpty
    @Column(name = "name", nullable = false)
    private String name;

    @NotEmpty
    @Column(name = "type", nullable = false, length = 100)
    private String type;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "deleted", nullable = false)
    private Boolean deleted;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false, nullable = false)
    private Date createdAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "deleted_at")
    private Date deletedAt;

    @ManyToMany(mappedBy = "roles")
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<User> users = new HashSet<User>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "tbl_role_assignment",
            joinColumns = @JoinColumn(name = "parent_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "child_id", referencedColumnName = "id")
    )
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<Role> childs = new HashSet<Role>();

    @JsonIgnore
    @ManyToMany(mappedBy = "childs")
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<Role> parents = new HashSet<Role>();

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.MERGE})
    @JoinTable(
            name = "tbl_role_privilege",
            joinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "privilege_id", referencedColumnName = "id")
    )
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<Privilege> privileges = new HashSet<Privilege>();

    /*
     * CLASS CONSTRUCTOR
     */

    public Role() {
        // do nothing
    }

    public Role(String name) {
        setName(name);
        setType("group");
        setStatus(1);
        setDeleted(false);
    }

    public Role(String name, String type) {
        setName(name);
        setType(type);
        setStatus(1);
        setDeleted(false);
    }

    /*
     * GETTER & SETTER
     */

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Boolean getDeleted() {
        return deleted;
    }

    public void setDeleted(Boolean deleted) {
        this.deleted = deleted;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Date getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(Date deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Set<User> getUsers() {
        return users;
    }

    public void setUsers(Set<User> users) {
        this.users = users;
    }

    public Set<Privilege> getPrivileges() {
        return privileges;
    }

    public void setPrivileges(Set<Privilege> privileges) {
        this.privileges = privileges;
    }

    public Set<Role> getChilds() {
        return childs;
    }

    public void setChilds(Set<Role> childs) {
        this.childs = childs;
    }

    public Set<Role> getParents() {
        return parents;
    }

    public void setParents(Set<Role> parents) {
        this.parents = parents;
    }

    @Override
    public int hashCode() {
        String x = getName() + getType();
        Date y = getDeletedAt();
        if (y != null) {
            x += y.toString();
        }
        return Objects.hashCode(x);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;

        Role other = (Role) obj;
        String x = getName() + getType();
        Date y = getDeletedAt();
        if (y != null) {
            x += y.toString();
        }

        String xx = other.getName() + other.getType();
        Date yy = other.getDeletedAt();
        if (yy != null) {
            xx += yy.toString();
        }
        return Objects.equals(x, xx);
    }
}
