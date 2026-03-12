package com.game.medievalrpg.graphics

class Animation(
    private val frames: List<Int>,
    private val frameDuration: Float,
    private val looping: Boolean = true
) {
    private var currentFrameIndex: Int = 0
    private var timer: Float = 0f
    var isFinished: Boolean = false
        private set

    val currentFrame: Int get() = frames.getOrElse(currentFrameIndex) { frames.first() }

    fun update(deltaTime: Float) {
        if (isFinished) return
        timer += deltaTime
        if (timer >= frameDuration) {
            timer -= frameDuration
            currentFrameIndex++
            if (currentFrameIndex >= frames.size) {
                if (looping) currentFrameIndex = 0
                else { currentFrameIndex = frames.size - 1; isFinished = true }
            }
        }
    }

    fun reset() { currentFrameIndex = 0; timer = 0f; isFinished = false }

    companion object {
        fun walkRight(frameDuration: Float = 0.1f) = Animation(listOf(0, 1, 2, 3), frameDuration)
        fun walkLeft(frameDuration: Float = 0.1f) = Animation(listOf(4, 5, 6, 7), frameDuration)
        fun attack(frameDuration: Float = 0.08f) = Animation(listOf(8, 9, 10, 11), frameDuration, looping = false)
        fun idle(frameDuration: Float = 0.2f) = Animation(listOf(0, 1), frameDuration)
        fun death(frameDuration: Float = 0.12f) = Animation(listOf(12, 13, 14, 15), frameDuration, looping = false)
        fun hurt(frameDuration: Float = 0.08f) = Animation(listOf(16, 17), frameDuration, looping = false)
    }
}

class AnimationController {
    private val animations = mutableMapOf<String, Animation>()
    private var currentState: String = "idle"

    fun addAnimation(state: String, animation: Animation) { animations[state] = animation }
    fun getAnimation(state: String): Animation? = animations[state]
    fun getCurrentAnimation(): Animation? = animations[currentState]

    fun setState(state: String) {
        if (currentState == state) return
        currentState = state
        animations[state]?.reset()
    }

    fun getCurrentState(): String = currentState

    fun update(deltaTime: Float) { getCurrentAnimation()?.update(deltaTime) }

    val currentFrame: Int get() = getCurrentAnimation()?.currentFrame ?: 0
}
