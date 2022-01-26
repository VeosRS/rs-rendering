package com.veosps.rsrendering.opengl.engine

import org.joml.Vector2d
import org.joml.Vector2f
import org.lwjgl.glfw.GLFW.*

class MouseInput(
    private val previousPosition: Vector2d = Vector2d(-1.0, -1.0),
    private val currentPosition: Vector2d = Vector2d(0.0, 0.0),
    val displayVector: Vector2f = Vector2f()
) {

    private var windowActive = false

    var leftButtonPressed = false
        private set
    var rightButtonPressed = false
        private set

    fun init(window: Window) {
        glfwSetCursorPosCallback(window.windowHandle) { _, xPos, yPos ->
            currentPosition.x = xPos
            currentPosition.y = yPos
        }
        glfwSetCursorEnterCallback(window.windowHandle) { _, entered ->
            windowActive = entered
        }
        glfwSetMouseButtonCallback(window.windowHandle) { handle, button, action, mode ->
            leftButtonPressed = button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS
            rightButtonPressed = button == GLFW_MOUSE_BUTTON_2 && action == GLFW_PRESS
        }
    }

    fun input() {
        displayVector.x = 0f
        displayVector.y = 0f

        if (previousPosition.x > 0 && previousPosition.y > 0 && windowActive) {
            val deltaX = currentPosition.x - previousPosition.x
            val deltaY = currentPosition.y - previousPosition.y
            val rotateX = deltaX != 0.0
            val rotateY = deltaY != 0.0

            if (rotateX) displayVector.y = deltaX.toFloat()
            if (rotateY) displayVector.x = deltaY.toFloat()
        }

        previousPosition.x = currentPosition.x
        previousPosition.y = currentPosition.y
    }
}