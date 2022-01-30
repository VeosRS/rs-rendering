package com.veosps.rsrendering.runescape.decoders.model

import com.veosps.rsrendering.runescape.definitions.Model
import io.netty.buffer.Unpooled

interface Decoder<T> {
    val data: ByteArray
    fun decode(id: Int): T
}

class ModelDecoder(val data: ByteArray) {

    fun decode(id: Int): Model {
        val buffer = Unpooled.wrappedBuffer(data)

        //val model1 = io.guthix.oldscape.cache.model.Model.decode(id, buffer)
        return if (data[data.size - 1].toInt() == -1 && data[data.size - 2].toInt() == -1) {
            modelDecoderV2(Model(id), buffer)
        } else {
            modelDecoderV1(Model(id), buffer)
        }
    }
}