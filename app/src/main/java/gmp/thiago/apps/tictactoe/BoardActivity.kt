package gmp.thiago.apps.tictactoe

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.preference.PreferenceManager
import android.view.View
import androidx.core.content.ContextCompat
import gmp.thiago.apps.ai.ComputerAI
import kotlinx.android.synthetic.main.activity_board.*

class BoardActivity : AppCompatActivity(), View.OnClickListener {

    enum class Result {
        TIE, COMPUTER, USER, GAME_PLAYING
    }

    private val prefs : SharedPreferences by lazy {
        PreferenceManager.getDefaultSharedPreferences(applicationContext)
    }

    private val areas = Array <View?>(9) {null}
    private val positions = Array(9) {' '}

    private var moves = 0

    private var totalTime = 0L
    private var mPlayerName : String? = null
    private var startTime = 0L

    private var userPlaying = false

    override fun onCreate(savedInstanceState: Bundle?) {
        if (prefs.getBoolean(getString(R.string.dark_theme_pref_key), false)) {
            setTheme(R.style.DarkAppTheme)
        } else {
            setTheme(R.style.AppTheme)
        }
        mPlayerName = prefs.getString(getString(R.string.player_name_pref_key), "")

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_board)

        areas[0] = area_0_0
        areas[1] = area_0_1
        areas[2] = area_0_2
        areas[3] = area_1_0
        areas[4] = area_1_1
        areas[5] = area_1_2
        areas[6] = area_2_0
        areas[7] = area_2_1
        areas[8] = area_2_2

        area_0_0.setOnClickListener(this)
        area_0_1.setOnClickListener(this)
        area_0_2.setOnClickListener(this)
        area_1_0.setOnClickListener(this)
        area_1_1.setOnClickListener(this)
        area_1_2.setOnClickListener(this)
        area_2_0.setOnClickListener(this)
        area_2_1.setOnClickListener(this)
        area_2_2.setOnClickListener(this)

        if ((Math.random() * 2).toInt() == 0) {
            userPlaying = true
            startTime = System.nanoTime()
            timerHandler.post(timerRunnable)
        } else {
            // Get Computer's play move
            getComputerMove()
        }

        overridePendingTransition(R.anim.abc_slide_in_top, R.anim.abc_slide_out_bottom)
    }

    override fun onClick(v: View) {
        val position = areas.indexOf(v)

        // Must not handle clicks when computer is thinking
        if (!userPlaying) {
            return
        }

        // Position is not filled yet. User can select it
        if (positions[position] == ' ') {
            positions[position] = 'X'
            v.background = ContextCompat.getDrawable(this, R.drawable.x_symbol)
            timerHandler.removeCallbacks(timerRunnable)
            userPlaying = false

            moves ++

            // Validate result
            val result = checkGameOver()
            when (result) {
                Result.TIE, Result.USER, Result.COMPUTER -> {
                    userPlaying = false
                    // Call Result Activity
                    val resultIntent = Intent(this, ResultActivity::class.java)
                    resultIntent.putExtra(getString(R.string.winner_key), result)
                    resultIntent.putExtra(getString(R.string.total_time_key), totalTime)
                    startActivity(resultIntent)

                    finish()
                }
                else -> {
                    // Call Computer
                    getComputerMove()
                }
            }

        }
    }

    private fun getComputerMove() {
        val computerMove = ComputerAI.getComputerMove(positions)
        handleComputerMove(computerMove)
    }

    private fun handleComputerMove(position: Int) {
        positions[position] = 'O'
        areas[position]?.background = ContextCompat.getDrawable(this, R.drawable.o_symbol)

        moves++

        // Validate result
        val result = checkGameOver()
        when (result) {
            Result.TIE, Result.USER, Result.COMPUTER -> {
                userPlaying = false
                // Call Result Activity
                val resultIntent = Intent(this, ResultActivity::class.java)
                resultIntent.putExtra(getString(R.string.winner_key), result)
                startActivity(resultIntent)
            }
            else -> {
                // Start User's move
                userPlaying = true
                startTime = System.nanoTime()
                timerHandler.post(timerRunnable)
            }
        }
    }

    val timerHandler = Handler()
    private val timerRunnable = object : Runnable {
        override fun run() {
            cronoTv.text = String.format(
                getString(R.string.time_mask),
                totalTime / 60000,
                totalTime / 1000 % 60,
                totalTime % 1000
            )
            totalTime += (System.nanoTime() - startTime) / 1000000
            startTime = System.nanoTime()

            timerHandler.postDelayed(this, 10)
        }
    }

    private fun checkGameOver() : Result {
        if (moves < 5) return Result.GAME_PLAYING
        // Horizontals
        if (positions[0] == positions[1] && positions[1] == positions[2]) {
            if (positions[0] == 'X') return Result.USER
            if (positions[0] == 'O') return Result.COMPUTER
        }
        if (positions[3] == positions[4] && positions[4] == positions[5]) {
            if (positions[3] == 'X') return Result.USER
            if (positions[3] == 'O') return Result.COMPUTER
        }
        if (positions[6] == positions[7] && positions[7] == positions[8]) {
            if (positions[6] == 'X') return Result.USER
            if (positions[6] == 'O') return Result.COMPUTER
        }

        // Verticals
        if (positions[0] == positions[3] && positions[3] == positions[6]) {
            if (positions[0] == 'X') return Result.USER
            if (positions[0] == 'O') return Result.COMPUTER
        }
        if (positions[1] == positions[4] && positions[4] == positions[7]) {
            if (positions[1] == 'X') return Result.USER
            if (positions[1] == 'O') return Result.COMPUTER
        }
        if (positions[2] == positions[5] && positions[5] == positions[8]) {
            if (positions[2] == 'X') return Result.USER
            if (positions[2] == 'O') return Result.COMPUTER
        }

        // Diagonals
        if (positions[0] == positions[4] && positions[4] == positions[8]) {
            if (positions[0] == 'X') return Result.USER
            if (positions[0] == 'O') return Result.COMPUTER
        }
        if (positions[2] == positions[4] && positions[4] == positions[6]) {
            if (positions[2] == 'X') return Result.USER
            if (positions[0] == 'O') return Result.COMPUTER
        }

        // In case no winner and no more spaces to play, call it a Tie
        if (moves == positions.size) return Result.TIE

        return Result.GAME_PLAYING
    }
}
