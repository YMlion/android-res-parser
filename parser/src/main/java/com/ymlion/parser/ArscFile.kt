package com.ymlion.parser

import com.ymlion.parser.entry.ResTableEntry
import com.ymlion.parser.entry.ResTableEntry.ResMapEntry
import com.ymlion.parser.entry.StringPoolString
import com.ymlion.parser.entry.StringPoolStyle
import com.ymlion.parser.head.ResChunkHeader
import com.ymlion.parser.head.ResPackageHeader
import com.ymlion.parser.head.ResStringPoolHeader
import com.ymlion.parser.head.ResTableHeader
import com.ymlion.parser.head.ResTypeHeader
import com.ymlion.parser.head.ResTypeSpecHeader
import com.ymlion.parser.util.ByteUtil
import java.io.File
import java.io.InputStream

/**
 * Created by YMlion on 2018/4/18.
 */
class ArscFile(file: File) : ResFile(file) {

    constructor(fileName: String) : this(File(fileName))

    override fun parse(): Boolean {
        println("start parse ${mFile.name}")
        val tableHeader = parseTableHeader()
        // 解析全局字符串资源值，字符串池存放所有资源的值，并且该值为字符串
        // 像所有的xml文件名、图片资源名、xml中定义的字符串、系统定义的字符串
        parseStringPool()
        val packageHeader = parsePackageHeader()
        // package chunk后面是资源类型字符串池和资源名称字符串池
        // 这两部分的结构和和前面的字符串池结构相同，只不过字符串样式数量为0
        // 先解析资源类型字符串池，资源类型有限，一般十多种
        parseStringPool()
        // 解析资源名称字符串池，资源定义中的属性(key)字符串存放在这里，值(value)有一部分存放在全局字符串池中，非字符串则存放在后面的entry中
        parseStringPool()
        // 后面同样属于package部分，根据资源类型数量，分别解析，直到全部解析完
        var resHeader = ResChunkHeader(mInput)
        // 有多少资源类型，后面就有多少 type spec
        for (i in 1..packageHeader.lastPublicType) {
            val specHeader = ResTypeSpecHeader(mInput, resHeader)
            // spec 资源数组
            mInput.skipBytes(4 * specHeader.entryCount)

            resHeader = ResChunkHeader(mInput)
            while (resHeader.type == 0x0201) {// 每种类型的资源可以有多种配置
                val typeHeader = ResTypeHeader(mInput, resHeader)
                val chunkEnd = mInput.filePointer + typeHeader.header.size - typeHeader.header.headSize
                // entry偏移数组
                mInput.skipBytes(typeHeader.entryCount * 4)
                // 读取资源项
                while (mInput.filePointer < chunkEnd) {// 一般情况下，会有entryCount个entry，但实际情况下，有可能是没有这么多的，所以根据整个type块的大小来确定是否读取完
                    val tableEntry = ResMapEntry(mInput)
                }
                if (tableHeader.header.size == mInput.filePointer.toInt()) {
                    break
                }
                resHeader = ResChunkHeader(mInput)
            }
        }
        val available = mInput.read() == -1
        if (available) {
            println("解析完毕.")
        } else {
            println("解析失败，还剩 ${mInput.length() - mInput.filePointer + 1} 字节")
        }
        mInput.close()

        return available
    }

    private fun parsePackageHeader(): ResPackageHeader {
        // 解析package header
        val packageHeader = ResPackageHeader(mInput)
        return packageHeader
    }

    private fun parseTableHeader(): ResTableHeader {
        val tableHeader = ResTableHeader(mInput).takeIf {
            it.header.type == ResChunkHeader.RES_TABLE_TYPE
        }
        checkNotNull(tableHeader)
        // length()方法获取的大小和实际大小有可能不同
        mInput.setLength(tableHeader?.header!!.size.toLong())
        return tableHeader
    }

    override fun resetPackageId(newId: Int): Boolean {
        // skip table header
        val tableHeader = ResTableHeader(mInput)
        assert(tableHeader.header.type == 2)
        // skip global string pool
        skipStringPool()
        val pkgHeader = ResPackageHeader(mInput)
        // find package id pointer
        mInput.seek(mInput.filePointer - pkgHeader.header.headSize + 8)
        mInput.write(newId)
        // current at 9
        mInput.skipBytes(pkgHeader.header.headSize - 9)
        // skip res type
        skipStringPool()
        // skip res name
        skipStringPool()
        // 后面同样属于package部分，根据资源类型数量，分别解析，直到全部解析完
        var resHeader = ResChunkHeader(mInput)
        // 有多少资源类型，后面就有多少 type spec
        for (i in 1..pkgHeader.lastPublicType) {
            val specHeader = ResTypeSpecHeader(mInput, resHeader)
            // spec 资源数组
            mInput.skipBytes(4 * specHeader.entryCount)

            resHeader = ResChunkHeader(mInput)
            while (resHeader.type == 0x0201) {// 每种类型的资源可以有多种配置
                val typeHeader = ResTypeHeader(mInput, resHeader)
                val chunkEnd = mInput.filePointer + typeHeader.header.size - typeHeader.header.headSize
                // entry偏移数组
                mInput.skipBytes(typeHeader.entryCount * 4)
                // 读取资源项
                while (mInput.filePointer < chunkEnd) {// 一般情况下，会有entryCount个entry，但实际情况下，有可能是没有这么多的，所以根据整个type块的大小来确定是否读取完
                    ResMapEntry(mInput, newId)
                }
                if (tableHeader.header.size == mInput.filePointer.toInt()) {
                    break
                }
                resHeader = ResChunkHeader(mInput)
            }
        }
        val available = mInput.read() == -1
        if (available) {
            println("更新 package id 完毕.")
        } else {
            println("更新id失败，还剩 ${mInput.length() - mInput.filePointer + 1} 字节")
        }
        mInput.close()

        return available
    }

    companion object {
        public fun parse(input: InputStream) {
            var tableHeader = ResTableHeader.parse(input)
            // 解析全局字符串资源值，字符串池存放所有资源的值，并且该值为字符串
            // 像所有的xml文件名、图片资源名、xml中定义的字符串、系统定义的字符串
            parseStringPool(input)
            // 解析package header
            val packageHeader = ResPackageHeader.parse(input)
            // package chunk后面是资源类型字符串池和资源名称字符串池
            // 这两部分的结构和和前面的字符串池结构相同，只不过字符串样式数量为0
            // 先解析资源类型字符串池，资源类型有限，一般十多种
            parseStringPool(input)
            // 解析资源名称字符串池，资源定义中的属性(key)字符串存放在这里，值(value)有一部分存放在全局字符串池中，非字符串则存放在后面的entry中
            parseStringPool(input)
            val offBytes = ByteArray(4)
            // 后面同样属于package部分，根据资源类型数量，分别解析，直到全部解析完
            var resHeader = ResChunkHeader.parse(input)
            // 有多少资源类型，后面就有多少 type spec
            for (j in 1..packageHeader.lastPublicType) {
                val specHeader = ResTypeSpecHeader.parse(input, resHeader)
                // spec 资源数组
                for (i in 0 until specHeader.entryCount) {
                    input.read(offBytes)
                }
                resHeader = ResChunkHeader.parse(input)
                while (resHeader.type == 0x0201) {// 每种类型的资源可以有多种配置
                    val typeHeader = ResTypeHeader.parse(input, resHeader)
                    var total = typeHeader.header.headSize
                    // entry偏移数组
                    for (i in 0 until typeHeader.entryCount) {
                        input.read(offBytes)
                    }
                    total += 4 * typeHeader.entryCount
                    // 读取资源项
                    while (total < typeHeader.header.size) {// 一般情况下，会有entryCount个entry，但实际情况下，有可能是没有这么多的，所以根据整个type块的大小来确定是否读取完
                        val tableEntry = ResTableEntry.parse(input)
                        total += if (tableEntry is ResMapEntry) {
                            16 + 12 * tableEntry.count
                        } else {
                            16
                        }
                    }
                    resHeader = ResChunkHeader.parse(input)
                }
            }
            val available = input.available()
            if (available == 0) {
                println("解析完毕.")
            } else {
                println("解析失败，还剩 $available 字节")
            }
        }

        private fun parseStringPool(input: InputStream) {
            val stringPoolHeader = ResStringPoolHeader.parse(input)
            // 结束的位置
            val endPosition = input.available() - stringPoolHeader.header.size + stringPoolHeader.header.headSize
            // 字符串偏移数组
            val stringOffsets = IntArray(stringPoolHeader.stringCount)
            val offBytes = ByteArray(4)
            for (i in 0 until stringPoolHeader.stringCount) {
                input.read(offBytes)
                stringOffsets[i] = ByteUtil.bytes2Int(offBytes, 0, 4)
            }
            // 字符串样式偏移数组
            val styleOffsets = IntArray(stringPoolHeader.styleCount)
            for (i in 0 until stringPoolHeader.styleCount) {
                input.read(offBytes)
                styleOffsets[i] = ByteUtil.bytes2Int(offBytes, 0, 4)
            }
            var total = 0
            // 开始读取字符串
            for (i in 0 until stringPoolHeader.stringCount) {
                val string = StringPoolString.parse(input, stringPoolHeader.flags)
                total += string.bytesNum
                println("$i  ${string.content}")
            }
            // 4字节对齐
            val makeUp = 4 - total % 4
            if (makeUp < 4) {
                skip(input, makeUp)
            }
            // 读取字符串样式
            for (i in 0 until stringPoolHeader.styleCount) {
                StringPoolStyle.parse(input)
            }
            // style数组以8字节0xFF作为结尾
            if (stringPoolHeader.styleCount > 0) {
                skip(input, 8)
            }
            val dif = input.available() - endPosition
            if (dif > 0) {// 解析完该部分之后，有可能有4个0x00结尾
                skip(input, dif)
            }
        }

        private fun skip(input: InputStream, num: Int) {
            input.read(ByteArray(num))
        }
    }

}