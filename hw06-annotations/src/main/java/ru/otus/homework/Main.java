package ru.otus.homework;

import ru.otus.homework.model.TestStatistics;
import ru.otus.homework.runner.TestRunner;

/**
 * Главный класс для демонстрации тестового фреймворка.
 */
public class Main {

    public static void main(String[] args) {
        TestStatistics statistics = TestRunner.runTests(ExampleTest.class);

        if (statistics.failedTests() > 0) {
            System.exit(1);
        }
    }
}
