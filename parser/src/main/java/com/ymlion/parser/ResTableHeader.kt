package com.ymlion.parser

import java.io.InputStream

/**
 * table header, 12 bytes
 *
 * Created by YMlion on 2018/4/17.
 */
class ResTableHeader {

    companion object {
        public fun read(inputStream: InputStream): ResTableHeader {
            val resTableHeader = ResTableHeader()
            resTableHeader.header = ResHeader.read(inputStream)
            val byteArray = ByteArray(4)
            inputStream.read(byteArray)
            resTableHeader.packageCount = ByteUtil.bytes2Int(byteArray, 0, 4)
            return resTableHeader
        }
    }

    /**
     * chunk header, 8 bytes
     */
    var header: ResHeader? = null
    /**
     * package count, 4 bytes, default is 1
     */
    var packageCount = 1
}