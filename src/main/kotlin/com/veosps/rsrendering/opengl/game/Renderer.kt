package com.veosps.rsrendering.opengl.game

import com.veosps.rsrendering.opengl.engine.GameItem
import com.veosps.rsrendering.opengl.engine.Window
import com.veosps.rsrendering.opengl.graphics.Camera
import com.veosps.rsrendering.opengl.graphics.ShaderProgram
import com.veosps.rsrendering.opengl.graphics.Transformation
import com.veosps.rsrendering.utils.loadResourceDataAsString
import org.joml.Matrix4f
import org.lwjgl.opengl.GL11

class Renderer {
    private val transformation: Transformation
    private var shaderProgram: ShaderProgram? = null

    init {
        transformation = Transformation()
    }

    fun init(window: Window) {
        // Create shader
        shaderProgram = ShaderProgram()
        shaderProgram?.createVertexShader(loadResourceDataAsString("/shaders/vertex.glsl"))
        shaderProgram?.createFragmentShader(loadResourceDataAsString("/shaders/fragment.glsl"))
        shaderProgram?.link()

        // Create uniforms for modelView and projection matrices and texture
        shaderProgram?.createUniform("projectionMatrix")
        shaderProgram?.createUniform("modelViewMatrix")
        shaderProgram?.createUniform("texture_sampler")
    }

    fun clear() {
        GL11.glClear(GL11.GL_COLOR_BUFFER_BIT or GL11.GL_DEPTH_BUFFER_BIT)
    }

    fun render(window: Window, camera: Camera, gameItems: Array<GameItem>) {
        clear()
        if (window.isResized) {
            GL11.glViewport(0, 0, window.width, window.height)
            window.isResized = false
        }
        shaderProgram?.bind()

        // Update projection Matrix
        val projectionMatrix: Matrix4f =
            transformation.getProjectionMatrix(FOV, window.width.toFloat(), window.height.toFloat(), Z_NEAR, Z_FAR)
        shaderProgram?.setUniform("projectionMatrix", projectionMatrix)

        // Update view Matrix
        val viewMatrix: Matrix4f = transformation.getViewMatrix(camera)
        shaderProgram?.setUniform("texture_sampler", 0)
        // Render each gameItem
        for (gameItem in gameItems) {
            // Set model view matrix for this item
            val modelViewMatrix: Matrix4f = transformation.getModelViewMatrix(gameItem, viewMatrix)
            shaderProgram?.setUniform("modelViewMatrix", modelViewMatrix)
            // Render the mes for this game item
            gameItem.getMesh().render()
        }
        shaderProgram?.unbind()
    }

    fun cleanup() {
        if (shaderProgram != null) {
            shaderProgram?.cleanup()
        }
    }

    companion object {
        /**
         * Field of View in Radians
         */
        private val FOV = Math.toRadians(60.0).toFloat()
        private const val Z_NEAR = 0.01f
        private const val Z_FAR = 1000f
    }
}