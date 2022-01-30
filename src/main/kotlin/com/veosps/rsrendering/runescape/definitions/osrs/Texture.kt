@file:Suppress("ArrayInDataClass")

package com.veosps.rsrendering.runescape.definitions.osrs

data class Texture(
    var id: Int,
    var fileIds: IntArray = intArrayOf()
)