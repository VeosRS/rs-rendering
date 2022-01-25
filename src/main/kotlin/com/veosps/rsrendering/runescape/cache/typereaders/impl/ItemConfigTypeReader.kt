package com.veosps.rsrendering.runescape.cache.typereaders.impl

import com.veosps.rsrendering.runescape.cache.typereaders.TypeReader
import com.veosps.rsrendering.runescape.cache.types.impl.ItemConfigType
import io.netty.buffer.ByteBuf

class ItemConfigTypeReader : TypeReader

fun ByteBuf.read(id: Int): ItemConfigType {
    val type = ItemConfigType()
    while (isReadable) {
        val instruction = readUnsignedByte().toInt()
        if (instruction == 0) break

        type.readBuffer(instruction, this)
    }
    return type
}

//fun ByteBuf.read(id: Int): ItemConfigType {
//    val type = ItemConfigType()
//    while (isReadable) {
//        val instruction = readUnsignedByte().toInt()
//        if (instruction == 0) break
//
//        with(type) {
//
//        }
//    }
//
//    return type
//}

