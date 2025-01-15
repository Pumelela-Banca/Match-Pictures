package com.example.matchpictures

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.RadioGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.matchpictures.models.BoardSize
import com.example.matchpictures.models.MemoryGame

class MainActivity : AppCompatActivity() { // Changed to extend AppCompatActivity

    companion object {
        private const val TAG = "MainActivity"
    }

    private lateinit var clRoot: ConstraintLayout
    private lateinit var memoryGame: MemoryGame
    private lateinit var board: RecyclerView
    private lateinit var numberMoves: TextView
    private lateinit var numberPairs: TextView
    private lateinit var adapter: MemoryBoardAdapter

    private var boardSize: BoardSize = BoardSize.EASY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar) // This should now be resolved
        toolbar.setTitleTextColor(ContextCompat.getColor(this, R.color.your_color))

        setUpBoard()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mi_refresh -> {
                if (memoryGame.getNumMoves() > 0 && !memoryGame.haveWonGame()) {
                    showAlertDialog("Quit your current game?", null, View.OnClickListener {
                        setUpBoard()
                    })
                } else {
                    setUpBoard()
                }
                return true
            }
            R.id.mi_new_size -> {
                showNewSizeDialog()
                return true
            }
            R.id.mi_custom -> {
                createFlashBoard()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun createFlashBoard() {
        TODO("Not yet implemented")
    }


    private fun showNewSizeDialog() {
        val boardsSizeView = LayoutInflater.from(this).inflate(R.layout.dialog_board_size, null)
        val radioGroupSize = boardsSizeView.findViewById<RadioGroup>(R.id.radioGroup)


        when (boardSize) {
            BoardSize.EASY -> radioGroupSize.check(R.id.rbEasy)
            BoardSize.MEDIUM -> radioGroupSize.check(R.id.rb_medium)
            BoardSize.HARD -> radioGroupSize.check(R.id.rb_Hard)
        }

        showAlertDialog("Choose new size", boardsSizeView, View.OnClickListener {
            // Handle new size selection
            boardSize = when (radioGroupSize.checkedRadioButtonId) {
                R.id.rbEasy -> BoardSize.EASY
                R.id.rb_medium -> BoardSize.MEDIUM
                R.id.rb_Hard -> BoardSize.HARD
                else -> boardSize
            }
            setUpBoard()
        })

    }

    private fun showAlertDialog(title: String, view: View?, positiveClickListener: View.OnClickListener) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(view)
            .setNegativeButton("Cancel", null)
            .setPositiveButton("OK") { _, _ ->
                positiveClickListener.onClick(null)
            }.show()
    }

    private fun updateGameWithFlip(position: Int) {
        if (memoryGame.haveWonGame()) {
            Toast.makeText(this, "You already won!", Toast.LENGTH_LONG).show()
            return
        }
        if (memoryGame.isCardFaceUp(position)) {
            Toast.makeText(this, "Invalid move!", Toast.LENGTH_SHORT).show()
            return
        }
        if (memoryGame.flipCard(position)) {
            Log.i(TAG, "Found a match! Num pairs found: ${memoryGame.numPairsFound}")
            numberPairs.text = "Pairs: ${memoryGame.numPairsFound} / ${boardSize.getNumPairs()}"
            if (memoryGame.haveWonGame()) {
                Toast.makeText(this, "You won! Congratulations.", Toast.LENGTH_LONG).show()
            }
        }
        numberMoves.text = "Moves: ${memoryGame.getNumMoves()}"
        adapter.notifyDataSetChanged()
    }

    private fun setUpBoard() {
        board = findViewById(R.id.board)
        numberMoves = findViewById(R.id.numberMoves)
        numberPairs = findViewById(R.id.numberPairs)
        when (boardSize) {
            BoardSize.EASY -> {
                numberMoves.text = "Easy: 4 x 2"
                numberPairs.text = "Pairs: 0 / 4"
            }
            BoardSize.MEDIUM -> {
                numberMoves.text = "Medium: 6 x 3"
                numberPairs.text = "Pairs: 0 / 6"
            }
            BoardSize.HARD -> {
                numberMoves.text = "Hard: 6 x 4"
                numberPairs.text = "Pairs: 0 / 12"
            }
        }


        memoryGame = MemoryGame(boardSize)

        adapter = MemoryBoardAdapter(this, boardSize, memoryGame.cards, object: MemoryBoardAdapter.CardClickListener {
            override fun onCardClicked(position: Int) {
                updateGameWithFlip(position)
            }
        })
        board.adapter = adapter
        board.layoutManager = GridLayoutManager(this, boardSize.getWidth())
    }
}
