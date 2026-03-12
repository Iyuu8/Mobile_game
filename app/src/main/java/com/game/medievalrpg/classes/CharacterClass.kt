package com.game.medievalrpg.classes

import com.game.medievalrpg.combat.Skill

abstract class CharacterClass(
    val name: String,
    val description: String,
    val baseHp: Int,
    val baseAttack: Int,
    val baseDefense: Int,
    val baseMagicPower: Int,
    val baseSpeed: Int,
    val hpGrowth: Float,
    val attackGrowth: Float,
    val defenseGrowth: Float,
    val magicGrowth: Float,
    val speedGrowth: Float
) {
    abstract val skills: List<Skill>
    abstract val primaryColor: Int
    abstract val secondaryColor: Int

    fun getHpAtLevel(level: Int): Int = (baseHp + hpGrowth * (level - 1)).toInt()
    fun getAttackAtLevel(level: Int): Int = (baseAttack + attackGrowth * (level - 1)).toInt()
    fun getDefenseAtLevel(level: Int): Int = (baseDefense + defenseGrowth * (level - 1)).toInt()
    fun getMagicAtLevel(level: Int): Int = (baseMagicPower + magicGrowth * (level - 1)).toInt()
    fun getSpeedAtLevel(level: Int): Int = (baseSpeed + speedGrowth * (level - 1)).toInt()

    fun getExpToNextLevel(level: Int): Int = (100 * level * 1.2f).toInt()
}
