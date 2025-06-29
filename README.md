# Tensai

A high-performance, annotation-based EventBus for Java applications with MethodHandles for optimal efficiency.

## Features

- High-performance event dispatching using MethodHandles instead of slow reflection
- Multiple event handlers in a single class via @Handler annotation
- Priority-based event handler execution
- Support for both annotation-based and functional (Consumer) event handling
- Type-safe event dispatching
- Simple and intuitive API

## Installation

### Gradle

```kotlin
repositories {
    mavenCentral()
}

dependencies {
    implementation("dev.hogoshi.tensai:tensai:1.0.0")
}
```

### Maven

```xml
<dependencies>
    <dependency>
        <groupId>dev.hogoshi.tensai</groupId>
        <artifactId>tensai</artifactId>
        <version>1.0.0</version>
    </dependency>
</dependencies>
```

> Note: Replace `1.0.0` with the desired version tag from the [releases page](https://github.com/zhogoshi/tensai/releases).

## Usage

### Creating Event Classes

```java
public class TestEvent {
    private final String message;
    
    public TestEvent(String message) {
        this.message = message;
    }
    
    public String getMessage() {
        return message;
    }
}
```

### Creating Event Listeners

```java
public class MyEventListener {
    @Handler(priority = 5)
    private void onTestEvent(TestEvent event) {
        System.out.println("TestEvent received: " + event.getMessage());
    }
    
    @Handler(priority = 10)
    public void onAnotherEvent(AnotherEvent event) {
        System.out.println("AnotherEvent received: " + event.getName());
    }
}
```

### Dispatching Events

```java
// Create event dispatcher
EventDispatcher dispatcher = new EventDispatcher();

// Register event handlers
dispatcher.registerHandler(TestEvent.class, e -> System.out.println("Lambda handler: " + e), 10);
dispatcher.registerHandler(new MyEventListener());

// Dispatch events
dispatcher.dispatch(new TestEvent("Hello World"));
dispatcher.dispatch(new AnotherEvent("Another event"));
```

## License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.
