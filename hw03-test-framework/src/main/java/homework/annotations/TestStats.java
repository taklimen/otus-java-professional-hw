package homework.annotations;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;

@Data
@Accessors(chain = true)
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
public class TestStats {
    int testsRun;
    int testsPassed;
    int testsFailed;
    int testsWithException;
    int testsWithReflectionException;

    public static TestStats createStats(TestResult result) {
        return switch (result) {
            case TEST_PASS -> new TestStats(1, 1, 0, 0, 0);
            case TEST_FAIL -> new TestStats(1, 0, 1, 0, 0);
            case TEST_EXCEPTION -> new TestStats(1, 0, 0, 1, 0);
            case TEST_REFLECTION_EXCEPTION -> new TestStats(1, 0, 0, 0, 1);
        };
    }

    public void printStats() {
        System.out.println("Tests run: " + getTestsRun());
        System.out.println("Tests passed: " + getTestsPassed());
        System.out.println("Total tests failed: " + (getTestsFailed() + getTestsWithException() +
                getTestsWithReflectionException()));
        System.out.println("Failed with exception: " + getTestsWithException());
        System.out.println("Failed with reflection error: " + getTestsWithReflectionException());
    }
}

