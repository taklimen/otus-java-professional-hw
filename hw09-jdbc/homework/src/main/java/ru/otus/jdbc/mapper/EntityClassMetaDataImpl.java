package ru.otus.jdbc.mapper;

import org.apache.commons.lang3.StringUtils;
import ru.otus.crm.model.annotation.Id;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class EntityClassMetaDataImpl<T> implements EntityClassMetaData<T> {
    public static final String SETTER_PREFIX = "set";
    private static final String GETTER_PREFIX = "get";
    private final List<Field> fields;
    private final Constructor<T> noArgsConstructor;
    private final String className;
    private final List<Method> getters;
    private final List<Method> setters;
    private final Field idField;

    public EntityClassMetaDataImpl(Class<T> entityClass) {
        className = entityClass.getName();
        fields = List.of(entityClass.getDeclaredFields());
        noArgsConstructor = Arrays.stream((Constructor<T>[]) entityClass.getConstructors())
                .filter(c -> c.getParameters().length == 0)
                .findFirst()
                .orElse(null);
        Method[] methods = entityClass.getMethods();
        getters = Arrays.stream(methods)
                .filter(method -> StringUtils.startsWith(method.getName(), GETTER_PREFIX))
                .collect(Collectors.toList());
        setters = Arrays.stream(methods)
                .filter(method -> StringUtils.startsWith(method.getName(), SETTER_PREFIX))
                .collect(Collectors.toList());
        idField = fields.stream()
                .filter(field -> field.isAnnotationPresent(Id.class))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Method getFieldSetter(String paramName) {
        return setters.stream()
                .filter(method -> StringUtils.equalsIgnoreCase(method.getName(), SETTER_PREFIX + paramName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public Method getFieldGetter(String paramName) {
        return getters.stream()
                .filter(method -> StringUtils.equalsIgnoreCase(method.getName(), GETTER_PREFIX + paramName))
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getName() {
        return className;
    }

    @Override
    public Constructor<T> getConstructor() {
        return noArgsConstructor;
    }

    @Override
    public Field getIdField() {
        return idField;
    }

    @Override
    public List<Field> getAllFields() {
        return fields;
    }

    @Override
    public List<Field> getFieldsWithoutId() {
        return fields.stream()
                .filter(field -> !field.isAnnotationPresent(Id.class))
                .collect(Collectors.toList());
    }
}
