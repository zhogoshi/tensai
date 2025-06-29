package dev.hogoshi.tensai;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;
import java.util.function.Consumer;

public class EventDispatcher {

    private final Map<Class<?>, List<HandlerRegistration>> handlers = new HashMap<>();
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();

    public void registerHandler(Object handlerInstance) {
        Class<?> handlerClass = handlerInstance.getClass();
        Arrays.stream(handlerClass.getDeclaredMethods())
                .filter(method -> method.isAnnotationPresent(Handler.class))
                .forEach(method -> {
                    Class<?> eventType = getEventTypeFromMethod(method);
                    if (eventType != null) {
                        try {
                            method.setAccessible(true);
                            MethodHandle methodHandle = lookup.unreflect(method);
                            int priority = method.getAnnotation(Handler.class).priority();
                            HandlerRegistration registration = new HandlerRegistration(handlerInstance, methodHandle, priority);
                            handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(registration);
                            handlers.get(eventType).sort(Comparator.comparingInt(HandlerRegistration::getPriority));
                        } catch (IllegalAccessException e) {
                            throw new RuntimeException("Failed to create method handle for " + method, e);
                        }
                    }
                });
    }

    private Class<?> getEventTypeFromMethod(Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 1) {
            return parameters[0].getType();
        }
        return null;
    }

    public <T> void registerHandler(Class<T> eventType, Consumer<T> handler, int priority) {
        HandlerRegistration registration = new HandlerRegistration(handler, priority);
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add(registration);
        handlers.get(eventType).sort(Comparator.comparingInt(HandlerRegistration::getPriority));
    }

    @SuppressWarnings("unchecked")
    public <T> void dispatch(T event) {
        List<HandlerRegistration> registrations = handlers.get(event.getClass());
        if (registrations != null) {
            for (HandlerRegistration registration : registrations) {
                try {
                    if (registration.isConsumer()) {
                        Consumer<T> consumer = (Consumer<T>) registration.getConsumer();
                        consumer.accept(event);
                    } else if (registration.hasMethodHandle()) {
                        registration.invokeMethodHandle(event);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
