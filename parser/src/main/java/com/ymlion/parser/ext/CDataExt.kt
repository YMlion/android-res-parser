package com.ymlion.parser.ext

import com.ymlion.parser.entry.ResValue
import com.ymlion.parser.util.ByteUtil
import java.io.RandomAccessFile

/**
 * 12 bytes，自定义的xml文件中只含有数据项的node值
 * <p>
 *     像<item>hello world</item>这种，其中"hello world"部分就是CDATA类型
 * </p>
 *
 * Created by YMlion on 2018/4/28.
 */
internal class CDataExt() {
    constructor(input: RandomAccessFile) : this() {
        val bytes = ByteArray(4)
        input.read(bytes)
        data = ByteUtil.bytes2Int(bytes, 0, 4)
        typedData = ResValue(input)
    }

    /**
     * the raw data index
     */
    var data = -1
    /**
     * 解析后的数据
     */
    lateinit var typedData: ResValue
}