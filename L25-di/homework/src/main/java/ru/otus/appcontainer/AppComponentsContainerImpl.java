package ru.otus.appcontainer;

import static java.util.Comparator.comparingInt;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import ru.otus.appcontainer.api.AppComponent;
import ru.otus.appcontainer.api.AppComponentsContainer;
import ru.otus.appcontainer.api.AppComponentsContainerConfig;

@SuppressWarnings("squid:S1068")
public class AppComponentsContainerImpl implements AppComponentsContainer {

    private final List<Object> appComponents = new ArrayList<>();
    private final Map<String, Object> appComponentsByName = new HashMap<>();

    public AppComponentsContainerImpl(Class<?> initialConfigClass) {
        processConfig(initialConfigClass);
    }

    public AppComponentsContainerImpl(Class<?>... initialConfigClasses) {
        processConfig(initialConfigClasses);
    }

    private void processConfig(Class<?>... configClasses) {
        Arrays.stream(configClasses)
            .sorted(comparingInt(config ->
                config.getAnnotation(AppComponentsContainerConfig.class).order()))
            .flatMap(configClass -> {
                checkConfigClass(configClass);
                Object configInstance;
                configInstance = tryToCreateConfigInstance(configClass);
                return getAnnotatedMethodsEntryStream(configClass, configInstance);
            })
            .sorted(comparingInt(methodEntry ->
                methodEntry.getKey().getAnnotation(AppComponent.class).order()))
            .forEach(entry -> {
                Method method = entry.getKey();
                Object configInstance = entry.getValue();
                String componentName =
                    method.getAnnotation(AppComponent.class).name();
                checkDuplicates(componentName);
                try {
                    Object[] args = getConstructorParameters(method);
                    Object componentInstance = method.invoke(configInstance, args);
                    appComponents.add(componentInstance);
                    appComponentsByName.put(componentName, componentInstance);
                } catch (Exception e) {
                    throw new RuntimeException(String.format("Failed to create component: %s", componentName), e);
                }
            });
    }

    private Stream<Map.Entry<Method, Object>> getAnnotatedMethodsEntryStream(
        Class<?> configClass, Object configInstance
    ) {
        return Arrays.stream(configClass.getDeclaredMethods())
            .filter(method -> method.isAnnotationPresent(AppComponent.class))
            .map(method -> Map.entry(method, configInstance));
    }

    private Object[] getConstructorParameters(Method method) {
        return Arrays.stream(method.getParameterTypes())
            .map(this::getAppComponent)
            .toArray();
    }

    private void checkDuplicates(String componentName) {
        if (appComponentsByName.containsKey(componentName)) {
            throw new IllegalArgumentException(String.format("Duplicate component name: %s", componentName));
        }
    }

    private Object tryToCreateConfigInstance(Class<?> configClass) {
        try {
            return configClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Failed to instantiate config: " + configClass.getName(), e);
        }
    }

    private void checkConfigClass(Class<?> configClass) {
        if (!configClass.isAnnotationPresent(AppComponentsContainerConfig.class)) {
            throw new IllegalArgumentException(String.format("Given class is not config %s", configClass.getName()));
        }
    }

    @Override
    public <C> C getAppComponent(Class<C> componentClass) {
        List<C> components = appComponents.stream()
            .filter(componentClass::isInstance)
            .map(componentClass::cast)
            .toList();
        if (components.isEmpty()) {
            throw new RuntimeException(String.format("Component not found: %s", componentClass));
        }
        if (components.size() > 1) {
            throw new RuntimeException(String.format("Multiple components found for class: %s", componentClass));
        }
        return components.getFirst();
    }

    @Override
    public <C> C getAppComponent(String componentName) {
        Object component = appComponentsByName.get(componentName);
        if (component == null) {
            throw new RuntimeException(String.format("Component not found: %s", componentName));
        }
        return (C) component;
    }
}
