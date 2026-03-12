package com.game.medievalrpg.classes

import android.graphics.Color
import com.game.medievalrpg.combat.Skill
import com.game.medievalrpg.combat.SkillFactory

class Wizard : CharacterClass(
    name = "Wizard",
    description = "A powerful arcane mage. Deals massive elemental damage but has low physical defense.",
    baseHp = 80,
    baseAttack = 6,
    baseDefense = 4,
    baseMagicPower = 25,
    baseSpeed = 5,
    hpGrowth = 7f,
    attackGrowth = 0.8f,
    defenseGrowth = 0.8f,
    magicGrowth = 6f,
    speedGrowth = 0.5f
) {
    override val skills: List<Skill> = listOf(
        SkillFactory.FIREBALL.copy(),
        SkillFactory.ICE_STORM.copy(),
        SkillFactory.LIGHTNING_BOLT.copy(),
        SkillFactory.ARCANE_SHIELD.copy()
    )
    override val primaryColor: Int = Color.parseColor("#1E90FF")
    override val secondaryColor: Int = Color.parseColor("#FF4500")
}
