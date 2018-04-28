package com.ymlion.parser.head

import com.ymlion.parser.util.ByteUtil
import java.io.RandomAccessFile

/**
 * res xml tree node header, 16 bytes
 *
 * Created by YMlion on 2018/4/28.
 */
internal class ResTreeNodeHeader() {
    constructor(input: RandomAccessFile) : this(input, ResChunkHeader(input))
    constructor(input: RandomAccessFile, header: ResChunkHeader) : this() {
        this.header = header
        val bytes = ByteArray(8)
        input.read(bytes)
        lineNumber = ByteUtil.bytes2Int(bytes, 0, 4)
        comment = ByteUtil.bytes2Int(bytes, 4, 4)
    }

    /**
     * header
     */
    lateinit var header: ResChunkHeader
    /**
     * 该标签在原文件中开始出现的行号
     */
    var lineNumber: Int = 2
    /**
     * 该标签的注释在字符串资源池中的位置，一般该值不会出现在资源池中
     */
    var comment: Int = -1
}