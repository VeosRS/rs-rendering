package com.veosps.rsrendering.utils

import com.veosps.rsrendering.opengl.graph.Texture
import com.veosps.rsrendering.random.InformativeTriangle
import com.veosps.rsrendering.runescape.RSMesh
import com.veosps.rsrendering.runescape.cache.texLoader
import com.veosps.rsrendering.runescape.definitions.Model
import com.veosps.rsrendering.runescape.definitions.extensions.computeTextureUVCoordinates
import org.joml.Vector3f
import java.awt.Color
import java.awt.image.BufferedImage
import java.io.File
import java.util.stream.Collectors
import java.util.stream.IntStream
import javax.imageio.ImageIO
import kotlin.math.*


private const val modelScale = 0.03f

fun buildRSMesh(model: Model): RSMesh = with(model) {
    computeTextureUVCoordinates()

    val mesh = RSMesh()

    val colors: List<Color> = IntStream.range(0, triangleCount)
        .filter { triangleInfo[it] < 2 }
        .mapToObj {
            val rgb = HSLColor(triangleColors[it].toInt()).asRGB()
            ColorHelper(rgb, 255.0f).color()
        }.toList()

    val textures = IntStream.range(0, triangleCount)
        .filter { triangleInfo[it] >= 2 }
        .mapToObj {
            texLoader.textures[triangleColors[it].toInt()]
        }.distinct()
        .collect(Collectors.toList())

    val vCoordinates: MutableList<Float> = ArrayList(triangleCount)
    for (i in 0 until triangleCount) {
        if (textureVCoordinates[i].size != 3) continue
        vCoordinates.add(textureVCoordinates[i][0])
        vCoordinates.add(textureVCoordinates[i][1])
        vCoordinates.add(textureVCoordinates[i][2])
    }

    val minV = vCoordinates.stream()
        .min(Comparator.naturalOrder())
        .map { floor(it) }
        .orElse(-1f)

    val maxV = vCoordinates.stream()
        .min(Comparator.naturalOrder())
        .map { ceil(it) }
        .orElse(-1f)

    val copies = (abs(maxV) + abs(minV)).toInt()

    val width = 128

    val rows = if (colors.isEmpty())
        0
    else
        max(1, if (colors.size > width && colors.size % width != 0) (colors.size / width) + 1 else (colors.size / width))

    val atlas = BufferedImage(width, rows + (textures.size * (128 * copies)), BufferedImage.TYPE_INT_ARGB)

    var colorIdx = 0
    var remaining = colors.size
    val col = min(max(1, colors.size), width)
    for (i in 0 until rows * col) {
        atlas.setRGB(i % col, i / col, colors[colorIdx++].rgb)
        remaining--
        if (remaining <= 0) break
    }

    var heightOff = rows
    textures.forEach {
        val sprite = texLoader.sprites[it.fileIds[0]]
        val textureWidth = sprite.width
        val textureHeight = sprite.height
        val image = BufferedImage(sprite.width, sprite.height, BufferedImage.TYPE_INT_ARGB)
        image.setRGB(0, 0, sprite.width, sprite.height, sprite.pixels, 0, sprite.width)

        for (i in 0 until copies) {
            for (j in 0 until textureWidth) {
                for (k in 0 until textureHeight) {
                    val colorToWrite = image.getRGB(j, k)
                    val y = (i * textureHeight) + k
                    atlas.setRGB(j, y + heightOff, colorToWrite)
                }
            }
        }
        heightOff += 384
    }

    mesh.material = Texture(atlas)
    ImageIO.write(atlas, "png", File("./data/dumps/texture-atlases/$modelId.png"))

    var idx = 0
    for (i in 0 until triangleCount) {
        val faceA = faceIndicesA[i]
        val faceB = faceIndicesB[i]
        val faceC = faceIndicesC[i]
        val vertexIndex1 = mesh.addVertex(faceA, verticesXCoordinate[faceA], verticesYCoordinate[faceA], verticesZCoordinate[faceA])
        val vertexIndex2 = mesh.addVertex(faceB, verticesXCoordinate[faceB], verticesYCoordinate[faceB], verticesZCoordinate[faceB])
        val vertexIndex3 = mesh.addVertex(faceC, verticesXCoordinate[faceC], verticesYCoordinate[faceC], verticesZCoordinate[faceC])
        val a = Vector3f(verticesXCoordinate[faceA].toFloat(), verticesYCoordinate[faceA].toFloat(), verticesZCoordinate[faceA].toFloat())
        val b = Vector3f(verticesXCoordinate[faceB].toFloat(), verticesYCoordinate[faceB].toFloat(), verticesZCoordinate[faceB].toFloat())
        val c = Vector3f(verticesXCoordinate[faceC].toFloat(), verticesYCoordinate[faceC].toFloat(), verticesZCoordinate[faceC].toFloat())
        mesh.triangles.add(
            InformativeTriangle(a.mul(modelScale), b.mul(modelScale), c.mul(modelScale), i, faceA, faceB, faceC)
        )

        mesh.addTriangle(i)

        val textureIndex1: Int
        val textureIndex2: Int
        val textureIndex3: Int

        if (model.triangleInfo[i] >= 2) {
            val u1 = textureUCoordinates[i][0]
            val u2 = textureUCoordinates[i][1]
            val u3 = textureUCoordinates[i][2]

            var v1 = textureVCoordinates[i][0]
            var v2 = textureVCoordinates[i][1]
            var v3 = textureVCoordinates[i][2]

            v1 = (v1 + minV) / (maxV - minV)
            v2 = (v2 + minV) / (maxV - minV)
            v3 = (v3 + minV) / (maxV - minV)

            textureIndex1 = mesh.addUV(u1, v1)
            textureIndex2 = mesh.addUV(u2, v2)
            textureIndex3 = mesh.addUV(u3, v3)
        } else {
            val u = (idx % width + 0.5f) / width
            val v = (idx / width + 0.5f) / atlas.height

            textureIndex1 = mesh.addUV(u, v)
            textureIndex2 = mesh.addUV(u, v)
            textureIndex3 = mesh.addUV(u, v)

            idx++
        }

        mesh.faces.addAll(
            listOf(
            vertexIndex1, vertexIndex2, vertexIndex3,
            textureIndex1, textureIndex2, textureIndex3
        )
        )
    }

    return mesh
}

class HSLColor(
    private val hsl: Int,
    private val table: IntArray = IntArray(128 * 512)
) {

    init {
        generatePalette()
    }

    private fun generatePalette() {
        val brightness = 0.8
        var index = 0
        for (y in 0 until 512) {
            val hue = ((y / 8) / 64.0) + 0.0078125
            val lightness = ((y and 0x7) / 8.0) + 0.0625
            for (x in 0 until 128) {
                val intensity = x.toDouble() / 128.0
                var red = intensity
                var green = intensity
                var blue = intensity

                if (lightness != 0.0) {
                    val a = if (intensity < 0.5) {
                        intensity * (1.0 + lightness)
                    } else {
                        (intensity + lightness) - (intensity * lightness)
                    }

                    val b = (2.0 * intensity) - a

                    var fRed = hue + (1.0 / 3.0)
                    var fBlue = hue - (1.0 / 3.0)

                    if (fRed > 1.0) fRed--
                    if (fBlue < 0.0) fBlue++

                    red = getValue(fRed, a, b)
                    green = getValue(hue, a, b)
                    blue = getValue(fBlue, a, b)
                }

                table[index++] = generatePalette((red * 256.0).toInt() shl 16 or ((green * 256.0).toInt() shl 8) or (blue * 256.0).toInt(), brightness)
            }
        }
    }

    private fun generatePalette(rgb: Int, brightness: Double): Int {
        var r = (rgb shr 16) / 256.0
        var g = (rgb shr 8 and 0xff) / 256.0
        var b = (rgb and 0xff) / 256.0
        r = r.pow(brightness)
        g = g.pow(brightness)
        b = b.pow(brightness)
        return ((r * 256.0).toInt() shl 16) + ((g * 256.0).toInt() shl 8) + (b * 256.0).toInt()
    }

    private fun getValue(value: Double, a: Double, b: Double): Double {
        if ((6.0 * value) < 1.0) return b + ((a - b) * 6.0 * value)
        if (2.0 * value < 1.0) return a
        if (3.0 * value < 2.0) return b + ((a - b) * ((2.0 / 3.0) - value) * 6.0)
        return b
    }

    fun asRGB(): Int {
        if (hsl >= table.size) return 0
        return table[hsl and 0xFFFF]
    }
}

class ColorHelper(rgb: Int, private val opacity: Float) {
    private val red = (rgb shr 16) and 0xFF
    private val green = (rgb shr 8) and 0xFF
    private val blue = rgb and 0xFF

    fun color(): Color {
        return Color(red / 255.0f, green / 255.0f, blue / 255.0f, opacity / 255.0f)
    }
}