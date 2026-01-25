package ru.otus.homework;

import ru.otus.homework.model.TestStatistics;
import ru.otus.homework.runner.TestRunner;

/**
 * Главный класс для демонстрации тестового фреймворка.
 */
public class Main {

    public static void main(String[] args) {
        TestStatistics statistics = TestRunner.runTests("ru.otus.homework.ExampleTest");

        if (statistics.failedTests() > 0) {
            System.exit(1);
        }
    }
}
