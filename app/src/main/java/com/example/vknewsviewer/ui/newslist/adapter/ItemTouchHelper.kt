package com.example.vknewsviewer.ui.newslist.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.PorterDuff
import android.graphics.PorterDuffXfermode
import android.graphics.drawable.ColorDrawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.ItemTouchHelper.ACTION_STATE_SWIPE
import androidx.recyclerview.widget.ItemTouchHelper.START
import androidx.recyclerview.widget.ItemTouchHelper.END
import androidx.recyclerview.widget.RecyclerView
import com.example.vknewsviewer.R

const val SWIPE_ICON_MARGIN = 100

interface ItemSwipeHelper {
    fun onRightSwipe(position: Int)
    fun onLeftSwipe(position: Int)
}

class ItemTouchHelperCallback(context: Context, private val adapter: ItemSwipeHelper) :
    ItemTouchHelper.SimpleCallback(0, START or END) {

    private val deleteIcon = ContextCompat.getDrawable(context, R.drawable.delete_icon)
    private val likeIcon = ContextCompat.getDrawable(context, R.drawable.like_icon)

    private val deleteIconWidth = deleteIcon?.intrinsicWidth
    private val deleteIconHeight = deleteIcon?.intrinsicHeight
    private val likeIconWidth = likeIcon?.intrinsicWidth
    private val likeIconHeight = likeIcon?.intrinsicHeight
    private val iconMargin = SWIPE_ICON_MARGIN * context.resources.displayMetrics.density.toInt()

    private val background = ColorDrawable()
    private val deleteBackgroundColor = context.resources.getColor(R.color.colorWarning)
    private val likeBackgroundColor = context.resources.getColor(R.color.colorPrimary)
    private val clearPaint = Paint().apply { xfermode = PorterDuffXfermode(PorterDuff.Mode.CLEAR) }

    override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        when (direction) {
            START -> adapter.onLeftSwipe(viewHolder.adapterPosition)
            END -> adapter.onRightSwipe(viewHolder.adapterPosition)
        }
    }

    private fun clearCanvas(c: Canvas?, left: Float, top: Float, right: Float, bottom: Float) {
        c?.drawRect(left, top, right, bottom, clearPaint)
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        if (actionState == ACTION_STATE_SWIPE) {

            val itemView = viewHolder.itemView
            val itemHeight = itemView.bottom - itemView.top
            val isCanceled = dX == 0f && !isCurrentlyActive

            if (isCanceled) {
                clearCanvas(c, itemView.right + dX, itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat())
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                return
            }

            if (dX > 0) { //DELETE
                background.color = deleteBackgroundColor
                background.setBounds(itemView.left + dX.toInt(), itemView.top, itemView.left, itemView.bottom)
                background.draw(c)
                if (deleteIcon != null) {
                    val deleteIconTop = itemView.top + (itemHeight - deleteIconHeight!!) / 2
                    val deleteIconLeft = itemView.left + iconMargin - deleteIconWidth!!
                    val deleteIconRight = itemView.left + iconMargin
                    val deleteIconBottom = deleteIconTop + deleteIconHeight
                    deleteIcon.setBounds(deleteIconLeft, deleteIconTop, deleteIconRight, deleteIconBottom)
                    deleteIcon.draw(c)
                }
            } else { //LIKE
                background.color = likeBackgroundColor
                background.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                background.draw(c)

                if (likeIcon != null) {
                    val likeIconTop = itemView.top + (itemHeight - likeIconHeight!!) / 2
                    val likeIconLeft = itemView.right - iconMargin - likeIconWidth!!
                    val likeIconRight = itemView.right - iconMargin
                    val likeIconBottom = likeIconTop + likeIconHeight
                    likeIcon.setBounds(likeIconLeft, likeIconTop, likeIconRight, likeIconBottom)
                    likeIcon.draw(c)
                }
            }
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
        }
    }
}
