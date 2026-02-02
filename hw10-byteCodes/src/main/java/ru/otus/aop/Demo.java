package ru.otus.aop;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.aop.interfaces.TestLoggingInterface;
import ru.otus.aop.interfaces.impl.TestLogging;
import ru.otus.aop.proxy.LoggingProxy;

/**
 * Демонстрационный класс для проверки работы автоматического логирования.
 */
public class Demo {

    private static final Logger logger = LoggerFactory.getLogger(Demo.class);

    public static void main(String[] args) {

        TestLoggingInterface testLogging = LoggingProxy.createProxy(TestLoggingInterface.class, new TestLogging());

        logger.info("=== Вызов методов с автоматическим логированием ===");

        // Вызов метода с одним параметром
        logger.info("--- Вызов calculation(6) ---");
        testLogging.calculation(6);

        // Вызов метода с двумя параметрами
        logger.info("--- Вызов calculation(10, 20) ---");
        testLogging.calculation(10, 20);

        // Вызов метода с тремя параметрами
        logger.info("--- Вызов calculation(100, 200, 'test') ---");
        testLogging.calculation(100, 200, "test");

        logger.info("=== Демонстрация завершена ===");
    }
}
