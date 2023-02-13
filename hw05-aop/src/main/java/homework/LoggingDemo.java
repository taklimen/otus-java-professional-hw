package homework;

public class LoggingDemo {
    public static void main(String[] args){
        action();
    }

    private static void action() {
        var myLogging = Ioc.createTestLoggingClass();
        myLogging.calculation(6);
        myLogging.calculation(6,6);
        myLogging.calculation(6,6, 6);
    }
}
