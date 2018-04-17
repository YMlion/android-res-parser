package com.ymlion.parser

/**
 * Created by YMlion on 2018/4/17.
 */
object ByteUtil {
    /**
     * 小端：低位在前，高位在后
     */
    fun bytes2Int(bytes: ByteArray, off: Int, size: Int) = when (size) {
        1 -> (bytes[off].toInt() and 0xff)
        2 -> (bytes[off].toInt() and 0xff) or ((bytes[off + 1].toInt() and 0xff) shl 8)
        3 -> (bytes[off].toInt() and 0xff) or ((bytes[off + 1].toInt() and 0xff) shl 8) or ((bytes[off + 2].toInt() and 0xff) shl 16)
        4 -> (bytes[off].toInt() and 0xff) or ((bytes[off + 1].toInt() and 0xff) shl 8) or ((bytes[off + 2].toInt() and 0xff) shl 16) or ((bytes[off + 3].toInt() and 0xff) shl 24)
        else -> 0
    }
}