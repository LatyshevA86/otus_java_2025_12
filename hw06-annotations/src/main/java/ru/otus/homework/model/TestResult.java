package ru.otus.homework.model;

/**
 * Результат выполнения одного теста.
 * Используется для передачи информации о результате между методами.
 */
public record TestResult(String testName, boolean passed, Throwable exception) {

    public static TestResult success(String testName) {
        return new TestResult(testName, true, null);
    }

    public static TestResult failure(String testName, Throwable exception) {
        return new TestResult(testName, false, exception);
    }
}
