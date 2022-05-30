package com.example.vknewsviewer.ui.newslist.adapter

import android.content.Context
import android.graphics.Canvas
import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView
import com.example.vknewsviewer.R
import com.example.vknewsviewer.utils.DAY_IN_MILLIS
import com.example.vknewsviewer.utils.POST_DIVIDER_DATE_FORMAT
import com.example.vknewsviewer.utils.getStartDay
import kotlinx.android.synthetic.main.post_list_divider_item.view.*
import java.text.SimpleDateFormat
import java.util.Date

class TimeDivider(private val context: Context) : RecyclerView.ItemDecoration() {

    private var dividerView: View? = null
    private val density = context.resources.displayMetrics.density
    private val dividerHeight = (DIVIDER_HEIGHT * density).toInt()

    override fun onDraw(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        val adapter = parent.adapter
        if (adapter is DecorationTypeProvider) {
            parent.children.forEach { child ->
                val adapterPosition = parent.getChildAdapterPosition(child)
                if (adapterPosition == RecyclerView.NO_POSITION) {
                    return
                }
                val decorationType = adapter.getType(adapterPosition)
                if (decorationType is DecorationType.DividerWithText) {
                    val timeDividerView = getDividerView(parent)
                    timeDividerView.dividerTextView.text = getDayCaption(decorationType.dividerDate)

                    c.save()
                    c.translate(0f, (child.top - timeDividerView.height).toFloat())
                    timeDividerView.draw(c)
                    c.restore()
                }
            }
        } else {
            super.onDraw(c, parent, state)
        }
    }

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        val position = parent.getChildAdapterPosition(view)
        if (position == RecyclerView.NO_POSITION) {
            return
        }
        val adapter = parent.adapter
        if (adapter is DecorationTypeProvider) {
            when (adapter.getType(position)) {
                is DecorationType.SimpleDivider ->
                    outRect.top = dividerHeight
                is DecorationType.DividerWithText ->
                    outRect.top = getDividerView(parent).height
            }
        }
    }

    private fun getDividerView(parent: RecyclerView): View {
        if (dividerView == null) {
            dividerView = LayoutInflater.from(parent.context).inflate(
                R.layout.post_list_divider_item, parent, false
            )
            dividerView!!.fixLayoutSizeIn(parent)
        }
        return dividerView!!
    }

    private fun View.fixLayoutSizeIn(parent: ViewGroup) {
        if (layoutParams == null) {
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )
        }
        val widthMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(parent.width, View.MeasureSpec.EXACTLY)
        val heightMeasureSpec =
            View.MeasureSpec.makeMeasureSpec(parent.height, View.MeasureSpec.UNSPECIFIED)
        val childWidthMeasureSpec = ViewGroup.getChildMeasureSpec(
            widthMeasureSpec,
            parent.paddingStart + parent.paddingEnd, layoutParams.width
        )
        val childHeightMeasureSpec = ViewGroup.getChildMeasureSpec(
            heightMeasureSpec,
            parent.paddingTop + parent.paddingBottom, layoutParams.height
        )
        measure(childWidthMeasureSpec, childHeightMeasureSpec)
        layout(0, 0, measuredWidth, measuredHeight)
    }

    private fun getDayCaption(date: Long): String {
        val today = Date().time
        val startThisDay = getStartDay(today)
        val startThisDate = getStartDay(date)

        return when (startThisDay - startThisDate) {
            0L -> context.getString(R.string.today)
            DAY_IN_MILLIS -> context.getString(R.string.yesterday)
            else -> SimpleDateFormat(POST_DIVIDER_DATE_FORMAT).format(Date(date))
        }
    }

    companion object {
        private const val DIVIDER_HEIGHT = 10
    }
}
