package ru.otus.appcontainer;

import static java.util.Comparator.comparingInt;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        Arrays.stream(initialConfigClasses)
            .sorted(comparingInt(configClass -> configClass.getAnnotation(AppComponentsContainerConfig.class).order()))
            .forEach(this::processConfig);
    }

    private void processConfig(Class<?> configClass) {
        checkConfigClass(configClass);
        Object configInstance = tryToCreateConfigInstance(configClass);
        Arrays.stream(configClass.getDeclaredMethods())
            .sorted(comparingInt(method -> method.getAnnotation(AppComponent.class).order()))
            .forEach(method -> {
                String componentName = method.getAnnotation(AppComponent.class).name();
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
