package com.ymlion.parser.entry

import com.ymlion.parser.util.ByteUtil
import java.io.RandomAccessFile

/**
 * 8 bytes
 *
 * Created by YMlion on 2018/4/28.
 */
internal class ResValue() {
    constructor(input: RandomAccessFile) : this() {
        val bytes = ByteArray(8)
        input.read(bytes)
        size = ByteUtil.bytes2Int(bytes, 0, 2)
        res0 = ByteUtil.bytes2Int(bytes, 2, 1)
        dataType = ByteUtil.bytes2Int(bytes, 3, 1)
        data = ByteUtil.bytes2Int(bytes, 4, 4)
    }

    /**
     * 该类字节数, 2 bytes
     */
    var size = 0
    /**
     * 0, 1 byte
     */
    var res0 = 0
    /**
     * 资源数据类型, 1 byte
     */
    var dataType = 0
    /**
     * 资源值或资源id，值或值在字符串池中的索引, 4 bytes
     */
    var data = 0
}