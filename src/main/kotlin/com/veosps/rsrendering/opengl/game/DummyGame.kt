@file:Suppress("NOTHING_TO_INLINE")

package com.veosps.rsrendering.opengl.game

import com.displee.compress.decompress
import com.github.michaelbull.logging.InlineLogger
import com.veosps.rsrendering.opengl.engine.*
import com.veosps.rsrendering.opengl.graph.Camera
import com.veosps.rsrendering.opengl.graph.Mesh
import com.veosps.rsrendering.opengl.graph.Texture
import com.veosps.rsrendering.runescape.cache.modelArchive
import com.veosps.rsrendering.runescape.cache.types.itemConfigList
import com.veosps.rsrendering.runescape.model.Model
import com.veosps.rsrendering.runescape.model.decoders.OSRSModelDecoder
import com.veosps.rsrendering.runescape.model.decoders.RS2ModelDecoder
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.springframework.stereotype.Component

private val logger = InlineLogger()

@Component
class DummyInitializer {

    fun start() {
        val gameLogic = DummyGame()
        val gameEngine = GameEngine("RS Renderer", 800, 600, true, gameLogic)
        gameEngine.run()
    }
}

class DummyGame : GameLogic {

    private val mouseSensitivity = 0.2f
    private val cameraPositionSteps = 0.05f

    private val camera = Camera()
    private val cameraIncrement = Vector3f()
    private val renderer = Renderer()

    private var gameItems: Array<GameItem> = emptyArray()

    private fun verticesToFloatArray(model: Model): FloatArray {
        val vertices = FloatArray(model.vertexCount * 3)

        for (vertex in 0 until model.vertexCount) {
            vertices[vertex * 3 + 0] = model.verticesXCoordinate[vertex].toFloat()
            vertices[vertex * 3 + 1] = model.verticesYCoordinate[vertex] * -1f
            vertices[vertex * 3 + 2] = model.verticesZCoordinate[vertex] * -1f
        }

        val scale = 15f
        for (vertex in vertices.indices) {
            vertices[vertex] /= scale
        }

        return vertices
    }

    private fun indicesToIntArray(model: Model): IntArray {
        val indices = IntArray(model.triangleCount * 3)

        for (index in 0 until model.triangleCount) {
            indices[index * 3 + 0] = model.faceIndicesA[index]
            indices[index * 3 + 1] = model.faceIndicesB[index]
            indices[index * 3 + 2] = model.faceIndicesC[index]
        }

        return indices
    }

    private fun texCoordsToFloatArray(model: Model): FloatArray {
        model.computeTextureUVCoordinates()

        val texCoords = FloatArray(model.triangleCount * 6)
        for (triangle in 0 until model.triangleCount) {
            val u = model.textureUCoordinates[triangle]
            val v = model.textureVCoordinates[triangle]
            texCoords[triangle * 6 + 0] = u[2]
            texCoords[triangle * 6 + 1] = v[2]
            texCoords[triangle * 6 + 2] = u[1]
            texCoords[triangle * 6 + 3] = v[1]
            texCoords[triangle * 6 + 4] = u[0]
            texCoords[triangle * 6 + 5] = v[0]
        }

        return texCoords
    }

    override fun init(window: Window) {
        renderer.init()

        val itemId = 6570

        val modelId = itemConfigList.single { it.id == itemId }.maleModel0
        logger.info { "Found model with id $modelId..." }

        val modelFile = modelArchive().readArchiveSector(9638) ?: error("Fuck off")

        val model = OSRSModelDecoder(modelFile.data).decode(modelId)
        val vertices = verticesToFloatArray(model)
        val indices = indicesToIntArray(model)
        val texCoords = texCoordsToFloatArray(model)

        val texture = Texture("./data/resources/textures/40.png")
        val mesh = Mesh(vertices, texCoords, indices, texture)
        val gameItem = GameItem(mesh)
        gameItem.scale = 0.5f
        gameItem.position(0f, -2f, -2f)
        gameItems = arrayOf(gameItem)
    }

    override fun input(window: Window, mouseInput: MouseInput) = with(window) {
        cameraIncrement.set(0.0f, 0.0f, 0.0f)

        if (forwards() || backwards() || left() || right() || up() || down())
            logger.debug { "Position(x=${camera.position.x}, y=${camera.position.y}, z=${camera.position.z})" }

        when {
            left() -> cameraIncrement.x = -5f
            right() -> cameraIncrement.x = 5f
            down() -> cameraIncrement.y = -5f
            up() -> cameraIncrement.y = 5f
            forwards() -> cameraIncrement.z = -5f
            backwards() -> cameraIncrement.z = 5f
        }
    }

    override fun update(interval: Float, mouseInput: MouseInput) {
        camera.move(
            offsetX = cameraIncrement.x * cameraPositionSteps,
            offsetY = cameraIncrement.y * cameraPositionSteps,
            offsetZ = cameraIncrement.z * cameraPositionSteps
        )

        if (mouseInput.rightButtonPressed) {
            logger.debug { "Rotation(x=${camera.rotation.x}, y=${camera.rotation.y}, z=${camera.rotation.z})" }
            val rotationVector = mouseInput.displayVector
            camera.rotate(rotationVector.x * mouseSensitivity, rotationVector.y * mouseSensitivity, 0f)
        }
    }

    override fun render(window: Window) = renderer.render(window, camera, gameItems)

    override fun cleanUp() {
        renderer.cleanUp()
        gameItems.forEach { it.cleanMesh() }
    }

    private inline fun Window.forwards() = isKeyPressed(GLFW_KEY_W)
    private inline fun Window.backwards() = isKeyPressed(GLFW_KEY_S)
    private inline fun Window.left() = isKeyPressed(GLFW_KEY_A)
    private inline fun Window.right() = isKeyPressed(GLFW_KEY_D)
    private inline fun Window.up() = isKeyPressed(GLFW_KEY_X) || isKeyPressed(GLFW_KEY_SPACE)
    private inline fun Window.down() = isKeyPressed(GLFW_KEY_Z) || isKeyPressed(GLFW_KEY_LEFT_CONTROL)
}