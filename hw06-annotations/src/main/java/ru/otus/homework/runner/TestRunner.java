package ru.otus.homework.runner;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.homework.annotations.After;
import ru.otus.homework.annotations.Before;
import ru.otus.homework.annotations.Test;
import ru.otus.homework.exception.TestExecutionException;
import ru.otus.homework.model.TestClassContext;
import ru.otus.homework.model.TestResult;
import ru.otus.homework.model.TestStatistics;

/**
 * Тест раннер - основной класс для запуска тестов.
 * Вся информация передается между методами через параметры и возвращаемые значения.
 */
@SuppressWarnings({"java:S3011", "java:S2629"})
public final class TestRunner {

    private final Logger logger = LoggerFactory.getLogger(TestRunner.class);

    public static final String LONG_LINE = "-----------------------------------------";

    /**
     * Запускает тесты в указанном классе.
     *
     * @param clazz класс с тестами
     * @return статистика выполнения тестов
     */
    public TestStatistics runTests(Class<?> clazz) {
        logger.info("Запуск тестов для класса: {}", clazz);
        TestClassContext context = analyzeTestClass(clazz);

        List<TestResult> results = executeAllTests(context);
        TestStatistics statistics = calculateStatistics(results);

        printResults(results, statistics);

        return statistics;
    }

    /**
     * Анализирует тестовый класс и собирает информацию о методах.
     */
    private TestClassContext analyzeTestClass(Class<?> testClass) {
        List<Method> beforeMethods = findMethodsWithAnnotation(testClass, Before.class);
        List<Method> testMethods = findMethodsWithAnnotation(testClass, Test.class);
        List<Method> afterMethods = findMethodsWithAnnotation(testClass, After.class);

        return new TestClassContext(testClass, beforeMethods, testMethods, afterMethods);
    }

    /**
     * Находит все методы с указанной аннотацией.
     */
    private List<Method> findMethodsWithAnnotation(Class<?> clazz, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(clazz.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(annotationClass))
                .toList();
    }

    /**
     * Выполняет все тесты.
     */
    private List<TestResult> executeAllTests(TestClassContext context) {
        List<TestResult> results = new ArrayList<>();

        for (Method testMethod : context.testMethods()) {
            TestResult result = executeSingleTest(context, testMethod);
            results.add(result);
        }

        return results;
    }

    /**
     * Выполняет один тест с созданием нового экземпляра класса.
     */
    private TestResult executeSingleTest(TestClassContext context, Method testMethod) {
        String testName = testMethod.getName();
        logger.info("Выполнение теста: {}", testName);

        Object testInstance = createTestInstance(context.testClass());

        boolean beforeSuccess = executeBeforeMethods(context.beforeMethods(), testInstance);
        if (!beforeSuccess) {
            // Даже если @Before упал, выполняем @After
            executeAfterMethods(context.afterMethods(), testInstance);
            return TestResult.failure(testName, new TestExecutionException("Ошибка в методе @Before"));
        }

        Throwable testException = executeTestMethod(testMethod, testInstance);

        executeAfterMethods(context.afterMethods(), testInstance);

        if (testException != null) {
            return TestResult.failure(testName, testException);
        }

        return TestResult.success(testName);
    }

    /**
     * Создает новый экземпляр тестового класса.
     */
    private Object createTestInstance(Class<?> testClass) {
        try {
            Constructor<?> constructor = testClass.getDeclaredConstructor();
            constructor.setAccessible(true);
            return constructor.newInstance();
        } catch (Exception e) {
            throw new TestExecutionException("Не удалось создать экземпляр класса: " + testClass.getName(), e);
        }
    }

    /**
     * Выполняет все методы @Before.
     */
    private boolean executeBeforeMethods(List<Method> beforeMethods, Object testInstance) {
        for (Method beforeMethod : beforeMethods) {
            try {
                executeMethod(testInstance, beforeMethod);
            } catch (Exception e) {
                logger.error(
                        "Ошибка в @Before методе {}: {}",
                        beforeMethod.getName(),
                        getRootCause(e).getMessage());
                return false;
            }
        }
        return true;
    }

    private void executeMethod(Object testInstance, Method method)
            throws InvocationTargetException, IllegalAccessException {
        method.setAccessible(true);
        method.invoke(testInstance);
    }

    /**
     * Выполняет тестовый метод.
     */
    private Throwable executeTestMethod(Method testMethod, Object testInstance) {
        try {
            executeMethod(testInstance, testMethod);
            return null;
        } catch (Exception e) {
            Throwable cause = getRootCause(e);
            logger.error("Ошибка в тесте {}: {}", testMethod.getName(), cause.getMessage());
            return cause;
        }
    }

    /**
     * Выполняет все методы @After.
     */
    private void executeAfterMethods(List<Method> afterMethods, Object testInstance) {
        for (Method afterMethod : afterMethods) {
            try {
                executeMethod(testInstance, afterMethod);
            } catch (Exception e) {
                logger.error(
                        "Ошибка в @After методе {}: {}",
                        afterMethod.getName(),
                        getRootCause(e).getMessage());
            }
        }
    }

    /**
     * Получает корневую причину исключения.
     */
    private Throwable getRootCause(Throwable throwable) {
        Throwable cause = throwable.getCause();
        return cause != null ? cause : throwable;
    }

    /**
     * Вычисляет статистику на основе результатов тестов.
     */
    private TestStatistics calculateStatistics(List<TestResult> results) {
        int total = results.size();
        int passed = (int) results.stream().filter(TestResult::passed).count();
        int failed = total - passed;

        return new TestStatistics(total, passed, failed);
    }

    /**
     * Выводит результаты тестирования.
     */
    private void printResults(List<TestResult> results, TestStatistics statistics) {
        logger.info(LONG_LINE);
        logger.info("Результаты тестирования:");
        logger.info(LONG_LINE);

        for (TestResult result : results) {
            if (result.passed()) {
                logger.info("✓ {} - PASSED", result.testName());
            } else {
                String errorMessage =
                        result.exception() != null ? result.exception().getMessage() : "Unknown error";
                logger.info("✗ {} - FAILED: {}", result.testName(), errorMessage);
            }
        }

        logger.info(LONG_LINE);
        logger.info(statistics.toString());
        logger.info(LONG_LINE);
    }
}
