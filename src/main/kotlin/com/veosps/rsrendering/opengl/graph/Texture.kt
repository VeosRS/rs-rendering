package com.veosps.rsrendering.opengl.graph

import org.lwjgl.opengl.GL11.*
import org.lwjgl.opengl.GL30.glGenerateMipmap
import org.lwjgl.stb.STBImage.*
import org.lwjgl.system.MemoryStack
import java.nio.ByteBuffer

class Texture(fileName: String) {
    val id = loadTexture(fileName)

    fun bind() = glBindTexture(GL_TEXTURE_2D, id)
    fun cleanUp() = glDeleteTextures(id)

    companion object {

        fun loadTexture(fileName: String): Int {
            var width: Int
            var height: Int
            var buffer: ByteBuffer

            MemoryStack.stackPush().use { stack ->
                val w = stack.mallocInt(1)
                val h = stack.mallocInt(1)
                val channel = stack.mallocInt(1)

                buffer = stbi_load(fileName, w, h, channel, 4) ?: error("Image file '$fileName' not loaded: ${stbi_failure_reason()}")

                width = w.get()
                height = h.get()
            }

            val textureId = glGenTextures()
            glBindTexture(GL_TEXTURE_2D, textureId)
            glPixelStorei(GL_UNPACK_ALIGNMENT, 1)
            glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, buffer)
            glGenerateMipmap(GL_TEXTURE_2D)
            stbi_image_free(buffer)

            return textureId
        }
    }
}