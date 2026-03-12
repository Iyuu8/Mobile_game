package com.game.medievalrpg

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import com.game.medievalrpg.data.GameState
import com.game.medievalrpg.data.SaveManager
import com.game.medievalrpg.ui.MenuScreens
import com.game.medievalrpg.ui.MenuState

class MainActivity : Activity() {

    private lateinit var menuScreens: MenuScreens

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        // Hide system UI
        window.decorView.systemUiVisibility = (
            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
            View.SYSTEM_UI_FLAG_FULLSCREEN or
            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
            View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION or
            View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        )

        menuScreens = MenuScreens(this)
        menuScreens.setState(MenuState.MAIN)

        menuScreens.onMainMenu = {
            GameState.reset()
            menuScreens.setState(MenuState.MAIN)
        }

        setContentView(menuScreens)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            window.decorView.systemUiVisibility = (
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or
                View.SYSTEM_UI_FLAG_FULLSCREEN or
                View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
            )
        }
    }

    override fun onBackPressed() {
        // No-op on main menu
    }
}
