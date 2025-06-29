package dev.hogoshi.tensai;

import lombok.Getter;

import java.lang.invoke.MethodHandle;
import java.util.function.Consumer;

public class HandlerRegistration {
    @Getter
    private final Object handlerInstance;
    @Getter
    private final int priority;
    @Getter
    private final Consumer<?> consumer;
    @Getter
    private final MethodHandle methodHandle;

    public HandlerRegistration(Object handlerInstance, MethodHandle methodHandle, int priority) {
        this.handlerInstance = handlerInstance;
        this.methodHandle = methodHandle;
        this.priority = priority;
        this.consumer = null;
    }

    public HandlerRegistration(Consumer<?> consumer, int priority) {
        this.handlerInstance = null;
        this.priority = priority;
        this.consumer = consumer;
        this.methodHandle = null;
    }

    public boolean isConsumer() {
        return consumer != null;
    }

    public boolean hasMethodHandle() {
        return methodHandle != null;
    }

    public void invokeMethodHandle(Object event) throws Throwable {
        if (methodHandle != null && handlerInstance != null) {
            methodHandle.invoke(handlerInstance, event);
        }
    }
}