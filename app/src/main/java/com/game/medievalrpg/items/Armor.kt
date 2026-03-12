package com.game.medievalrpg.items

import kotlin.random.Random

enum class ArmorSlot { HELMET, CHEST, BOOTS, ACCESSORY }

class Armor(
    id: String,
    name: String,
    description: String,
    rarity: ItemRarity,
    value: Int,
    val slot: ArmorSlot,
    val defenseBonus: Int,
    val hpBonus: Int = 0,
    val speedBonus: Int = 0,
    val magicBonus: Int = 0,
    val attackBonus: Int = 0
) : Item(id, name, description, ItemType.ARMOR, rarity, value) {

    override fun toMap(): Map<String, Any> = mapOf(
        "id" to id, "type" to "ARMOR", "slot" to slot.name,
        "defenseBonus" to defenseBonus, "hpBonus" to hpBonus,
        "speedBonus" to speedBonus, "magicBonus" to magicBonus
    )

    companion object {
        val ALL_ARMOR: List<Armor> = listOf(
            // Helmets
            Armor("leather_cap", "Leather Cap", "Basic head protection.", ItemRarity.COMMON, 20, ArmorSlot.HELMET, 2, hpBonus = 10),
            Armor("iron_helm", "Iron Helm", "A sturdy iron helmet.", ItemRarity.COMMON, 80, ArmorSlot.HELMET, 5, hpBonus = 20),
            Armor("steel_helm", "Steel Helm", "Protects against heavy blows.", ItemRarity.UNCOMMON, 200, ArmorSlot.HELMET, 9, hpBonus = 35),
            Armor("knights_helm", "Knight's Helm", "The helmet of a veteran knight.", ItemRarity.RARE, 500, ArmorSlot.HELMET, 14, hpBonus = 50),
            Armor("crown_of_valor", "Crown of Valor", "Worn by legendary warriors.", ItemRarity.EPIC, 1500, ArmorSlot.HELMET, 20, hpBonus = 80, attackBonus = 5),
            Armor("shadow_crown", "Shadow Crown", "A crown of absolute darkness.", ItemRarity.LEGENDARY, 5000, ArmorSlot.HELMET, 28, hpBonus = 120, attackBonus = 10, magicBonus = 15),

            // Chestplates
            Armor("cloth_robe", "Cloth Robe", "Thin magical protection.", ItemRarity.COMMON, 25, ArmorSlot.CHEST, 1, magicBonus = 5),
            Armor("leather_armor", "Leather Armor", "Flexible leather chest piece.", ItemRarity.COMMON, 60, ArmorSlot.CHEST, 4, hpBonus = 15),
            Armor("chain_mail", "Chain Mail", "Interlocked rings of iron.", ItemRarity.UNCOMMON, 180, ArmorSlot.CHEST, 8, hpBonus = 30),
            Armor("plate_armor", "Plate Armor", "Heavy steel plate armor.", ItemRarity.RARE, 600, ArmorSlot.CHEST, 16, hpBonus = 60),
            Armor("mithril_plate", "Mithril Plate", "Forged from rare mithril.", ItemRarity.EPIC, 2000, ArmorSlot.CHEST, 24, hpBonus = 100, speedBonus = 1),
            Armor("void_armor", "Void Armor", "Armor woven from the void itself.", ItemRarity.LEGENDARY, 7000, ArmorSlot.CHEST, 35, hpBonus = 150, speedBonus = 2, magicBonus = 20),

            // Boots
            Armor("worn_boots", "Worn Boots", "Old boots barely holding together.", ItemRarity.COMMON, 15, ArmorSlot.BOOTS, 1, speedBonus = 1),
            Armor("leather_boots", "Leather Boots", "Comfortable and durable.", ItemRarity.COMMON, 50, ArmorSlot.BOOTS, 2, speedBonus = 2),
            Armor("iron_boots", "Iron Boots", "Heavy but protective.", ItemRarity.UNCOMMON, 150, ArmorSlot.BOOTS, 5, hpBonus = 15, speedBonus = 1),
            Armor("shadow_boots", "Shadow Boots", "Move silently through darkness.", ItemRarity.RARE, 400, ArmorSlot.BOOTS, 6, speedBonus = 4, attackBonus = 3),
            Armor("wind_walkers", "Wind Walkers", "Run faster than the wind.", ItemRarity.EPIC, 1200, ArmorSlot.BOOTS, 8, speedBonus = 6, hpBonus = 30),
            Armor("void_treads", "Void Treads", "Step between worlds.", ItemRarity.LEGENDARY, 4000, ArmorSlot.BOOTS, 12, speedBonus = 8, hpBonus = 60, attackBonus = 8),

            // Accessories
            Armor("copper_ring", "Copper Ring", "A simple copper ring.", ItemRarity.COMMON, 30, ArmorSlot.ACCESSORY, 0, hpBonus = 15),
            Armor("silver_amulet", "Silver Amulet", "A protective silver charm.", ItemRarity.UNCOMMON, 120, ArmorSlot.ACCESSORY, 2, hpBonus = 25, magicBonus = 5),
            Armor("ruby_pendant", "Ruby Pendant", "Enhances your attack power.", ItemRarity.RARE, 450, ArmorSlot.ACCESSORY, 3, attackBonus = 8, hpBonus = 20),
            Armor("sages_ring", "Sage's Ring", "Amplifies magical abilities.", ItemRarity.EPIC, 1400, ArmorSlot.ACCESSORY, 4, magicBonus = 20, hpBonus = 40),
            Armor("amulet_of_kings", "Amulet of Kings", "Only the worthy may wear this.", ItemRarity.LEGENDARY, 6000, ArmorSlot.ACCESSORY, 8, hpBonus = 100, attackBonus = 15, magicBonus = 15, speedBonus = 3)
        )

        fun getById(id: String): Armor? = ALL_ARMOR.find { it.id == id }
        fun getBySlot(slot: ArmorSlot): List<Armor> = ALL_ARMOR.filter { it.slot == slot }
        fun getByRarity(rarity: ItemRarity): List<Armor> = ALL_ARMOR.filter { it.rarity == rarity }
        fun getRandomForLevel(level: Int): Armor {
            val rarity = when {
                level >= 20 -> if (Random.nextFloat() < 0.1f) ItemRarity.LEGENDARY else if (Random.nextFloat() < 0.3f) ItemRarity.EPIC else ItemRarity.RARE
                level >= 10 -> if (Random.nextFloat() < 0.2f) ItemRarity.RARE else if (Random.nextFloat() < 0.4f) ItemRarity.UNCOMMON else ItemRarity.COMMON
                else -> if (Random.nextFloat() < 0.15f) ItemRarity.UNCOMMON else ItemRarity.COMMON
            }
            return getByRarity(rarity).randomOrNull() ?: ALL_ARMOR.first()
        }
    }
}
