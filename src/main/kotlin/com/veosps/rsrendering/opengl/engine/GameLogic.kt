package com.veosps.rsrendering.opengl.engine


interface GameLogic {
    fun init(window: Window)
    fun input(window: Window, mouseInput: MouseInput)
    fun update(interval: Float, mouseInput: MouseInput)
    fun render(window: Window)
    fun cleanUp()
}