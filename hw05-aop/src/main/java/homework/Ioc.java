package homework;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Ioc {

    private Ioc() {
    }

    static TestLoggingInterface createTestLoggingClass() {
        InvocationHandler handler = new DemoInvocationHandler(new TestLogging());
        return (TestLoggingInterface) Proxy.newProxyInstance(Ioc.class.getClassLoader(),
                new Class<?>[]{TestLoggingInterface.class}, handler);
    }

    static class DemoInvocationHandler implements InvocationHandler {
        private final TestLoggingInterface testLogging;
        private final Set<Method> loggedMethods = new HashSet<>();

        DemoInvocationHandler(TestLoggingInterface testLogging) {
            this.testLogging = testLogging;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            if (needLogging(method)) {
                System.out.println("method: " + method.getName() + ", params: " + Arrays.toString(args));
            }
            return method.invoke(testLogging, args);
        }

        private boolean needLogging(Method method) {
            if (loggedMethods.contains(method)) {
                return true;
            }
            if (method.isAnnotationPresent(Log.class)) {
                loggedMethods.add(method);
                return true;
            }
            return false;
        }
    }
}
