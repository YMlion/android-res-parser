package com.ymlion.parser

import java.io.InputStream

/**
 * 8 bytes
 *
 * Created by YMlion on 2018/4/17.
 */
class ResHeader() {
    companion object {
        public const val RES_NULL_TYPE = 0x0000
        public const val RES_STRING_POOL_TYPE = 0x0001
        public const val RES_TABLE_TYPE = 0x0002
        public const val RES_XML_TYPE = 0x0003
        // Chunk types in RES_TABLE_TYPE
        public const val RES_TABLE_PACKAGE_TYPE = 0x0200
        public const val RES_TABLE_TYPE_TYPE = 0x0201
        public const val RES_TABLE_TYPE_SPEC_TYPE = 0x0202
        public const val RES_TABLE_LIBRARY_TYPE = 0x0203

        public fun parse(inputStream: InputStream): ResHeader {
            val resHeader = ResHeader()
            val byteArray = ByteArray(8)
            inputStream.read(byteArray)
            resHeader.type = ByteUtil.bytes2Int(byteArray, 0, 2)
            resHeader.headSize = ByteUtil.bytes2Int(byteArray, 2, 2)
            resHeader.size = ByteUtil.bytes2Int(byteArray, 4, 4)
            return resHeader
        }
    }

    constructor(type: Int, headSize: Int, size: Int) : this() {
        this.type = type
        this.headSize = headSize
        this.size = size
    }

    /**
     * chunk type, 2 bytes
     */
    var type = RES_NULL_TYPE
    /**
     * chunk head size, 2 bytes
     */
    var headSize = 8
    /**
     * chunk size = head size + data size, 4 bytes
     */
    var size = 0

    override fun toString(): String {
        var string = "chunk type        : "
        string += when (type) {
            RES_NULL_TYPE -> "null"
            RES_STRING_POOL_TYPE -> "string-pool"
            RES_TABLE_TYPE -> "table"
            RES_XML_TYPE -> "xml"
            RES_TABLE_PACKAGE_TYPE -> "package"
            RES_TABLE_TYPE_TYPE -> "table-type"
            RES_TABLE_TYPE_SPEC_TYPE -> "table-spec"
            RES_TABLE_LIBRARY_TYPE -> "table-library"
            else -> type
        }
        string += "\nhead size         : $headSize\nsize              : $size"
        return string
    }
}