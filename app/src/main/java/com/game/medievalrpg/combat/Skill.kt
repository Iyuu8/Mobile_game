package com.game.medievalrpg.combat

enum class SkillType {
    PHYSICAL, MAGIC, BUFF, DEBUFF, HEAL, SUMMON
}

enum class SkillTarget {
    SINGLE_ENEMY, ALL_ENEMIES, SELF, ALLY
}

data class Skill(
    val id: String,
    val name: String,
    val description: String,
    val type: SkillType,
    val target: SkillTarget,
    val baseDamage: Int,
    val manaCost: Int,
    val cooldownMax: Float,        // seconds
    var cooldownCurrent: Float = 0f,
    val range: Float = 150f,
    val aoeRadius: Float = 0f,
    val statusEffect: StatusEffect? = null,
    val damageMultiplier: Float = 1.0f,
    val healAmount: Int = 0,
    val summonType: String = ""
) {
    val isReady: Boolean get() = cooldownCurrent <= 0f

    fun use() {
        cooldownCurrent = cooldownMax
    }

    fun update(deltaTime: Float) {
        if (cooldownCurrent > 0f) {
            cooldownCurrent = maxOf(0f, cooldownCurrent - deltaTime)
        }
    }

    fun cooldownPercent(): Float = if (cooldownMax <= 0f) 0f else cooldownCurrent / cooldownMax
}

enum class StatusEffectType {
    POISON, BURN, FREEZE, STUN, CURSE, BLEED, SLOW, WEAKEN
}

data class StatusEffect(
    val type: StatusEffectType,
    val duration: Float,
    val tickDamage: Int = 0,
    val statModifier: Float = 1.0f
)

object SkillFactory {
    // Knight skills
    val SHIELD_BASH = Skill(
        id = "shield_bash",
        name = "Shield Bash",
        description = "Bash the enemy with your shield, stunning them.",
        type = SkillType.PHYSICAL,
        target = SkillTarget.SINGLE_ENEMY,
        baseDamage = 25,
        manaCost = 0,
        cooldownMax = 4f,
        statusEffect = StatusEffect(StatusEffectType.STUN, 1.5f),
        damageMultiplier = 1.2f
    )
    val HOLY_STRIKE = Skill(
        id = "holy_strike",
        name = "Holy Strike",
        description = "A divine strike that deals holy damage.",
        type = SkillType.MAGIC,
        target = SkillTarget.SINGLE_ENEMY,
        baseDamage = 40,
        manaCost = 20,
        cooldownMax = 5f,
        damageMultiplier = 1.5f
    )
    val BATTLE_CRY = Skill(
        id = "battle_cry",
        name = "Battle Cry",
        description = "Increase your attack temporarily.",
        type = SkillType.BUFF,
        target = SkillTarget.SELF,
        baseDamage = 0,
        manaCost = 15,
        cooldownMax = 12f,
        statusEffect = StatusEffect(StatusEffectType.WEAKEN, 5f, statModifier = 1.3f)
    )
    val IRON_FORTRESS = Skill(
        id = "iron_fortress",
        name = "Iron Fortress",
        description = "Greatly increase your defense for a short time.",
        type = SkillType.BUFF,
        target = SkillTarget.SELF,
        baseDamage = 0,
        manaCost = 25,
        cooldownMax = 15f,
        statusEffect = StatusEffect(StatusEffectType.SLOW, 6f, statModifier = 2.0f)
    )

    // Bandit skills
    val BACKSTAB = Skill(
        id = "backstab",
        name = "Backstab",
        description = "Deal massive damage from behind.",
        type = SkillType.PHYSICAL,
        target = SkillTarget.SINGLE_ENEMY,
        baseDamage = 60,
        manaCost = 0,
        cooldownMax = 6f,
        damageMultiplier = 2.5f
    )
    val SMOKE_BOMB = Skill(
        id = "smoke_bomb",
        name = "Smoke Bomb",
        description = "Throw a smoke bomb to evade enemies.",
        type = SkillType.BUFF,
        target = SkillTarget.SELF,
        baseDamage = 0,
        manaCost = 10,
        cooldownMax = 8f,
        statusEffect = StatusEffect(StatusEffectType.SLOW, 3f)
    )
    val POISON_BLADE = Skill(
        id = "poison_blade",
        name = "Poison Blade",
        description = "Coat your blade in poison.",
        type = SkillType.DEBUFF,
        target = SkillTarget.SINGLE_ENEMY,
        baseDamage = 15,
        manaCost = 15,
        cooldownMax = 7f,
        statusEffect = StatusEffect(StatusEffectType.POISON, 5f, tickDamage = 8)
    )
    val STEAL = Skill(
        id = "steal",
        name = "Steal",
        description = "Steal gold from an enemy.",
        type = SkillType.PHYSICAL,
        target = SkillTarget.SINGLE_ENEMY,
        baseDamage = 10,
        manaCost = 0,
        cooldownMax = 10f
    )

    // Necromancer skills
    val SUMMON_SKELETON = Skill(
        id = "summon_skeleton",
        name = "Summon Skeleton",
        description = "Raise a skeleton warrior to fight for you.",
        type = SkillType.SUMMON,
        target = SkillTarget.SELF,
        baseDamage = 0,
        manaCost = 30,
        cooldownMax = 10f,
        summonType = "SKELETON_SOLDIER"
    )
    val LIFE_DRAIN = Skill(
        id = "life_drain",
        name = "Life Drain",
        description = "Drain life from an enemy to heal yourself.",
        type = SkillType.MAGIC,
        target = SkillTarget.SINGLE_ENEMY,
        baseDamage = 35,
        manaCost = 25,
        cooldownMax = 5f,
        healAmount = 20,
        damageMultiplier = 1.3f
    )
    val CURSE = Skill(
        id = "curse",
        name = "Curse",
        description = "Curse an enemy, weakening their stats.",
        type = SkillType.DEBUFF,
        target = SkillTarget.SINGLE_ENEMY,
        baseDamage = 5,
        manaCost = 20,
        cooldownMax = 8f,
        statusEffect = StatusEffect(StatusEffectType.CURSE, 8f, statModifier = 0.7f)
    )
    val RAISE_DEAD_ARMY = Skill(
        id = "raise_dead_army",
        name = "Raise Dead Army",
        description = "Raise multiple skeletons at once.",
        type = SkillType.SUMMON,
        target = SkillTarget.SELF,
        baseDamage = 0,
        manaCost = 80,
        cooldownMax = 25f,
        aoeRadius = 200f,
        summonType = "SKELETON_ARMY"
    )

    // Wizard skills
    val FIREBALL = Skill(
        id = "fireball",
        name = "Fireball",
        description = "Hurl a ball of fire at enemies.",
        type = SkillType.MAGIC,
        target = SkillTarget.SINGLE_ENEMY,
        baseDamage = 55,
        manaCost = 25,
        cooldownMax = 3f,
        aoeRadius = 80f,
        statusEffect = StatusEffect(StatusEffectType.BURN, 3f, tickDamage = 10),
        damageMultiplier = 1.6f
    )
    val ICE_STORM = Skill(
        id = "ice_storm",
        name = "Ice Storm",
        description = "Call down a storm of ice to freeze enemies.",
        type = SkillType.MAGIC,
        target = SkillTarget.ALL_ENEMIES,
        baseDamage = 40,
        manaCost = 40,
        cooldownMax = 8f,
        aoeRadius = 150f,
        statusEffect = StatusEffect(StatusEffectType.FREEZE, 2f)
    )
    val LIGHTNING_BOLT = Skill(
        id = "lightning_bolt",
        name = "Lightning Bolt",
        description = "Strike an enemy with lightning.",
        type = SkillType.MAGIC,
        target = SkillTarget.SINGLE_ENEMY,
        baseDamage = 70,
        manaCost = 35,
        cooldownMax = 5f,
        damageMultiplier = 2.0f
    )
    val ARCANE_SHIELD = Skill(
        id = "arcane_shield",
        name = "Arcane Shield",
        description = "Create a magical shield to absorb damage.",
        type = SkillType.BUFF,
        target = SkillTarget.SELF,
        baseDamage = 0,
        manaCost = 30,
        cooldownMax = 12f,
        statusEffect = StatusEffect(StatusEffectType.SLOW, 5f, statModifier = 1.5f)
    )

    // Shadow King skills
    val SHADOW_STEP = Skill(
        id = "shadow_step",
        name = "Shadow Step",
        description = "Teleport behind an enemy.",
        type = SkillType.PHYSICAL,
        target = SkillTarget.SINGLE_ENEMY,
        baseDamage = 45,
        manaCost = 20,
        cooldownMax = 4f,
        damageMultiplier = 1.8f
    )
    val DARK_BLADE = Skill(
        id = "dark_blade",
        name = "Dark Blade",
        description = "Strike with a blade of pure darkness.",
        type = SkillType.MAGIC,
        target = SkillTarget.SINGLE_ENEMY,
        baseDamage = 65,
        manaCost = 30,
        cooldownMax = 5f,
        statusEffect = StatusEffect(StatusEffectType.CURSE, 4f, statModifier = 0.8f),
        damageMultiplier = 2.0f
    )
    val SHADOW_CLONE = Skill(
        id = "shadow_clone",
        name = "Shadow Clone",
        description = "Create a shadow clone that mimics your attacks.",
        type = SkillType.SUMMON,
        target = SkillTarget.SELF,
        baseDamage = 0,
        manaCost = 40,
        cooldownMax = 15f,
        summonType = "SHADOW_CLONE"
    )
    val VOID_EMBRACE = Skill(
        id = "void_embrace",
        name = "Void Embrace",
        description = "Embrace the void, dealing massive area damage.",
        type = SkillType.MAGIC,
        target = SkillTarget.ALL_ENEMIES,
        baseDamage = 90,
        manaCost = 60,
        cooldownMax = 20f,
        aoeRadius = 200f,
        damageMultiplier = 2.5f
    )
}
