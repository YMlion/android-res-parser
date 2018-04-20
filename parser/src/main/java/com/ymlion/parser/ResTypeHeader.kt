package com.ymlion.parser

import java.io.InputStream

/**
 * type header, 20 bytes
 *
 * Created by YMlion on 2018/4/19.
 */
class ResTypeHeader {

    companion object {
        public fun parse(inputStream: InputStream): ResTypeHeader {
            return parse(inputStream, ResHeader.parse(inputStream))
        }

        public fun parse(inputStream: InputStream, header: ResHeader): ResTypeHeader {
            val typeHeader = ResTypeHeader()
            typeHeader.header = header
            val byteArray = ByteArray(12)
            inputStream.read(byteArray)
            // id之后三位保留，为0
            typeHeader.id = ByteUtil.bytes2Int(byteArray, 0, 1)
            typeHeader.entryCount = ByteUtil.bytes2Int(byteArray, 4, 4)
            typeHeader.entriesStart = ByteUtil.bytes2Int(byteArray, 8, 4)
            // 开始读取配置信息
//            val lengthBytes = ByteArray(2)
//            inputStream.read(lengthBytes)
//            val length = ByteUtil.bytes2Int(lengthBytes, 0, 2)
            inputStream.read(ByteArray(header.headSize - 20))
            println(typeHeader)
            return typeHeader
        }
    }

    /**
     * chunk header, 8 bytes
     */
    var header: ResHeader? = null

    /**
     * type id, 1 byte
     */
    var id = 0
    /**
     * 该类型资源数量，4 bytes
     */
    var entryCount = 0
    /**
     * offset, 4 bytes
     */
    var entriesStart = 0

    override fun toString(): String {
        return StringBuilder().append("ResTypeHeader:").append('\n').append(
                        header.toString()).append('\n').append("type id           : $id").append(
                        '\n').append("entry count       : $entryCount").append('\n').append(
                        "entry start       : $entriesStart").toString()
    }
}