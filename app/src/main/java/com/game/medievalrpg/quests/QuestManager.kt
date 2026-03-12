package com.game.medievalrpg.quests

import com.game.medievalrpg.entities.Enemy
import com.game.medievalrpg.entities.Player

class QuestManager {

    private val quests = mutableMapOf<String, Quest>()
    var onQuestStarted: ((Quest) -> Unit)? = null
    var onQuestCompleted: ((Quest) -> Unit)? = null
    var onObjectiveProgress: ((Quest, QuestObjective) -> Unit)? = null

    init {
        QuestDatabase.ALL_QUESTS.forEach { quests[it.id] = it.copy() }
    }

    fun getQuest(id: String): Quest? = quests[id]

    fun getActiveQuests(): List<Quest> = quests.values.filter { it.status == QuestStatus.ACTIVE }
    fun getCompletedQuests(): List<Quest> = quests.values.filter { it.status == QuestStatus.COMPLETED }
    fun getAvailableQuests(playerLevel: Int): List<Quest> = quests.values.filter { q ->
        q.status == QuestStatus.NOT_STARTED &&
        q.requiredLevel <= playerLevel &&
        q.prerequisiteQuestIds.all { prereq ->
            quests[prereq]?.status == QuestStatus.COMPLETED
        }
    }

    fun startQuest(id: String): Boolean {
        val quest = quests[id] ?: return false
        if (quest.status != QuestStatus.NOT_STARTED) return false
        quest.start()
        onQuestStarted?.invoke(quest)
        return true
    }

    fun completeQuest(id: String, player: Player): Boolean {
        val quest = quests[id] ?: return false
        if (!quest.isComplete) return false
        quest.complete()
        player.gainExp(quest.reward.exp)
        player.gold += quest.reward.gold
        quest.reward.itemIds.forEach { itemId ->
            com.game.medievalrpg.items.Weapon.getById(itemId)?.let { player.inventory.addItem(it) }
            com.game.medievalrpg.items.Armor.getById(itemId)?.let { player.inventory.addItem(it) }
        }
        onQuestCompleted?.invoke(quest)
        quest.nextQuestId?.let { nextId -> quests[nextId]?.let { startQuest(nextId) } }
        return true
    }

    fun onEnemyKilled(enemy: Enemy, player: Player) {
        val targetId = enemy.type.name
        getActiveQuests().forEach { quest ->
            quest.objectives.filter { !it.isComplete && it.type == ObjectiveType.KILL_ENEMIES && it.targetId == targetId }
                .forEach { obj ->
                    obj.progress()
                    onObjectiveProgress?.invoke(quest, obj)
                }
            if (quest.isComplete) completeQuest(quest.id, player)
        }
    }

    fun onLocationReached(locationId: String, player: Player) {
        getActiveQuests().forEach { quest ->
            quest.objectives.filter { !it.isComplete && it.type == ObjectiveType.REACH_LOCATION && it.targetId == locationId }
                .forEach { obj ->
                    obj.progress()
                    onObjectiveProgress?.invoke(quest, obj)
                }
            if (quest.isComplete) completeQuest(quest.id, player)
        }
    }

    fun onNPCTalked(npcId: String, player: Player) {
        getActiveQuests().forEach { quest ->
            quest.objectives.filter { !it.isComplete && it.type == ObjectiveType.TALK_TO_NPC && it.targetId == npcId }
                .forEach { obj ->
                    obj.progress()
                    onObjectiveProgress?.invoke(quest, obj)
                }
            if (quest.isComplete) completeQuest(quest.id, player)
        }
    }

    fun initNewGame() {
        startQuest("tutorial")
    }

    fun getSaveData(): Pair<List<String>, List<String>> {
        val completed = quests.values.filter { it.status == QuestStatus.COMPLETED }.map { it.id }
        val active = quests.values.filter { it.status == QuestStatus.ACTIVE }.map { it.id }
        return Pair(completed, active)
    }

    fun loadSaveData(completedIds: List<String>, activeIds: List<String>) {
        completedIds.forEach { quests[it]?.status = QuestStatus.COMPLETED }
        activeIds.forEach { quests[it]?.status = QuestStatus.ACTIVE }
    }
}
