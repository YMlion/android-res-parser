package com.ymlion.parser.head

import com.ymlion.parser.util.ByteUtil
import java.io.InputStream
import java.io.RandomAccessFile

/**
 * 类型规范type spec header, 16 bytes
 *
 * Created by YMlion on 2018/4/19.
 */
internal class ResTypeSpecHeader() {
    constructor(file: RandomAccessFile, resChunkHeader: ResChunkHeader) : this() {
        header = resChunkHeader
        val byteArray = ByteArray(8)
        file.read(byteArray)
        id = ByteUtil.bytes2Int(byteArray, 0, 1)
        // id之后是三个保留字段，默认为0
        entryCount = ByteUtil.bytes2Int(byteArray, 4, 4)
    }

    companion object {
        public fun parse(inputStream: InputStream): ResTypeSpecHeader {
            return parse(inputStream, ResChunkHeader.parse(inputStream))
        }

        public fun parse(inputStream: InputStream,
                resChunkHeader: ResChunkHeader): ResTypeSpecHeader {
            val specHeader = ResTypeSpecHeader()
            specHeader.header = resChunkHeader
            val byteArray = ByteArray(8)
            inputStream.read(byteArray)
            specHeader.id = ByteUtil.bytes2Int(byteArray, 0, 1)
            // id之后是三个保留字段，默认为0
            specHeader.entryCount = ByteUtil.bytes2Int(byteArray, 4, 4)
            println(specHeader.toString())
            return specHeader
        }
    }

    /**
     * chunk header, 8 bytes
     */
    var header: ResChunkHeader? = null
    /**
     * 资源类型id，1 byte, start with 1
     */
    var id = 0
    /**
     * 同类型资源数量, 4 bytes
     */
    var entryCount = 0

    override fun toString(): String {
        return StringBuilder().append("ResTypeSpecHeader:").append('\n').append(
                header.toString()).append('\n').append("type id           : $id").append(
                '\n').append("entry count       : $entryCount").toString()
    }
}