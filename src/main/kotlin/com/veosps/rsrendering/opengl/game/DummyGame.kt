@file:Suppress("NOTHING_TO_INLINE")

package com.veosps.rsrendering.opengl.game

import com.displee.compress.decompress
import com.github.michaelbull.logging.InlineLogger
import com.veosps.rsrendering.opengl.engine.*
import com.veosps.rsrendering.opengl.graph.Camera
import com.veosps.rsrendering.opengl.graph.Mesh
import com.veosps.rsrendering.runescape.cache.modelArchive
import com.veosps.rsrendering.runescape.cache.types.itemConfigList
import com.veosps.rsrendering.runescape.decoders.model.ModelDecoder
import com.veosps.rsrendering.runescape.loaders.OSRSTextureLoader
import com.veosps.rsrendering.utils.buildRSMesh
import org.joml.Vector3f
import org.lwjgl.glfw.GLFW.*
import org.springframework.stereotype.Component

private val logger = InlineLogger()

@Component
class DummyInitializer(private val dummyGame: DummyGame) {
    fun start() = GameEngine("RS Renderer", 800, 600, true, dummyGame).run()
}

@Component
class DummyGame(
    private val textureLoader: OSRSTextureLoader
) : GameLogic {

    private val mouseSensitivity = 0.2f
    private val cameraPositionStep = 0.05f

    private val camera = Camera()
    private val cameraIncrement = Vector3f()
    private val renderer = Renderer()

    private var gameItems: Array<GameItem> = emptyArray()

    override fun init(window: Window) {
        renderer.init()

        val itemId = 6570

        val modelId = itemConfigList.single { it.id == itemId }.maleModel0
        logger.info { "Found model with id $modelId..." }

        val modelFile = modelArchive().readArchiveSector(modelId) ?: error("Fuck off")
        val model = ModelDecoder(modelFile.decompress()).decode(modelId)

        val rsMesh = buildRSMesh(model)

        val vertices = rsMesh.verticesToFloatArray()
        val indices = rsMesh.indicesToIntArray()
        val texCoords = rsMesh.textureCoordinatesToFloatArray()

        val texture = rsMesh.material ?: error("Yeah, no, you will need a material...")
        val mesh = Mesh(vertices, texCoords, indices, texture)
        val gameItem = GameItem(mesh)
        gameItem.scale = 0.5f
        gameItem.position(0f, -2f, -2f)
        gameItems = arrayOf(gameItem)
    }

    override fun input(window: Window, mouseInput: MouseInput) = with(window) {
        cameraIncrement.set(0.0f, 0.0f, 0.0f)

        //if (forwards() || backwards() || left() || right() || up() || down())
        //logger.debug { "Position(x=${camera.position.x}, y=${camera.position.y}, z=${camera.position.z})" }

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
            offsetX = cameraIncrement.x() * cameraPositionStep,
            offsetY = cameraIncrement.y() * cameraPositionStep,
            offsetZ = cameraIncrement.z() * cameraPositionStep
        )

        if (mouseInput.rightButtonPressed) {
            //logger.debug { "Rotation(x=${camera.rotation.x}, y=${camera.rotation.y}, z=${camera.rotation.z})" }
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