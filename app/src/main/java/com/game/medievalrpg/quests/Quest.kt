package com.game.medievalrpg.quests

enum class QuestStatus { NOT_STARTED, ACTIVE, COMPLETED, FAILED }
enum class ObjectiveType { KILL_ENEMIES, COLLECT_ITEMS, REACH_LOCATION, TALK_TO_NPC, ESCORT, SURVIVE }

data class QuestObjective(
    val id: String,
    val description: String,
    val type: ObjectiveType,
    val targetId: String,
    val requiredCount: Int,
    var currentCount: Int = 0,
    var isComplete: Boolean = false
) {
    fun progress(amount: Int = 1) {
        currentCount = minOf(requiredCount, currentCount + amount)
        if (currentCount >= requiredCount) isComplete = true
    }
    fun getProgressText(): String = "$currentCount / $requiredCount"
}

data class QuestReward(
    val exp: Int,
    val gold: Int,
    val itemIds: List<String> = emptyList()
)

class Quest(
    val id: String,
    val title: String,
    val description: String,
    val isMainQuest: Boolean,
    val requiredLevel: Int,
    val objectives: List<QuestObjective>,
    val reward: QuestReward,
    val prerequisiteQuestIds: List<String> = emptyList(),
    val nextQuestId: String? = null
) {
    var status: QuestStatus = QuestStatus.NOT_STARTED

    val isComplete: Boolean get() = objectives.all { it.isComplete }
    val progressPercent: Float get() {
        val total = objectives.size
        return if (total == 0) 1f else objectives.count { it.isComplete } / total.toFloat()
    }

    fun start() { status = QuestStatus.ACTIVE }
    fun complete() { status = QuestStatus.COMPLETED }
    fun fail() { status = QuestStatus.FAILED }

    fun progressObjective(objectiveId: String, amount: Int = 1) {
        objectives.find { it.id == objectiveId }?.progress(amount)
    }

    fun progressByTarget(targetId: String, amount: Int = 1) {
        objectives.filter { it.targetId == targetId && !it.isComplete }.forEach { it.progress(amount) }
    }
}

object QuestDatabase {
    val ALL_QUESTS: List<Quest> = listOf(
        // ===== MAIN QUESTS =====
        Quest(
            id = "tutorial",
            title = "A New Beginning",
            description = "Learn the basics of combat and movement in the village.",
            isMainQuest = true,
            requiredLevel = 1,
            objectives = listOf(
                QuestObjective("move", "Move around using the joystick", ObjectiveType.REACH_LOCATION, "tutorial_zone", 1),
                QuestObjective("attack", "Attack a training dummy", ObjectiveType.KILL_ENEMIES, "training_dummy", 1),
                QuestObjective("talk", "Speak with Elder Aldric", ObjectiveType.TALK_TO_NPC, "elder_aldric", 1)
            ),
            reward = QuestReward(exp = 50, gold = 20),
            nextQuestId = "goblin_menace"
        ),
        Quest(
            id = "goblin_menace",
            title = "The Goblin Menace",
            description = "Goblins have been raiding our village. Find and defeat their chief.",
            isMainQuest = true,
            requiredLevel = 1,
            objectives = listOf(
                QuestObjective("kill_scouts", "Defeat Goblin Scouts (0/5)", ObjectiveType.KILL_ENEMIES, "GOBLIN_SCOUT", 5),
                QuestObjective("kill_chief", "Defeat the Goblin Chief", ObjectiveType.KILL_ENEMIES, "GOBLIN_CHIEF", 1)
            ),
            reward = QuestReward(exp = 200, gold = 80, itemIds = listOf("iron_sword")),
            prerequisiteQuestIds = listOf("tutorial"),
            nextQuestId = "into_the_forest"
        ),
        Quest(
            id = "into_the_forest",
            title = "Into the Dark Forest",
            description = "Orc warbands have been spotted in the dark forest. Drive them back.",
            isMainQuest = true,
            requiredLevel = 3,
            objectives = listOf(
                QuestObjective("kill_orcs", "Defeat Orc Grunts (0/8)", ObjectiveType.KILL_ENEMIES, "ORC_GRUNT", 8),
                QuestObjective("kill_berserkers", "Defeat Orc Berserkers (0/4)", ObjectiveType.KILL_ENEMIES, "ORC_BERSERKER", 4),
                QuestObjective("reach_camp", "Reach the orc encampment", ObjectiveType.REACH_LOCATION, "orc_camp", 1)
            ),
            reward = QuestReward(exp = 400, gold = 150, itemIds = listOf("steel_sword")),
            prerequisiteQuestIds = listOf("goblin_menace"),
            nextQuestId = "warlords_head"
        ),
        Quest(
            id = "warlords_head",
            title = "The Warlord's Head",
            description = "The Orc Warlord commands all orc forces. Defeat him to break their power.",
            isMainQuest = true,
            requiredLevel = 5,
            objectives = listOf(
                QuestObjective("kill_armored", "Defeat Armored Orcs (0/4)", ObjectiveType.KILL_ENEMIES, "ARMORED_ORC", 4),
                QuestObjective("kill_warlord", "Defeat the Orc Warlord", ObjectiveType.KILL_ENEMIES, "ORC_WARLORD", 1)
            ),
            reward = QuestReward(exp = 600, gold = 250, itemIds = listOf("knights_blade")),
            prerequisiteQuestIds = listOf("into_the_forest"),
            nextQuestId = "frozen_depths"
        ),
        Quest(
            id = "frozen_depths",
            title = "Frozen Depths",
            description = "Strange undead have been rising in the frozen caves. Investigate.",
            isMainQuest = true,
            requiredLevel = 10,
            objectives = listOf(
                QuestObjective("kill_trolls", "Defeat Ice Trolls (0/6)", ObjectiveType.KILL_ENEMIES, "ICE_TROLL", 6),
                QuestObjective("kill_skeletons", "Defeat Skeleton Knights (0/4)", ObjectiveType.KILL_ENEMIES, "SKELETON_KNIGHT", 4),
                QuestObjective("reach_depths", "Reach the deepest chamber", ObjectiveType.REACH_LOCATION, "frozen_depths", 1)
            ),
            reward = QuestReward(exp = 1000, gold = 400),
            prerequisiteQuestIds = listOf("warlords_head"),
            nextQuestId = "elder_troll_boss"
        ),
        Quest(
            id = "elder_troll_boss",
            title = "The Elder Troll",
            description = "The Elder Troll controls the frozen caverns. Defeat it.",
            isMainQuest = true,
            requiredLevel = 13,
            objectives = listOf(
                QuestObjective("kill_elder", "Defeat the Elder Troll", ObjectiveType.KILL_ENEMIES, "ELDER_TROLL", 1)
            ),
            reward = QuestReward(exp = 1500, gold = 600, itemIds = listOf("mithril_plate")),
            prerequisiteQuestIds = listOf("frozen_depths"),
            nextQuestId = "dark_castle"
        ),
        Quest(
            id = "dark_castle",
            title = "Assault on the Dark Castle",
            description = "The Shadow King's castle looms over the land. Storm its gates.",
            isMainQuest = true,
            requiredLevel = 15,
            objectives = listOf(
                QuestObjective("kill_dark_knights", "Defeat Dark Knights (0/6)", ObjectiveType.KILL_ENEMIES, "DARK_KNIGHT", 6),
                QuestObjective("kill_demons", "Defeat Demons (0/4)", ObjectiveType.KILL_ENEMIES, "DEMON", 4),
                QuestObjective("kill_lich", "Defeat the Lich guardian", ObjectiveType.KILL_ENEMIES, "LICH", 1)
            ),
            reward = QuestReward(exp = 2000, gold = 800),
            prerequisiteQuestIds = listOf("elder_troll_boss"),
            nextQuestId = "shadows_end"
        ),
        Quest(
            id = "shadows_end",
            title = "Shadow's End",
            description = "Face the Shadow King himself and end his reign of darkness forever.",
            isMainQuest = true,
            requiredLevel = 20,
            objectives = listOf(
                QuestObjective("kill_shadow_king", "Defeat the Shadow King", ObjectiveType.KILL_ENEMIES, "SHADOW_KING_BOSS", 1)
            ),
            reward = QuestReward(exp = 5000, gold = 2000, itemIds = listOf("excalibur", "void_armor")),
            prerequisiteQuestIds = listOf("dark_castle")
        ),

        // ===== SIDE QUESTS =====
        Quest(
            id = "lost_supplies",
            title = "Lost Supplies",
            description = "A merchant's supply cart was ambushed. Recover the stolen goods.",
            isMainQuest = false,
            requiredLevel = 2,
            objectives = listOf(
                QuestObjective("kill_bandits", "Defeat Goblin thieves (0/6)", ObjectiveType.KILL_ENEMIES, "GOBLIN_WARRIOR", 6)
            ),
            reward = QuestReward(exp = 150, gold = 100)
        ),
        Quest(
            id = "wolf_hunt",
            title = "Wolf Hunt",
            description = "Wolves have been attacking livestock. Thin their numbers.",
            isMainQuest = false,
            requiredLevel = 2,
            objectives = listOf(
                QuestObjective("kill_wolves", "Defeat Wolves (0/10)", ObjectiveType.KILL_ENEMIES, "WOLF", 10)
            ),
            reward = QuestReward(exp = 180, gold = 90)
        ),
        Quest(
            id = "missing_child",
            title = "The Missing Child",
            description = "A child wandered into the forest. Find them before nightfall.",
            isMainQuest = false,
            requiredLevel = 1,
            objectives = listOf(
                QuestObjective("find_child", "Search the forest for the child", ObjectiveType.REACH_LOCATION, "forest_clearing", 1),
                QuestObjective("escort_child", "Escort the child back safely", ObjectiveType.ESCORT, "child_npc", 1)
            ),
            reward = QuestReward(exp = 200, gold = 60)
        ),
        Quest(
            id = "ancient_artifact",
            title = "Ancient Artifact",
            description = "Ruins deep in the cave hold an ancient relic. Retrieve it.",
            isMainQuest = false,
            requiredLevel = 6,
            objectives = listOf(
                QuestObjective("reach_ruins", "Find the ancient ruins in the cave", ObjectiveType.REACH_LOCATION, "cave_ruins", 1),
                QuestObjective("kill_guardians", "Defeat Skeleton guardians (0/5)", ObjectiveType.KILL_ENEMIES, "SKELETON_SOLDIER", 5)
            ),
            reward = QuestReward(exp = 350, gold = 200, itemIds = listOf("silver_amulet"))
        ),
        Quest(
            id = "monster_slayer",
            title = "Monster Slayer",
            description = "A hunter needs proof you can handle dangerous beasts.",
            isMainQuest = false,
            requiredLevel = 4,
            objectives = listOf(
                QuestObjective("kill_dire_wolves", "Defeat Dire Wolves (0/5)", ObjectiveType.KILL_ENEMIES, "DIRE_WOLF", 5),
                QuestObjective("kill_forest_troll", "Defeat a Forest Troll", ObjectiveType.KILL_ENEMIES, "FOREST_TROLL", 1)
            ),
            reward = QuestReward(exp = 300, gold = 140, itemIds = listOf("hunters_bow"))
        ),
        Quest(
            id = "collector",
            title = "The Collector",
            description = "A scholar wants trophies from various monsters.",
            isMainQuest = false,
            requiredLevel = 8,
            objectives = listOf(
                QuestObjective("kill_skeletons_side", "Defeat Skeleton Mages (0/3)", ObjectiveType.KILL_ENEMIES, "SKELETON_MAGE", 3),
                QuestObjective("kill_vampires", "Defeat Vampires (0/3)", ObjectiveType.KILL_ENEMIES, "VAMPIRE", 3),
                QuestObjective("kill_cave_trolls", "Defeat Cave Trolls (0/2)", ObjectiveType.KILL_ENEMIES, "CAVE_TROLL", 2)
            ),
            reward = QuestReward(exp = 600, gold = 300, itemIds = listOf("arcane_staff"))
        ),
        Quest(
            id = "arena_champion",
            title = "Arena Champion",
            description = "Prove your worth in the village arena by defeating waves of enemies.",
            isMainQuest = false,
            requiredLevel = 5,
            objectives = listOf(
                QuestObjective("arena_wave1", "Survive Arena Wave 1 (0/5 enemies)", ObjectiveType.KILL_ENEMIES, "GOBLIN_WARRIOR", 5),
                QuestObjective("arena_wave2", "Survive Arena Wave 2 (0/4 enemies)", ObjectiveType.KILL_ENEMIES, "ORC_GRUNT", 4),
                QuestObjective("arena_wave3", "Survive Arena Wave 3 (0/3 enemies)", ObjectiveType.KILL_ENEMIES, "SKELETON_KNIGHT", 3)
            ),
            reward = QuestReward(exp = 500, gold = 250, itemIds = listOf("battle_axe"))
        ),
        Quest(
            id = "hermit_request",
            title = "The Hermit's Request",
            description = "An old hermit living in the forest needs protection from shadow wolves.",
            isMainQuest = false,
            requiredLevel = 8,
            objectives = listOf(
                QuestObjective("kill_shadow_wolves", "Defeat Shadow Wolves (0/8)", ObjectiveType.KILL_ENEMIES, "SHADOW_WOLF", 8)
            ),
            reward = QuestReward(exp = 450, gold = 220, itemIds = listOf("shadow_dagger"))
        ),
        Quest(
            id = "haunted_graveyard",
            title = "Haunted Graveyard",
            description = "The graveyard has become overrun with undead. Cleanse it.",
            isMainQuest = false,
            requiredLevel = 9,
            objectives = listOf(
                QuestObjective("clear_archers", "Defeat Skeleton Archers (0/6)", ObjectiveType.KILL_ENEMIES, "SKELETON_ARCHER", 6),
                QuestObjective("clear_mages", "Defeat Skeleton Mages (0/4)", ObjectiveType.KILL_ENEMIES, "SKELETON_MAGE", 4)
            ),
            reward = QuestReward(exp = 550, gold = 280)
        ),
        Quest(
            id = "dragon_egg",
            title = "Dragon Egg",
            description = "A rare dragon egg has been spotted in the lair. Retrieve it for a mage.",
            isMainQuest = false,
            requiredLevel = 16,
            objectives = listOf(
                QuestObjective("defeat_fire_dragon", "Defeat a Fire Dragon", ObjectiveType.KILL_ENEMIES, "FIRE_DRAGON", 1),
                QuestObjective("retrieve_egg", "Reach the dragon's nest", ObjectiveType.REACH_LOCATION, "dragon_nest", 1)
            ),
            reward = QuestReward(exp = 2000, gold = 900, itemIds = listOf("dragon_bow"))
        )
    )

    fun getById(id: String): Quest? = ALL_QUESTS.find { it.id == id }
    fun getMainQuests(): List<Quest> = ALL_QUESTS.filter { it.isMainQuest }
    fun getSideQuests(): List<Quest> = ALL_QUESTS.filter { !it.isMainQuest }
}
