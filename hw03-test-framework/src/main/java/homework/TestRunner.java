package homework;

import homework.annotations.After;
import homework.annotations.Before;
import homework.annotations.Test;
import homework.annotations.TestResult;
import homework.annotations.TestStats;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BinaryOperator;
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

        TestStats stats = tests.stream()
                .map(test -> runTest(before, after, constructor, test))
                .reduce(sumStats())
                .orElse(new TestStats());

        stats.printStats();
    }

    private static BinaryOperator<TestStats> sumStats() {
        return (stat1, stat2) -> new TestStats()
                .setTestsRun(stat1.getTestsRun() + stat2.getTestsRun())
                .setTestsFailed(stat1.getTestsFailed() + stat2.getTestsFailed())
                .setTestsPassed(stat1.getTestsPassed() + stat2.getTestsPassed())
                .setTestsWithException(stat1.getTestsWithException() + stat2.getTestsWithException())
                .setTestsWithReflectionException(stat1.getTestsWithReflectionException() + stat2.getTestsWithReflectionException());
    }

    private static TestStats runTest(Method before, Method after, Constructor<?> constructor, Method test) {
        Runnable cleanup = () -> {
        };
        TestStats stats;
        try {
            var testClassInstance = constructor.newInstance();
            runBefore(before, testClassInstance);
            cleanup = () -> runAfter(after, testClassInstance);
            test.invoke(testClassInstance);
            stats = TestStats.createStats(TestResult.TEST_PASS);
        } catch (InvocationTargetException e) {
            try {
                throw e.getTargetException();
            } catch (AssertionError ex) {
                stats = TestStats.createStats(TestResult.TEST_FAIL);
            } catch (Throwable ex) {
                stats = TestStats.createStats(TestResult.TEST_EXCEPTION);
            }
        } catch (InstantiationException | IllegalAccessException e) {
            stats = TestStats.createStats(TestResult.TEST_REFLECTION_EXCEPTION);
        }
        cleanup.run();
        return stats;
    }

    private static void runBefore(Method before, Object testClassInstanse) throws InvocationTargetException, IllegalAccessException {
        if (Objects.nonNull(before))
            before.invoke(testClassInstanse);
    }

    private static void runAfter(Method method, Object testClassInstanse) {
        Optional.ofNullable(method)
                .ifPresent(method1 -> {
                    try {
                        method.invoke(testClassInstanse);
                    } catch (Exception e) {
                        System.out.println("Exception in method annotated with @After");
                    }
                });
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

