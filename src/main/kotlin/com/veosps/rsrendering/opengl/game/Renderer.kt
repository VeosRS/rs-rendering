package com.veosps.rsrendering.opengl.game

import com.veosps.rsrendering.opengl.engine.GameItem
import com.veosps.rsrendering.opengl.engine.Window
import com.veosps.rsrendering.opengl.graph.Camera
import com.veosps.rsrendering.opengl.graph.ShaderProgram
import com.veosps.rsrendering.opengl.graph.Transformation
import com.veosps.rsrendering.utils.loadResourceDataAsString
import com.veosps.rsrendering.utils.radians
import org.lwjgl.opengl.GL11.*

private val FOV = radians(60.0f).toFloat()
private const val Z_NEAR = 0.01f
private const val Z_FAR = 1000.0f

class Renderer(
    private val vertexShader: String? = null,
    private val fragmentShader: String? = null,
    private val transformation: Transformation = Transformation(),
    private var shaderProgram: ShaderProgram? = null
) {
    private val projectionMatrixName = "projectionMatrix"
    private val modelViewMatrixName = "modelViewMatrix"
    private val textureSamplerName = "textureSampler"

    fun cleanUp() = shaderProgram?.cleanUp()
    private fun clear() = glClear(GL_COLOR_BUFFER_BIT or GL_DEPTH_BUFFER_BIT)

    fun init() {
        shaderProgram = ShaderProgram()
        shaderProgram?.createVertexShader(vertexShader ?: loadResourceDataAsString("/shaders/vertex.glsl"))
            ?: error("Shader program was not found.")
        shaderProgram?.createFragmentShader(fragmentShader ?: loadResourceDataAsString("/shaders/fragment.glsl"))
            ?: error("Shader program was not found.")
        shaderProgram?.link() ?: error("Shader program was not found.")

        shaderProgram?.createUniform(projectionMatrixName) ?: error("Shader program was not found.")
        shaderProgram?.createUniform(modelViewMatrixName) ?: error("Shader program was not found.")
        shaderProgram?.createUniform(textureSamplerName) ?: error("Shader program was not found.")
    }

    fun render(window: Window, camera: Camera, gameItems: Array<GameItem>) {
        clear()

        if (window.resized) {
            glViewport(0, 0, window.width, window.height)
            window.resized = false
        }

        shaderProgram?.bind() ?: error("Shader program was not found.")

        val projectionMatrix = transformation.projectionMatrix(
            FOV, window.width.toFloat(), window.height.toFloat(), Z_NEAR, Z_FAR
        )

        shaderProgram?.uniform(projectionMatrixName, projectionMatrix) ?: error("Shader program was not found.")

        val viewMatrix = transformation.viewMatrix(camera)

        shaderProgram?.uniform(textureSamplerName, 0) ?: error("Shader program was not found.")
        gameItems.forEach {
            val modelViewMatrix = transformation.modelViewMatrix(it, viewMatrix)
            shaderProgram?.uniform(modelViewMatrixName, modelViewMatrix) ?: error("Shader program was not found.")
            it.renderMesh()
        }

        shaderProgram?.unbind() ?: error("Shader program was not found.")
    }
}