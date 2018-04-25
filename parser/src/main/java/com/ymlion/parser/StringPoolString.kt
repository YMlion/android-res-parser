package com.ymlion.parser

import java.io.InputStream
import java.io.RandomAccessFile
import java.nio.charset.Charset

/**
 * 一般前两个字节各自表示长度，当为128时，则多出一字节修正
 *
 * Created by YMlion on 2018/4/18.
 */
class StringPoolString() {

    constructor(file: RandomAccessFile, flags: Int) : this() {
        len = file.read()
        if (len >= 128) {
            file.read()
        }
        len = file.read()
        if (len >= 128) {
            len = file.read() or ((len and 0x7f) shl 8)
        }
        val bytes = ByteArray(len)
        file.read(bytes)
        file.read()
        val charset = if (flags >= 0x100) {
            "UTF-8"
        } else {
            file.read()
            "UTF-16"
        }
        content = String(bytes, Charset.forName(charset))
    }

    companion object {
        /**
         * @param flags 字符串编码格式
         */
        fun parse(input: InputStream, flags: Int): StringPoolString {
            val string = StringPoolString()
            string.len = input.read()
            string.bytesNum += 1
            if (string.len >= 128) {
                input.read()
                string.bytesNum += 1
            }
            string.len = input.read()
            string.bytesNum += 1
            if (string.len >= 128) {
                string.len = input.read() or ((string.len and 0x7f) shl 8)
                string.bytesNum += 1
            }
            val bytes = ByteArray(string.len)
            input.read(bytes)
            input.read()
            string.bytesNum += string.len + 1
            val charset = if (flags >= 0x100) {
                "UTF-8"
            } else {
                input.read()
                string.bytesNum += 1
                "UTF-16"
            }
            string.content = String(bytes, Charset.forName(charset))
            return string
        }
    }

    /**
     * string length, 1 byte
     */
    var len = 0
    /**
     * string content, length bytes
     */
    lateinit var content: String
    /**
     * 1 byte or 2 bytes
     */
    var endMark = 0

    var bytesNum = 0

    override fun toString(): String {
        return content
    }
}