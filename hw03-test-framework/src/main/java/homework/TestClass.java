package homework;

import homework.annotations.After;
import homework.annotations.Before;
import homework.annotations.Test;

public class TestClass {

    @Before
    public void before() {
        System.out.println("Before " + this);
    }

    @After
    public void after() {
        System.out.println("After " + this);
    }

    @Test
    public void testPass() {
        System.out.println("Pass " + this);
    }

    @Test
    public void testFail() {
        System.out.println("Fail " + this);
        throw new AssertionError("Test failed");
    }

    @Test
    public void testException() {
        System.out.println("Exception " + this);
        var i = Integer.parseInt("lololo");
    }
}
