package com.veosps.rsrendering.opengl.graphics

import org.lwjgl.opengl.*
import org.lwjgl.system.MemoryUtil
import java.nio.FloatBuffer
import java.nio.IntBuffer


class Mesh(val positions: FloatArray, val textCoords: FloatArray, val indices: IntArray, val texture: Texture) {
    var vaoId = 0
    private var vboIdList: MutableList<Int> = mutableListOf()
    var vertexCount = 0

    init {
        var posBuffer: FloatBuffer? = null
        var textCoordsBuffer: FloatBuffer? = null
        var indicesBuffer: IntBuffer? = null
        try {
            vertexCount = indices.size
            vboIdList = ArrayList()
            vaoId = GL30.glGenVertexArrays()
            GL30.glBindVertexArray(vaoId)

            // Position VBO
            var vboId = GL15.glGenBuffers()
            vboIdList.add(vboId)
            posBuffer = MemoryUtil.memAllocFloat(positions.size)
            posBuffer.put(positions).flip()
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, posBuffer, GL15.GL_STATIC_DRAW)
            GL20.glEnableVertexAttribArray(0)
            GL20.glVertexAttribPointer(0, 3, GL11.GL_FLOAT, false, 0, 0)

            // Texture coordinates VBO
            vboId = GL15.glGenBuffers()
            vboIdList.add(vboId)
            textCoordsBuffer = MemoryUtil.memAllocFloat(textCoords.size)
            textCoordsBuffer.put(textCoords).flip()
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vboId)
            GL15.glBufferData(GL15.GL_ARRAY_BUFFER, textCoordsBuffer, GL15.GL_STATIC_DRAW)
            GL20.glEnableVertexAttribArray(1)
            GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 0)

            // Index VBO
            vboId = GL15.glGenBuffers()
            vboIdList.add(vboId)
            indicesBuffer = MemoryUtil.memAllocInt(indices.size)
            indicesBuffer.put(indices).flip()
            GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, vboId)
            GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, indicesBuffer, GL15.GL_STATIC_DRAW)
            GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
            GL30.glBindVertexArray(0)
        } finally {
            if (posBuffer != null) {
                MemoryUtil.memFree(posBuffer)
            }
            if (textCoordsBuffer != null) {
                MemoryUtil.memFree(textCoordsBuffer)
            }
            if (indicesBuffer != null) {
                MemoryUtil.memFree(indicesBuffer)
            }
        }
    }

    fun render() {
        // Activate firs texture bank
        GL13.glActiveTexture(GL13.GL_TEXTURE0)
        // Bind the texture
        GL11.glBindTexture(GL11.GL_TEXTURE_2D, texture.id)

        // Draw the mesh
        GL30.glBindVertexArray(vaoId)
        GL11.glDrawElements(GL11.GL_TRIANGLES, vertexCount, GL11.GL_UNSIGNED_INT, 0)

        // Restore state
        GL30.glBindVertexArray(0)
    }

    fun cleanUp() {
        GL20.glDisableVertexAttribArray(0)

        // Delete the VBOs
        GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0)
        for (vboId in vboIdList!!) {
            GL15.glDeleteBuffers(vboId)
        }

        // Delete the texture
        texture.cleanup()

        // Delete the VAO
        GL30.glBindVertexArray(0)
        GL30.glDeleteVertexArrays(vaoId)
    }
}