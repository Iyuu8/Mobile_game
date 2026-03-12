package com.game.medievalrpg.data

import android.content.Context
import org.json.JSONObject

object SaveManager {

    private const val PREFS_NAME = "medieval_rpg_save"
    private const val KEY_SAVE_DATA = "save_data"
    private const val KEY_SETTINGS = "settings"

    fun saveGame(context: Context, state: GameState) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putString(KEY_SAVE_DATA, state.toJson().toString()).apply()
    }

    fun loadGame(context: Context): GameState? {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val jsonStr = prefs.getString(KEY_SAVE_DATA, null) ?: return null
        return try {
            GameState.fromJson(JSONObject(jsonStr))
        } catch (e: Exception) {
            null
        }
    }

    fun deleteSave(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_SAVE_DATA).apply()
    }

    fun hasSave(context: Context): Boolean {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.contains(KEY_SAVE_DATA)
    }

    fun saveSetting(context: Context, key: String, value: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val settingsStr = prefs.getString(KEY_SETTINGS, "{}") ?: "{}"
        val settingsJson = try { JSONObject(settingsStr) } catch (e: Exception) { JSONObject() }
        settingsJson.put(key, value)
        prefs.edit().putString(KEY_SETTINGS, settingsJson.toString()).apply()
    }

    fun loadSetting(context: Context, key: String, default: String = ""): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val settingsStr = prefs.getString(KEY_SETTINGS, "{}") ?: "{}"
        val settingsJson = try { JSONObject(settingsStr) } catch (e: Exception) { JSONObject() }
        return settingsJson.optString(key, default)
    }
}
