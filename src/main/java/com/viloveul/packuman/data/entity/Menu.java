package com.viloveul.packuman.data.entity;

import com.viloveul.packuman.util.listener.AuditTrailListener;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "tbl_menu", indexes = {
        @Index(name = "parent_id", columnList = "parent_id"),
        @Index(name = "label", columnList = "label"),
        @Index(name = "description", columnList = "description"),
        @Index(name = "menu_type", columnList = "menu_type"),
        @Index(name = "menu_url", columnList = "menu_url"),
        @Index(name = "menu_order", columnList = "menu_order"),
        @Index(name = "status", columnList = "status"),
        @Index(name = "deleted", columnList = "deleted"),
        @Index(name = "created_at", columnList = "created_at")
})
@JsonIgnoreProperties({"hibernateLazyInitializer", "event"})
@EntityListeners({AuditTrailListener.class})
public class Menu implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false, nullable = false)
    private Long id;

    @Column(name = "parent_id", updatable = false, insertable = false)
    private Long parentId;

    @NotEmpty
    @Column(name = "label")
    private String label;

    @Column(name = "description", length = 500)
    private String description;

    @Column(name = "menu_type", length = 100)
    private String type;

    @Column(name = "menu_url")
    private String url;

    @Column(name = "menu_order", length = 11)
    private Integer order;

    @Column(name = "icon_url")
    private String icon;

    @Column(name = "status")
    private Integer status;

    @Column(name = "deleted")
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

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "role_id", referencedColumnName = "id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Role role;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id", referencedColumnName = "id")
    @NotFound(action = NotFoundAction.IGNORE)
    private Menu parent;

    @OneToMany(mappedBy = "parent")
    @JsonIgnore
    @NotFound(action = NotFoundAction.IGNORE)
    private Set<Menu> childs = new HashSet<Menu>();

    /*
     * CLASS CONSTRUCTOR
     */

    public Menu() {
        // do nothing
    }

    public Menu(String label, String url) {
        setLabel(label);
        setUrl(url);
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

    public Long getParentId() {
        return parentId;
    }

    public void setParentId(Long parentId) {
        this.parentId = parentId;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Integer getOrder() {
        return order;
    }

    public void setOrder(Integer order) {
        this.order = order;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public Menu getParent() {
        return parent;
    }

    public void setParent(Menu parent) {
        this.parent = parent;
    }

    public Set<Menu> getChilds() {
        return childs;
    }

    public void setChilds(Set<Menu> childs) {
        this.childs = childs;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getLabel() + getType() + getUrl());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Menu other = (Menu) obj;
        return Objects.equals(getLabel() + getType() + getUrl(), other.getLabel() + other.getType() + other.getUrl());
    }
}
