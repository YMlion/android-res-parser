package com.ymlion.parser

import com.ymlion.parser.rule.TimeSpaceRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.io.File

/**
 * Created by YMlion on 2018/4/28.
 */
class XmlFileTest {

    @Rule
    lateinit var mTestRule: TestRule
    @Before
    fun setUp() {
        mTestRule= TimeSpaceRule()
    }

    @Test
    fun parse() {
        XmlFile("res" + File.separator + "activity.xml").parse()
        XmlFile("res" + File.separator + "drawable.xml").parse()
        XmlFile("res" + File.separator + "AndroidManifest.xml").parse()
    }

    @Test
    fun resetPackageId() {
        XmlFile("res" + File.separator + "activity.xml").resetPackageId(0x66)
        XmlFile("res" + File.separator + "drawable.xml").resetPackageId(0x66)
        XmlFile("res" + File.separator + "AndroidManifest.xml").resetPackageId(0x66)
    }

    @After
    fun tearDown() {
    }
}