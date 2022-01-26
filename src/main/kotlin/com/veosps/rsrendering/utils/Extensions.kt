package com.veosps.rsrendering.utils

import org.lwjgl.system.MemoryUtil
import java.nio.Buffer

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