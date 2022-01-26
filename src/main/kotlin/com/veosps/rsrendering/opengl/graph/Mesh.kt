package com.veosps.rsrendering.opengl.graph

import com.veosps.rsrendering.utils.freeFromMemory
import org.lwjgl.opengl.GL30.*
import org.lwjgl.system.MemoryUtil.memAllocFloat
import org.lwjgl.system.MemoryUtil.memAllocInt
import java.nio.FloatBuffer
import java.nio.IntBuffer

class Mesh(
    private val positions: FloatArray,
    private val textureCoordinates: FloatArray,
    private val indices: IntArray,
    private val texture: Texture
) {
    private val vaoId = glGenVertexArrays()
    private val vboIdList = mutableListOf<Int>()
    private val vertexCount = indices.size

    init {
        init()
    }

    private fun init() {
        var posBuffer: FloatBuffer? = null
        var texCoordsBuffer: FloatBuffer? = null
        var indicesBuffer: IntBuffer? = null

        try {
            glBindVertexArray(vaoId)

            posBuffer = createVbo(positions, 0) { glVertexAttribPointer(0, 3, GL_FLOAT, false, 0, 0) }
            texCoordsBuffer = createVbo(textureCoordinates, 1) { glVertexAttribPointer(1, 2, GL_FLOAT, false, 0, 0) }
            indicesBuffer = createVbo(indices)

            glBindBuffer(GL_ARRAY_BUFFER, 0)
            glBindVertexArray(0)
        } finally {
            posBuffer.freeFromMemory()
            texCoordsBuffer.freeFromMemory()
            indicesBuffer.freeFromMemory()
        }
    }

    private fun createVbo(input: FloatArray, index: Int, callback: () -> Unit = {}): FloatBuffer {
        val vboId = glGenBuffers()
        vboIdList.add(vboId)
        val buffer = memAllocFloat(input.size)
        buffer.put(input).flip()
        glBindBuffer(GL_ARRAY_BUFFER, vboId)
        glBufferData(GL_ARRAY_BUFFER, buffer, GL_STATIC_DRAW)
        glEnableVertexAttribArray(index)
        callback()
        return buffer
    }

    private fun createVbo(input: IntArray): IntBuffer {
        val vboId = glGenBuffers()
        vboIdList.add(vboId)
        val buffer = memAllocInt(input.size)
        buffer.put(input).flip()
        glBindBuffer(GL_ELEMENT_ARRAY_BUFFER, vboId)
        glBufferData(GL_ELEMENT_ARRAY_BUFFER, buffer, GL_STATIC_DRAW)
        return buffer
    }

    /**
     * Activate first texture bank, bind the texture, draw the mesh and then restore state.
     */
    fun render() {
        glActiveTexture(GL_TEXTURE0)
        glBindTexture(GL_TEXTURE_2D, texture.id)
        glBindVertexArray(vaoId)
        glDrawElements(GL_TRIANGLES, vertexCount, GL_UNSIGNED_INT, 0)
        glBindVertexArray(0)
    }

    /**
     * Clean up and remove mesh from video memory.
     */
    fun cleanUp() {
        glDisableVertexAttribArray(0)
        glBindBuffer(GL_ARRAY_BUFFER, 0)
        vboIdList.forEach { glDeleteBuffers(it) }
        texture.cleanUp()
        glBindVertexArray(0)
        glDeleteVertexArrays(vaoId)
    }
}