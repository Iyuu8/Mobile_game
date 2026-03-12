package com.game.medievalrpg.classes

import android.graphics.Color
import com.game.medievalrpg.combat.Skill
import com.game.medievalrpg.combat.SkillFactory

class Necromancer : CharacterClass(
    name = "Necromancer",
    description = "A dark mage who commands the dead. Summons undead minions and drains life from enemies.",
    baseHp = 90,
    baseAttack = 8,
    baseDefense = 5,
    baseMagicPower = 20,
    baseSpeed = 5,
    hpGrowth = 8f,
    attackGrowth = 1f,
    defenseGrowth = 1f,
    magicGrowth = 5f,
    speedGrowth = 0.5f
) {
    override val skills: List<Skill> = listOf(
        SkillFactory.SUMMON_SKELETON.copy(),
        SkillFactory.LIFE_DRAIN.copy(),
        SkillFactory.CURSE.copy(),
        SkillFactory.RAISE_DEAD_ARMY.copy()
    )
    override val primaryColor: Int = Color.parseColor("#4B0082")
    override val secondaryColor: Int = Color.parseColor("#00FF00")
}
