@file:Suppress("ArrayInDataClass")

package com.veosps.rsrendering.runescape.definitions

import com.veosps.rsrendering.runescape.definitions.extensions.computeTextureUVCoordinates

/**
 * Model to house decoded data from a 3D model in the RuneScape cache.
 */
data class Model(
    var modelId: Int = -1,
    var version: Int = -1,
    var textureUCoordinates: Array<FloatArray> = arrayOf(),
    var textureVCoordinates: Array<FloatArray> = arrayOf(),
    var faceMaterial: ShortArray = shortArrayOf(),
    var faceTexture: ShortArray = shortArrayOf(),
    var textureMap: ShortArray = shortArrayOf(),
    var faceTextureMasks: ByteArray = byteArrayOf(),
    var vertexCount: Int = 0,
    var triangleCount: Int = 0,
    var verticesXCoordinate: IntArray = intArrayOf(),
    var verticesYCoordinate: IntArray = intArrayOf(),
    var verticesZCoordinate: IntArray = intArrayOf(),
    var faceIndicesA: IntArray = intArrayOf(),
    var faceIndicesB: IntArray = intArrayOf(),
    var faceIndicesC: IntArray = intArrayOf(),
    var triangleInfo: IntArray = intArrayOf(),
    var trianglePriorities: ByteArray = byteArrayOf(),
    var triangleAlpha: IntArray = intArrayOf(),
    var triangleColors: ShortArray = shortArrayOf(),
    var modelPriority: Byte = 0,
    var texturedFaces: Int = 0,
    var textureVertexA: ShortArray = shortArrayOf(),
    var textureVertexB: ShortArray = shortArrayOf(),
    var textureVertexC: ShortArray = shortArrayOf(),
    var vertexLabels: IntArray = intArrayOf(),
    var triangleLabels: IntArray = intArrayOf(),
    var particleVertices: IntArray = intArrayOf(),
    var vertexWeights: IntArray = intArrayOf(),
    var triangleSkin: IntArray = intArrayOf()
) {

    fun verticesToFloatArray(): FloatArray {
        val vertices = FloatArray(vertexCount * 3)

        for (vertex in 0 until vertexCount) {
            vertices[vertex * 3 + 0] = verticesXCoordinate[vertex].toFloat()
            vertices[vertex * 3 + 1] = verticesYCoordinate[vertex] * -1f
            vertices[vertex * 3 + 2] = verticesZCoordinate[vertex] * -1f
        }

        val scale = 15f
        for (vertex in vertices.indices) {
            vertices[vertex] /= scale
        }

        return vertices
    }

    fun indicesToIntArray(): IntArray {
        val indices = IntArray(triangleCount * 3)

        for (index in 0 until triangleCount) {
            indices[index * 3 + 0] = faceIndicesA[index]
            indices[index * 3 + 1] = faceIndicesB[index]
            indices[index * 3 + 2] = faceIndicesC[index]
        }

        return indices
    }

    fun texCoordsToFloatArray(): FloatArray {
        computeTextureUVCoordinates()

        val texCoords = FloatArray(triangleCount * 6)
        for (triangle in 0 until triangleCount) {
            val u = textureUCoordinates[triangle]
            val v = textureVCoordinates[triangle]
            texCoords[triangle * 6 + 0] = u[2]
            texCoords[triangle * 6 + 1] = v[2]
            texCoords[triangle * 6 + 2] = u[1]
            texCoords[triangle * 6 + 3] = v[1]
            texCoords[triangle * 6 + 4] = u[0]
            texCoords[triangle * 6 + 5] = v[0]
        }

        return texCoords
    }
}