package com.ymlion.parser

import com.ymlion.parser.entry.Attribute
import com.ymlion.parser.ext.AttrExt
import com.ymlion.parser.ext.EndElementExt
import com.ymlion.parser.ext.NamespaceExt
import com.ymlion.parser.head.ResChunkHeader
import com.ymlion.parser.head.ResTreeNodeHeader
import java.io.File
import kotlin.math.min

/**
 * parse binary xml files and reset package id
 *
 * @param file binary xml file
 * Created by YMlion on 2018/4/28.
 */
class XmlFile(file: File) : ResFile(file) {

    constructor(filePath: String) : this(File(filePath))

    private val stringPool = arrayListOf<String>()

    override fun parse(): Boolean {
        println("start parse ${mFile.name}")
        val xmlHeader = ResChunkHeader(mInput).takeIf {
            it.type == ResChunkHeader.RES_XML_TYPE
        }?.apply { println(this) }
        checkNotNull(xmlHeader)
        // 字符串池，xml中用的的字符串
        parseStringPool(stringPool)

        var chunkHeader = ResChunkHeader(mInput)
        if (chunkHeader.type == ResChunkHeader.RES_XML_RESOURCE_MAP_TYPE) {// 该部分并不是必须存在的
            // 资源id，属于xml中具有资源id的属性，像系统属性layout_width等和自定义属性，系统属性id值固定
            // 每个id有4字节
            mInput.skipBytes(chunkHeader.size - chunkHeader.headSize)
            println("属性资源共有${(chunkHeader.size - chunkHeader.headSize) / 4}个")
            chunkHeader = ResChunkHeader(mInput)
        }
        while (chunkHeader.type == ResChunkHeader.RES_XML_START_NAMESPACE_TYPE) {
            // 开始解析namespace，该部分是前后对应的，start with this namespace, then end with this too.
            ResTreeNodeHeader(mInput, chunkHeader)
            val nsExt = NamespaceExt(mInput)
            println("namespace xmlns:${stringPool[nsExt.prefix]}=\"${stringPool[nsExt.uri]}\" start")
            // namespace有可能并不止一个
            chunkHeader = ResChunkHeader(mInput)
        }
        // 开始解析具体node，同样每个node都是前后对应的，有开始就有结束，并且node里面可以包含node
        while (chunkHeader.type == ResChunkHeader.RES_XML_START_ELEMENT_TYPE) {
            // 开始解析namespace，该部分是前后对应的，start with this namespace, then end with this too.
            ResTreeNodeHeader(mInput, chunkHeader)
            val attrExt = AttrExt(mInput)
            println("${stringPool[attrExt.name]} node start:")
            for (i in 1..attrExt.attributeCount) {
                val attr = Attribute(mInput)
                if (attr.name >= 0 && attr.name < stringPool.size) {
                    println("${stringPool[attr.name]} : ${attr.typedValue.data}")
                }
            }
            // namespace有可能并不止一个
            chunkHeader = ResChunkHeader(mInput)
            while (chunkHeader.type == ResChunkHeader.RES_XML_END_ELEMENT_TYPE) {
                ResTreeNodeHeader(mInput, chunkHeader)
                val endElementExt = EndElementExt(mInput)
                println("${stringPool[endElementExt.name]} end.")
                chunkHeader = ResChunkHeader(mInput)
            }
        }
        // todo cdata类型解析

        // 解析与前面对应的namespace结束
        while (chunkHeader.type == ResChunkHeader.RES_XML_END_NAMESPACE_TYPE) {
            // 开始解析namespace，该部分是前后对应的，start with this namespace, then end with this too.
            ResTreeNodeHeader(mInput, chunkHeader)
            val nsExt = NamespaceExt(mInput)
            println("namespace xmlns:${stringPool[nsExt.prefix]}=\"${stringPool[nsExt.uri]}\" end.")
            if (mInput.filePointer == xmlHeader!!.size.toLong()) {
                break
            }
            // namespace有可能并不止一个
            chunkHeader = ResChunkHeader(mInput)
        }
        println("解析结束。")
        mInput.close()
        return true
    }

    override fun resetPackageId(newId: Int): Boolean {
        return true
    }

}