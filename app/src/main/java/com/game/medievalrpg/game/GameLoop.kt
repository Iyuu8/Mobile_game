package com.game.medievalrpg.game

class GameLoop(private val gameView: GameView) : Thread() {

    @Volatile var running: Boolean = false
    private val targetFPS: Int = 60
    private val targetFrameTimeNs: Long = 1_000_000_000L / targetFPS

    override fun run() {
        var lastTimeNs = System.nanoTime()
        var fpsTimer = 0L
        var frameCount = 0

        while (running) {
            val nowNs = System.nanoTime()
            val elapsedNs = nowNs - lastTimeNs
            lastTimeNs = nowNs
            val deltaTime = (elapsedNs / 1_000_000_000.0).toFloat().coerceIn(0f, 0.05f)

            gameView.update(deltaTime)
            gameView.render()

            fpsTimer += elapsedNs
            frameCount++
            if (fpsTimer >= 1_000_000_000L) {
                fpsTimer = 0; frameCount = 0
            }

            val frameTimeNs = System.nanoTime() - nowNs
            val sleepTimeNs = targetFrameTimeNs - frameTimeNs
            if (sleepTimeNs > 0) {
                try { sleep(sleepTimeNs / 1_000_000, (sleepTimeNs % 1_000_000).toInt()) }
                catch (e: InterruptedException) { break }
            }
        }
    }

    fun startLoop() { running = true; start() }

    fun stopLoop() {
        running = false
        try { join(1000) } catch (e: InterruptedException) { interrupt() }
    }
}
