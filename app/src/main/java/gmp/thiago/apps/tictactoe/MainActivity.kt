package gmp.thiago.apps.tictactoe

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.Menu
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), View.OnClickListener {

    private val prefs : SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }
    private var darkTheme = false

    private var mPlayerName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        darkTheme = prefs.getBoolean(getString(R.string.dark_theme_pref_key), false)
        if (darkTheme) {
            setTheme(R.style.DarkAppTheme)
        } else {
            setTheme(R.style.AppTheme)
        }

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        start_game_btn.setOnClickListener(this)
        change_name_btn.setOnClickListener(this)

        this.mPlayerName = prefs.getString(getString(R.string.player_name_pref_key), null)
        if (mPlayerName != null) {
            change_name_btn.visibility = View.VISIBLE
            playerName.visibility = View.INVISIBLE
            greetings.visibility = View.VISIBLE
            greetings.text = String.format(getString(R.string.greetings), mPlayerName)
        } else {
            playerName.visibility = View.VISIBLE
            change_name_btn.visibility = View.INVISIBLE
            greetings.visibility = View.INVISIBLE
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {prefs.getBoolean(getString(R.string.dark_theme_pref_key), false)
        menuInflater.inflate(R.menu.settings_menu, menu)
        if (!darkTheme) {
            menu?.getItem(0)?.isChecked = true
        } else {
            menu?.getItem(1)?.isChecked = true
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.light_theme_settings , R.id.dark_theme_settings -> {
                prefs.edit().putBoolean(getString(R.string.dark_theme_pref_key), item.itemId == R.id.dark_theme_settings).apply()
                item.isChecked = true

                val restoreIntent = Intent(this, MainActivity::class.java)
                startActivity(restoreIntent)
                finish()
            }
            else -> return super.onOptionsItemSelected(item)
        }
        return true
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.start_game_btn -> {
                if (null == mPlayerName) {
                    mPlayerName = playerName.text.toString()
                }
                if (!mPlayerName.isNullOrEmpty()) {
                    playerName.visibility = View.INVISIBLE
                    change_name_btn.visibility = View.VISIBLE
                    prefs.edit().putString(getString(R.string.player_name_pref_key), mPlayerName).commit()

                    // Start BoardActivity Activity
                    val boardIntent = Intent(this, BoardActivity::class.java)
                    startActivity(boardIntent)

                    overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom)

                    finish()
                }
            }

            R.id.change_name_btn -> {
                change_name_btn.visibility = View.INVISIBLE
                greetings.visibility = View.INVISIBLE
                playerName.visibility = View.VISIBLE
                mPlayerName = null
                playerName.text.clear()
                prefs.edit().remove(getString(R.string.player_name_pref_key)).commit()
            }

            else -> {
                //Do nothing}
            }
        }
    }
}
