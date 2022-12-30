package ru.otus;
import com.google.common.base.Predicates;
import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;

import java.util.Collection;
import java.util.List;


public class HelloOtus {
    public static void main(String... args) {
        List<String> buildTools = Lists.newArrayList("ant", null, "maven", null, "gradle");
        Collection<String> result = Collections2.filter(buildTools, Predicates.notNull());

        System.out.println(result);
    }
}
