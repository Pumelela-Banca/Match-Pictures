package com.example.matchpictures.models

import android.util.Log
import android.widget.Toast
import com.example.matchpictures.utils.DEFAULT_ICONS
import kotlin.math.log

class MemoryGame(private val boardSize: BoardSize) {

    val cards: List<MemoryCard>
    var numPairsFound = 0

    private var indexOfSingleSelectedCard: Int? = null

    private var numCardFlips = 0

    init {
        val chosenImages = DEFAULT_ICONS.shuffled().take(boardSize.getNumPairs())
        val randomizedImages = (chosenImages + chosenImages).shuffled()
        cards = randomizedImages.map { MemoryCard(it) }
    }

    /**
     * Flips a card at the given position. Returns true if a match is found.
     */
    fun flipCard(position: Int): Boolean {
        // Ensure the position is within bounds
        numCardFlips++
        if (position < 0 || position >= cards.size) {
            throw IllegalArgumentException("Invalid card position: $position")
        }

        val card = cards[position]
        var foundMatch = false

        // Ensure card is not already face up
        if (card.isFaceUp) {
            throw IllegalStateException("Card is already face up: $position")
        }

        if (indexOfSingleSelectedCard == null) {
            restoreCards()
            indexOfSingleSelectedCard = position
        } else {
            foundMatch = checkForMatch(indexOfSingleSelectedCard!!, position)
            indexOfSingleSelectedCard = null
        }
        card.isFaceUp = !card.isFaceUp
        return foundMatch
    }

    /**
     * Checks if the cards at the given positions match.
     * Updates the number of pairs found if a match is found.
     */
    private fun checkForMatch(position1: Int, position2: Int): Boolean {
        // Ensure the positions are within bounds
        if (position1 < 0 || position1 >= cards.size || position2 < 0 || position2 >= cards.size) {
            throw IllegalArgumentException("Invalid card positions: $position1, $position2")
        }

        if (cards[position1].identifier != cards[position2].identifier) {
            return false
        }
        cards[position1].isMatched = true
        cards[position2].isMatched = true
        numPairsFound++
        return true
    }

    /**
     * Restores cards that are not matched to their default face-down state.
     */
    private fun restoreCards() {
        for (card in cards) {
            if (!card.isMatched) {
                card.isFaceUp = false
            }
        }
    }
    fun haveWonGame(): Boolean {
        return numPairsFound == boardSize.getNumPairs()
    }

    fun isCardFaceUp(position: Int): Boolean {
        return cards[position].isFaceUp
    }

    fun getNumMoves(): Int {
        return numCardFlips / 2
    }
}
