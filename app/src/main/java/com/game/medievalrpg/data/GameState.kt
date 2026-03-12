package com.game.medievalrpg.data

import android.content.Context
import org.json.JSONObject
import org.json.JSONArray

data class PlayerSaveData(
    val name: String,
    val characterClass: String,
    val level: Int,
    val exp: Int,
    val hp: Int,
    val maxHp: Int,
    val attack: Int,
    val defense: Int,
    val magicPower: Int,
    val speed: Int,
    val gold: Int,
    val x: Float,
    val y: Float,
    val currentZone: String,
    val inventoryJson: String,
    val completedQuests: List<String>,
    val activeQuests: List<String>
)

data class GameState(
    var isNewGame: Boolean = true,
    var playerSaveData: PlayerSaveData? = null,
    var selectedClass: String = "Knight",
    var playerName: String = "Hero",
    var totalPlayTime: Long = 0L,
    var saveSlot: Int = 0
) {
    fun toJson(): JSONObject {
        val json = JSONObject()
        json.put("isNewGame", isNewGame)
        json.put("selectedClass", selectedClass)
        json.put("playerName", playerName)
        json.put("totalPlayTime", totalPlayTime)
        json.put("saveSlot", saveSlot)
        playerSaveData?.let { pd ->
            val pdJson = JSONObject()
            pdJson.put("name", pd.name)
            pdJson.put("characterClass", pd.characterClass)
            pdJson.put("level", pd.level)
            pdJson.put("exp", pd.exp)
            pdJson.put("hp", pd.hp)
            pdJson.put("maxHp", pd.maxHp)
            pdJson.put("attack", pd.attack)
            pdJson.put("defense", pd.defense)
            pdJson.put("magicPower", pd.magicPower)
            pdJson.put("speed", pd.speed)
            pdJson.put("gold", pd.gold)
            pdJson.put("x", pd.x)
            pdJson.put("y", pd.y)
            pdJson.put("currentZone", pd.currentZone)
            pdJson.put("inventoryJson", pd.inventoryJson)
            val completedArr = JSONArray()
            pd.completedQuests.forEach { completedArr.put(it) }
            pdJson.put("completedQuests", completedArr)
            val activeArr = JSONArray()
            pd.activeQuests.forEach { activeArr.put(it) }
            pdJson.put("activeQuests", activeArr)
            json.put("playerData", pdJson)
        }
        return json
    }

    companion object {
        val instance: GameState = GameState()

        fun reset() {
            instance.isNewGame = true
            instance.playerSaveData = null
            instance.selectedClass = "Knight"
            instance.playerName = "Hero"
            instance.totalPlayTime = 0L
        }

        fun hasSave(context: Context): Boolean {
            val prefs = context.getSharedPreferences("medieval_rpg_save", Context.MODE_PRIVATE)
            return prefs.contains("save_data")
        }

        fun fromJson(json: JSONObject): GameState {
            val state = GameState()
            state.isNewGame = json.optBoolean("isNewGame", true)
            state.selectedClass = json.optString("selectedClass", "Knight")
            state.playerName = json.optString("playerName", "Hero")
            state.totalPlayTime = json.optLong("totalPlayTime", 0L)
            state.saveSlot = json.optInt("saveSlot", 0)
            if (json.has("playerData")) {
                val pd = json.getJSONObject("playerData")
                val completedList = mutableListOf<String>()
                val completedArr = pd.optJSONArray("completedQuests") ?: JSONArray()
                for (i in 0 until completedArr.length()) completedList.add(completedArr.getString(i))
                val activeList = mutableListOf<String>()
                val activeArr = pd.optJSONArray("activeQuests") ?: JSONArray()
                for (i in 0 until activeArr.length()) activeList.add(activeArr.getString(i))
                state.playerSaveData = PlayerSaveData(
                    name = pd.optString("name", "Hero"),
                    characterClass = pd.optString("characterClass", "Knight"),
                    level = pd.optInt("level", 1),
                    exp = pd.optInt("exp", 0),
                    hp = pd.optInt("hp", 100),
                    maxHp = pd.optInt("maxHp", 100),
                    attack = pd.optInt("attack", 10),
                    defense = pd.optInt("defense", 5),
                    magicPower = pd.optInt("magicPower", 5),
                    speed = pd.optInt("speed", 5),
                    gold = pd.optInt("gold", 0),
                    x = pd.optDouble("x", 100.0).toFloat(),
                    y = pd.optDouble("y", 100.0).toFloat(),
                    currentZone = pd.optString("currentZone", "VILLAGE"),
                    inventoryJson = pd.optString("inventoryJson", "[]"),
                    completedQuests = completedList,
                    activeQuests = activeList
                )
            }
            return state
        }
    }
}
