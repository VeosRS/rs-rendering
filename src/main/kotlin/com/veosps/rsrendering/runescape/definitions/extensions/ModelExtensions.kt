package com.veosps.rsrendering.runescape.definitions.extensions

import com.veosps.rsrendering.runescape.definitions.Model

fun Model.computeTextureUVCoordinates() {
    textureUCoordinates = Array(triangleCount) { FloatArray(3) }
    textureVCoordinates = Array(triangleCount) { FloatArray(3) }

    for (i in 0 until triangleCount) {

        var textureCoordinate = when (faceTexture.isEmpty()) {
            true -> -1
            false -> faceTexture[i].toInt()
        }

        val textureIdx = when (faceMaterial.isEmpty()) {
            true -> -1
            else -> faceMaterial[i].toInt() and 0xFFFF
        }

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
                val textureRenderType = if (textureMap.isEmpty()) 0 else textureMap[textureCoordinate]

                @Suppress("DuplicatedCode")
                if (textureRenderType == 0.toShort()) {
                    val faceVertexIdx1 = faceIndicesA[i]
                    val faceVertexIdx2 = faceIndicesB[i]
                    val faceVertexIdx3 = faceIndicesC[i]

                    val texturedFaceVertexIdx1 = textureVertexA[textureCoordinate].toInt()
                    val texturedFaceVertexIdx2 = textureVertexB[textureCoordinate].toInt()
                    val texturedFaceVertexIdx3 = textureVertexC[textureCoordinate].toInt()

                    val triangleX = verticesXCoordinate[texturedFaceVertexIdx1].toFloat()
                    val triangleY = verticesYCoordinate[texturedFaceVertexIdx2].toFloat()
                    val triangleZ = verticesZCoordinate[texturedFaceVertexIdx3].toFloat()

                    val tx2Tx1 = verticesXCoordinate[texturedFaceVertexIdx2].toFloat() - triangleX
                    val ty2Ty1 = verticesYCoordinate[texturedFaceVertexIdx2].toFloat() - triangleY
                    val tz2Tz1 = verticesZCoordinate[texturedFaceVertexIdx2].toFloat() - triangleZ

                    val tx3Tx1 = verticesXCoordinate[texturedFaceVertexIdx3].toFloat() - triangleX
                    val ty3Ty1 = verticesYCoordinate[texturedFaceVertexIdx3].toFloat() - triangleY
                    val tz3Tz1 = verticesZCoordinate[texturedFaceVertexIdx3].toFloat() - triangleZ

                    val vxT1 = verticesXCoordinate[faceVertexIdx1].toFloat() - triangleX
                    val vyT1 = verticesYCoordinate[faceVertexIdx1].toFloat() - triangleY
                    val vzT1 = verticesZCoordinate[faceVertexIdx1].toFloat() - triangleZ

                    val vx2Tx1 = verticesXCoordinate[faceVertexIdx2].toFloat() - triangleX
                    val vy2Ty1 = verticesYCoordinate[faceVertexIdx2].toFloat() - triangleY
                    val vz2Tz1 = verticesZCoordinate[faceVertexIdx2].toFloat() - triangleZ

                    val vx3Tx1 = verticesXCoordinate[faceVertexIdx3].toFloat() - triangleX
                    val vy3Ty1 = verticesYCoordinate[faceVertexIdx3].toFloat() - triangleY
                    val vz3Tz1 = verticesZCoordinate[faceVertexIdx3].toFloat() - triangleZ

                    val f897 = (ty2Ty1 * tz3Tz1 - tz2Tz1 * ty3Ty1)
                    val f898 = (tz2Tz1 * tx3Tx1 - tx2Tx1 * tz3Tz1)
                    val f899 = (tx2Tx1 * ty3Ty1 - ty2Ty1 * tx3Tx1)

                    var f900 = (ty3Ty1 * f899 - tz3Tz1 * f898)
                    var f901 = (tz3Tz1 * f897 - tx3Tx1 * f899)
                    var f902 = (tx3Tx1 * f898 - ty3Ty1 * f897)
                    var f903 = 1.0f / (f900 * tx2Tx1 + f901 * ty2Ty1 + f902 * tz2Tz1)

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