package com.ymlion.parser

import java.io.InputStream

/**
 * 字符串样式， 一个字符串可以对应于多个样式
 *
 * 字符串样式以0xFFFFFFFF结尾，在样式池结束以8字节0xFF结尾
 *
 * Created by YMlion on 2018/4/18.
 */
class StringPoolStyle {

    companion object {
        public fun parse(input: InputStream) {
            val style = StringPoolStyle()
            val bytes = ByteArray(16)
            input.read(bytes)
            style.name = ByteUtil.bytes2Int(bytes, 0, 4)
            style.firstChar = ByteUtil.bytes2Int(bytes, 4, 4)
            style.lastChar = ByteUtil.bytes2Int(bytes, 8, 4)
        }
    }

    /**
     * 该值是字符串在字符串池中的索引，找到后即对应的样式名称，4 bytes
     */
    var name = 0
    /**
     * 字符串中应用该样式的起始位置，4 bytes
     */
    var firstChar = 0
    /**
     * 字符串中应用该样式的终止位置，4 bytes
     */
    var lastChar = 0
    /**
     * 4 bytes
     */
    var endMark = 0xffffffff

}