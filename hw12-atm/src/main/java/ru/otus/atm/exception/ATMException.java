package ru.otus.atm.exception;

/**
 * Исключение, возникающее при операциях с банкоматом.
 */
public class ATMException extends RuntimeException {

    public ATMException(String message) {
        super(message);
    }
}
