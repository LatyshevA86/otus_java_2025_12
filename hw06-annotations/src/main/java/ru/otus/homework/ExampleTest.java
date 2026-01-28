package ru.otus.homework;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.annotations.After;
import ru.otus.homework.annotations.Before;
import ru.otus.homework.annotations.Test;

/**
 * Пример тестового класса для демонстрации тестового фреймворка.
 */
class ExampleTest {

    private static final Logger logger = LoggerFactory.getLogger(ExampleTest.class);

    @Before
    public void setUp() {
        logger.info("@Before: setUp()");
    }

    @After
    public void tearDown() {
        logger.info("@After: tearDown() - очистка ресурсов");
    }

    @Test
    public void testSuccess() {
        logger.info("@Test: testSuccess() - выполняется успешный тест");
        logger.info("@Test: testSuccess() - тест прошёл успешно");
    }

    @Test
    public void testFailure() {
        logger.info("@Test: testFailure() - выполняется тест, который должен упасть");
        throw new IllegalStateException("Специально брошенное исключение для демонстрации");
    }
}
