package com.game.medievalrpg.combat

import com.game.medievalrpg.entities.Enemy
import com.game.medievalrpg.entities.Player
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.random.Random

object DamageCalculator {

    private const val CRIT_MULTIPLIER = 2.0f

    fun calculatePlayerDamage(player: Player, enemy: Enemy, isCritical: Boolean = false): Int {
        val base = player.baseAttackDamage.toFloat()
        val reduced = max(1f, base - enemy.defense * 0.5f)
        val critMult = if (isCritical) CRIT_MULTIPLIER else 1.0f
        val variance = 0.9f + Random.nextFloat() * 0.2f
        return max(1, (reduced * critMult * variance).toInt())
    }

    fun calculateSkillDamage(player: Player, skill: Skill, enemy: Enemy): Int {
        val base = when (skill.type) {
            SkillType.PHYSICAL -> player.baseAttackDamage.toFloat() * skill.damageMultiplier
            SkillType.MAGIC    -> player.magicPower.toFloat() * skill.damageMultiplier
            else -> skill.baseDamage.toFloat()
        }
        val reduced = max(1f, base + skill.baseDamage - enemy.defense * 0.3f)
        val variance = 0.85f + Random.nextFloat() * 0.3f
        return max(1, (reduced * variance).toInt())
    }

    fun calculateEnemyDamage(enemy: Enemy, player: Player): Int {
        val base = enemy.attack.toFloat()
        val reduced = max(1f, base - player.defense * 0.6f)
        val variance = 0.85f + Random.nextFloat() * 0.3f
        return max(1, (reduced * variance).toInt())
    }

    fun isCriticalHit(critChance: Float): Boolean = Random.nextFloat() < critChance

    fun getAoeTargets(source: Float, sourceY: Float, enemies: List<Enemy>, radius: Float): List<Enemy> {
        return enemies.filter { enemy ->
            val dx = enemy.centerX - source
            val dy = enemy.centerY - sourceY
            sqrt(dx * dx + dy * dy) <= radius
        }
    }
}
