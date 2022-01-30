package com.veosps.rsrendering.opengl.graph

import com.veosps.rsrendering.utils.getRGBA
import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.glGenerateMipmap
import java.awt.image.BufferedImage
import java.nio.ByteBuffer

class Texture(private val image: BufferedImage) {
    val id = loadTexture()

    fun bind() = glBindTexture(GL_TEXTURE_2D, id)
    fun cleanUp() = glDeleteTextures(id)

    private fun loadTexture(): Int {
        val buffer = generateTextureBuffer()

        val textureId = glGenTextures()
        glBindTexture(GL_TEXTURE_2D, textureId)
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, image.width, image.height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer)
        glGenerateMipmap(GL_TEXTURE_2D)

        return textureId
    }

    private fun generateTextureBuffer(): ByteBuffer {
        val buffer = ByteBuffer.allocateDirect(4 * image.width * image.height)

        for (y in 0 until image.height) {
            for (x in 0 until image.width) {
                buffer.put(image.getRGBA(x, y))
            }
        }

        return buffer.flip()
    }
}