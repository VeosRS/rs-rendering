package com.veosps.rsrendering.opengl.game

import com.veosps.rsrendering.opengl.engine.*
import com.veosps.rsrendering.opengl.graphics.Camera
import com.veosps.rsrendering.opengl.graphics.Mesh
import com.veosps.rsrendering.opengl.graphics.Texture
import com.veosps.rsrendering.runescape.model.ModelData
import com.veosps.rsrendering.runescape.model.decoders.OSRSModelDecoder
import com.veosps.rsrendering.utils.loadResourceData
import com.veosps.rsrendering.utils.loadResourceDataAsString
import org.joml.Vector2f
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW

fun main() {
    val gameLogic = DummyGame()
    val gameEngine = GameEngine("RS Renderer", 450, 450, false, gameLogic)
    gameEngine.run()
}

class DummyGame : IGameLogic {
    private val cameraInc: Vector3f
    private val renderer: Renderer
    private val camera: Camera
    private var gameItems: Array<GameItem> = arrayOf()

    init {
        renderer = Renderer()
        camera = Camera()
        cameraInc = Vector3f()
    }

    private fun verticesToFloatArray(model: ModelData): FloatArray {
        val vertices = FloatArray(model.vertexCount * 3)
        for (vertex in 0 until model.vertexCount) {
            vertices[vertex * 3] = model.verticesXCoordinate[vertex].toFloat()
            vertices[vertex * 3 + 1] = model.verticesYCoordinate[vertex] * -1f
            vertices[vertex * 3 + 2] = model.verticesZCoordinate[vertex] * -1f
        }
        val scale = 10f
        for (vertex in vertices.indices) {
            vertices[vertex] /= scale
        }
        return vertices
    }

    private fun indicesToIntArray(model: ModelData): IntArray {
        val indices = IntArray(model.triangleCount * 3)
        for (index in 0 until model.triangleCount) {
            indices[index * 3] = model.faceIndicesA[index]
            indices[index * 3 + 1] = model.faceIndicesB[index]
            indices[index * 3 + 2] = model.faceIndicesC[index]
        }
        return indices
    }

    private fun texCoordsToFloatArray(model: ModelData): FloatArray {
        model.computeTextureUVCoordinates()
        val texCoords = FloatArray(model.triangleCount * 6)
        for (triangle in 0 until model.triangleCount) {
            val u: FloatArray = model.textureUCoordinates.get(triangle)
            val v: FloatArray = model.textureVCoordinates.get(triangle)
            texCoords[triangle * 6] = u[2]
            texCoords[triangle * 6 + 1] = v[2]
            texCoords[triangle * 6 + 2] = u[1]
            texCoords[triangle * 6 + 3] = v[1]
            texCoords[triangle * 6 + 4] = u[0]
            texCoords[triangle * 6 + 5] = v[0]
        }
        return texCoords
    }

    override fun init(window: Window) {
        renderer.init(window)
        val data = loadResourceData("/models/9638.dat")
        val model: ModelData = OSRSModelDecoder(data).decode()
        val vertices = verticesToFloatArray(model)
        val indices = indicesToIntArray(model)
        val texCoords = texCoordsToFloatArray(model)
        val texture = Texture("./data/resources/textures/40.png")
        val mesh = Mesh(vertices, texCoords, indices, texture)
        val gameItem1 = GameItem(mesh)
        gameItem1.scale = 0.5f
        gameItem1.setPosition(0f, 0f, -2f)
        gameItems = arrayOf(gameItem1)
    }

    override fun input(window: Window, mouseInput: MouseInput) {
        cameraInc[0f, 0f] = 0f
        if (window.isKeyPressed(GLFW.GLFW_KEY_W)) {
            cameraInc.z = -5f
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_S)) {
            cameraInc.z = 5f
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_A)) {
            cameraInc.x = -5f
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_D)) {
            cameraInc.x = 5f
        }
        if (window.isKeyPressed(GLFW.GLFW_KEY_Z)) {
            cameraInc.y = -5f
        } else if (window.isKeyPressed(GLFW.GLFW_KEY_X)) {
            cameraInc.y = 5f
        }
    }

    override fun update(interval: Float, mouseInput: MouseInput) {
        // Update camera position
        camera.movePosition(cameraInc.x * CAMERA_POS_STEP, cameraInc.y * CAMERA_POS_STEP, cameraInc.z * CAMERA_POS_STEP)

        // Update camera based on mouse
        if (mouseInput.isRightButtonPressed) {
            val rotVec: Vector2f = mouseInput.displVec
            camera.moveRotation(rotVec.x * MOUSE_SENSITIVITY, rotVec.y * MOUSE_SENSITIVITY, 0f)
        }
    }

    override fun render(window: Window) {
        renderer.render(window, camera, gameItems)
    }

    override fun cleanup() {
        renderer.cleanup()
        for (gameItem in gameItems) {
            gameItem.getMesh().cleanUp()
        }
    }

    companion object {
        private const val MOUSE_SENSITIVITY = 0.2f
        private const val CAMERA_POS_STEP = 0.05f
    }
}