package com.veosps.rsrendering.runescape.model.decoders

import com.veosps.rsrendering.runescape.buffer.readSignedSmart
import com.veosps.rsrendering.runescape.model.ModelData
import io.netty.buffer.Unpooled
import kotlin.experimental.and

/**
 * Decode a model from the RuneScape cache to prepare it for OpenGL to render.
 */
class OSRSModelDecoder(
    private val data: ByteArray
) : IDecoder {

    @Suppress("UnnecessaryVariable")
    override fun decode(): ModelData = with(ModelData()) {
        val initialData = Unpooled.wrappedBuffer(data)

        val first = initialData.copy()
        val second = initialData.copy()
        val third = initialData.copy()
        val fourth = initialData.copy()
        val fifth = initialData.copy()

        first.readerIndex(data.size - 18)

        vertexCount = first.readUnsignedShort()
        triangleCount = first.readUnsignedShort()
        texturedFaces = first.readUnsignedByte().toInt()

        val renderTypeOpcode = first.readUnsignedByte().toInt()
        val renderPriorityOpcode = first.readUnsignedByte().toInt()
        val triangleAlphaOpcode = first.readUnsignedByte().toInt()
        val triangleSkinOpcode = first.readUnsignedByte().toInt()
        val vertexLabelOpcode = first.readUnsignedByte().toInt()

        val verticesXCoordinateOffset = first.readUnsignedShort()
        val verticesYCoordinateOffset = first.readUnsignedShort()
        first.readUnsignedShort() // verticesZCoordinateOffset

        val triangleIndicesOffset = first.readUnsignedShort()

        val vertexFlagOffset = 0

        var readerPos = vertexCount

        val triangleCompressTypeOffset = readerPos
        readerPos += triangleCount

        val facePriorityOffset = readerPos
        if (renderPriorityOpcode == 255) readerPos += triangleCount

        val triangleSkinOffset = readerPos
        if (triangleSkinOpcode == 1) readerPos += triangleCount

        val renderTypeOffset = readerPos
        if (renderTypeOpcode == 1) readerPos += triangleCount

        val vertexLabelOffset = readerPos
        if (vertexLabelOpcode == 1) readerPos += vertexCount

        val triangleAlphaOffset = readerPos
        if (triangleAlphaOpcode == 1) readerPos += triangleCount

        val indicesOffset = readerPos
        readerPos += triangleIndicesOffset

        val triangleColorOffset = readerPos
        readerPos += triangleCount * 2

        val textureOffset = readerPos
        readerPos += texturedFaces * 6

        val xOffset = readerPos
        readerPos += verticesXCoordinateOffset

        val yOffset = readerPos
        readerPos += verticesYCoordinateOffset

        val zOffset = readerPos

        verticesXCoordinate = IntArray(vertexCount)
        verticesYCoordinate = IntArray(vertexCount)
        verticesZCoordinate = IntArray(vertexCount)
        faceIndicesA = IntArray(triangleCount)
        faceIndicesB = IntArray(triangleCount)
        faceIndicesC = IntArray(triangleCount)

        if (texturedFaces > 0) {
            textureMap = ShortArray(texturedFaces)
            textureVertexA = ShortArray(texturedFaces)
            textureVertexB = ShortArray(texturedFaces)
            textureVertexC = ShortArray(texturedFaces)
        }

        if (vertexLabelOpcode == 1) {
            vertexLabels = IntArray(vertexCount)
        }

        if (renderTypeOpcode == 1) {
            triangleInfo = IntArray(triangleCount)
            faceTexture = ShortArray(triangleCount)
            faceMaterial = ShortArray(triangleCount)
            faceTextureMasks = ByteArray(triangleCount)
        }

        if (renderPriorityOpcode == 255) {
            trianglePriorities = ByteArray(triangleCount)
        } else {
            modelPriority = renderPriorityOpcode.toByte()
        }

        if (triangleAlphaOpcode == 1) {
            triangleAlpha = IntArray(triangleCount)
        }

        if (triangleSkinOpcode == 1) {
            triangleLabels = IntArray(triangleCount)
        }

        triangleColors = ShortArray(triangleCount)
        first.readerIndex(vertexFlagOffset)
        second.readerIndex(xOffset)
        third.readerIndex(yOffset)
        fourth.readerIndex(zOffset)
        fifth.readerIndex(vertexLabelOffset)

        var baseX = 0
        var baseY = 0
        var baseZ = 0

        for (point in 0 until vertexCount) {
            val flag = first.readUnsignedByte()

            var x = 0
            if ((flag and 0x1).toInt() != 0) x = second.readSignedSmart()

            var y = 0
            if ((flag and 0x2).toInt() != 0) y = third.readSignedSmart()

            var z = 0
            if ((flag and 0x4).toInt() != 0) z = fourth.readSignedSmart()

            verticesXCoordinate[point] = baseX + x
            verticesYCoordinate[point] = baseY + y
            verticesZCoordinate[point] = baseZ + z

            baseX = verticesXCoordinate[point]
            baseY = verticesYCoordinate[point]
            baseZ = verticesZCoordinate[point]

            if (vertexLabelOpcode == 1) vertexLabels[point] = fifth.readUnsignedByte().toInt()
        }

        first.readerIndex(triangleColorOffset)
        second.readerIndex(renderTypeOffset)
        third.readerIndex(facePriorityOffset)
        fourth.readerIndex(triangleAlphaOffset)
        fifth.readerIndex(triangleSkinOffset)

        for (face in 0 until triangleCount) {
            val color = first.readUnsignedShort()
            triangleColors[face] = color.toShort()

            if (renderTypeOpcode == 1) triangleInfo[face] = second.readUnsignedByte().toInt()

            if (renderPriorityOpcode == 255) trianglePriorities[face] = third.readByte()

            if (triangleAlphaOpcode == 1) {
                triangleAlpha[face] = fourth.readByte().toInt()
                if (triangleAlpha[face] < 0) triangleAlpha[face] = (256 + triangleAlpha[face])
            }

            if (triangleSkinOpcode == 1) triangleLabels[face] = fifth.readUnsignedByte().toInt()
        }

        first.readerIndex(indicesOffset)
        second.readerIndex(triangleCompressTypeOffset)

        var indexA = 0
        var indexB = 0
        var indexC = 0
        var offset = 0
        var coordinate: Int

        for (face in 0 until triangleCount) {
            val opcode = second.readUnsignedByte().toInt()

            if (opcode == 1) {
                indexA = first.readSignedSmart() + offset
                offset = indexA
                indexB = first.readSignedSmart() + offset
                offset = indexB
                indexC = first.readSignedSmart() + offset
                offset = indexC

                faceIndicesA[face] = indexA
                faceIndicesB[face] = indexB
                faceIndicesC[face] = indexC
            }

            if (opcode == 2) {
                indexB = indexC
                indexC = first.readSignedSmart() + offset
                offset = indexC

                faceIndicesA[face] = indexA
                faceIndicesB[face] = indexB
                faceIndicesC[face] = indexC
            }

            if (opcode == 3) {
                indexA = indexC
                indexC = first.readSignedSmart() + offset
                offset = indexC
                faceIndicesA[face] = indexA
                faceIndicesB[face] = indexB
                faceIndicesC[face] = indexC
            }

            if (opcode == 4) {
                coordinate = indexA
                indexA = indexB
                indexB = coordinate
                indexC = first.readSignedSmart() + offset
                offset = indexC

                faceIndicesA[face] = indexA
                faceIndicesB[face] = indexB
                faceIndicesC[face] = indexC
            }
        }

        first.readerIndex(textureOffset)

        for (face in 0 until texturedFaces) {
            textureMap[face] = 0
            textureVertexA[face] = first.readUnsignedShort().toShort()
            textureVertexB[face] = first.readUnsignedShort().toShort()
            textureVertexC[face] = first.readUnsignedShort().toShort()
        }

        if (triangleInfo.isEmpty()) triangleInfo = IntArray(triangleCount)

        return@with this
    }
}