package org.masouras.util;

import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import lombok.experimental.UtilityClass;
import org.hibernate.Hibernate;

import java.lang.reflect.Field;
import java.util.Collection;

@UtilityClass
public class HibernateUtil {

    public static void initializeLazy(Object entity) {
        if (entity == null) return;
        Class<?> clazz = entity.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);

            try {
                Object value = field.get(entity);
                if (value == null) continue;

                // @ManyToOne, @OneToOne
                if (field.isAnnotationPresent(ManyToOne.class) || field.isAnnotationPresent(OneToOne.class)) {
                    Hibernate.initialize(value);
                }

                // @OneToMany, @ManyToMany
                if (field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class)) {
                    if (value instanceof Collection<?> collection) {
                        Hibernate.initialize(collection);
                        for (Object element : collection) {
                            initializeLazy(element);
                        }
                    }
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException("Failed to initialize lazy field: " + field.getName(), e);
            }
        }
    }
}
