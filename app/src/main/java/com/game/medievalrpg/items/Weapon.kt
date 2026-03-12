package com.game.medievalrpg.items

import kotlin.random.Random

enum class WeaponType { SWORD, DAGGER, STAFF, BOW, AXE, HAMMER, SPEAR }

class Weapon(
    id: String,
    name: String,
    description: String,
    rarity: ItemRarity,
    value: Int,
    val weaponType: WeaponType,
    val attackBonus: Int,
    val magicBonus: Int = 0,
    val speedBonus: Int = 0,
    val critChance: Float = 0.05f,
    val range: Float = 80f
) : Item(id, name, description, ItemType.WEAPON, rarity, value) {

    override fun toMap(): Map<String, Any> = mapOf(
        "id" to id, "type" to "WEAPON", "weaponType" to weaponType.name,
        "attackBonus" to attackBonus, "magicBonus" to magicBonus,
        "speedBonus" to speedBonus, "critChance" to critChance
    )

    companion object {
        val ALL_WEAPONS: List<Weapon> = listOf(
            // Common swords
            Weapon("rusty_sword", "Rusty Sword", "A worn blade barely holding its edge.", ItemRarity.COMMON, 10, WeaponType.SWORD, 5),
            Weapon("iron_sword", "Iron Sword", "A reliable iron sword.", ItemRarity.COMMON, 50, WeaponType.SWORD, 10),
            Weapon("steel_sword", "Steel Sword", "Forged from quality steel.", ItemRarity.UNCOMMON, 150, WeaponType.SWORD, 18),
            Weapon("knights_blade", "Knight's Blade", "A sword carried by veteran knights.", ItemRarity.RARE, 400, WeaponType.SWORD, 28, critChance = 0.10f),
            Weapon("holy_avenger", "Holy Avenger", "Blessed by the light itself.", ItemRarity.EPIC, 1200, WeaponType.SWORD, 45, magicBonus = 20, critChance = 0.15f),
            Weapon("excalibur", "Excalibur", "The legendary sword of kings.", ItemRarity.LEGENDARY, 5000, WeaponType.SWORD, 70, magicBonus = 35, speedBonus = 3, critChance = 0.25f),

            // Daggers
            Weapon("rusty_dagger", "Rusty Dagger", "A small corroded blade.", ItemRarity.COMMON, 8, WeaponType.DAGGER, 4, speedBonus = 1),
            Weapon("iron_dagger", "Iron Dagger", "Light and quick.", ItemRarity.COMMON, 40, WeaponType.DAGGER, 8, speedBonus = 2, critChance = 0.10f),
            Weapon("shadow_dagger", "Shadow Dagger", "Forged in darkness.", ItemRarity.RARE, 350, WeaponType.DAGGER, 22, speedBonus = 4, critChance = 0.18f),
            Weapon("venom_fang", "Venom Fang", "Drips with deadly poison.", ItemRarity.EPIC, 1000, WeaponType.DAGGER, 35, speedBonus = 5, critChance = 0.22f),
            Weapon("death_whisper", "Death's Whisper", "Kills silently and surely.", ItemRarity.LEGENDARY, 4000, WeaponType.DAGGER, 55, speedBonus = 8, critChance = 0.35f),

            // Staffs
            Weapon("wooden_staff", "Wooden Staff", "A basic mage's staff.", ItemRarity.COMMON, 30, WeaponType.STAFF, 2, magicBonus = 8),
            Weapon("crystal_staff", "Crystal Staff", "Channels magic effectively.", ItemRarity.UNCOMMON, 200, WeaponType.STAFF, 3, magicBonus = 18),
            Weapon("arcane_staff", "Arcane Staff", "Amplifies arcane spells.", ItemRarity.RARE, 500, WeaponType.STAFF, 5, magicBonus = 30, critChance = 0.08f),
            Weapon("void_scepter", "Void Scepter", "Channels the power of the void.", ItemRarity.EPIC, 1500, WeaponType.STAFF, 8, magicBonus = 50, critChance = 0.12f),
            Weapon("archmage_staff", "Archmage's Staff", "Wielded by the greatest wizards.", ItemRarity.LEGENDARY, 6000, WeaponType.STAFF, 12, magicBonus = 80, critChance = 0.20f),

            // Bows
            Weapon("short_bow", "Short Bow", "A simple hunting bow.", ItemRarity.COMMON, 35, WeaponType.BOW, 7, range = 200f),
            Weapon("hunters_bow", "Hunter's Bow", "Crafted by skilled hunters.", ItemRarity.UNCOMMON, 180, WeaponType.BOW, 15, speedBonus = 1, range = 220f),
            Weapon("elven_bow", "Elven Bow", "Crafted with elven precision.", ItemRarity.RARE, 450, WeaponType.BOW, 25, speedBonus = 2, critChance = 0.14f, range = 250f),
            Weapon("dragon_bow", "Dragon Bow", "Strung with a dragon's sinew.", ItemRarity.EPIC, 1300, WeaponType.BOW, 40, speedBonus = 3, critChance = 0.18f, range = 280f),

            // Axes and Hammers
            Weapon("battle_axe", "Battle Axe", "A heavy cleaving axe.", ItemRarity.UNCOMMON, 130, WeaponType.AXE, 20, critChance = 0.08f),
            Weapon("great_hammer", "Great Hammer", "Crushes armor with brute force.", ItemRarity.RARE, 380, WeaponType.HAMMER, 32, critChance = 0.06f),
            Weapon("war_spear", "War Spear", "Reach out and strike foes.", ItemRarity.UNCOMMON, 120, WeaponType.SPEAR, 14, range = 120f)
        )

        fun getById(id: String): Weapon? = ALL_WEAPONS.find { it.id == id }
        fun getByRarity(rarity: ItemRarity): List<Weapon> = ALL_WEAPONS.filter { it.rarity == rarity }
        fun getRandomForLevel(level: Int): Weapon {
            val rarity = when {
                level >= 20 -> if (Random.nextFloat() < 0.1f) ItemRarity.LEGENDARY else if (Random.nextFloat() < 0.3f) ItemRarity.EPIC else ItemRarity.RARE
                level >= 10 -> if (Random.nextFloat() < 0.2f) ItemRarity.RARE else if (Random.nextFloat() < 0.4f) ItemRarity.UNCOMMON else ItemRarity.COMMON
                else -> if (Random.nextFloat() < 0.15f) ItemRarity.UNCOMMON else ItemRarity.COMMON
            }
            return getByRarity(rarity).randomOrNull() ?: ALL_WEAPONS.first()
        }
    }
}
