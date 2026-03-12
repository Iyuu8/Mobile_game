package com.game.medievalrpg

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.game.medievalrpg.data.GameState
import com.game.medievalrpg.data.SaveManager
import com.game.medievalrpg.game.GameView
import com.game.medievalrpg.ui.MenuScreens
import com.game.medievalrpg.ui.MenuState

class GameActivity : Activity() {

    private lateinit var gameView: GameView
    private lateinit var overlayMenu: MenuScreens
    private var overlayVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        )

        val loadSave = intent.getBooleanExtra("load_save", false)
        if (loadSave) GameState.instance.isNewGame = false

        gameView = GameView(this)
        val rootLayout = android.widget.FrameLayout(this)
        rootLayout.addView(gameView, android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        ))

        overlayMenu = MenuScreens(this)
        overlayMenu.visibility = View.GONE
        rootLayout.addView(overlayMenu, android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        ))

        setContentView(rootLayout)

        setupCallbacks()
    }

    private fun setupCallbacks() {
        gameView.onPauseRequested = { showPauseMenu() }
        gameView.onGameOver = { showGameOver() }
        gameView.onVictory  = { showVictory() }
        gameView.onQuestLog = { showQuestLog() }

        overlayMenu.onResume = {
            hideOverlay()
            gameView.resumeGame()
        }
        overlayMenu.onSave = {
            gameView.saveGame()
            android.widget.Toast.makeText(this, "Game Saved!", android.widget.Toast.LENGTH_SHORT).show()
        }
        overlayMenu.onMainMenu = {
            gameView.engine.soundManager.stopMusic()
            finish()
        }
        overlayMenu.onQuestLogClose = {
            if (overlayMenu.menuState == com.game.medievalrpg.ui.MenuState.QUEST_LOG) {
                hideOverlay()
                gameView.resumeGame()
            }
        }
    }

    private fun showPauseMenu() {
        runOnUiThread {
            overlayMenu.setState(MenuState.PAUSE)
            overlayMenu.visibility = View.VISIBLE
            overlayVisible = true
        }
    }

    private fun showGameOver() {
        runOnUiThread {
            overlayMenu.setState(MenuState.GAME_OVER)
            overlayMenu.visibility = View.VISIBLE
            overlayVisible = true
        }
    }

    private fun showVictory() {
        runOnUiThread {
            overlayMenu.setState(MenuState.VICTORY)
            overlayMenu.visibility = View.VISIBLE
            overlayVisible = true
        }
    }

    private fun showQuestLog() {
        runOnUiThread {
            gameView.pauseGame()
            overlayMenu.setState(MenuState.QUEST_LOG)
            overlayMenu.visibility = View.VISIBLE
            overlayVisible = true
        }
    }

    private fun hideOverlay() {
        runOnUiThread {
            overlayMenu.visibility = View.GONE
            overlayVisible = false
        }
    }

    override fun onPause() {
        super.onPause()
        gameView.pauseGame()
        gameView.engine.soundManager.pauseMusic()
    }

    override fun onResume() {
        super.onResume()
        if (!overlayVisible) gameView.resumeGame()
        gameView.engine.soundManager.resumeMusic()
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
        )
    }

    override fun onBackPressed() {
        if (overlayVisible && overlayMenu.menuState == MenuState.PAUSE) {
            hideOverlay()
            gameView.resumeGame()
        } else if (!overlayVisible) {
            showPauseMenu()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        gameView.engine.soundManager.release()
    }
}
