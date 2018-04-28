package com.ymlion.parser

import java.io.File

/**
 * parse binary xml files and reset package id
 *
 * @param file binary xml file
 * Created by YMlion on 2018/4/28.
 */
class XmlFile(file: File) : ResFile(file) {

    override fun parse(): Boolean {
        return true
    }

    override fun resetPackageId(newId: Int): Boolean {
        return true
    }

}