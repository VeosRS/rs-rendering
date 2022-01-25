package com.veosps.rsrendering.opengl.engine

class GameEngine(windowTitle: String?, width: Int, height: Int, vSync: Boolean, gameLogic: IGameLogic) :
    Runnable {
    private val window: Window
    private val timer: Timer
    private val gameLogic: IGameLogic
    private val mouseInput: MouseInput

    init {
        window = Window(windowTitle!!, width, height, vSync)
        mouseInput = MouseInput()
        this.gameLogic = gameLogic
        timer = Timer()
    }

    override fun run() {
        try {
            init()
            gameLoop()
        } catch (excp: Exception) {
            excp.printStackTrace()
        } finally {
            cleanup()
        }
    }

    @Throws(Exception::class)
    protected fun init() {
        window.init()
        timer.init()
        mouseInput.init(window)
        gameLogic.init(window)
    }

    protected fun gameLoop() {
        var elapsedTime: Float
        var accumulator = 0f
        val interval = 1f / TARGET_UPS
        val running = true
        while (running && !window.windowShouldClose()) {
            elapsedTime = timer.elapsedTime
            accumulator += elapsedTime
            input()
            while (accumulator >= interval) {
                update(interval)
                accumulator -= interval
            }
            render()
            if (!window.isvSync()) {
                sync()
            }
        }
    }

    protected fun cleanup() {
        gameLogic.cleanup()
    }

    private fun sync() {
        val loopSlot = 1f / TARGET_FPS
        val endTime: Double = timer.lastLoopTime + loopSlot
        while (timer.time < endTime) {
            try {
                Thread.sleep(1)
            } catch (ie: InterruptedException) {
            }
        }
    }

    protected fun input() {
        mouseInput.input(window)
        gameLogic.input(window, mouseInput)
    }

    protected fun update(interval: Float) {
        gameLogic.update(interval, mouseInput)
    }

    protected fun render() {
        gameLogic.render(window)
        window.update()
    }

    companion object {
        const val TARGET_FPS = 75
        const val TARGET_UPS = 30
    }
}