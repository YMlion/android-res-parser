package com.ymlion.parser.ext

import com.ymlion.parser.util.ByteUtil
import java.io.RandomAccessFile

/**
 * 8 bytes
 * Created by YMlion on 2018/4/28.
 */
class EndElementExt() {
    constructor(input: RandomAccessFile): this() {
        val bytes = ByteArray(8)
        input.read(bytes)
        ns = ByteUtil.bytes2Int(bytes, 0, 4)
        name = ByteUtil.bytes2Int(bytes, 4, 4)
    }

    /**
     * 该节点的命名空间在字符串池中的索引，一般没有, 4 bytes
     */
    var ns = -1
    /**
     * 该节点的名称在字符串池中的索引, 4 bytes
     */
    var name = 0
}