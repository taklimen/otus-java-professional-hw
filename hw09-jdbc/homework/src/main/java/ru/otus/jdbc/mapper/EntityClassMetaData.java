package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

/**
 * "Разбирает" объект на составные части
 */
public interface EntityClassMetaData<T> {
       String getName();

    Constructor<T> getConstructor();

    //Поле Id должно определять по наличию аннотации Id
    //Аннотацию @Id надо сделать самостоятельно
    Field getIdField();

    List<Field> getAllFields();

    List<Field> getFieldsWithoutId();

    Method getFieldSetter(String paramName);

    Method getFieldGetter(String paramName);
}
