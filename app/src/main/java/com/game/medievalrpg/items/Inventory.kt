package com.game.medievalrpg.items

import org.json.JSONArray
import org.json.JSONObject

class Inventory(val maxCapacity: Int = 30) {
    private val items = mutableListOf<Item>()

    var equippedWeapon: Weapon? = null
    var equippedHelmet: Armor? = null
    var equippedChest: Armor? = null
    var equippedBoots: Armor? = null
    var equippedAccessory: Armor? = null

    val size: Int get() = items.size
    val isFull: Boolean get() = items.size >= maxCapacity

    fun addItem(item: Item): Boolean {
        if (isFull) return false
        items.add(item)
        return true
    }

    fun removeItem(item: Item): Boolean = items.remove(item)
    fun removeItemById(id: String): Boolean = items.removeAll { it.id == id }

    fun getItems(): List<Item> = items.toList()
    fun getWeapons(): List<Weapon> = items.filterIsInstance<Weapon>()
    fun getArmor(): List<Armor> = items.filterIsInstance<Armor>()
    fun findItem(id: String): Item? = items.find { it.id == id }

    fun equipWeapon(weapon: Weapon) {
        equippedWeapon?.let { items.add(it) }
        items.remove(weapon)
        equippedWeapon = weapon
    }

    fun equipArmor(armor: Armor) {
        val old = when (armor.slot) {
            ArmorSlot.HELMET    -> equippedHelmet.also { equippedHelmet = armor }
            ArmorSlot.CHEST     -> equippedChest.also { equippedChest = armor }
            ArmorSlot.BOOTS     -> equippedBoots.also { equippedBoots = armor }
            ArmorSlot.ACCESSORY -> equippedAccessory.also { equippedAccessory = armor }
        }
        old?.let { items.add(it) }
        items.remove(armor)
    }

    fun unequipWeapon() {
        equippedWeapon?.let { if (!isFull) { items.add(it); equippedWeapon = null } }
    }

    fun getTotalDefenseBonus(): Int =
        (equippedHelmet?.defenseBonus ?: 0) +
        (equippedChest?.defenseBonus ?: 0) +
        (equippedBoots?.defenseBonus ?: 0) +
        (equippedAccessory?.defenseBonus ?: 0)

    fun getTotalAttackBonus(): Int =
        (equippedWeapon?.attackBonus ?: 0) +
        (equippedHelmet?.attackBonus ?: 0) +
        (equippedChest?.attackBonus ?: 0) +
        (equippedBoots?.attackBonus ?: 0) +
        (equippedAccessory?.attackBonus ?: 0)

    fun getTotalMagicBonus(): Int =
        (equippedWeapon?.magicBonus ?: 0) +
        (equippedHelmet?.magicBonus ?: 0) +
        (equippedChest?.magicBonus ?: 0) +
        (equippedAccessory?.magicBonus ?: 0)

    fun getTotalHpBonus(): Int =
        (equippedHelmet?.hpBonus ?: 0) +
        (equippedChest?.hpBonus ?: 0) +
        (equippedBoots?.hpBonus ?: 0) +
        (equippedAccessory?.hpBonus ?: 0)

    fun getTotalSpeedBonus(): Int =
        (equippedWeapon?.speedBonus ?: 0) +
        (equippedBoots?.speedBonus ?: 0) +
        (equippedAccessory?.speedBonus ?: 0)

    fun getTotalValue(): Int = items.sumOf { it.value }

    fun toJson(): JSONArray {
        val arr = JSONArray()
        items.forEach { item ->
            val obj = JSONObject(item.toMap().mapValues { it.value.toString() })
            arr.put(obj)
        }
        return arr
    }

    companion object {
        fun fromJson(json: JSONArray): Inventory {
            val inv = Inventory()
            for (i in 0 until json.length()) {
                val obj = json.getJSONObject(i)
                when (obj.optString("type")) {
                    "WEAPON" -> Weapon.getById(obj.optString("id"))?.let { inv.addItem(it) }
                    "ARMOR"  -> Armor.getById(obj.optString("id"))?.let { inv.addItem(it) }
                }
            }
            return inv
        }
    }
}
