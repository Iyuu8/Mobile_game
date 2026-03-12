package com.game.medievalrpg.game

import android.content.Context
import com.game.medievalrpg.audio.SoundManager
import com.game.medievalrpg.classes.*
import com.game.medievalrpg.combat.CombatSystem
import com.game.medievalrpg.combat.SkillType
import com.game.medievalrpg.data.GameState
import com.game.medievalrpg.data.SaveManager
import com.game.medievalrpg.entities.Enemy
import com.game.medievalrpg.entities.Player
import com.game.medievalrpg.graphics.Renderer
import com.game.medievalrpg.items.Armor
import com.game.medievalrpg.items.Weapon
import com.game.medievalrpg.quests.QuestManager
import com.game.medievalrpg.ui.*
import com.game.medievalrpg.world.*
import kotlin.random.Random

class GameEngine(private val context: Context) {

    lateinit var player: Player
    lateinit var world: World
    lateinit var camera: Camera
    lateinit var combatSystem: CombatSystem
    lateinit var questManager: QuestManager
    lateinit var soundManager: SoundManager
    lateinit var renderer: Renderer
    lateinit var hud: HUD
    lateinit var joystick: Joystick
    lateinit var actionButtons: ActionButtons
    lateinit var inventoryUI: InventoryUI

    var screenWidth: Int = 1280
    var screenHeight: Int = 720

    var isRunning: Boolean = false
    var isPaused: Boolean = false
    var isGameOver: Boolean = false
    var isVictory: Boolean = false

    private var zoneTransitionAlpha: Float = 0f
    private var isTransitioning: Boolean = false
    private var pendingZone: ZoneType? = null

    // Notification queue
    private val notifications = mutableListOf<Pair<String, Float>>()

    // Callbacks for UI state changes
    var onPause: (() -> Unit)? = null
    var onGameOver: (() -> Unit)? = null
    var onVictory: (() -> Unit)? = null
    var onQuestLog: (() -> Unit)? = null

    fun initialize(sw: Int, sh: Int) {
        screenWidth = sw; screenHeight = sh

        val cls: CharacterClass = when (GameState.instance.selectedClass) {
            "Bandit"      -> Bandit()
            "Necromancer" -> Necromancer()
            "Wizard"      -> Wizard()
            "ShadowKing"  -> ShadowKing()
            else          -> Knight()
        }

        world = World(ZoneType.VILLAGE)
        player = Player(300f, 400f, cls)

        player.onLevelUp = { lvl ->
            hud.showLevelUp(lvl)
            soundManager.playLevelUp()
            addNotification("Level Up! You are now level $lvl!")
        }
        player.onDeath = { isGameOver = true; onGameOver?.invoke() }

        camera = Camera(screenWidth = sw, screenHeight = sh)
        camera.snapTo(player.centerX, player.centerY,
            world.currentZone.tileMap.getPixelWidth().toFloat(),
            world.currentZone.tileMap.getPixelHeight().toFloat())

        combatSystem = CombatSystem()
        questManager = QuestManager()
        questManager.initNewGame()

        soundManager = SoundManager(context)
        renderer = Renderer()
        hud = HUD(sw, sh)

        val joyX = 130f; val joyY = sh - 140f
        joystick = Joystick(joyX, joyY, 100f, 45f)
        actionButtons = ActionButtons(sw, sh)
        inventoryUI = InventoryUI(sw, sh)

        actionButtons.onButtonPressed = { btnId -> handleButtonAction(btnId) }

        inventoryUI.onEquipItem = { item ->
            when (item) {
                is Weapon -> player.inventory.equipWeapon(item)
                is Armor  -> player.inventory.equipArmor(item)
            }
            player.refreshStats()
            addNotification("Equipped: ${item.name}")
        }

        // Wire quest callbacks
        questManager.onQuestCompleted = { quest ->
            hud.showNotification("Quest Complete: ${quest.title}!")
            soundManager.playQuestComplete()
            addNotification("Quest Complete: ${quest.title}! +${quest.reward.gold}g +${quest.reward.exp}xp")
        }
        questManager.onQuestStarted = { quest ->
            addNotification("New Quest: ${quest.title}")
        }

        world.onZoneChanged = { zone ->
            hud.showZoneEntered(zone)
            questManager.onLocationReached(zone.name.lowercase(), player)
        }

        isRunning = true

        // Load save if continuing
        if (!GameState.instance.isNewGame) loadSave()
        else {
            // Give starter equipment
            val starterWeapon = when (cls.name) {
                "Knight" -> Weapon.getById("iron_sword")
                "Bandit" -> Weapon.getById("iron_dagger")
                "Wizard", "Necromancer" -> Weapon.getById("wooden_staff")
                else     -> Weapon.getById("iron_sword")
            }
            starterWeapon?.let { player.inventory.addItem(it); player.inventory.equipWeapon(it) }
            player.inventory.addItem(Armor.getById("leather_armor")!!)
            player.inventory.equipArmor(Armor.getById("leather_armor")!!)
            player.refreshStats()
        }
    }

    fun update(deltaTime: Float) {
        if (!isRunning || isPaused || isGameOver) return

        // Transition
        if (isTransitioning) {
            zoneTransitionAlpha += deltaTime * 2f
            if (zoneTransitionAlpha >= 1f) {
                pendingZone?.let { world.changeZone(it, player); pendingZone = null }
                isTransitioning = false
                zoneTransitionAlpha = 0f
            }
            return
        }

        hud.update(deltaTime)
        combatSystem.update(deltaTime)

        // Player movement from joystick
        val spd = player.speed * 18f
        player.velocityX = joystick.directionX * spd
        player.velocityY = joystick.directionY * spd

        // Collision-resolved movement
        val zone = world.currentZone
        val (nx, ny) = Collision.resolveEntityVsTileMap(
            player.x, player.y, player.width, player.height,
            player.velocityX * deltaTime, player.velocityY * deltaTime,
            zone.tileMap
        )
        player.x = nx; player.y = ny
        player.velocityX = 0f; player.velocityY = 0f
        player.update(deltaTime)

        world.update(deltaTime, player)
        camera.update(player.centerX, player.centerY,
            zone.tileMap.getPixelWidth().toFloat(),
            zone.tileMap.getPixelHeight().toFloat(), deltaTime)

        // NPC interaction range check
        zone.npcs.forEach { npc ->
            npc.isInteracting = npc.isPlayerInRange(player.centerX, player.centerY)
        }

        // Enemy attacks player
        zone.getActiveEnemies().forEach { enemy ->
            enemy.onAttackPlayer = { dmg ->
                player.takeDamage(dmg)
                soundManager.playHurt()
            }
        }

        // Check zone-boundary triggers
        checkZoneBoundaries()

        // Auto-attack enemies near player when attack button pressed (handled in action)
        updateSkillCooldownUI()

        // Process notifications
        if (notifications.isNotEmpty()) {
            val (text, _) = notifications.first()
            hud.showNotification(text)
            notifications.removeAt(0)
        }
    }

    private fun checkZoneBoundaries() {
        val mapW = world.currentZone.tileMap.getPixelWidth().toFloat()
        val mapH = world.currentZone.tileMap.getPixelHeight().toFloat()
        val tileSize = world.currentZone.tileMap.tileSize.toFloat()

        // Zone edge transitions
        if (player.x <= tileSize) triggerZoneChange(getPreviousZone())
        if (player.x >= mapW - player.width - tileSize) triggerZoneChange(getNextZone())
    }

    private fun getPreviousZone(): ZoneType? {
        val adj = world.getAdjacentZones(world.currentZoneType)
        return adj.firstOrNull()
    }

    private fun getNextZone(): ZoneType? {
        val adj = world.getAdjacentZones(world.currentZoneType)
        return adj.lastOrNull()
    }

    private fun triggerZoneChange(zone: ZoneType?) {
        if (zone == null || isTransitioning) return
        isTransitioning = true
        pendingZone = zone
        SaveManager.saveGame(context, buildSaveState())
    }

    fun handleAttack() {
        if (!player.performAttack()) return
        soundManager.playAttack()
        val zone = world.currentZone
        val enemies = zone.getActiveEnemies() + listOfNotNull(zone.bossEnemy)
        enemies.filter { !it.isDead && player.distanceTo(it) <= player.attackRange }.forEach { enemy ->
            val event = combatSystem.playerAttacksEnemy(player, enemy)
            if (enemy.isDead) onEnemyDied(enemy)
        }
    }

    fun handleSkill(index: Int) {
        val skill = player.useSkill(index) ?: return
        soundManager.playSkill()
        val zone = world.currentZone
        val enemies = zone.getActiveEnemies() + listOfNotNull(zone.bossEnemy)
        combatSystem.playerUsesSkill(player, skill, enemies.filter { !it.isDead })
        enemies.filter { it.isDead }.forEach { onEnemyDied(it) }

        if (skill.type == SkillType.HEAL) hud.showNotification("Healed!")
        if (skill.summonType.isNotEmpty()) addNotification("${skill.name}!")
    }

    fun handleInteract() {
        val zone = world.currentZone
        zone.npcs.filter { it.isInteracting }.firstOrNull()?.let {
            // Interaction handled by dialogue system in GameView
            return
        }
        // Check chest
        val chest = zone.checkChestAt(player.centerX, player.centerY)
        chest?.let { (row, col) ->
            zone.openChest(row, col)
            val lvl = player.level
            if (Random.nextBoolean()) {
                val item = Weapon.getRandomForLevel(lvl)
                if (player.inventory.addItem(item)) addNotification("Found: ${item.name}!")
                else addNotification("Backpack full!")
            } else {
                val item = Armor.getRandomForLevel(lvl)
                if (player.inventory.addItem(item)) addNotification("Found: ${item.name}!")
                else addNotification("Backpack full!")
            }
            val gold = (10 + Random.nextFloat() * 30 * lvl).toInt()
            player.gold += gold
            addNotification("Found $gold gold!")
            soundManager.playPickup()
        }
    }

    private fun onEnemyDied(enemy: Enemy) {
        player.gainExp(enemy.expReward)
        player.gold += enemy.goldReward
        questManager.onEnemyKilled(enemy, player)
        addNotification("+${enemy.expReward} EXP  +${enemy.goldReward} Gold")
        soundManager.playDeath()

        // Check victory condition on high-tier boss kills
        val bossTypes = setOf(EnemyType.LICH, EnemyType.ANCIENT_DRAGON)
        if (enemy.type in bossTypes) {
            checkVictoryCondition()
        }
    }

    private fun checkVictoryCondition() {
        val mainQuests = questManager.getCompletedQuests().filter { it.isMainQuest }
        if (mainQuests.size >= 8 || world.currentZoneType == ZoneType.CASTLE &&
            world.currentZone.areAllEnemiesDefeated()) {
            isVictory = true
            onVictory?.invoke()
        }
    }

    private fun handleButtonAction(btnId: String) {
        when (btnId) {
            "attack"    -> handleAttack()
            "skill1"    -> handleSkill(0)
            "skill2"    -> handleSkill(1)
            "skill3"    -> handleSkill(2)
            "interact"  -> handleInteract()
            "inventory" -> inventoryUI.toggle()
            "quests"    -> onQuestLog?.invoke()
            "pause"     -> { isPaused = true; onPause?.invoke() }
        }
    }

    private fun updateSkillCooldownUI() {
        val pcts = player.skills.take(3).map { it.cooldownPercent() }
        actionButtons.updateCooldowns(pcts)
    }

    fun addNotification(text: String) { notifications.add(Pair(text, 2f)) }

    fun pause() { isPaused = true }
    fun resume() { isPaused = false }

    fun saveGame() { SaveManager.saveGame(context, buildSaveState()) }

    private fun buildSaveState(): GameState {
        val state = GameState.instance
        state.isNewGame = false
        val (completed, active) = questManager.getSaveData()
        state.playerSaveData = com.game.medievalrpg.data.PlayerSaveData(
            name = GameState.instance.playerName,
            characterClass = player.characterClass.name,
            level = player.level,
            exp = player.exp,
            hp = player.hp,
            maxHp = player.maxHp,
            attack = player.attack,
            defense = player.defense,
            magicPower = player.magicPower,
            speed = player.speed,
            gold = player.gold,
            x = player.x,
            y = player.y,
            currentZone = world.currentZoneType.name,
            inventoryJson = player.inventory.toJson().toString(),
            completedQuests = completed,
            activeQuests = active
        )
        return state
    }

    private fun loadSave() {
        val state = SaveManager.loadGame(context) ?: return
        val pd = state.playerSaveData ?: return
        player.level = pd.level; player.exp = pd.exp; player.hp = pd.hp
        player.gold = pd.gold; player.x = pd.x; player.y = pd.y
        player.refreshStats()
        questManager.loadSaveData(pd.completedQuests, pd.activeQuests)
        try {
            val zone = ZoneType.valueOf(pd.currentZone)
            if (zone != ZoneType.VILLAGE) world.changeZone(zone, player)
        } catch (e: Exception) { /* use default zone */ }
    }

    fun getZoneTransitionAlpha(): Float = if (isTransitioning) zoneTransitionAlpha else 0f
}
