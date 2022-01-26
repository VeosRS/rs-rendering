@file:Suppress("ArrayInDataClass")

package com.veosps.rsrendering.runescape.model

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

    fun computeTextureUVCoordinates() {
        textureUCoordinates = Array(triangleCount) { floatArrayOf() }
        textureVCoordinates = Array(triangleCount) { floatArrayOf() }

        for (i in 0 until triangleCount) {
            var textureCoordinate = (faceTexture.getOrNull(i) ?: -1).toInt()
            val textureIdx = (faceMaterial.getOrNull(i) ?: -1).toInt()

            if (textureIdx != -1) {
                val u = FloatArray(3)
                val v = FloatArray(3)

                if (textureCoordinate == -1) {
                    u[0] = 0.0f
                    v[0] = 1.0f

                    u[1] = 1.0f
                    v[1] = 1.0f

                    u[2] = 0.0f
                    v[2] = 0.0f
                } else {
                    textureCoordinate = textureCoordinate and 0xFF
                    val textureRenderType = (textureMap.getOrNull(textureCoordinate) ?: 0).toInt()

                    @Suppress("DuplicatedCode")
                    if (textureRenderType == 0) {
                        val faceVertexIdx1 = faceIndicesA[i]
                        val faceVertexIdx2 = faceIndicesB[i]
                        val faceVertexIdx3 = faceIndicesC[i]

                        val texturedFaceVertexIdx1 = textureVertexA[textureCoordinate].toInt()
                        val texturedFaceVertexIdx2 = textureVertexB[textureCoordinate].toInt()
                        val texturedFaceVertexIdx3 = textureVertexC[textureCoordinate].toInt()

                        val triangleX = verticesXCoordinate[texturedFaceVertexIdx1]
                        val triangleY = verticesYCoordinate[texturedFaceVertexIdx2]
                        val triangleZ = verticesZCoordinate[texturedFaceVertexIdx3]

                        val tx2Tx1 = verticesXCoordinate[texturedFaceVertexIdx2] - triangleX
                        val ty2Ty1 = verticesYCoordinate[texturedFaceVertexIdx2] - triangleY
                        val tz2Tz1 = verticesZCoordinate[texturedFaceVertexIdx2] - triangleZ

                        val tx3Tx1 = verticesXCoordinate[texturedFaceVertexIdx3] - triangleX
                        val ty3Ty1 = verticesYCoordinate[texturedFaceVertexIdx3] - triangleY
                        val tz3Tz1 = verticesZCoordinate[texturedFaceVertexIdx3] - triangleZ

                        val vxT1 = verticesXCoordinate[faceVertexIdx1] - triangleX
                        val vyT1 = verticesYCoordinate[faceVertexIdx1] - triangleY
                        val vzT1 = verticesZCoordinate[faceVertexIdx1] - triangleZ

                        val vx2Tx1 = verticesXCoordinate[faceVertexIdx2] - triangleX
                        val vy2Ty1 = verticesYCoordinate[faceVertexIdx2] - triangleY
                        val vz2Tz1 = verticesZCoordinate[faceVertexIdx2] - triangleZ

                        val vx3Tx1 = verticesXCoordinate[faceVertexIdx3] - triangleX
                        val vy3Ty1 = verticesYCoordinate[faceVertexIdx3] - triangleY
                        val vz3Tz1 = verticesZCoordinate[faceVertexIdx3] - triangleZ

                        val f897: Float = (ty2Ty1 * tz3Tz1 - tz2Tz1 * ty3Ty1).toFloat()
                        val f898: Float = (tz2Tz1 * tx3Tx1 - tx2Tx1 * tz3Tz1).toFloat()
                        val f899: Float = (tx2Tx1 * ty3Ty1 - ty2Ty1 * tx3Tx1).toFloat()

                        var f900: Float = (ty3Ty1 * f899 - tz3Tz1 * f898)
                        var f901: Float = (tz3Tz1 * f897 - tx3Tx1 * f899)
                        var f902: Float = (tx3Tx1 * f898 - ty3Ty1 * f897)
                        var f903: Float = 1.0f / (f900 * tx2Tx1 + f901 * ty2Ty1 + f902 * tz2Tz1)

                        u[0] = (f900 * vxT1 + f901 * vyT1 + f902 * vzT1) * f903
                        u[1] = (f900 * vx2Tx1 + f901 * vy2Ty1 + f902 * vz2Tz1) * f903
                        u[2] = (f900 * vx3Tx1 + f901 * vy3Ty1 + f902 * vz3Tz1) * f903

                        f900 = ty2Ty1 * f899 - tz2Tz1 * f898
                        f901 = tz2Tz1 * f897 - tx2Tx1 * f899
                        f902 = tx2Tx1 * f898 - ty2Ty1 * f897
                        f903 = 1.0f / (f900 * tx3Tx1 + f901 * ty3Ty1 + f902 * tz3Tz1)

                        v[0] = (f900 * vxT1 + f901 * vyT1 + f902 * vzT1) * f903
                        v[1] = (f900 * vx2Tx1 + f901 * vy2Ty1 + f902 * vz2Tz1) * f903
                        v[2] = (f900 * vx3Tx1 + f901 * vy3Ty1 + f902 * vz3Tz1) * f903
                    }
                }

                textureUCoordinates[i] = u
                textureVCoordinates[i] = v
            }
        }
    }
}
