package com.ymlion.parser.util

import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileReader
import java.io.FileWriter

/**
 * Created by YMlion on 2018/4/26.
 */
object FileEditor {

    public fun resetR(rJava: File, newId: String): Boolean {
        val input = BufferedReader(FileReader(rJava))
        val outFile = File(rJava.parent, "R.java.tmp")
        if (outFile.exists()) {
            outFile.delete()
        }
        val out = BufferedWriter(FileWriter(outFile))
        var line = input.readLine()
        while (line != null) {
            out.write(line.replace("0x7f", "0x$newId"))
            out.newLine()
            line = input.readLine()
        }
        input.close()
        out.flush()
        out.close()

        rJava.delete()
        outFile.renameTo(rJava)

        return true
    }
}