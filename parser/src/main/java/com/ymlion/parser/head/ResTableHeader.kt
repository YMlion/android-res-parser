package com.ymlion.parser.head

import com.ymlion.parser.util.ByteUtil
import java.io.InputStream
import java.io.RandomAccessFile

/**
 * table header, 12 bytes
 *
 * Created by YMlion on 2018/4/17.
 */
internal class ResTableHeader() {

    constructor(file: RandomAccessFile) : this() {
        header = ResChunkHeader(file)
        val byteArray = ByteArray(4)
        file.read(byteArray)
        packageCount = ByteUtil.bytes2Int(byteArray, 0, 4)
    }

    companion object {
        public fun parse(inputStream: InputStream): ResTableHeader {
            val resTableHeader = ResTableHeader()
            resTableHeader.header = ResChunkHeader.parse(inputStream)
            val byteArray = ByteArray(4)
            inputStream.read(byteArray)
            resTableHeader.packageCount = ByteUtil.bytes2Int(byteArray, 0, 4)
            println(resTableHeader.toString())
            return resTableHeader
        }
    }

    /**
     * chunk header, 8 bytes
     */
    lateinit var header: ResChunkHeader
    /**
     * package count, 4 bytes, default is 1
     */
    var packageCount = 1

    override fun toString(): String {
        return StringBuilder().append("ResTableHeader:").append('\n').append(
                header.toString()).append('\n').append(
                "package count     : $packageCount").toString()
    }
}