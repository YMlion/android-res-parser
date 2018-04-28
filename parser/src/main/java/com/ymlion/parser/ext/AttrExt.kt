package com.ymlion.parser.ext

import com.ymlion.parser.util.ByteUtil
import java.io.RandomAccessFile

/**
 * 20 bytes
 *
 * Created by YMlion on 2018/4/28.
 */
internal class AttrExt() {
    constructor(input: RandomAccessFile): this() {
        val bytes = ByteArray(20)
        input.read(bytes)
        ns = ByteUtil.bytes2Int(bytes, 0, 4)
        name = ByteUtil.bytes2Int(bytes, 4, 4)
        attributeStart = ByteUtil.bytes2Int(bytes, 8, 2)
        attributeSize = ByteUtil.bytes2Int(bytes, 10, 2)
        attributeCount = ByteUtil.bytes2Int(bytes, 12, 2)
        idIndex = ByteUtil.bytes2Int(bytes, 14, 2)
        classIndex = ByteUtil.bytes2Int(bytes, 16, 2)
        styleIndex = ByteUtil.bytes2Int(bytes, 18, 2)
    }

    /**
     * 该节点的命名空间在字符串池中的索引，一般没有, 4 bytes
     */
    var ns = -1
    /**
     * 该节点的名称在字符串池中的索引, 4 bytes
     */
    var name = 0
    /**
     * 2 bytes, 该node的属性偏移
     */
    var attributeStart = 0
    /**
     * 2 bytes，该node的每个属性大小
     */
    var attributeSize = 0
    /**
     * 2 bytes，该节点属性数量
     */
    var attributeCount = 0
    /**
     * 2 bytes，id属性索引，从1开始，0表示没有，一般为0
     */
    var idIndex = 0
    /**
     * 2 bytes, class属性索引，从1开始，0表示没有，一般为0
     */
    var classIndex = 0
    /**
     * 2 bytes, style属性索引，从1开始，0表示没有，一般为0
     */
    var styleIndex = 0
}