package com.ymlion.parser.head

import com.ymlion.parser.util.ByteUtil
import java.io.InputStream
import java.io.RandomAccessFile

/**
 * 28 bytes，主要描述package中定义的字符串值和字符串样式数量和位置
 *
 * Created by YMlion on 2018/4/18.
 */
internal class ResStringPoolHeader() {

    constructor(file: RandomAccessFile) : this() {
        header = ResChunkHeader(file)
        val byteArray = ByteArray(20)
        file.read(byteArray)
        stringCount = ByteUtil.bytes2Int(byteArray, 0, 4)
        styleCount = ByteUtil.bytes2Int(byteArray, 4, 4)
        flags = ByteUtil.bytes2Int(byteArray, 8, 4)
        stringsStart = ByteUtil.bytes2Int(byteArray, 12, 4)
        stylesStart = ByteUtil.bytes2Int(byteArray, 16, 4)
    }

    companion object {
        public fun parse(inputStream: InputStream): ResStringPoolHeader {
            val stringPoolHeader = ResStringPoolHeader()
            stringPoolHeader.header = ResChunkHeader.parse(inputStream)
            val byteArray = ByteArray(20)
            inputStream.read(byteArray)
            stringPoolHeader.stringCount = ByteUtil.bytes2Int(byteArray, 0, 4)
            stringPoolHeader.styleCount = ByteUtil.bytes2Int(byteArray, 4, 4)
            stringPoolHeader.flags = ByteUtil.bytes2Int(byteArray, 8, 4)
            stringPoolHeader.stringsStart = ByteUtil.bytes2Int(byteArray, 12, 4)
            stringPoolHeader.stylesStart = ByteUtil.bytes2Int(byteArray, 16, 4)
            println(stringPoolHeader.toString())
            return stringPoolHeader
        }
    }

    /**
     * chunk head, 8 bytes
     */
    lateinit var header: ResChunkHeader
    /**
     * strings count, 4 bytes
     */
    var stringCount: Int = 0
    /**
     * style count, 4 bytes
     */
    var styleCount: Int = 0
    /**
     * string 编码格式, 默认UTF-8, 4 bytes
     */
    var flags: Int = 0x100
    /**
     * strings start offset, 4 bytes
     */
    var stringsStart: Int = 0
    /**
     * styles start offset, 4 bytes
     */
    var stylesStart: Int = 0

    override fun toString(): String {
        return StringBuilder().append("ResStringPoolHeader:").append('\n').append(
                header.toString()).append('\n').append("string count      : $stringCount").append(
                '\n').append("style count       : $styleCount").append('\n').append(
                "flags             : $flags").append('\n').append(
                "strings start     : $stringsStart").append('\n').append(
                "styles start      : $stylesStart").toString()
    }

}