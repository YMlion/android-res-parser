package com.ymlion.parser.head

import com.ymlion.parser.util.ByteUtil
import java.io.InputStream
import java.io.RandomAccessFile

/**
 * 8 bytes
 *
 * Created by YMlion on 2018/4/17.
 */
internal class ResChunkHeader() {
    companion object {
        public const val RES_NULL_TYPE = 0x0000
        public const val RES_STRING_POOL_TYPE = 0x0001
        public const val RES_TABLE_TYPE = 0x0002
        public const val RES_XML_TYPE = 0x0003

        // Chunk types in RES_XML_TYPE
        const val RES_XML_FIRST_CHUNK_TYPE    = 0x0100
        const val RES_XML_START_NAMESPACE_TYPE= 0x0100
        const val RES_XML_END_NAMESPACE_TYPE  = 0x0101
        const val RES_XML_START_ELEMENT_TYPE  = 0x0102
        const val RES_XML_END_ELEMENT_TYPE    = 0x0103
        const val RES_XML_CDATA_TYPE          = 0x0104
        const val RES_XML_LAST_CHUNK_TYPE     = 0x017f
        // This contains a uint32_t array mapping strings in the string
        // pool back to resource identifiers.  It is optional.
        const val RES_XML_RESOURCE_MAP_TYPE   = 0x0180

        // Chunk types in RES_TABLE_TYPE
        public const val RES_TABLE_PACKAGE_TYPE = 0x0200
        public const val RES_TABLE_TYPE_TYPE = 0x0201
        public const val RES_TABLE_TYPE_SPEC_TYPE = 0x0202
        public const val RES_TABLE_LIBRARY_TYPE = 0x0203

        public fun parse(inputStream: InputStream): ResChunkHeader {
            val resHeader = ResChunkHeader()
            val byteArray = ByteArray(8)
            inputStream.read(byteArray)
            resHeader.type = ByteUtil.bytes2Int(byteArray, 0, 2)
            resHeader.headSize = ByteUtil.bytes2Int(byteArray, 2, 2)
            resHeader.size = ByteUtil.bytes2Int(byteArray, 4, 4)
            return resHeader
        }
    }

    constructor(file: RandomAccessFile) : this() {
        val byteArray = ByteArray(8)
        file.read(byteArray)
        type = ByteUtil.bytes2Int(byteArray, 0, 2)
        headSize = ByteUtil.bytes2Int(byteArray, 2, 2)
        size = ByteUtil.bytes2Int(byteArray, 4, 4)
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