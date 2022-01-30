@file:Suppress("ArrayInDataClass")

package com.veosps.rsrendering.runescape.definitions.osrs

data class Sprite(
    var id: Int = -1,
    var frame: Int = -1,
    var offsetX: Int = -1,
    var offsetY: Int = -1,
    var width: Int = -1,
    var height: Int = -1,
    var pixels: IntArray = intArrayOf(),
    var maxWidth: Int = -1,
    var maxHeight: Int = -1,
    var pixelIdx: ByteArray = byteArrayOf(),
    var palette: IntArray = intArrayOf()
)