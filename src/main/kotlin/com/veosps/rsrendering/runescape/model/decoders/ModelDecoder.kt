package com.veosps.rsrendering.runescape.model.decoders

import com.veosps.rsrendering.runescape.model.Model

interface ModelDecoder {
    val data: ByteArray
    fun decode(modelId: Int): Model
}