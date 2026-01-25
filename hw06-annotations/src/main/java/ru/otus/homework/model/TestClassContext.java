package ru.otus.homework.model;

import java.lang.reflect.Method;
import java.util.List;

/**
 * Контекст тестового класса с информацией о методах.
 * Используется для передачи информации между методами TestRunner.
 */
public record TestClassContext(
        Class<?> testClass, List<Method> beforeMethods, List<Method> testMethods, List<Method> afterMethods) {}
