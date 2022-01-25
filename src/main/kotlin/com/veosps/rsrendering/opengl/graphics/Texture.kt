package com.veosps.rsrendering.opengl.graphics

import org.lwjgl.opengl.GL11
import org.lwjgl.opengl.GL30
import org.lwjgl.stb.STBImage
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer


class Texture(val id: Int) {

    constructor(fileName: String) : this(loadTexture(fileName)) {}

    fun bind() {
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, id)
    }

    fun cleanup() {
        GL11.glDeleteTextures(id)
    }

    companion object {
        @Throws(Exception::class)
        private fun loadTexture(fileName: String): Int {
            var width: Int
            var height: Int
            var buf: ByteBuffer?
            MemoryStack.stackPush().use { stack ->
                val w = stack.mallocInt(1)
                val h = stack.mallocInt(1)
                val channels = stack.mallocInt(1)
                buf = STBImage.stbi_load(fileName, w, h, channels, 4)
                if (buf == null) {
                    throw Exception("Image file [" + fileName + "] not loaded: " + STBImage.stbi_failure_reason())
                }
                width = w.get()
                height = h.get()
            }

            // Create a new OpenGL texture
            val textureId = GL11.glGenTextures()
            // Bind the texture
            GL11.glBindTexture(GL11.GL_TEXTURE_2D, textureId)

            // Tell OpenGL how to unpack the RGBA bytes. Each component is 1 byte size
            GL11.glPixelStorei(GL11.GL_UNPACK_ALIGNMENT, 1)

            //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);
            //glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);

            // Upload the texture data
            GL11.glTexImage2D(
                GL11.GL_TEXTURE_2D, 0, GL11.GL_RGBA, width, height, 0,
                GL11.GL_RGBA, GL11.GL_UNSIGNED_BYTE, buf
            )
            // Generate Mip Map
            GL30.glGenerateMipmap(GL11.GL_TEXTURE_2D)
            STBImage.stbi_image_free(buf!!)
            return textureId
        }
    }
}