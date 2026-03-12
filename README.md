# Medieval Fantasy RPG 🏰⚔️

A complete 2D pixel art RPG mobile game built in Kotlin for Android, featuring medieval fantasy settings, multiple character classes, and an epic story.

---

## 🎮 Features

### Character Classes
| Class | Playstyle | Skills |
|-------|-----------|--------|
| **Knight** | Tank — high HP & defense | Shield Bash, Holy Strike, Battle Cry, Iron Fortress |
| **Bandit** | Rogue — high speed & crit | Backstab, Smoke Bomb, Poison Blade, Steal |
| **Necromancer** | Summoner — medium HP, high magic | Summon Skeleton, Life Drain, Curse, Raise Dead Army |
| **Wizard** | Glass cannon — very high magic | Fireball, Ice Storm, Lightning Bolt, Arcane Shield |
| **Shadow King** | Balanced — shadow abilities | Shadow Step, Dark Blade, Shadow Clone, Void Embrace |

### Enemies (28 types)
- **Goblins**: Scout, Warrior, Shaman, Chief
- **Orcs**: Grunt, Berserker, Warlord, Armored
- **Trolls**: Forest, Cave, Ice, Elder
- **Skeletons**: Soldier, Archer, Mage, Knight
- **Wolves/Beasts**: Wolf, Dire Wolf, Shadow Wolf, Alpha
- **Dragons**: Young, Fire, Ice, Ancient (final boss)
- **Special**: Dark Knight, Vampire, Demon, Lich

### Weapons & Equipment
- **Categories**: Swords, Daggers, Staffs, Bows — 15+ weapons each
- **Rarity**: Common, Uncommon, Rare, Epic, Legendary
- **Slots**: Weapon, Shield, Helmet, Armor, Boots, Accessory

### Story: "The Shadow's Return" (~30 min)
- **Act 1** — Tutorial village + Goblin Chief boss
- **Act 2** — Haunted forest + Orc Warlord boss
- **Act 3** — Frozen caves + Ice Troll Elder boss
- **Act 4** — Dark castle + Ancient Dragon + Shadow King (final boss)

### Side Quests (10)
Lost Supplies · Wolf Hunt · The Missing Child · Ancient Artifact · Monster Slayer · Collector · Arena Champion · The Hermit's Request · Haunted Graveyard · Dragon Egg

### Controls (Minecraft Mobile Style)
- 🕹️ **Left side**: Virtual joystick for movement (with dead zone)
- 🗡️ **Right side**: Attack, Skill 1/2/3, Interact buttons
- 📦 Inventory · ⏸ Pause · 📜 Quest Log buttons

---

## 📁 Project Structure

```
app/src/main/java/com/game/medievalrpg/
├── MainActivity.kt          # Main menu
├── GameActivity.kt          # Game host activity
├── game/
│   ├── GameEngine.kt        # Core engine — ties all systems together
│   ├── GameView.kt          # SurfaceView — renders at 60 FPS
│   ├── GameLoop.kt          # Game loop thread
│   └── Camera.kt            # Smooth camera follow
├── entities/
│   ├── GameObject.kt        # Base entity
│   ├── Player.kt            # Player (stats, skills, inventory, leveling)
│   ├── Enemy.kt             # 28 enemy types with patrol/chase/attack AI
│   └── NPC.kt               # NPCs with dialogue trees
├── classes/
│   ├── CharacterClass.kt    # Abstract base class
│   ├── Knight.kt / Bandit.kt / Necromancer.kt / Wizard.kt / ShadowKing.kt
├── combat/
│   ├── CombatSystem.kt      # Damage numbers, hit detection
│   ├── Skill.kt             # 20 unique skills with cooldowns/AOE/status effects
│   └── DamageCalculator.kt  # Formula-based damage calculation
├── items/
│   ├── Item.kt / Weapon.kt / Armor.kt
│   └── Inventory.kt         # Equip system + stat bonuses
├── world/
│   ├── TileMap.kt           # Tile-based maps for 7 zones
│   ├── Zone.kt              # Enemy/NPC spawning
│   ├── World.kt             # Zone transitions
│   └── Collision.kt         # AABB collision detection
├── quests/
│   ├── Quest.kt             # 8 main + 10 side quests
│   ├── QuestManager.kt      # Tracks active/completed quests
│   └── DialogueSystem.kt    # Branching dialogue
├── ui/
│   ├── Joystick.kt          # Multi-touch virtual joystick
│   ├── ActionButtons.kt     # Action buttons with cooldown display
│   ├── HUD.kt               # HP/mana bars, level, gold
│   ├── InventoryUI.kt       # Inventory screen
│   └── MenuScreens.kt       # Main menu, pause, class select, game over
├── graphics/
│   ├── SpriteSheet.kt       # Bitmap sprite sheet management
│   ├── Animation.kt         # Frame animation controller
│   └── Renderer.kt          # Draws all entities programmatically
├── audio/
│   └── SoundManager.kt      # SoundPool + MediaPlayer audio
└── data/
    ├── GameState.kt          # Serialisable game state
    └── SaveManager.kt        # JSON save/load via SharedPreferences
```

---

## 🚀 Setup & Build

### Requirements
- Android Studio Hedgehog (2023.1.1) or newer
- Android SDK 34
- Kotlin 1.9+
- Minimum device: Android 7.0 (API 24)

### Steps
1. **Clone the repository**
   ```bash
   git clone https://github.com/Iyuu8/Mobile_game.git
   cd Mobile_game
   ```

2. **Open in Android Studio**
   - File → Open → select the `Mobile_game` folder
   - Let Gradle sync complete

3. **Run the game**
   - Connect an Android device or start an emulator (landscape orientation recommended)
   - Click **Run ▶** or press `Shift+F10`

4. **Build APK**
   ```bash
   ./gradlew assembleDebug
   # APK output: app/build/outputs/apk/debug/app-debug.apk
   ```

---

## 🎮 How to Play

1. **Start** → tap **New Game** on the main menu
2. **Choose your class** — each has unique stats and skills
3. **Move** using the left virtual joystick
4. **Attack** with the sword button; use **Skill 1/2/3** for special abilities
5. **Interact** (chest icon) to talk to NPCs, open chests, use doors
6. **Level up** by defeating enemies — gain stat points automatically
7. **Equip items** found in chests or bought from the village shop
8. **Follow quests** using the Quest Log (scroll icon top-right)
9. **Save** via the Pause menu or automatically on zone transitions

---

## 🛠️ Technical Details

| Feature | Implementation |
|---------|---------------|
| Rendering | Android `SurfaceView` + `Canvas` (programmatic pixel art) |
| Game loop | Dedicated `Thread` targeting 60 FPS (`nanoseconds` precision) |
| Input | Multi-touch `MotionEvent` with pointer ID tracking |
| Save/Load | JSON serialised to `SharedPreferences` |
| Audio | `SoundPool` (SFX) + `MediaPlayer` (BGM) |
| Maps | Procedural `IntArray` tile maps per zone |
| AI | Finite state machine: Idle → Patrol → Chase → Attack |

---

## 📜 License

MIT License — feel free to use, modify, and distribute.