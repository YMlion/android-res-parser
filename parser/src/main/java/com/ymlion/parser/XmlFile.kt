package com.ymlion.parser

import com.ymlion.parser.entry.Attribute
import com.ymlion.parser.ext.AttrExt
import com.ymlion.parser.ext.CDataExt
import com.ymlion.parser.ext.EndElementExt
import com.ymlion.parser.ext.NamespaceExt
import com.ymlion.parser.head.ResChunkHeader
import com.ymlion.parser.head.ResTreeNodeHeader
import java.io.File

/**
 * parse binary xml files and reset package id
 *
 * @param file binary xml file
 *
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
        var nodeHeader = ResTreeNodeHeader(mInput, chunkHeader)
        // 开始解析namespace，该部分是前后对应的，start with this namespace, then end with this too.
        while (nodeHeader.header.type == ResChunkHeader.RES_XML_START_NAMESPACE_TYPE) {
            val nsExt = NamespaceExt(mInput)
            println("namespace xmlns:${stringPool[nsExt.prefix]}=\"${stringPool[nsExt.uri]}\" start")
            // namespace有可能并不止一个
            nodeHeader = ResTreeNodeHeader(mInput)
        }
        // 开始解析具体node，同样每个node都是前后对应的，有开始就有结束，并且node里面可以包含node
        while (nodeHeader.header.type == ResChunkHeader.RES_XML_START_ELEMENT_TYPE) {
            val attrExt = AttrExt(mInput)
            println("${stringPool[attrExt.name]} node start:")
            for (i in 0 until attrExt.attributeCount) {
                val attr = Attribute(mInput)
                if (attr.name >= 0 && attr.name < stringPool.size) {
                    println("${stringPool[attr.name]} : ${attr.typedValue.data}")
                }
            }
            nodeHeader = ResTreeNodeHeader(mInput)
            // 在没有attribute时，如果该node仅仅是一项数据，则是CDATA类型
            // 在android资源打包过程中，values类型的xml文件是不会被编译为二进制xml文件的，其他类型的xml文件没有这
            // 种格式的节点，所以正常情况下是不会有这种类型的数据，但自定义的xml文件就有可能包含了。
            if (nodeHeader.header.type == ResChunkHeader.RES_XML_CDATA_TYPE) {
                val cDataExt = CDataExt(mInput)
                println("data is ${stringPool[cDataExt.data]}")
                nodeHeader = ResTreeNodeHeader(mInput)
            }
            // node结束有可能并不止一个
            while (nodeHeader.header.type == ResChunkHeader.RES_XML_END_ELEMENT_TYPE) {
                val endElementExt = EndElementExt(mInput)
                println("${stringPool[endElementExt.name]} end.")
                if (mInput.filePointer == xmlHeader!!.size.toLong()) {
                    println("解析结束。")
                    mInput.close()
                    return true
                }
                nodeHeader = ResTreeNodeHeader(mInput)
            }
        }

        // 解析与前面对应的namespace结束
        while (nodeHeader.header.type == ResChunkHeader.RES_XML_END_NAMESPACE_TYPE) {
            val nsExt = NamespaceExt(mInput)
            println("namespace xmlns:${stringPool[nsExt.prefix]}=\"${stringPool[nsExt.uri]}\" end.")
            if (mInput.filePointer == xmlHeader!!.size.toLong()) {
                break
            }
            // namespace有可能并不止一个
            nodeHeader = ResTreeNodeHeader(mInput)
        }
        println("解析结束。")
        mInput.close()
        return true
    }

    override fun resetPackageId(newId: Int): Boolean {
        println("replace ${mFile.name} original package id 0x7f to $newId")

        val xmlHeader = ResChunkHeader(mInput).takeIf {
            it.type == ResChunkHeader.RES_XML_TYPE
        }
        checkNotNull(xmlHeader)

        skipStringPool()

        var chunkHeader = ResChunkHeader(mInput)
        if (chunkHeader.type == ResChunkHeader.RES_XML_RESOURCE_MAP_TYPE) {// 该部分并不是必须存在的
            // 资源id，属于xml中具有资源id的属性，像系统属性layout_width等和自定义属性，系统属性id值固定
            // 每个id有4字节
            val count = (chunkHeader.size - chunkHeader.headSize) / 4
            var bytes = ByteArray(4)
            for (i in 0 until count) {
                mInput.read(bytes)
                if (bytes[3] == 0x7f.toByte()) {
                    mInput.seek(mInput.filePointer - 1)
                    mInput.write(newId)
                }
            }
            println("属性资源共有${count}个")
            chunkHeader = ResChunkHeader(mInput)
        }

        // 开始解析namespace，该部分是前后对应的，start with this namespace, then end with this too.
        while (chunkHeader.type == ResChunkHeader.RES_XML_START_NAMESPACE_TYPE) {
            mInput.skipBytes(16)
            // namespace有可能并不止一个
            chunkHeader = ResChunkHeader(mInput)
        }

        // 开始解析具体node，同样每个node都是前后对应的，有开始就有结束，并且node里面可以包含node
        while (chunkHeader.type == ResChunkHeader.RES_XML_START_ELEMENT_TYPE) {
            mInput.skipBytes(8)
            val attrExt = AttrExt(mInput)
            for (i in 0 until attrExt.attributeCount) {
                mInput.skipBytes(19)
                if (mInput.read() == 0x7f) {
                    mInput.seek(mInput.filePointer - 1)
                    mInput.write(newId)
                }
            }
            chunkHeader = ResChunkHeader(mInput)
            // 在没有attribute时，如果该node仅仅是一项数据，则是CDATA类型
            // 在android资源打包过程中，values类型的xml文件是不会被编译为二进制xml文件的，其他类型的xml文件没有这
            // 种格式的节点，所以正常情况下是不会有这种类型的数据，但自定义的xml文件就有可能包含了。
            if (chunkHeader.type == ResChunkHeader.RES_XML_CDATA_TYPE) {
                mInput.skipBytes(20)
                chunkHeader = ResChunkHeader(mInput)
            }
            // node结束有可能并不止一个
            while (chunkHeader.type == ResChunkHeader.RES_XML_END_ELEMENT_TYPE) {
                mInput.skipBytes(16)
                if (mInput.filePointer == xmlHeader!!.size.toLong()) {
                    println("replace finish.")
                    mInput.close()
                    return true
                }
                chunkHeader = ResChunkHeader(mInput)
            }
        }

        // 解析与前面对应的namespace结束
        while (chunkHeader.type == ResChunkHeader.RES_XML_END_NAMESPACE_TYPE) {
            mInput.skipBytes(16)
            if (mInput.filePointer == xmlHeader!!.size.toLong()) {
                break
            }
            // namespace有可能并不止一个
            chunkHeader = ResChunkHeader(mInput)
        }

        println("replace finish.")
        mInput.close()
        return true
    }

}