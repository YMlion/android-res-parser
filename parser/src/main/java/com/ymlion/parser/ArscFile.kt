package com.ymlion.parser

import java.io.InputStream

/**
 * Created by YMlion on 2018/4/18.
 */
public class ArscFile {
    companion object {
        public fun parse(input: InputStream) {
            var tableHeader = ResTableHeader.parse(input)
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
            // 开始读取字符串
            for (i in 0 until stringPoolHeader.stringCount) {
                var string = StringPoolString.parse(input, stringPoolHeader.flags)
            }
            // 读取字符串样式
            for (i in 0 until stringPoolHeader.styleCount) {
                var style = StringPoolStyle.parse(input)
            }
            // style数组以8字节0xFF作为结尾
            if (stringPoolHeader.styleCount > 0) {
                input.read(ByteArray(8))
            } else {// 只有字符串数组时，最后有一个0x00作为结尾，否则没有
                input.read()
            }
            // 解析package header
            val packageHeader = ResPackageHeader.parse(input)

        }
    }

}