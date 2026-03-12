package com.game.medievalrpg.entities

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.game.medievalrpg.classes.*
import com.game.medievalrpg.combat.Skill
import com.game.medievalrpg.items.Inventory
import com.game.medievalrpg.items.Weapon

class Player(
    x: Float,
    y: Float,
    val characterClass: CharacterClass
) : GameObject(x, y, 48f, 64f) {

    var level: Int = 1
    var exp: Int = 0
    var maxHp: Int = characterClass.baseHp
    var hp: Int = maxHp
    var maxMana: Int = 100
    var mana: Int = maxMana
    var attack: Int = characterClass.baseAttack
    var defense: Int = characterClass.baseDefense
    var magicPower: Int = characterClass.baseMagicPower
    var speed: Int = characterClass.baseSpeed
    var gold: Int = 0

    val inventory = Inventory()
    val skills: List<Skill> = characterClass.skills.map { it.copy() }

    var isAttacking: Boolean = false
    var attackCooldown: Float = 0f
    val attackCooldownMax: Float = 0.5f
    var isInvincible: Boolean = false
    var invincibleTimer: Float = 0f

    private val paint = Paint()
    private var animTimer: Float = 0f
    private var attackTimer: Float = 0f

    var onLevelUp: ((Int) -> Unit)? = null
    var onDeath: (() -> Unit)? = null

    init {
        refreshStats()
    }

    fun refreshStats() {
        val lvl = level
        maxHp = characterClass.getHpAtLevel(lvl) + inventory.getTotalHpBonus()
        attack = characterClass.getAttackAtLevel(lvl) + inventory.getTotalAttackBonus()
        defense = characterClass.getDefenseAtLevel(lvl) + inventory.getTotalDefenseBonus()
        magicPower = characterClass.getMagicAtLevel(lvl) + inventory.getTotalMagicBonus()
        speed = characterClass.getSpeedAtLevel(lvl) + inventory.getTotalSpeedBonus()
        hp = minOf(hp, maxHp)
    }

    override fun update(deltaTime: Float) {
        move(deltaTime)
        animTimer += deltaTime
        if (attackCooldown > 0f) attackCooldown -= deltaTime
        if (isInvincible) {
            invincibleTimer -= deltaTime
            if (invincibleTimer <= 0f) isInvincible = false
        }
        if (isAttacking) {
            attackTimer += deltaTime
            if (attackTimer >= 0.3f) { isAttacking = false; attackTimer = 0f }
        }
        skills.forEach { it.update(deltaTime) }
        mana = minOf(maxMana, mana + (2 * deltaTime).toInt())
    }

    override fun draw(canvas: Canvas, offsetX: Float, offsetY: Float) {
        val sx = x - offsetX
        val sy = y - offsetY

        // Body
        paint.color = characterClass.primaryColor
        canvas.drawRect(sx + 8, sy + 20, sx + width - 8, sy + height, paint)

        // Head
        paint.color = 0xFFFFCC99.toInt()
        canvas.drawOval(sx + 10, sy + 2, sx + width - 10, sy + 26, paint)

        // Eyes
        paint.color = Color.BLACK
        if (facingRight) {
            canvas.drawCircle(sx + 28, sy + 12, 3f, paint)
        } else {
            canvas.drawCircle(sx + 20, sy + 12, 3f, paint)
        }

        // Weapon indicator
        if (isAttacking) {
            paint.color = characterClass.secondaryColor
            paint.alpha = 200
            if (facingRight) canvas.drawRect(sx + width, sy + 20, sx + width + 20, sy + 36, paint)
            else canvas.drawRect(sx - 20, sy + 20, sx, sy + 36, paint)
            paint.alpha = 255
        }

        // Class accent
        paint.color = characterClass.secondaryColor
        canvas.drawRect(sx + 10, sy + 24, sx + width - 10, sy + 30, paint)

        // HP bar above character
        val hpBarW = width
        val hpBarH = 6f
        paint.color = Color.DKGRAY
        canvas.drawRect(sx, sy - 10f, sx + hpBarW, sy - 10f + hpBarH, paint)
        paint.color = Color.GREEN
        canvas.drawRect(sx, sy - 10f, sx + hpBarW * (hp.toFloat() / maxHp), sy - 10f + hpBarH, paint)

        // Invincibility flash
        if (isInvincible && (animTimer * 8).toInt() % 2 == 0) {
            paint.color = Color.WHITE
            paint.alpha = 120
            canvas.drawRect(sx, sy, sx + width, sy + height, paint)
            paint.alpha = 255
        }
    }

    fun takeDamage(damage: Int) {
        if (isInvincible) return
        val actualDamage = maxOf(1, damage - defense)
        hp -= actualDamage
        isInvincible = true
        invincibleTimer = 0.6f
        if (hp <= 0) { hp = 0; onDeath?.invoke() }
    }

    fun heal(amount: Int) { hp = minOf(maxHp, hp + amount) }

    fun gainExp(amount: Int) {
        exp += amount
        val expNeeded = characterClass.getExpToNextLevel(level)
        if (exp >= expNeeded) {
            exp -= expNeeded
            level++
            refreshStats()
            hp = maxHp
            mana = maxMana
            onLevelUp?.invoke(level)
        }
    }

    fun performAttack(): Boolean {
        if (attackCooldown > 0f) return false
        isAttacking = true
        attackTimer = 0f
        attackCooldown = attackCooldownMax
        return true
    }

    fun useSkill(index: Int): Skill? {
        val skill = skills.getOrNull(index) ?: return null
        if (!skill.isReady || mana < skill.manaCost) return null
        mana -= skill.manaCost
        skill.use()
        return skill
    }

    fun getExpPercent(): Float {
        val needed = characterClass.getExpToNextLevel(level).toFloat()
        return if (needed <= 0f) 1f else exp / needed
    }

    fun getHpPercent(): Float = if (maxHp <= 0) 0f else hp.toFloat() / maxHp
    fun getManaPercent(): Float = if (maxMana <= 0) 0f else mana.toFloat() / maxMana

    val attackRange: Float get() = inventory.equippedWeapon?.range ?: 80f
    val baseAttackDamage: Int get() = attack + (inventory.equippedWeapon?.attackBonus ?: 0)
}
