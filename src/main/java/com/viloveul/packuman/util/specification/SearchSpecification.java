package com.viloveul.packuman.util.specification;

import org.springframework.data.jpa.domain.Specification;

import javax.persistence.Transient;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class SearchSpecification<T> implements Specification<T> {

    private Serializable model;

    public SearchSpecification(Serializable model) {
        this.model = model;
    }

    @Override
    public Predicate toPredicate(Root<T> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        Field[] fields = model.getClass().getDeclaredFields();
        for (Field field : fields) {
            if (!field.isAnnotationPresent(Transient.class)) {
                boolean isFieldAccessible = field.isAccessible();
                if (!isFieldAccessible) {
                    field.setAccessible(true);
                }

                try {
                    Class<?> type = field.getType();
                    Object v = field.get(model);
                    if (!type.isPrimitive() && v != null && !v.equals("") && !(v instanceof Collection) && !(v instanceof Map)) {
                        if (type == String.class) {
                            predicates.add(cb.like(root.get(field.getName()), String.valueOf(field.get(model)).toLowerCase() + "%"));
                        } else if (type == Boolean.class) {
                            predicates.add(cb.equal(root.get(field.getName()), field.get(model)));
                        } else {
                            predicates.add(cb.like(root.get(field.getName()).as(String.class), field.get(model).toString() + "%"));
                        }
                    }
                } catch (IllegalArgumentException | IllegalAccessException e) {
                    System.out.println(e.getMessage());
                } catch (Exception ex) {
                    System.out.println("ERROR");
                    System.out.println(ex.getMessage());
                }

                if (!isFieldAccessible) {
                    field.setAccessible(false);
                }
            }
        }
        if (predicates.size() > 0) {
            return cb.and(predicates.toArray(new Predicate[0]));
        }
        return null;
    }
}
