package com.game.medievalrpg.items

enum class ItemRarity { COMMON, UNCOMMON, RARE, EPIC, LEGENDARY }
enum class ItemType { WEAPON, ARMOR, CONSUMABLE, QUEST, ACCESSORY }

abstract class Item(
    val id: String,
    val name: String,
    val description: String,
    val type: ItemType,
    val rarity: ItemRarity,
    val value: Int,
    val weight: Float = 1f
) {
    val rarityColor: Int get() = when (rarity) {
        ItemRarity.COMMON    -> 0xFFAAAAAA.toInt()
        ItemRarity.UNCOMMON  -> 0xFF00FF00.toInt()
        ItemRarity.RARE      -> 0xFF4444FF.toInt()
        ItemRarity.EPIC      -> 0xFFAA00FF.toInt()
        ItemRarity.LEGENDARY -> 0xFFFF8C00.toInt()
    }

    abstract fun toMap(): Map<String, Any>
}
