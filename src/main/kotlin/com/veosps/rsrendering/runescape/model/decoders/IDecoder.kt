package com.veosps.rsrendering.runescape.model.decoders

import com.veosps.rsrendering.runescape.model.ModelData

interface IDecoder {
    fun decode(): ModelData
}