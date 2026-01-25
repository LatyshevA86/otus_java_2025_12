package ru.otus.reflection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@SuppressWarnings({"java:S1481", "java:S1854", "java:S2133"})
public class ReflectionIntro {
    private static final Logger log = LoggerFactory.getLogger(ReflectionIntro.class);

    public static void main(String[] args) throws ClassNotFoundException {
        // Получение Class у класса
        var clazz1 = ReflectionIntro.class;
        log.info("simpleName_1:{}", clazz1.getSimpleName());

        // Получение Class у экземпляра
        var app = new ReflectionIntro();
        var clazz2 = app.getClass();
        log.info("simpleName_2:{}", clazz2.getSimpleName());

        // Получение Class через строковое имя класса
        // Например, для создания экземпляров классов, отсутствующих при компиляции (JDBC-драйвер)
        var classString = Class.forName("java.lang.String");
        log.info("simpleName_3:{}", classString.getSimpleName());
        log.info("canonicalName:{}", classString.getCanonicalName());

        // Получение Class у примитива
        var classInt = int.class;
        log.info("TypeName int:{}", classInt.getTypeName());

        // Получение Class у обёртки примитива
        var classInteger = Integer.class;
        log.info("TypeName Integer:{}", classInteger.getTypeName());
    }
}
