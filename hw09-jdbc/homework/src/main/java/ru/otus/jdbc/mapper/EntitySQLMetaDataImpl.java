package ru.otus.jdbc.mapper;

import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {
    private final EntityClassMetaData entityClassMetaData;
    private final String tableName;
    public EntitySQLMetaDataImpl(EntityClassMetaData entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
        this.tableName = StringUtils.substringAfterLast(entityClassMetaData.getName(), ".").toLowerCase();
    }

    @Override
    public String getSelectAllSql() {
        return String.format("select * from %s", tableName);
    }

    @Override
    public String getSelectByIdSql() {
        return String.format("select * from %s where %s = ?",
                tableName,
                entityClassMetaData.getIdField().getName());
    }

    @Override
    public String getInsertSql() {
        int fieldsCount = entityClassMetaData.getAllFields().size();
        String columnNames = ((List<Field>) entityClassMetaData.getFieldsWithoutId()).stream()
                .map(Field::getName)
                .collect(Collectors.joining(", "));
        String paramsPlaceholders = IntStream.range(0, fieldsCount - 1)
                .mapToObj(index -> "?")
                .collect(Collectors.joining(", "));
        return String.format("insert into %s(%s) values (%s)", tableName, columnNames, paramsPlaceholders);
    }

    @Override
    public String getUpdateSql() {
        String updateColumns = ((List<Field>) entityClassMetaData.getFieldsWithoutId()).stream()
                .map(Field::getName)
                .map(name -> String.format("%s = ?", name, name))
                .collect(Collectors.joining(", "));
        return String.format("update %s set %s where %s = ?",
                tableName,
                updateColumns,
                entityClassMetaData.getIdField().getName());
    }
}
