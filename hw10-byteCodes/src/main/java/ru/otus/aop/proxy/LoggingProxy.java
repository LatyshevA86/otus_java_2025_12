package ru.otus.aop.proxy;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.otus.aop.annotation.Log;

/**
 * Прокси-обёртка для создания экземпляров с автоматическим логированием.
 * Использует механизм Dynamic Proxy для перехвата вызовов методов,
 * помеченных аннотацией @Log, и вывода параметров в консоль.
 */
public class LoggingProxy {

    private LoggingProxy() {}

    /**
     * Создает прокси-объект для указанного интерфейса и реализации.
     *
     * @param interfaceClass интерфейс, который должен реализовывать прокси
     * @param implementation реализация интерфейса
     * @param <T>            тип интерфейса
     * @return прокси-объект с поддержкой логирования
     */
    @SuppressWarnings("unchecked")
    public static <T> T createProxy(Class<T> interfaceClass, T implementation) {
        return (T) Proxy.newProxyInstance(
                interfaceClass.getClassLoader(),
                new Class<?>[] {interfaceClass},
                new LoggingInvocationHandler(implementation));
    }

    /**
     * InvocationHandler для обработки вызовов методов.
     */
    private static class LoggingInvocationHandler implements InvocationHandler {

        private static final Logger logger = LoggerFactory.getLogger(LoggingInvocationHandler.class);

        private final Object target;
        private final Set<MethodSignature> loggedMethods;

        LoggingInvocationHandler(Object target) {
            this.target = target;
            this.loggedMethods = collectLoggedMethods(target.getClass());
        }

        /**
         * Собирает сигнатуры методов, помеченных аннотацией @Log.
         */
        private Set<MethodSignature> collectLoggedMethods(Class<?> clazz) {
            Set<MethodSignature> methods = new HashSet<>();
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.isAnnotationPresent(Log.class)) {
                    methods.add(new MethodSignature(method.getName(), method.getParameterTypes()));
                }
            }
            return methods;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            MethodSignature signature = new MethodSignature(method.getName(), method.getParameterTypes());

            if (loggedMethods.contains(signature)) {
                logMethodCall(method, args);
            }

            return method.invoke(target, args);
        }

        /**
         * Логирует вызов метода с параметрами.
         */
        private void logMethodCall(Method method, Object[] args) {
            String methodName = method.getName();
            String params;

            if (args == null || args.length == 0) {
                params = "";
            } else {
                params = IntStream.range(0, args.length)
                        .mapToObj(i -> "param" + (i + 1) + ": " + args[i])
                        .collect(Collectors.joining(", "));
            }

            if (params.isEmpty()) {
                logger.info("executed method: {}", methodName);
            } else {
                logger.info("executed method: {}, {}", methodName, params);
            }
        }
    }

    /**
     * Класс для представления сигнатуры метода (имя + типы параметров).
     * Используется для корректного сравнения перегруженных методов.
     */
    private static class MethodSignature {
        private final String name;
        private final Class<?>[] parameterTypes;

        MethodSignature(String name, Class<?>[] parameterTypes) {
            this.name = name;
            this.parameterTypes = parameterTypes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            MethodSignature that = (MethodSignature) o;
            return name.equals(that.name) && Arrays.equals(parameterTypes, that.parameterTypes);
        }

        @Override
        public int hashCode() {
            int result = name.hashCode();
            result = 31 * result + Arrays.hashCode(parameterTypes);
            return result;
        }
    }
}
