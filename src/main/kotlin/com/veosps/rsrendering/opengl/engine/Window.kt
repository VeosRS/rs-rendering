package com.veosps.rsrendering.opengl.engine

import org.lwjgl.glfw.GLFW.*
import org.lwjgl.glfw.GLFWErrorCallback
import org.lwjgl.opengl.GL.createCapabilities
import org.lwjgl.opengl.GL11.*
import org.lwjgl.system.MemoryUtil.NULL

class Window(
    private val title: String,
    var width: Int,
    var height: Int,
    var resized: Boolean = false,
    val vSync: Boolean = true
) {

    var windowHandle: Long = -1L

    fun setClearColor(red: Float, green: Float, blue: Float, alpha: Float) =
        glClearColor(red, green, blue, alpha)

    fun isKeyPressed(keyCode: Int) =
        glfwGetKey(windowHandle, keyCode) == GLFW_PRESS

    fun windowShouldCLose() =
        glfwWindowShouldClose(windowHandle)

    fun init() {
        GLFWErrorCallback.createPrint(System.err).set()

        if (!glfwInit()) error("Unable to initialize GLFW.")

        glfwDefaultWindowHints()
        glfwWindowHint(GLFW_VISIBLE, GL_FALSE)
        glfwWindowHint(GLFW_RESIZABLE, GL_TRUE)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MAJOR, 3)
        glfwWindowHint(GLFW_CONTEXT_VERSION_MINOR, 2)
        glfwWindowHint(GLFW_OPENGL_PROFILE, GLFW_OPENGL_CORE_PROFILE)
        glfwWindowHint(GLFW_OPENGL_FORWARD_COMPAT, GL_TRUE)

        windowHandle = glfwCreateWindow(width, height, title, NULL, NULL)

        glfwSetFramebufferSizeCallback(windowHandle) { _, width, height ->
            this.width = width
            this.height = height
            this.resized = true
        }

        glfwSetKeyCallback(windowHandle) {window, key, _, action, _ ->
            if (key == GLFW_KEY_ESCAPE && action == GLFW_RELEASE)
                glfwSetWindowShouldClose(window, true)
        }

        val videoMode = glfwGetVideoMode(glfwGetPrimaryMonitor()) ?: error("Failed to get video mode.")
        glfwSetWindowPos(windowHandle, (videoMode.width() - width) / 2, (videoMode.height() - height) / 2)

        glfwMakeContextCurrent(windowHandle)

        if (vSync) glfwSwapInterval(1)

        glfwShowWindow(windowHandle)

        createCapabilities()

        glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        glEnable(GL_DEPTH_TEST)
    }

    fun update() {
        glfwSwapBuffers(windowHandle)
        glfwPollEvents()
    }
}