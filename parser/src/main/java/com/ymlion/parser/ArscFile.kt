package com.ymlion.parser

import com.ymlion.parser.ResTableEntry.ResMapEntry
import java.io.InputStream

/**
 * Created by YMlion on 2018/4/18.
 */
public class ArscFile {
    companion object {
        public fun parse(input: InputStream) {
            var tableHeader = ResTableHeader.parse(input)
            parseStringPool(input)
            // 解析package header
            val packageHeader = ResPackageHeader.parse(input)
            // package head之后以4个0x00分割
            skip(input, 4)
            // package chunk后面是资源类型字符串池和资源名称字符串池
            // 这两部分的结构和和前面的字符串池结构相同，只不过字符串样式数量为0
            // 先解析资源类型字符串池
            parseStringPool(input)
            // 解析资源名称字符串池
            parseStringPool(input)
            val offBytes = ByteArray(4)
            // 后面同样属于package部分，更加资源类型数量，分别解析，直到全部解析完
            var resHeader = ResHeader.parse(input)
            // 有多少资源类型，后面就有多少 type spec
            for (j in 1..packageHeader.lastPublicType) {
                val specHeader = ResTypeSpecHeader.parse(input, resHeader)
                // spec 资源数组
                for (i in 0 until specHeader.entryCount) {
                    input.read(offBytes)
                }
                resHeader = ResHeader.parse(input)
                while (resHeader.type == 0x0201) {// 每种类型的资源可以有多种配置
                    val typeHeader = ResTypeHeader.parse(input, resHeader)
                    var total = typeHeader.header!!.headSize
                    // entry偏移数组
                    for (i in 0 until typeHeader.entryCount) {
                        input.read(offBytes)
                    }
                    total += 4 * typeHeader.entryCount
                    // 读取资源项
                    while (total < typeHeader.header!!.size) {// 一般情况下，会有entryCount个entry，但实际情况下，有可能是没有这么多的，所以根据整个type块的大小来确定是否读取完
                        val tableEntry = ResTableEntry.parse(input)
                        total += if (tableEntry is ResMapEntry) {
                            16 + 12 * tableEntry.count
                        } else {
                            16
                        }
                    }
                    resHeader = ResHeader.parse(input)
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
            }
            // 4字节对齐
            val makeUp = 4 - total % 4
            if (makeUp < 4) {
                skip(input, makeUp)
            }
            // 读取字符串样式
            for (i in 0 until stringPoolHeader.styleCount) {
                var style = StringPoolStyle.parse(input)
            }
            // style数组以8字节0xFF作为结尾
            if (stringPoolHeader.styleCount > 0) {
                skip(input, 8)
            }
        }

        private fun skip(input: InputStream, num: Int) {
            input.read(ByteArray(num))
        }
    }

}