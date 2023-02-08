package homework;

public class TestRunnerMain {
    public static void main(String... args) {
        if (args.length < 1) {
            System.out.println("Argument not found. Please pass test class name as an argument");
            return;
        }
        TestRunner.runTests(args[0]);
    }
}
