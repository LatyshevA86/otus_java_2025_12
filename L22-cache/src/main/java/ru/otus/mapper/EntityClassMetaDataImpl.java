package ru.otus.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {

    private final Class<T> clazz;
    private Field idField = null;
    private List<Field> fieldsWithoutId = null;

    public EntityClassMetaDataImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    @Override
    public String getName() {
        return clazz.getSimpleName();
    }

    @Override
    public Constructor<T> getConstructor() {
        try {
            var paramTypes = getAllFields().stream().map(Field::getType).toArray(Class[]::new);
            var constructor = clazz.getDeclaredConstructor(paramTypes);
            constructor.setAccessible(true);
            return constructor;
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("Constructor not found for " + clazz.getName(), e);
        }
    }

    @Override
    public Field getIdField() {
        if (idField != null) {
            return idField;
        }
        idField = getAllFields().stream()
                .filter(field -> field.isAnnotationPresent(ru.otus.crm.model.Id.class))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No @Id field found in " + clazz.getName()));
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return Arrays.asList(clazz.getDeclaredFields());
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        if (fieldsWithoutId != null) {
            return fieldsWithoutId;
        }
        fieldsWithoutId = getAllFields().stream()
                .filter(field -> !field.isAnnotationPresent(ru.otus.crm.model.Id.class))
                .toList();
        return fieldsWithoutId;
    }
}
