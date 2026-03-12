package com.game.medievalrpg.classes

import android.graphics.Color
import com.game.medievalrpg.combat.Skill
import com.game.medievalrpg.combat.SkillFactory

class Bandit : CharacterClass(
    name = "Bandit",
    description = "A cunning rogue who strikes from the shadows. High damage and speed, low defense.",
    baseHp = 100,
    baseAttack = 18,
    baseDefense = 6,
    baseMagicPower = 4,
    baseSpeed = 9,
    hpGrowth = 10f,
    attackGrowth = 4f,
    defenseGrowth = 1.5f,
    magicGrowth = 0.8f,
    speedGrowth = 0.8f
) {
    override val skills: List<Skill> = listOf(
        SkillFactory.BACKSTAB.copy(),
        SkillFactory.SMOKE_BOMB.copy(),
        SkillFactory.POISON_BLADE.copy(),
        SkillFactory.STEAL.copy()
    )
    override val primaryColor: Int = Color.parseColor("#2F4F2F")
    override val secondaryColor: Int = Color.parseColor("#8B0000")
}
