package com.game.medievalrpg.entities

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import kotlin.math.abs
import kotlin.math.sqrt
import kotlin.random.Random

enum class EnemyType(
    val displayName: String,
    val baseHp: Int,
    val baseAttack: Int,
    val baseDefense: Int,
    val baseSpeed: Float,
    val expReward: Int,
    val goldReward: Int,
    val detectionRange: Float,
    val attackRange: Float,
    val primaryColor: Int,
    val secondaryColor: Int,
    val size: Float = 48f
) {
    GOBLIN_SCOUT("Goblin Scout", 30, 5, 2, 120f, 15, 5, 200f, 70f, 0xFF228B22.toInt(), 0xFFFFFF00.toInt()),
    GOBLIN_WARRIOR("Goblin Warrior", 55, 10, 4, 100f, 30, 10, 200f, 70f, 0xFF006400.toInt(), 0xFFA0522D.toInt()),
    GOBLIN_SHAMAN("Goblin Shaman", 40, 15, 3, 90f, 40, 15, 250f, 150f, 0xFF2E8B57.toInt(), 0xFF9400D3.toInt()),
    GOBLIN_CHIEF("Goblin Chief", 200, 22, 8, 95f, 150, 60, 220f, 80f, 0xFF006400.toInt(), 0xFFFF0000.toInt(), 60f),
    ORC_GRUNT("Orc Grunt", 80, 14, 6, 85f, 50, 18, 180f, 80f, 0xFF556B2F.toInt(), 0xFFCC4400.toInt()),
    ORC_BERSERKER("Orc Berserker", 100, 22, 5, 110f, 70, 25, 200f, 80f, 0xFF556B2F.toInt(), 0xFFFF4500.toInt()),
    ORC_WARLORD("Orc Warlord", 350, 35, 15, 90f, 300, 120, 220f, 90f, 0xFF556B2F.toInt(), 0xFFFFD700.toInt(), 64f),
    ARMORED_ORC("Armored Orc", 150, 18, 18, 70f, 100, 40, 180f, 85f, 0xFF808080.toInt(), 0xFF556B2F.toInt()),
    FOREST_TROLL("Forest Troll", 200, 25, 12, 70f, 120, 50, 200f, 100f, 0xFF3CB371.toInt(), 0xFF8B4513.toInt(), 72f),
    CAVE_TROLL("Cave Troll", 280, 30, 18, 65f, 180, 70, 190f, 100f, 0xFF696969.toInt(), 0xFF8B4513.toInt(), 72f),
    ICE_TROLL("Ice Troll", 320, 35, 20, 60f, 200, 80, 200f, 100f, 0xFF87CEEB.toInt(), 0xFFFFFFFF.toInt(), 72f),
    ELDER_TROLL("Elder Troll", 600, 50, 30, 55f, 500, 200, 220f, 110f, 0xFF2F4F4F.toInt(), 0xFFFFD700.toInt(), 80f),
    SKELETON_SOLDIER("Skeleton Soldier", 50, 12, 5, 80f, 35, 12, 200f, 75f, 0xFFF5F5DC.toInt(), 0xFF808080.toInt()),
    SKELETON_ARCHER("Skeleton Archer", 40, 16, 3, 85f, 40, 15, 280f, 200f, 0xFFF5F5DC.toInt(), 0xFF8B4513.toInt()),
    SKELETON_MAGE("Skeleton Mage", 35, 20, 2, 75f, 50, 20, 280f, 180f, 0xFFF5F5DC.toInt(), 0xFF9400D3.toInt()),
    SKELETON_KNIGHT("Skeleton Knight", 120, 20, 14, 75f, 90, 35, 190f, 80f, 0xFFF5F5DC.toInt(), 0xFFC0C0C0.toInt()),
    WOLF("Wolf", 45, 12, 3, 150f, 25, 8, 220f, 65f, 0xFF808080.toInt(), 0xFFFFFF00.toInt()),
    DIRE_WOLF("Dire Wolf", 80, 20, 5, 160f, 55, 18, 240f, 70f, 0xFF2F4F4F.toInt(), 0xFFFF4500.toInt(), 56f),
    SHADOW_WOLF("Shadow Wolf", 100, 25, 8, 170f, 70, 25, 250f, 70f, 0xFF1C1C1C.toInt(), 0xFF9400D3.toInt(), 56f),
    ALPHA_WOLF("Alpha Wolf", 250, 35, 12, 155f, 200, 80, 260f, 75f, 0xFF1C1C1C.toInt(), 0xFFFF0000.toInt(), 64f),
    YOUNG_DRAGON("Young Dragon", 400, 45, 20, 100f, 400, 150, 280f, 130f, 0xFFFF4500.toInt(), 0xFFFFD700.toInt(), 80f),
    FIRE_DRAGON("Fire Dragon", 700, 65, 30, 95f, 700, 250, 300f, 150f, 0xFFFF0000.toInt(), 0xFFFF8C00.toInt(), 96f),
    ICE_DRAGON("Ice Dragon", 700, 60, 35, 90f, 700, 250, 300f, 150f, 0xFF87CEEB.toInt(), 0xFFFFFFFF.toInt(), 96f),
    ANCIENT_DRAGON("Ancient Dragon", 1500, 90, 50, 85f, 2000, 800, 350f, 180f, 0xFF4B0082.toInt(), 0xFFFFD700.toInt(), 112f),
    DARK_KNIGHT("Dark Knight", 300, 42, 28, 85f, 350, 130, 220f, 95f, 0xFF1C1C1C.toInt(), 0xFF9400D3.toInt(), 64f),
    VAMPIRE("Vampire", 250, 38, 15, 130f, 300, 120, 240f, 90f, 0xFF8B0000.toInt(), 0xFF4B0082.toInt()),
    DEMON("Demon", 450, 55, 25, 110f, 500, 180, 260f, 100f, 0xFFCC0000.toInt(), 0xFFFF8C00.toInt(), 72f),
    LICH("Lich", 800, 80, 40, 70f, 1500, 600, 300f, 200f, 0xFF4B0082.toInt(), 0xFF00FF00.toInt(), 80f)
}

class Enemy(
    x: Float,
    y: Float,
    val type: EnemyType,
    val level: Int = 1
) : GameObject(x, y, type.size, type.size) {

    val maxHp: Int = (type.baseHp * (1 + 0.1f * (level - 1))).toInt()
    var hp: Int = maxHp
    val attack: Int = (type.baseAttack * (1 + 0.08f * (level - 1))).toInt()
    val defense: Int = (type.baseDefense * (1 + 0.06f * (level - 1))).toInt()
    val speed: Float = type.baseSpeed
    val expReward: Int = (type.expReward * (1 + 0.1f * (level - 1))).toInt()
    val goldReward: Int = (type.goldReward * (1 + 0.1f * (level - 1))).toInt()

    private enum class AIState { PATROL, CHASE, ATTACK, STUNNED, DEAD }
    private var aiState: AIState = AIState.PATROL

    private var patrolTimer: Float = 0f
    private var patrolDirX: Float = 1f
    private var patrolDirY: Float = 0f
    private var patrolChangeTimer: Float = 0f
    private var attackCooldown: Float = 0f
    private val attackCooldownMax: Float = 1.5f
    private var stunTimer: Float = 0f
    private var animTimer: Float = 0f

    var isDead: Boolean = false
    var onDeath: ((Enemy) -> Unit)? = null
    var onAttackPlayer: ((Int) -> Unit)? = null

    private val paint = Paint()

    val patrolOriginX: Float = x
    val patrolOriginY: Float = y
    private val patrolRadius: Float = 120f

    override fun update(deltaTime: Float) {
        if (isDead) return
        animTimer += deltaTime
        if (attackCooldown > 0f) attackCooldown -= deltaTime

        when (aiState) {
            AIState.STUNNED -> {
                stunTimer -= deltaTime
                velocityX = 0f; velocityY = 0f
                if (stunTimer <= 0f) aiState = AIState.PATROL
            }
            AIState.PATROL -> doPatrol(deltaTime)
            AIState.CHASE  -> { /* handled by GameEngine with player ref */ }
            AIState.ATTACK -> { /* handled by GameEngine */ }
            AIState.DEAD   -> {}
        }
        move(deltaTime)
    }

    private fun doPatrol(deltaTime: Float) {
        patrolChangeTimer -= deltaTime
        if (patrolChangeTimer <= 0f) {
            patrolChangeTimer = 2f + Random.nextFloat() * 3f
            val angle = Random.nextDouble() * Math.PI * 2
            patrolDirX = Math.cos(angle).toFloat()
            patrolDirY = Math.sin(angle).toFloat()
            if (Random.nextFloat() < 0.3f) { patrolDirX = 0f; patrolDirY = 0f }
        }
        val distFromOrigin = sqrt((x - patrolOriginX) * (x - patrolOriginX) + (y - patrolOriginY) * (y - patrolOriginY))
        if (distFromOrigin > patrolRadius) {
            val dx = patrolOriginX - x; val dy = patrolOriginY - y
            val len = sqrt(dx * dx + dy * dy)
            patrolDirX = dx / len; patrolDirY = dy / len
        }
        velocityX = patrolDirX * speed * 0.4f
        velocityY = patrolDirY * speed * 0.4f
    }

    fun updateAI(playerX: Float, playerY: Float, deltaTime: Float) {
        if (isDead || aiState == AIState.STUNNED) return
        val dx = playerX - centerX; val dy = playerY - centerY
        val dist = sqrt(dx * dx + dy * dy)

        when {
            dist <= type.attackRange -> {
                aiState = AIState.ATTACK
                velocityX = 0f; velocityY = 0f
                if (attackCooldown <= 0f) {
                    val dmg = maxOf(1, attack - 0)
                    onAttackPlayer?.invoke(dmg)
                    attackCooldown = attackCooldownMax
                }
            }
            dist <= type.detectionRange -> {
                aiState = AIState.CHASE
                val len = sqrt(dx * dx + dy * dy)
                velocityX = (dx / len) * speed
                velocityY = (dy / len) * speed
                if (dx > 0) facingRight = true else if (dx < 0) facingRight = false
            }
            else -> {
                if (aiState == AIState.CHASE) aiState = AIState.PATROL
            }
        }
    }

    override fun draw(canvas: Canvas, offsetX: Float, offsetY: Float) {
        if (isDead) return
        val sx = x - offsetX; val sy = y - offsetY
        val w = width; val h = height

        // Body
        paint.color = type.primaryColor
        canvas.drawRect(sx + w * 0.1f, sy + h * 0.35f, sx + w * 0.9f, sy + h, paint)

        // Head
        paint.color = type.primaryColor
        canvas.drawOval(sx + w * 0.15f, sy, sx + w * 0.85f, sy + h * 0.45f, paint)

        // Eyes
        paint.color = type.secondaryColor
        val eyeY = sy + h * 0.18f
        if (facingRight) {
            canvas.drawCircle(sx + w * 0.6f, eyeY, w * 0.08f, paint)
        } else {
            canvas.drawCircle(sx + w * 0.4f, eyeY, w * 0.08f, paint)
        }

        // HP bar
        val hpBarW = w; val hpBarH = 5f
        paint.color = Color.DKGRAY
        canvas.drawRect(sx, sy - 10f, sx + hpBarW, sy - 10f + hpBarH, paint)
        val hpFraction = hp.toFloat() / maxHp
        paint.color = when {
            hpFraction > 0.6f -> Color.GREEN
            hpFraction > 0.3f -> Color.YELLOW
            else -> Color.RED
        }
        canvas.drawRect(sx, sy - 10f, sx + hpBarW * hpFraction, sy - 10f + hpBarH, paint)

        // Name label
        paint.color = Color.WHITE
        paint.textSize = 16f
        canvas.drawText(type.displayName, sx, sy - 14f, paint)
    }

    fun takeDamage(damage: Int): Int {
        val actualDamage = maxOf(1, damage - defense)
        hp -= actualDamage
        if (hp <= 0) { hp = 0; isDead = true; onDeath?.invoke(this) }
        return actualDamage
    }

    fun stun(duration: Float) {
        aiState = AIState.STUNNED
        stunTimer = duration
        velocityX = 0f; velocityY = 0f
    }

    fun getHpPercent(): Float = if (maxHp <= 0) 0f else hp.toFloat() / maxHp
}
