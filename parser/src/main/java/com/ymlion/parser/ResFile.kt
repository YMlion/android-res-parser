package com.ymlion.parser

import com.ymlion.parser.entry.StringPoolString
import com.ymlion.parser.entry.StringPoolStyle
import com.ymlion.parser.head.ResStringPoolHeader
import java.io.File
import java.io.RandomAccessFile

/**
 * android res file: xml and arsc
 *
 * @param mFile binary res file
 *
 * Created by YMlion on 2018/4/28.
 */
abstract class ResFile(protected var mFile: File) {
    constructor(filePath: String) : this(File(filePath))

    protected var mInput: RandomAccessFile = RandomAccessFile(mFile, "rw")

    /**
     * parse the binary res file
     */
    abstract fun parse(): Boolean

    /**
     * reset package id, like [parse]
     *
     * @param newId new package id
     */
    abstract fun resetPackageId(newId: Int): Boolean

    /**
     * parse string pool
     */
    protected fun parseStringPool() {
        parseStringPool(arrayListOf())
    }
    /**
     * parse string pool
     *
     * @param
     */
    protected fun parseStringPool(strings: MutableList<String>) {
        // 开始位置
        val startPosition = mInput.filePointer
        // 解析头部
        val stringPoolHeader = ResStringPoolHeader(mInput).apply {
            // 字符串偏移数组
            // 字符串样式偏移数组
            mInput.skipBytes((stringCount + styleCount) * 4)
        }
        // 开始读取字符串
        for (i in 0 until stringPoolHeader.stringCount) {
            val string = StringPoolString(mInput, stringPoolHeader.flags)
//            println("$i  ${string.content}")
            strings.add(string.content)
        }
        // 4字节对齐
        val makeUp = 4 - mInput.filePointer % 4
        if (makeUp < 4) {
            mInput.skipBytes(makeUp.toInt())
        }
        // 读取字符串样式
        for (i in 0 until stringPoolHeader.styleCount) {
            StringPoolStyle(mInput)
        }
        // style数组以8字节0xFF作为结尾
        if (stringPoolHeader.styleCount > 0) {
            mInput.skipBytes(8)
        }
        val dif = startPosition + stringPoolHeader.header.size - mInput.filePointer
        if (dif > 0) {// 解析完该部分之后，有可能有4个0x00结尾
            mInput.skipBytes(dif.toInt())
        }
    }

    /**
     * skip string pool when reset package id
     */
    protected fun skipStringPool() {
        with(ResStringPoolHeader(mInput)) {
            mInput.skipBytes(header.size - header.headSize)
        }
    }

    /**
     * close resource
     */
    fun close() {
        mInput.close()
        mInput.close()
    }
}