package ru.otus.mapper;

import static java.util.stream.Collectors.joining;

import java.lang.reflect.Field;
import java.util.Collections;

public class EntitySQLMetaDataImpl implements EntitySQLMetaData {

    private final EntityClassMetaData<?> entityClassMetaData;
    private String selectAllSql = null;
    private String selectByIdSql = null;
    private String insertSql = null;
    private String updateSql = null;

    public EntitySQLMetaDataImpl(EntityClassMetaData<?> entityClassMetaData) {
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public String getSelectAllSql() {
        if (selectAllSql != null) {
            return selectAllSql;
        }
        String tableName = entityClassMetaData.getName().toLowerCase();
        selectAllSql = "select * from %s;".formatted(tableName);
        return selectAllSql;
    }

    @Override
    public String getSelectByIdSql() {
        if (selectByIdSql != null) {
            return selectByIdSql;
        }
        String tableName = entityClassMetaData.getName().toLowerCase();
        String idFieldName = entityClassMetaData.getIdField().getName();
        selectByIdSql = "select * from %s where %s = ?;".formatted(tableName, idFieldName);
        return selectByIdSql;
    }

    @Override
    public String getInsertSql() {
        if (insertSql != null) {
            return insertSql;
        }
        String tableName = entityClassMetaData.getName().toLowerCase();
        String fieldNames =
                entityClassMetaData.getAllFields().stream().map(Field::getName).collect(joining(","));
        String placeHolders = String.join(
                ",", Collections.nCopies(entityClassMetaData.getAllFields().size(), "?"));
        insertSql = "insert into %s(%s) values(%s)".formatted(tableName, fieldNames, placeHolders);
        return insertSql;
    }

    @Override
    public String getUpdateSql() {
        if (updateSql != null) {
            return updateSql;
        }
        String tableName = entityClassMetaData.getName().toLowerCase();
        String idFieldName = entityClassMetaData.getIdField().getName();
        String fieldNamesWithoutId = entityClassMetaData.getFieldsWithoutId().stream()
                .map(field -> "%s = ?".formatted(field.getName()))
                .collect(joining(","));
        updateSql = "update %s set %s where %s = ?".formatted(tableName, fieldNamesWithoutId, idFieldName);
        return updateSql;
    }
}
