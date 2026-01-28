package ru.otus.homework.exception;

/**
 * Исключение, возникающее при выполнении теста.
 */
public class TestExecutionException extends RuntimeException {

    public TestExecutionException(String message) {
        super(message);
    }

    public TestExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
}
