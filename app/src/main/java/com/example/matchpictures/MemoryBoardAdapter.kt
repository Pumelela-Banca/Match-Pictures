package com.example.matchpictures

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.matchpictures.models.BoardSize
import com.example.matchpictures.models.MemoryCard
import java.lang.Float.min

class MemoryBoardAdapter(
    private val context: Context,
    private val numCards: BoardSize,
    private val cards: List<MemoryCard>,
    private val cardClickListener: CardClickListener) :
    RecyclerView.Adapter<MemoryBoardAdapter.ViewHolder>() {

    companion object {
        private const val MARGIN_SIZE = 10
        private const val TAG = "MemoryBoardAdapter"
    }

    interface CardClickListener {
        fun onCardClicked(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val cardWidth = parent.width / numCards.getWidth() - (2 * MARGIN_SIZE)
        val cardHeight = parent.height / numCards.getHeight() - (2 * MARGIN_SIZE)
        val cardSideLength = kotlin.math.min(cardWidth, cardHeight)
        val view = LayoutInflater.from(context).inflate(R.layout.memory_card, parent, false)
        val layoutParams = view.findViewById<CardView>(R.id.cardView).layoutParams
        layoutParams.width = cardSideLength
        layoutParams.height = cardSideLength

        // Apply margin
        if (layoutParams is ViewGroup.MarginLayoutParams) {
            layoutParams.setMargins(MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE, MARGIN_SIZE)
        }

        return ViewHolder(view)
    }

    override fun getItemCount() = numCards.numCards

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(position)
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imageButton = itemView.findViewById<ImageButton>(R.id.imageButton)
        fun bind(position: Int) {
            val memoryCard :MemoryCard = cards[position]
            imageButton.setImageResource(if (memoryCard.isFaceUp)  memoryCard.identifier else R.drawable.ic_launcher_background)

            imageButton.alpha = if (memoryCard.isMatched) .4f else 1.0f
            val colorStateList = if (memoryCard.isMatched) ContextCompat.getColorStateList(context, R.color.color_gray) else null
            ViewCompat.setBackgroundTintList(imageButton, colorStateList)

            imageButton.setOnClickListener {
                Log.i(TAG, "Clicked on position $position")
                cardClickListener.onCardClicked(position)
            }
        }
    }
}
