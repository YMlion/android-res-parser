package com.ymlion.parser.head

import com.ymlion.parser.util.ByteUtil
import java.io.InputStream
import java.io.RandomAccessFile

/**
 * package header, 288 bytes
 *
 * Created by YMlion on 2018/4/18.
 */
class ResPackageHeader() {
    constructor(file: RandomAccessFile) : this() {
        header = ResHeader(file)
        val byteArray = ByteArray(4)
        file.read(byteArray)
        id = ByteUtil.bytes2Int(byteArray, 0, 4)
        val stringBytes = ByteArray(256)
        file.read(stringBytes)
        val builder = StringBuilder()
        for (i in 0..255 step 2) {
            val char = ByteUtil.bytes2Int(stringBytes, i, 2)
            if (char == 0) {
                break
            }
            builder.append(char.toChar())
        }
        name = builder.toString()
        file.read(byteArray)
        typeStrings = ByteUtil.bytes2Int(byteArray, 0, 4)
        file.read(byteArray)
        lastPublicType = ByteUtil.bytes2Int(byteArray, 0, 4)
        file.read(byteArray)
        keyStrings = ByteUtil.bytes2Int(byteArray, 0, 4)
        file.read(byteArray)
        lastPublicKey = ByteUtil.bytes2Int(byteArray, 0, 4)
        val available = header.headSize - 284
        file.skipBytes(available)
    }

    companion object {
        public fun parse(inputStream: InputStream): ResPackageHeader {
            val packageHeader = ResPackageHeader()
            packageHeader.header = ResHeader.parse(inputStream)
            val byteArray = ByteArray(4)
            inputStream.read(byteArray)
            packageHeader.id = ByteUtil.bytes2Int(byteArray, 0, 4)
            val stringBytes = ByteArray(256)
            inputStream.read(stringBytes)
            val builder = StringBuilder()
            for (i in 0..255 step 2) {
                val char = ByteUtil.bytes2Int(stringBytes, i, 2)
                if (char == 0) {
                    break
                }
                builder.append(char.toChar())
            }
            packageHeader.name = builder.toString()
            inputStream.read(byteArray)
            packageHeader.typeStrings = ByteUtil.bytes2Int(byteArray, 0, 4)
            inputStream.read(byteArray)
            packageHeader.lastPublicType = ByteUtil.bytes2Int(byteArray, 0, 4)
            inputStream.read(byteArray)
            packageHeader.keyStrings = ByteUtil.bytes2Int(byteArray, 0, 4)
            inputStream.read(byteArray)
            packageHeader.lastPublicKey = ByteUtil.bytes2Int(byteArray, 0, 4)
            val available = packageHeader.header.headSize - 284
            if (available > 0) {
                inputStream.read(ByteArray(available))
            }
            println(packageHeader)
            return packageHeader
        }
    }

    /**
     * chunk header, 8 bytes
     */
    lateinit var header: ResHeader
    /**
     * package id, 应用默认为0x7F，系统应用为0x01
     */
    var id = 0x7f
    /**
     * package name，256 bytes, 除去字符为以0x00填充
     */
    lateinit var name: String
    /**
     * 资源类型字符串池相对于头部的偏移，4 bytes
     */
    var typeStrings = 0
    /**
     * 资源类型种数，4 bytes
     */
    var lastPublicType = 0
    /**
     * 资源名称字符串池相对于头部偏移，4 bytes
     */
    var keyStrings = 0
    /**
     * 资源名称数量
     */
    var lastPublicKey = 0

    override fun toString(): String {
        return StringBuilder().append("ResPackageHeader:").append('\n').append(
                header.toString()).append('\n').append("package id        : $id").append(
                '\n').append("package name      : $name").append('\n').append(
                "type offset       : $typeStrings").append('\n').append(
                "type num          : $lastPublicType").append('\n').append(
                "key offset        : $keyStrings").append('\n').append(
                "key num           : $lastPublicKey").toString()
    }

}