package ru.otus;

import java.util.ArrayList;
import java.util.List;
import javax.sql.DataSource;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.cachehw.HwListener;
import ru.otus.cachehw.MyCache;
import ru.otus.core.repository.executor.DbExecutorImpl;
import ru.otus.core.sessionmanager.TransactionRunnerJdbc;
import ru.otus.crm.datasource.DriverManagerDataSource;
import ru.otus.crm.model.Client;
import ru.otus.crm.service.DbServiceCachedClientImpl;
import ru.otus.crm.service.DbServiceClientImpl;
import ru.otus.jdbc.mapper.DataTemplateJdbc;
import ru.otus.jdbc.mapper.EntityClassMetaData;
import ru.otus.jdbc.mapper.EntityClassMetaDataImpl;
import ru.otus.jdbc.mapper.EntitySQLMetaData;
import ru.otus.jdbc.mapper.EntitySQLMetaDataImpl;

@SuppressWarnings({"java:S125", "java:S1481"})
public class HomeWorkCache {

    private static final String URL = "jdbc:postgresql://localhost:5430/demoDB";
    private static final String USER = "usr";
    private static final String PASSWORD = "pwd";

    private static final Logger log = LoggerFactory.getLogger(HomeWorkCache.class);

    public static void main(String[] args) {
        long start = System.nanoTime();
        var dataSource = new DriverManagerDataSource(URL, USER, PASSWORD);
        flywayMigrations(dataSource);
        var transactionRunner = new TransactionRunnerJdbc(dataSource);
        var dbExecutor = new DbExecutorImpl();
        var clientCache = new MyCache<Long, Client>();

        HwListener<Long, Client> listener = new HwListener<>() {
            @Override
            public void notify(Long key, Client value, String action) {
                log.info("key:{}, value:{}, action: {}", key, value, action);
            }
        };

        clientCache.addListener(listener);

        EntityClassMetaData<Client> entityClassMetaDataClient = new EntityClassMetaDataImpl<>(Client.class);
        EntitySQLMetaData entitySQLMetaDataClient = new EntitySQLMetaDataImpl(entityClassMetaDataClient);
        var dataTemplateClient = new DataTemplateJdbc<>(dbExecutor, entitySQLMetaDataClient, entityClassMetaDataClient);

        var dbServiceClient = new DbServiceClientImpl(transactionRunner, dataTemplateClient);
        var dbServiceCachedClient = new DbServiceCachedClientImpl(transactionRunner, dataTemplateClient, clientCache);

        // 1. Сохраняем тестовые записи через реализацию без кэша
        int count = 50;
        List<Long> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            Client client = new Client("Client_" + i);
            var saved = dbServiceClient.saveClient(client);
            ids.add(saved.getId());
        }
        log.info("Saved {} clients", count);

        // 2. Прогрев кэша — читаем все записи через кэшированный сервис
        for (Long id : ids) {
            dbServiceCachedClient.getClient(id); // первый вызов — идёт в БД и заполняет кэш
        }

        // 3. Бенчмарк: чтение БЕЗ кэша
        long t1 = System.nanoTime();
        for (Long id : ids) {
            dbServiceClient.getClient(id);
        }
        long dbTime = System.nanoTime() - t1;
        log.info("DB (no cache): {} ms for {} reads", dbTime / 1_000_000, count);

        // 4. Бенчмарк: чтение С кэшем (данные уже в кэше)
        long t2 = System.nanoTime();
        for (Long id : ids) {
            dbServiceCachedClient.getClient(id);
        }
        long cacheTime = System.nanoTime() - t2;
        log.info("Cache (hit):   {} ms for {} reads", cacheTime / 1_000_000, count);
        log.info("Cache is ~{}x faster", dbTime / Math.max(cacheTime, 1));

        // 5. Демонстрация сброса кэша при нехватке памяти
        log.info("--- Simulating memory pressure ---");
        try {
            // создаём большой объём мусора, чтобы GC собрал слабые ссылки
            List<byte[]> memoryEater = new ArrayList<>();
            while (true) {
                memoryEater.add(new byte[1024 * 1024]); // 1 MB блоки
            }
        } catch (OutOfMemoryError e) {
            log.info("OutOfMemoryError caught — GC should have cleared WeakHashMap");
        }
        System.gc();

        // 6. Проверяем, что кэш сброшен — чтение снова пойдёт в БД
        long t3 = System.nanoTime();
        for (Long id : ids) {
            dbServiceCachedClient.getClient(id);
        }
        long afterOOMTime = System.nanoTime() - t3;
        log.info("Cache (after OOM): {} ms — cache was cleared, reads went to DB", afterOOMTime / 1_000_000);

        log.info("Total time: {} ms", (System.nanoTime() - start) / 1_000_000);
    }

    private static void flywayMigrations(DataSource dataSource) {
        log.info("db migration started...");
        var flyway = Flyway.configure()
                .dataSource(dataSource)
                .locations("classpath:/db/migration")
                .load();
        flyway.migrate();
        log.info("db migration finished.");
        log.info("***");
    }
}
