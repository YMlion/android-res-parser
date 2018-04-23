package com.ymlion.parser

import java.io.InputStream

/**
 * 资源项数据结构，该部分的解析确定了一个资源的定义
 *
 * Created by YMlion on 2018/4/19.
 */
open class ResTableEntry {

    companion object {
        public fun parse(input: InputStream): ResTableEntry {
            var bytes = ByteArray(8)
            input.read(bytes)
            val size = ByteUtil.bytes2Int(bytes, 0, 2)
            val flags = ByteUtil.bytes2Int(bytes, 2, 2)
            val key = ByteUtil.bytes2Int(bytes, 4, 4)
            val entry = if (flags == 1) {
                input.read(bytes)// 读取map部分
                ResMapEntry()
            } else {
                ResTableEntry()
            }
            entry.size = size
            entry.flags = flags
            entry.key = key
            if (entry is ResMapEntry) {
                entry.parent = ByteUtil.bytes2Int(bytes, 0, 4)
                entry.count = ByteUtil.bytes2Int(bytes, 4, 4)
                bytes = ByteArray(12)
                for (i in 1..entry.count) {
                    // 解析map数组
                    input.read(bytes)
                    var tableMap = ResTableMap()
                    tableMap.name = ByteUtil.bytes2Int(bytes, 0, 4)
                    tableMap.value = ResValue()
                    tableMap.value.size = ByteUtil.bytes2Int(bytes, 4, 2)
                    tableMap.value.dataType = ByteUtil.bytes2Int(bytes, 7, 1)
                    tableMap.value.data = ByteUtil.bytes2Int(bytes, 8, 4)
                }
            } else {
                input.read(bytes)
                val value = ResValue()
                value.size = ByteUtil.bytes2Int(bytes, 0, 2)
                value.dataType = ByteUtil.bytes2Int(bytes, 3, 1)
                value.data = ByteUtil.bytes2Int(bytes, 4, 4)
            }
            return entry
        }
    }

    /**
     * 2 bytes
     */
    var size = 0
    /**
     * flags为1时，该资源为bag资源，该entry为map entry，即多了8字节，并且在该entry之后，会有map数组；
     * 否则为非bag资源，在该entry之后，为value。
     * <p> 2 bytes </p>
     */
    var flags = 1

    /**
     * 资源名称在资源名称字符串池中的索引，4字节
     */
    var key = 0

    class ResMapEntry : ResTableEntry() {
        /**
         * 父节点，4字节
         */
        var parent = 0
        /**
         * bag资源可取值数量，4字节
         */
        var count = 0
    }

    class ResTableMap {
        /**
         * 资源真正的id, 4 bytes
         */
        var name = 0

        lateinit var value: ResValue
    }

    class ResValue {
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
         * 资源值，bag类资源的值或非bag类资源值在字符串池中的索引, 4 bytes
         */
        var data = 0
    }
}