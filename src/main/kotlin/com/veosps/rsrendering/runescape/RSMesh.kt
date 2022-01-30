package com.veosps.rsrendering.runescape

import com.veosps.rsrendering.opengl.graph.Texture
import com.veosps.rsrendering.random.InformativeTriangle
import com.veosps.rsrendering.runescape.cache.texLoader
import com.veosps.rsrendering.runescape.definitions.Model
import com.veosps.rsrendering.runescape.definitions.extensions.computeTextureUVCoordinates
import org.joml.Vector3f
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.lang.Math.pow
import java.util.stream.Collectors
import java.util.stream.IntStream
import javax.imageio.ImageIO
import kotlin.math.*

data class RSMesh(
    var material: Texture? = null,
    val points: MutableList<Float> = mutableListOf(),
    val faces: MutableList<Int> = mutableListOf(),
    val textureCoordinates: MutableList<Float> = mutableListOf(),
    val vertexMap: MutableMap<Int, Int> = mutableMapOf(),
    val triangleMap: MutableMap<Int, Int> = mutableMapOf(),
    val triangles: MutableList<InformativeTriangle> = mutableListOf()
) {

    fun addVertex(vertex: Int, x: Int, y: Int, z: Int): Int {
        val onset = points.size / 3
        points.addAll(listOf(x.toFloat(), y.toFloat(), z.toFloat()))
        vertexMap.putIfAbsent(vertex, onset)
        return vertexMap[vertex] ?: 0
    }

    fun addUV(u: Float, v: Float): Int {
        val onset = textureCoordinates.size / 2
        textureCoordinates.addAll(listOf(u, v))
        return onset
    }

    fun addTriangle(triangle: Int) {
        triangleMap.putIfAbsent(triangle, triangleMap.size * 9)
    }

    fun verticesToFloatArray(): FloatArray {
        val vertices = FloatArray(triangles.size * 3)

        for (vertex in 0 until triangles.size) {

        }

        return floatArrayOf()
    }

    fun indicesToIntArray(): IntArray {
        return intArrayOf()
    }

    fun textureCoordinatesToFloatArray(): FloatArray {
        return floatArrayOf()
    }
}
