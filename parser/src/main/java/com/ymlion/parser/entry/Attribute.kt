package com.ymlion.parser.entry

import com.ymlion.parser.util.ByteUtil
import java.io.RandomAccessFile

/**
 * 20 bytes
 *
 * Created by YMlion on 2018/4/28.
 */
internal class Attribute() {
    constructor(input: RandomAccessFile): this() {
        val bytes = ByteArray(12)
        input.read(bytes)
        ns = ByteUtil.bytes2Int(bytes, 0, 4)
        name = ByteUtil.bytes2Int(bytes, 4, 4)
        rawValue = ByteUtil.bytes2Int(bytes, 8, 4)
        typedValue = ResValue(input)
    }
    /**
     * 该属性的命名空间在字符串池中的索引，一般没有, 4 bytes
     */
    var ns = -1
    /**
     * 该属性的名称在字符串池中的索引, 4 bytes
     */
    var name = 0
    /**
     * 该属性的值在字符串池中的索引, 4 bytes
     */
    var rawValue = 0

    /**
     * 解析之后的数据
     */
    lateinit var typedValue: ResValue
}