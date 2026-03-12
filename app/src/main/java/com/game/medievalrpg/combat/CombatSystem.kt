package com.game.medievalrpg.combat

import com.game.medievalrpg.entities.Enemy
import com.game.medievalrpg.entities.Player

data class CombatEvent(
    val type: EventType,
    val damage: Int = 0,
    val isCritical: Boolean = false,
    val message: String = "",
    val x: Float = 0f,
    val y: Float = 0f,
    var displayTimer: Float = 1.2f
) {
    enum class EventType { DAMAGE, HEAL, CRIT, MISS, STATUS, LEVEL_UP, SKILL_USE, ENEMY_DEATH }
}

class CombatSystem {

    val events = mutableListOf<CombatEvent>()
    private val damageNumbers = mutableListOf<DamageNumber>()

    data class DamageNumber(
        var x: Float, var y: Float,
        val text: String,
        val color: Int,
        var timer: Float = 1.0f,
        var vy: Float = -80f
    )

    fun update(deltaTime: Float) {
        events.removeAll { event ->
            event.displayTimer -= deltaTime
            event.displayTimer <= 0f
        }
        damageNumbers.removeAll { num ->
            num.timer -= deltaTime
            num.y += num.vy * deltaTime
            num.timer <= 0f
        }
    }

    fun playerAttacksEnemy(player: Player, enemy: Enemy): CombatEvent {
        val critChance = player.inventory.equippedWeapon?.critChance ?: 0.05f
        val isCrit = DamageCalculator.isCriticalHit(critChance)
        val damage = DamageCalculator.calculatePlayerDamage(player, enemy, isCrit)
        val actualDamage = enemy.takeDamage(damage)
        val eventType = if (isCrit) CombatEvent.EventType.CRIT else CombatEvent.EventType.DAMAGE
        val event = CombatEvent(eventType, actualDamage, isCrit, if (isCrit) "Critical!" else "",
            enemy.centerX, enemy.y)
        events.add(event)
        addDamageNumber(enemy.centerX, enemy.y - 10f, "-$actualDamage",
            if (isCrit) 0xFFFF4500.toInt() else 0xFFFF6666.toInt())
        return event
    }

    fun playerUsesSkill(player: Player, skill: Skill, enemies: List<Enemy>): List<CombatEvent> {
        val resultEvents = mutableListOf<CombatEvent>()
        if (skill.type == SkillType.HEAL || skill.type == SkillType.BUFF) {
            val healAmt = skill.healAmount + (player.magicPower * 0.3f).toInt()
            player.heal(healAmt)
            val event = CombatEvent(CombatEvent.EventType.HEAL, healAmt, false, "+$healAmt HP",
                player.centerX, player.y)
            events.add(event)
            addDamageNumber(player.centerX, player.y, "+$healAmt", 0xFF00FF00.toInt())
            resultEvents.add(event)
            return resultEvents
        }
        val targets = if (skill.aoeRadius > 0f) {
            DamageCalculator.getAoeTargets(player.centerX, player.centerY, enemies, skill.aoeRadius)
        } else {
            enemies.filter { it.distanceTo(player) <= skill.range }.take(1)
        }
        targets.forEach { enemy ->
            val damage = DamageCalculator.calculateSkillDamage(player, skill, enemy)
            enemy.takeDamage(damage)
            skill.statusEffect?.let { effect ->
                when (effect.type) {
                    StatusEffectType.STUN -> enemy.stun(effect.duration)
                    else -> {}
                }
            }
            val event = CombatEvent(CombatEvent.EventType.SKILL_USE, damage, false, skill.name,
                enemy.centerX, enemy.y)
            events.add(event)
            addDamageNumber(enemy.centerX, enemy.y - 10f, "-$damage", 0xFFAA44FF.toInt())
            resultEvents.add(event)
        }
        if (skill.healAmount > 0) {
            player.heal(skill.healAmount)
            addDamageNumber(player.centerX, player.y, "+${skill.healAmount}", 0xFF00FF00.toInt())
        }
        return resultEvents
    }

    private fun addDamageNumber(x: Float, y: Float, text: String, color: Int) {
        damageNumbers.add(DamageNumber(x, y, text, color))
    }

    fun getDamageNumbers(): List<DamageNumber> = damageNumbers.toList()
}
