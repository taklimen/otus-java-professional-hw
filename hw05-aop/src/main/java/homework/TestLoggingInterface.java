package homework;

public interface TestLoggingInterface {
    @Log
    void calculation(int param);
    void calculation(int param, int param1);
    @Log
    void calculation(int param, int param1, int param2);
}
