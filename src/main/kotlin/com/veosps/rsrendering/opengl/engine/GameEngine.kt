package com.veosps.rsrendering.opengl.engine


class GameEngine(
    windowTitle: String,
    width: Int,
    height: Int,
    vSync: Boolean = true,
    private val gameLogic: GameLogic,
) : Runnable {
    private val targetFps = 75
    private val targetUps = 30

    private val window = Window(windowTitle, width, height, vSync)
    private val mouseInput = MouseInput()
    private val timer = Timer()

    fun init() {
        window.init()
        timer.init()
        mouseInput.init(window)
        gameLogic.init(window)
    }

    private fun gameLoop() {
        var elapsedTime: Float
        var accumulator = 0f
        @Suppress("SameParameterValue") val interval = 1f / targetUps

        while (!window.windowShouldCLose()) {
            elapsedTime = timer.elapsedTime()
            accumulator += elapsedTime

            input()

            while (accumulator >= interval) {
                update(interval)
                accumulator -= interval
            }

            render()

            if (!window.vSync) sync()
        }
    }

    fun cleanUp() {
        gameLogic.cleanUp()
    }

    fun input() {
        mouseInput.input()
        gameLogic.input(window, mouseInput)
    }

    @Suppress("SameParameterValue")
    private fun update(interval: Float) = gameLogic.update(interval, mouseInput)

    fun render() {
        gameLogic.render(window)
        window.update()
    }

    private fun sync() {
        val loopSlot = 1f / targetFps
        val endTime = timer.lastLoopTime + loopSlot
        while (timer.time() < endTime) {
            Thread.sleep(1)
        }
    }

    override fun run() {
        try {
            init()
            gameLoop()
        } finally {
            cleanUp()
        }
    }
}