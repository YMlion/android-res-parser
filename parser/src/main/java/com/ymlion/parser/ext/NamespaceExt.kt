package com.ymlion.parser.ext

import com.ymlion.parser.util.ByteUtil
import java.io.RandomAccessFile

/**
 * 8 bytes
 *
 * Created by YMlion on 2018/4/28.
 */
internal class NamespaceExt() {
    constructor(input: RandomAccessFile): this() {
        val bytes = ByteArray(8)
        input.read(bytes)
        prefix = ByteUtil.bytes2Int(bytes, 0, 4)
        uri = ByteUtil.bytes2Int(bytes, 4, 4)
    }

    /**
     * namespace前缀在字符串池中的位置, like android in xmlns:android="the URI"
     */
    var prefix: Int = 0
    /**
     * index of namespace' URI in string pool
     */
    var uri: Int = 0
}