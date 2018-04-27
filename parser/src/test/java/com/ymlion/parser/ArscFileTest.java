package com.ymlion.parser;

import com.ymlion.parser.rule.TimeSpaceRule;
import com.ymlion.parser.util.FileEditor;
import java.io.File;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestRule;

import static org.junit.Assert.assertTrue;

/**
 * Created by YMlion on 2018/4/26.
 */
public class ArscFileTest {

    private ArscFile mArscFile;
    @Rule public TestRule mTestRule = new TimeSpaceRule();

    @Before public void setUp() throws Exception {
        mArscFile = new ArscFile("res" + File.separator + "resources.arsc");
    }

    @Test public void parseTest() {
        mArscFile.parse();
    }

    @Test public void resetPackageIdTest() {
        assertTrue(mArscFile.resetPackageId(0x66));
    }

    @Test public void resetR() {
        assertTrue(
                FileEditor.INSTANCE.resetRJava(new File("res" + File.separator + "R.java"), "66"));
    }

    @After public void tearDown() throws Exception {
    }
}