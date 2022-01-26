package com.veosps.rsrendering.opengl.engine

class Timer {
    var lastLoopTime = 0.0
        private set

    fun init() {
        lastLoopTime = time()
    }

    fun time() = System.nanoTime() / 1_000_000_000.0

    fun elapsedTime(): Float {
        val time = time()
        val elapsedTime = (time - lastLoopTime).toFloat()
        lastLoopTime = time

        return elapsedTime
    }
}