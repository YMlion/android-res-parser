package com.ymlion.parser

import java.io.File

/**
 * Created by YMlion on 2018/4/28.
 */
class XmlFile(file: File) : ResFile(file) {
    override fun resetPackageId(newId: Int): Boolean {
        return true
    }

    override fun parse(): Boolean {
        return true
    }

}