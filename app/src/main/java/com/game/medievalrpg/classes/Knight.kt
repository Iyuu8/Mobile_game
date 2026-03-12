package com.game.medievalrpg.classes

import android.graphics.Color
import com.game.medievalrpg.combat.Skill
import com.game.medievalrpg.combat.SkillFactory

class Knight : CharacterClass(
    name = "Knight",
    description = "A stalwart warrior clad in heavy armor. High defense and HP with powerful melee strikes.",
    baseHp = 150,
    baseAttack = 15,
    baseDefense = 12,
    baseMagicPower = 3,
    baseSpeed = 4,
    hpGrowth = 18f,
    attackGrowth = 3f,
    defenseGrowth = 3.5f,
    magicGrowth = 0.5f,
    speedGrowth = 0.3f
) {
    override val skills: List<Skill> = listOf(
        SkillFactory.SHIELD_BASH.copy(),
        SkillFactory.HOLY_STRIKE.copy(),
        SkillFactory.BATTLE_CRY.copy(),
        SkillFactory.IRON_FORTRESS.copy()
    )
    override val primaryColor: Int = Color.parseColor("#C0C0C0")
    override val secondaryColor: Int = Color.parseColor("#FFD700")
}
