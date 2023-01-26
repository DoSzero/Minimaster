package com.minicoin.minimaster.presenters

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.TextView
import com.minicoin.minimaster.R

class MainActivity : AppCompatActivity() {

    private var playAgain: Button? = null
    private var displayWinner: TextView? = null
    private var player: Int = 0
    private var startingState: IntArray = intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2)
    private var gameRunning: Boolean = true
    private var gridLayout: GridLayout? = null

    private val winningPositions: Array<IntArray> = arrayOf(
        intArrayOf(0, 1, 2),
        intArrayOf(3, 4, 5),
        intArrayOf(6, 7, 8),
        intArrayOf(0, 3, 6),
        intArrayOf(1, 4, 7),
        intArrayOf(2, 5, 8),
        intArrayOf(0, 4, 8),
        intArrayOf(2, 4, 6)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        displayWinner = findViewById(R.id.display_winner)
        playAgain = findViewById(R.id.play_again)
        gridLayout = findViewById(R.id.my_grid_layout)

        playAgain!!.setOnClickListener {
            displayWinner?.text = ""
            gameRunning = true
            playAgain!!.visibility = View.INVISIBLE
            startingState = intArrayOf(2, 2, 2, 2, 2, 2, 2, 2, 2)

            for (x in 0 until gridLayout!!.childCount) {
                val image: ImageView = gridLayout!!.getChildAt(x) as ImageView
                image.setImageDrawable(null)
            }
        }
    }

    @SuppressLint("SetTextI18n")
    fun rollCoin(view: View) {
        val tapped: ImageView = view as ImageView
        val tappedTag = tapped.tag

        if (startingState[tappedTag.toString().toInt()] == 2 && gameRunning) {
            startingState[tappedTag.toString().toInt()] = player
            tapped.translationY = -1500F

            if (player == 0) {
                tapped.setImageResource(R.drawable.img_blue)
                player = 1
            } else {
                tapped.setImageResource(R.drawable.img_green)
                player = 0
            }

            tapped.animate().translationYBy(1500F).duration = 200
            for (winningPosition in winningPositions) {
                if (startingState[winningPosition[0]] == startingState[winningPosition[1]]
                    && startingState[winningPosition[1]] == startingState[winningPosition[2]]
                    && startingState[winningPosition[0]] != 2
                ) {
                    gameRunning = false
                    playAgain!!.visibility = View.VISIBLE
                    if (player == 1){
                        displayWinner?.text = "Blue has won"
                    }else{
                        displayWinner?.text = "Green has Won"
                    }
                }
            }
        }
    }
}