package com.viloveul.packuman.data.repository;

import com.viloveul.packuman.data.entity.Menu;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuRepository extends JpaRepository<Menu, Long>, JpaSpecificationExecutor<Menu> {

    @Query("SELECT DISTINCT a.type FROM Menu a")
    List<String> getTypes();

    List<Menu> findAllByType(String type);

    List<Menu> findAllByType(String type, Sort sort);

    List<Menu> findAllByTypeAndIsActive(String type, boolean active, Sort sort);

    List<Menu> findAllByTypeAndIsActive(String type, boolean active);

    @Query("SELECT MAX(a.order) FROM Menu a WHERE a.parentId = ?1")
    Integer maxByParentId(Long id);

    @Query("SELECT MAX(a.order) FROM Menu a")
    Integer maxByParentIdIsNull();
}
