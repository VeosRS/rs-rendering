package com.veosps.rsrendering.utils

import org.lwjgl.system.MemoryUtil
import java.awt.image.BufferedImage
import java.nio.Buffer
import java.nio.ByteBuffer

fun Any.loadResourceDataAsString(fileName: String) =
    this.javaClass.getResourceAsStream(fileName)
        ?.readAllBytes()
        ?.decodeToString() ?: error("File not found: $fileName")


fun Any.loadResourceData(fileName: String) =
    this.javaClass.getResourceAsStream(fileName)?.readAllBytes() ?: error("File not found.")


fun radians(input: Float) = Math.toRadians(input.toDouble())

fun sinF(input: Double): Float = kotlin.math.sin(input.toFloat())
fun cosF(input: Double): Float = kotlin.math.cos(input.toFloat())

fun Buffer?.freeFromMemory() {
    if (this == null) return
    MemoryUtil.memFree(this)
}

fun BufferedImage.getRGBA(x: Int, y: Int): ByteArray {
    val argbPixel = getRGB(x, y)
    val rgba = ByteArray(4)
    rgba[0] = ((argbPixel shr 16) and 0xFF).toByte()
    rgba[1] = ((argbPixel shr 8) and 0xFF).toByte()
    rgba[2] = (argbPixel and 0xFF).toByte()
    rgba[3] = ((argbPixel shr 24) and 0xFF).toByte()
    return rgba
}

