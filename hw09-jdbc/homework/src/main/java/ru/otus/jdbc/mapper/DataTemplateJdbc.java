package ru.otus.jdbc.mapper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.DataTemplateException;
import ru.otus.core.repository.executor.DbExecutor;
import ru.otus.core.repository.executor.DbExecutorImpl;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сохратяет объект в базу, читает объект из базы
 */
public class DataTemplateJdbc<T> implements DataTemplate<T> {
    private static final Logger log = LoggerFactory.getLogger(DataTemplateJdbc.class);
    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(DbExecutorImpl dbExecutor,
                            EntitySQLMetaData entitySQLMetaData,
                            EntityClassMetaData<T> entityClassMetaData) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectByIdSql(), List.of(id), rs -> {
            try {
                if (rs.next()) {
                    return mapResult(rs);
                }
                log.info("No result found by id: {}", id);
                return null;
            } catch (Exception e) {
                throw new DataTemplateException(e);
            }
        });
    }

    private T mapResult(ResultSet rs) throws ReflectiveOperationException {
        Map<Method, Object> settersWithValues = entityClassMetaData.getAllFields().stream()
                .map(field -> mapField(rs, field))
                .filter(Objects::nonNull)
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        T resultEntity = entityClassMetaData.getConstructor().newInstance();
        settersWithValues.forEach((setter, value) -> applySetter(resultEntity, setter, value));
        return resultEntity;
    }

    private void applySetter(T entity, Method setter, Object value) {
        try {
            setter.invoke(entity, value);
        } catch (Exception e) {
            log.warn("Cannot apply setter: {} to entity: {} with value: {}", setter.getName(), entity, value.toString());
        }
    }

    private AbstractMap.SimpleEntry<Method, Object> mapField(ResultSet rs, Field field) {
        String paramName = field.getName();
        Method setter = entityClassMetaData.getFieldSetter(paramName);
        Object paramValue = getParamByName(rs, paramName);
        if (Objects.isNull(setter) || Objects.isNull(paramValue)) {
            return null;
        }
        return new AbstractMap.SimpleEntry<>(setter, paramValue);
    }

    private static Object getParamByName(ResultSet rs, String paramName) {
        Object paramValue;
        try {
            paramValue = rs.getObject(paramName);
        } catch (SQLException e) {
            return null;
        }
        return paramValue;
    }

    @Override
    public List<T> findAll(Connection connection) {
        return dbExecutor.executeSelect(connection, entitySQLMetaData.getSelectAllSql(), Collections.emptyList(), rs -> {
            var result = new ArrayList<T>();
            try {
                while (rs.next()) {
                    result.add(mapResult(rs));
                }
                return result;
            } catch (Exception e) {
                throw new DataTemplateException(e);
            }
        }).orElseThrow(() -> new DataTemplateException(new RuntimeException("Error during findAll")));
    }

    @Override
    public long insert(Connection connection, T entity) {
        try {
            // We rely on fields order to form param list, but we could also parse sql to match column order
            return dbExecutor.executeStatement(connection, entitySQLMetaData.getInsertSql(),
                    mapEntityToParamList(entity));
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private List<Object> mapEntityToParamList(T entity) {
        return entityClassMetaData.getFieldsWithoutId().stream()
                .map(field -> entityClassMetaData.getFieldGetter(field.getName()))
                .map(getter -> getFieldValue(entity, getter))
                .collect(Collectors.toList());
    }

    private Object getFieldValue(T entity, Method getter) {
        if (getter == null) {
            return null;
        }
        try {
            return getter.invoke(entity);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void update(Connection connection, T entity) {
        try {
            List<Object> params = mapEntityToParamList(entity);
            params.add(getIdValue(entity));
            dbExecutor.executeStatement(connection, entitySQLMetaData.getUpdateSql(), params);
        } catch (Exception e) {
            throw new DataTemplateException(e);
        }
    }

    private Object getIdValue(T entity) {
        Method idGetter = entityClassMetaData.getFieldGetter(entityClassMetaData.getIdField().getName());
        return getFieldValue(entity, idGetter);
    }
}
