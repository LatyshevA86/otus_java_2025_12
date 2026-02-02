package ru.otus.aop.interfaces.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.aop.annotation.Log;
import ru.otus.aop.interfaces.TestLoggingInterface;

/**
 * Реализация интерфейса TestLoggingInterface с аннотациями @Log.
 */
public class TestLogging implements TestLoggingInterface {

    private static final Logger logger = LoggerFactory.getLogger(TestLogging.class);

    @Log
    @Override
    public void calculation(int param) {
        logger.info("Выполняется calculation с param = {}", param);
    }

    @Log
    @Override
    public void calculation(int param1, int param2) {
        logger.info("Выполняется calculation с param1 = {}, param2 = {}", param1, param2);
    }

    @Log
    @Override
    public void calculation(int param1, int param2, String param3) {
        logger.info("Выполняется calculation с param1 = {}, param2 = {}, param3 = {}", param1, param2, param3);
    }
}
