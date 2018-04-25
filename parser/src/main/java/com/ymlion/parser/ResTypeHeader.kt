package com.ymlion.parser

import java.io.InputStream
import java.io.RandomAccessFile

/**
 * type header, 20 + 52 bytes
 *
 * Created by YMlion on 2018/4/19.
 */
class ResTypeHeader() {

    constructor(file: RandomAccessFile, resHeader: ResHeader) : this() {
        header = resHeader
        val byteArray = ByteArray(12)
        file.read(byteArray)
        // id之后三位保留，为0
        id = ByteUtil.bytes2Int(byteArray, 0, 1)
        entryCount = ByteUtil.bytes2Int(byteArray, 4, 4)
        entriesStart = ByteUtil.bytes2Int(byteArray, 8, 4)
        // 开始读取配置信息
        file.skipBytes(header.headSize - 20)
    }

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
            inputStream.read(ByteArray(header.headSize - 20))
            println(typeHeader)
            return typeHeader
        }
    }

    /**
     * chunk header, 8 bytes
     */
    lateinit var header: ResHeader

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

    /**
     * 该部分配置信息和版本相关，大小有可能不同
     */
    class ResTableConfig {
        /**
         * 该部分大小，默认大小52字节，4 bytes
         */
        var size = 0x34
        /**
         * 移动设备国家代码，2 bytes，中国460
         */
        var mcc = 0
        /**
         * 移动网络代码，2 bytes，移动00，和mcc确定网络运营商
         */
        var mnc = 0
        /**
         * 4 bytes，前两个字节为语言，后两个为国家
         */
        var locale = 0
        /**
         * 方向, 1 byte
         */
        var orientation = 0
        /**
         * 1 byte
         */
        var touchscreen = 0
        /**
         * 2 bytes
         */
        var density = 0
        /**
         * 4 bytes, 4字节分别对应不同属性
         */
        var input = 0
        /**
         * 2 bytes
         */
        var screenWidth = 0
        /**
         * 2 bytes
         */
        var screenHeight = 0
        /**
         * 2 bytes
         */
        var sdkVersion = 0
        /**
         * 2 bytes, always 0
         */
        var minorVersion = 0
        /**
         * 4 bytes, 1 + 1 + 2 bytes对应不同属性
         */
        var screenConfig = 0
        /**
         * 2 bytes
         */
        var screenWidthDp = 0
        /**
         * 2 bytes
         */
        var screenHeightDp = 0
        /**
         * 4 bytes
         */
        var localeScript = CharArray(4)
        /**
         * 8 bytes
         */
        var localeVariant = CharArray(8)
        /**
         * 4 bytes
         */
        var screenConfig2 = 0
    }
}