package se.umu.mada0474.weatherapp

import android.content.Intent
import android.content.SharedPreferences
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import kotlin.system.exitProcess


/**
 * This class manages the toolbar.
 * @author Mahmoud Daabas
 */
open class ToolbarHandlerActivity : AppCompatActivity() {

    /**
     * Creates the toolbar
     */
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_menu, menu)
        return true
    }

    /**
     * Handles the selected options in the toolbar menu.
     */
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                onBackPressedDispatcher.onBackPressed()
                true
            }

            R.id.menu_item_2 -> {
                val intent = Intent(this, SearchHistoryActivity::class.java)
                startActivity(intent)
                return true
            }

            R.id.menu_item_3 -> {
                val settings: SharedPreferences =
                getSharedPreferences("sharedPrefs", MODE_PRIVATE)
                settings.edit().clear().apply()
                return true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }
}