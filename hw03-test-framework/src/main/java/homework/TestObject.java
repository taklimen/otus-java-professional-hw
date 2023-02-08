package homework;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;
import java.util.Optional;

@FieldDefaults(makeFinal = true, level = AccessLevel.PRIVATE)
@RequiredArgsConstructor
public class TestObject {
    Method before;
    Method test;
    Method after;
    Object instance;

    public TestResult runTest() {
        Runnable cleanup = () -> {
        };
        TestResult result;
        try {
            runBefore();
            cleanup = this::runAfter;
            test.invoke(instance);
            result = TestResult.TEST_PASS;
        } catch (InvocationTargetException e) {
            try {
                throw e.getTargetException();
            } catch (AssertionError ex) {
                result = TestResult.TEST_FAIL;
            } catch (Throwable ex) {
                result = TestResult.TEST_EXCEPTION;
            }
        } catch (IllegalAccessException e) {
            result = TestResult.TEST_REFLECTION_EXCEPTION;
        }
        cleanup.run();
        return result;
    }

    private void runBefore() throws InvocationTargetException, IllegalAccessException {
        if (Objects.nonNull(before))
            before.invoke(instance);
    }

    private void runAfter() {
        Optional.ofNullable(after)
                .ifPresent(method1 -> {
                    try {
                        after.invoke(instance);
                    } catch (Exception e) {
                        System.out.println("Exception in method annotated with @After");
                    }
                });
    }
}
