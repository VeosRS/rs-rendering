package com.veosps.rsrendering.runescape.loaders

import com.displee.compress.decompress
import com.github.michaelbull.logging.InlineLogger
import com.veosps.rsrendering.runescape.cache.cache
import com.veosps.rsrendering.runescape.cache.spriteIndex
import com.veosps.rsrendering.runescape.cache.textureIndex
import com.veosps.rsrendering.runescape.definitions.osrs.Sprite
import com.veosps.rsrendering.runescape.definitions.osrs.Texture
import io.netty.buffer.Unpooled
import org.springframework.stereotype.Component
import java.awt.image.BufferedImage
import java.awt.image.BufferedImage.TYPE_INT_ARGB
import java.awt.image.BufferedImage.TYPE_INT_RGB
import java.io.File
import javax.imageio.ImageIO

var dumpSprites = false
var dumpTextures = false

@Component
class OSRSTextureLoader(
    val textures: MutableList<Texture> = mutableListOf(),
    val sprites: MutableList<Sprite> = mutableListOf()
) {

    private val logger = InlineLogger()

    private val flagVertical = 0b01
    private val flagAlpha = 0b10

    fun findSprite(id: Int): Sprite {
        return sprites[textures[id].fileIds[0]]
    }

    fun load() {
        val textureArchive = textureIndex().archive(0)
        val textureFiles = textureArchive?.files?.values ?: error("No files found in texture archive...")

        textureFiles.filter { it.data != null }
            .forEach { textures.add(loadTexture(it.id, it.data!!)) }

        logger.info { "Loaded ${textures.size} textures..." }

        val spriteIndex = spriteIndex()
        for (i in 0 until spriteIndex.archives().size) {
            val sector = spriteIndex.readArchiveSector(i) ?: continue
            sprites.add(loadSprites(i, sector.decompress())[0])
        }

        logger.info { "Loaded ${sprites.size} sprites..." }

        if (dumpSprites) {
            sprites.forEach {
                if (it.width > 0 || it.height > 0) {
                    val image = BufferedImage(it.width, it.height, TYPE_INT_ARGB)
                    image.setRGB(0, 0, it.width, it.height, it.pixels, 0, it.width)
                    ImageIO.write(image, "png", File("./data/dumps/sprites/${it.id}.png"))
                }
            }
        }

        if (dumpTextures) {
            textures.forEach {
                val sprite = sprites[it.fileIds[0]]
                val image = BufferedImage(sprite.width, sprite.height, TYPE_INT_ARGB)
                image.setRGB(0, 0, sprite.width, sprite.height, sprite.pixels, 0, sprite.width)
                ImageIO.write(image, "png", File("./data/dumps/textures/${it.id}.png"))
            }
        }
    }

    private fun loadTexture(id: Int, data: ByteArray): Texture {
        val definition = Texture(id = id)

        val buffer = Unpooled.wrappedBuffer(data)

        buffer.readUnsignedShort() // field1777 ignored for now
        buffer.readUnsignedByte() // field1778 ignored for now

        val count = buffer.readUnsignedByte().toInt()
        val files = IntArray(count)

        for (i in 0 until count) {
            files[i] = buffer.readUnsignedShort()
        }

        definition.fileIds = files

        if (count > 1) {
            for (i in 0 until count) {
                buffer.readUnsignedByte() // field1780 ignored for now
            }
        }

        for (i in 0 until count) {
            buffer.readInt() // field1786 ignored for now
        }

        buffer.readUnsignedByte() // field1783 ignored for now
        buffer.readUnsignedByte() // field1782 ignored for now

        return definition
    }

    private fun loadSprites(id: Int, data: ByteArray): Array<Sprite> {
        val buffer = Unpooled.wrappedBuffer(data)

        buffer.readerIndex(buffer.writerIndex() - 2)
        val spriteCount = buffer.readUnsignedShort()

        val sprites = arrayOfNulls<Sprite>(spriteCount)

        buffer.readerIndex(buffer.writerIndex() - 7 - spriteCount * 8)

        val width = buffer.readUnsignedShort()
        val height = buffer.readUnsignedShort()
        val paletteLength = buffer.readUnsignedByte() + 1

        for (i in 0 until spriteCount) {
            sprites[i] = Sprite(
                id,
                frame = i,
                maxWidth = width,
                maxHeight = height
            )
        }

        for (i in 0 until spriteCount) {
            sprites[i]?.offsetX = buffer.readUnsignedShort()
        }

        for (i in 0 until spriteCount) {
            sprites[i]?.offsetY = buffer.readUnsignedShort()
        }

        for (i in 0 until spriteCount) {
            sprites[i]?.width = buffer.readUnsignedShort()
        }

        for (i in 0 until spriteCount) {
            sprites[i]?.height = buffer.readUnsignedShort()
        }

        buffer.readerIndex(buffer.writerIndex() - 7 - spriteCount * 8 - (paletteLength - 1) * 3)

        val palette = IntArray(paletteLength)

        for (i in 1 until paletteLength) {
            palette[i] = buffer.readMedium()
            if (palette[i] == 0) {
                palette[i] = 1
            }
        }

        buffer.readerIndex(0)

        for (i in 0 until spriteCount) {
            val definition = sprites[i] ?: error("Sprite at $i was null, that's not supposed to happen!")

            val spriteWidth = definition.width
            val spriteHeight = definition.height
            val dimension = spriteWidth * spriteHeight
            val pixelPaletteIndices = ByteArray(dimension)
            val pixelAlphas = ByteArray(dimension)

            definition.pixelIdx = pixelPaletteIndices
            definition.palette = palette

            val flags = buffer.readUnsignedByte().toInt()

            if ((flags and flagVertical) == 0) {
                for (j in 0 until dimension) {
                    pixelPaletteIndices[j] = buffer.readByte()
                }
            } else {
                for (j in 0 until spriteWidth) {
                    for (k in 0 until spriteHeight) {
                        pixelPaletteIndices[spriteWidth * k + j] = buffer.readByte()
                    }
                }
            }

            if ((flags and flagAlpha) != 0) {
                if ((flags and flagVertical) == 0) {
                    for (j in 0 until dimension) {
                        pixelAlphas[j] = buffer.readByte()
                    }
                } else {
                    for (j in 0 until spriteWidth) {
                        for (k in 0 until spriteHeight) {
                            pixelAlphas[spriteWidth * k + j] = buffer.readByte()
                        }
                    }
                }
            } else {
                for (j in 0 until dimension) {
                    val index = pixelPaletteIndices[j].toInt()
                    if (index != 0) pixelAlphas[j] = 0xFF.toByte()
                }
            }

            val pixels = IntArray(dimension)

            for (j in 1 until dimension) {
                val index = pixelPaletteIndices[j].toInt() and 0xFF
                pixels[j] = palette[index] or (pixelAlphas[j].toInt() shl 24)
            }

            definition.pixels = pixels
        }

        return sprites.mapNotNull { it }.toTypedArray()
    }
}