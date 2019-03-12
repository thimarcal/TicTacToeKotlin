package gmp.thiago.apps.tictactoe

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import kotlinx.android.synthetic.main.activity_result.*

class ResultActivity : AppCompatActivity() {

    private val prefs : SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }
    private var mPlayerName : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        if (prefs.getBoolean(getString(R.string.dark_theme_pref_key), false)) {
            setTheme(R.style.DarkAppTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        mPlayerName = prefs.getString(getString(R.string.player_name_pref_key), "")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)

        val totalTime = intent.getLongExtra(getString(R.string.total_time_key), 0L)

        when (intent.getSerializableExtra(getString(R.string.winner_key))) {
            BoardActivity.Result.USER -> {
                result_tv.text = String.format(getString(R.string.result_user), mPlayerName)
                totalTime_tv.text = String.format("%02d:%02d.%03d",
                    totalTime / 60000, totalTime / 1000 % 60,
                    totalTime % 1000
                )
            }
            BoardActivity.Result.COMPUTER -> {
                result_tv.text = getString(R.string.result_computer)
            }
            BoardActivity.Result.TIE -> {
                result_tv.text = getString(R.string.result_tie)
            }
            else -> {
                // Do nothing
            }
        }

        playBtn.setOnClickListener{
            val boardIntent = Intent(this, BoardActivity::class.java)
            startActivity(boardIntent)

            finish()
        }
        overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom)
    }
}
