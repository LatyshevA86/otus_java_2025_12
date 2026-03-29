package ru.otus.jdbc.mapper;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import ru.otus.core.repository.DataTemplate;
import ru.otus.core.repository.executor.DbExecutor;

/** Сохраняет объект в базу, читает объект из базы */
@SuppressWarnings("java:S1068")
public class DataTemplateJdbc<T> implements DataTemplate<T> {

    private final DbExecutor dbExecutor;
    private final EntitySQLMetaData entitySQLMetaData;
    private final EntityClassMetaData<T> entityClassMetaData;

    public DataTemplateJdbc(
            DbExecutor dbExecutor, EntitySQLMetaData entitySQLMetaData, EntityClassMetaData<T> entityClassMetaData
    ) {
        this.dbExecutor = dbExecutor;
        this.entitySQLMetaData = entitySQLMetaData;
        this.entityClassMetaData = entityClassMetaData;
    }

    @Override
    public Optional<T> findById(Connection connection, long id) {
        String selectQuery = entitySQLMetaData.getSelectByIdSql();
        return dbExecutor.executeSelect(connection, selectQuery, List.of(id), resultSet -> {
            try {
                if (resultSet.next()) {
                    var constructor = entityClassMetaData.getConstructor();
                    var idField = entityClassMetaData.getIdField();
                    var fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
                    return getEntity(resultSet, fieldsWithoutId, idField, constructor);
                }
                return null;
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public List<T> findAll(Connection connection) {
        String selectQuery = entitySQLMetaData.getSelectAllSql();
        return dbExecutor
                .executeSelect(connection, selectQuery, Collections.emptyList(), resultSet -> {
                    try {
                        var constructor = entityClassMetaData.getConstructor();
                        var idField = entityClassMetaData.getIdField();
                        var fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
                        var result = new ArrayList<T>();
                        while (resultSet.next()) {
                            result.add(getEntity(resultSet, fieldsWithoutId, idField, constructor));
                        }
                        return result;
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                })
                .orElse(new ArrayList<>());
    }

    @Override
    public long insert(Connection connection, T client) {
        String insertQuery = entitySQLMetaData.getInsertSql();
        var fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
        var params = new ArrayList<>(fieldsWithoutId.size() + 1);
        long randomId = ThreadLocalRandom.current().nextInt();
        params.add(randomId);
        for (var field : fieldsWithoutId) {
            addParam(client, field, params);
        }
        return dbExecutor.executeStatement(connection, insertQuery, params);
    }

    @Override
    public void update(Connection connection, T client) {
        String updateQuery = entitySQLMetaData.getUpdateSql();
        var fieldsWithoutId = entityClassMetaData.getFieldsWithoutId();
        var params = new ArrayList<>(fieldsWithoutId.size() + 1);
        for (var field : fieldsWithoutId) {
            addParam(client, field, params);
        }
        addParam(client, entityClassMetaData.getIdField(), params);
        dbExecutor.executeStatement(connection, updateQuery, params);
    }

    private void addParam(T client, Field field, ArrayList<Object> params) {
        try {
            field.setAccessible(true);
            params.add(field.get(client));
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private T getEntity(ResultSet resultSet, List<Field> fieldsWithoutId, Field idField, Constructor<T> constructor)
        throws IllegalAccessException, InstantiationException, InvocationTargetException, SQLException
    {
        var args = new Object[fieldsWithoutId.size() + 1];
        args[0] = resultSet.getObject(idField.getName());
        for (var idx = 0; idx < fieldsWithoutId.size(); idx++) {
            args[idx + 1] = resultSet.getObject(fieldsWithoutId.get(idx).getName());
        }
        return constructor.newInstance(args);
    }
}
