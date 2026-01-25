package ru.otus.homework.model;

/**
 * Record для хранения статистики выполнения тестов.
 */
public record TestStatistics(int totalTests, int passedTests, int failedTests) {

    @Override
    public String toString() {
        return """
              Статистика тестов:
              Всего тестов: %d
              Успешно: %d
              Провалено: %d"""
                .formatted(totalTests, passedTests, failedTests);
    }
}
