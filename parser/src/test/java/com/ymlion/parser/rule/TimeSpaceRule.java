package com.ymlion.parser.rule;

import org.junit.rules.TestRule;
import org.junit.runner.Description;
import org.junit.runners.model.Statement;

/**
 * Created by YMlion on 2018/4/26.
 */
public class TimeSpaceRule implements TestRule {
    @Override public Statement apply(Statement base, Description description) {
        return new Statement() {
            @Override public void evaluate() throws Throwable {
                long start = System.currentTimeMillis();
                base.evaluate();
                long space = System.currentTimeMillis() - start;
                System.out.println(description.getTestClass().getSimpleName()
                        + ":"
                        + description.getMethodName()
                        + " space "
                        + space
                        + "ms.");
            }
        };
    }
}
