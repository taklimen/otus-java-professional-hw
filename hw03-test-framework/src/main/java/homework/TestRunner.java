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
import java.util.function.BinaryOperator;
import java.util.stream.Collectors;

class TestRunner {

    public static void runTests(String testClassName) {
        Class<?> testClass;
        try {
            testClass = Class.forName("homework." + testClassName);
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
                .filter(Objects::nonNull)
                .map(testMethod -> {
                    try {
                        return new TestObject(before, testMethod, after, constructor.newInstance()).runTest();
                    } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                        System.out.println("Failed to construct test class instance");
                        return TestResult.TEST_REFLECTION_EXCEPTION;
                    }
                })
                .map(TestStats::createStats)
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
                .setTestsWithReflectionException(stat1.getTestsWithReflectionException() +
                        stat2.getTestsWithReflectionException());
    }

    private static List<Method> findMethodsWithAnnotation(Method[] methods, Class<? extends
            Annotation> annotationClass) {
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

