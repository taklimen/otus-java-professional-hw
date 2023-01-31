package homework;

import homework.annotations.After;
import homework.annotations.Before;
import homework.annotations.Test;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

class TestRunner {
    public static void main(String... args) {
        if (args.length < 1) {
            System.out.println("Argument not found. Please pass test class name as an argument");
            return;
        }
        System.out.println(args[0]);

        Class<?> testClass;
        try {
            testClass = Class.forName("homework." + args[0]);
        } catch (ClassNotFoundException e) {
            System.out.println("Test class not found. Please provide a name of an existing class of homework package as an argument");
            return;
        }
        System.out.println("Found class: " + testClass.getName());

        var methods = testClass.getMethods();

        var before = findMethodWithAnnotation(methods, Before.class);
        var after = findMethodWithAnnotation(methods, After.class);
        var tests = findMethodsWithAnnotation(methods, Test.class);

        Constructor<?> constructor;
        try {
            constructor = testClass.getConstructor();
        } catch (NoSuchMethodException e) {
            System.out.println("Please provide a test class with a public no args constructor");
            return;
        }

        AtomicInteger testsRun = new AtomicInteger();
        AtomicInteger testsPassed = new AtomicInteger();
        AtomicInteger testsFailed = new AtomicInteger();
        AtomicInteger testsWithException = new AtomicInteger();
        AtomicInteger testsWithReflectionException = new AtomicInteger();

        tests.parallelStream().forEach(test -> {
            try {
                testsRun.incrementAndGet();

                var testClassInstanse = constructor.newInstance();
                if (Objects.nonNull(before)) before.invoke(testClassInstanse);
                test.invoke(testClassInstanse);
                if (Objects.nonNull(after)) after.invoke(testClassInstanse);

                testsPassed.incrementAndGet();
            } catch (InvocationTargetException e) {
                try {
                    throw e.getTargetException();
                } catch (AssertionError ex) {
                    testsFailed.incrementAndGet();
                } catch (Throwable ex) {
                    testsWithException.incrementAndGet();
                }
            } catch (InstantiationException | IllegalAccessException e) {
                testsWithReflectionException.incrementAndGet();
            }
        });
        System.out.println("Tests run: " + testsRun.get());
        System.out.println("Tests passed: " + testsPassed.get());
        System.out.println("Total tests failed: " + (testsFailed.get() + testsWithException.get() +
                testsWithReflectionException.get()));
        System.out.println("Failed with exception: " + testsWithException.get());
        System.out.println("Failed with reflection error: " + testsWithReflectionException.get());
    }

    private static List<Method> findMethodsWithAnnotation(Method[] methods, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(annotationClass))
                .collect(Collectors.toList());
    }

    private static Method findMethodWithAnnotation(Method[] methods, Class<? extends Annotation> annotationClass) {
        return Arrays.stream(methods)
                .filter(method -> method.isAnnotationPresent(annotationClass))
                .findFirst()
                .orElse(null);
    }
}

