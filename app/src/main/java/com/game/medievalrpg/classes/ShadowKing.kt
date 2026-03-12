package com.game.medievalrpg.classes

import android.graphics.Color
import com.game.medievalrpg.combat.Skill
import com.game.medievalrpg.combat.SkillFactory

class ShadowKing : CharacterClass(
    name = "Shadow King",
    description = "The ultimate dark warrior. Combines stealth, magic and brute force. Unlocked class.",
    baseHp = 120,
    baseAttack = 20,
    baseDefense = 8,
    baseMagicPower = 18,
    baseSpeed = 8,
    hpGrowth = 14f,
    attackGrowth = 4.5f,
    defenseGrowth = 2f,
    magicGrowth = 4f,
    speedGrowth = 0.7f
) {
    override val skills: List<Skill> = listOf(
        SkillFactory.SHADOW_STEP.copy(),
        SkillFactory.DARK_BLADE.copy(),
        SkillFactory.SHADOW_CLONE.copy(),
        SkillFactory.VOID_EMBRACE.copy()
    )
    override val primaryColor: Int = Color.parseColor("#1A001A")
    override val secondaryColor: Int = Color.parseColor("#9400D3")
}
