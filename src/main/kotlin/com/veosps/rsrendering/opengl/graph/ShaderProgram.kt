package com.veosps.rsrendering.opengl.graph

import org.joml.Matrix4f
import org.lwjgl.opengl.GL20.*
import org.lwjgl.system.MemoryStack

class ShaderProgram {
    private val uniforms = mutableMapOf<String, Int>()

    private val programId = when (val id = glCreateProgram()) {
        0 -> error("Could not create shader program!")
        else -> id
    }
    private var vertexShaderId = -1
    private var fragmentShaderId = -1

    fun createUniform(name: String) {
        val location = glGetUniformLocation(programId, name)
        uniforms[name] = if (location >= 0) location else error("Could not find uniform: $name")
    }

    fun uniform(name: String, value: Matrix4f) {
        val uniform = uniforms[name] ?: error("Uniform '$name' does not exist.")
        MemoryStack.stackPush().use { stack -> glUniformMatrix4fv(uniform, false, value[stack.mallocFloat(16)]) }
    }

    fun uniform(name: String, value: Int) {
        val uniform = uniforms[name] ?: error("Uniform '$name' does not exist.")
        glUniform1i(uniform, value)
    }

    fun createVertexShader(code: String) {
        vertexShaderId = createShader(code, GL_VERTEX_SHADER)
    }

    fun createFragmentShader(code: String) {
        fragmentShaderId = createShader(code, GL_FRAGMENT_SHADER)
    }

    private fun createShader(code: String, type: Int): Int {
        val shaderId = glCreateShader(type)
        if (shaderId == 0) error("Error creating shader of type $type!")

        glShaderSource(shaderId, code)
        glCompileShader(shaderId)

        if (glGetShaderi(shaderId, GL_COMPILE_STATUS) == 0)
            error("Failed to compile shader code: ${glGetShaderInfoLog(shaderId, 1024)}")

        glAttachShader(programId, shaderId)

        return shaderId
    }

    fun link() {
        glLinkProgram(programId)
        if (glGetProgrami(programId, GL_LINK_STATUS) == 0)
            error("Failed to link shader code: ${glGetProgramInfoLog(programId, 1024)}")

        if (vertexShaderId != 0) glDetachShader(programId, vertexShaderId)
        if (fragmentShaderId != 0) glDetachShader(programId, fragmentShaderId)

        glValidateProgram(programId)
        if (glGetProgrami(programId, GL_VALIDATE_STATUS) == 0)
            error("Error validating shader code: ${glGetProgramInfoLog(programId, 1024)}")
    }

    fun bind() = glUseProgram(programId)

    fun unbind() = glUseProgram(0)

    fun cleanUp() {
        unbind()
        if (programId != 0) glDeleteProgram(programId)
    }
}
