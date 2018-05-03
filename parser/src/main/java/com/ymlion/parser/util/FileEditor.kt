package com.ymlion.parser.util

import java.io.File

/**
 * Created by YMlion on 2018/4/26.
 */
object FileEditor {

    public fun resetRJava(rJava: File, newId: String): Boolean {
        val outFile = File(rJava.parent, "R.java.tmp").apply {
            takeIf { exists() }?.delete()
        }
        val out = outFile.bufferedWriter()
        out.use {
            rJava.forEachLine {
                out.write(it.replace("0x7f", "0x$newId"))
                out.newLine()
            }
        }

        rJava.delete()
        outFile.renameTo(rJava)

        return true
    }

    public fun resetRJava(rJava: File, newId: Int): Boolean {
        return resetRJava(rJava, newId.toString(16))
    }
}