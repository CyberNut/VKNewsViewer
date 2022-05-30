package com.example.vknewsviewer.viewgroups

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop
import com.example.vknewsviewer.R
import kotlinx.android.synthetic.main.view_image_post.view.*
import kotlin.math.max

class PostWithImageLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : ViewGroup(context, attrs, defStyleAttr) {

    var titleText = EMPTY_TITLE
        set(value) {
            field = value
            titleTextView.text = value
            invalidate()
            requestLayout()
        }

    var dateText = EMPTY_DATE
        set(value) {
            field = value
            dateTextView.text = value
            invalidate()
            requestLayout()
        }

    var contentText = EMPTY_CONTENT
        set(value) {
            field = value
            contentTextView.text = value
            invalidate()
            requestLayout()
        }

    var numberOfLikes = DEFAULT_NUMBER_OF_LIKES_AND_COMMENTS
        set(value) {
            field = value
            numberOfLikesTextView.text = value.toString()
            invalidate()
            requestLayout()
        }

    var numberOfComments = DEFAULT_NUMBER_OF_LIKES_AND_COMMENTS
        set(value) {
            field = value
            numberOfCommentsTextView.text = value.toString()
            invalidate()
            requestLayout()
        }

    init {
        LayoutInflater.from(context).inflate(R.layout.view_image_post, this, true)

        context.theme.obtainStyledAttributes(attrs, R.styleable.PostWithImageLayout, 0, 0).apply {
            try {
                titleText = getString(R.styleable.PostWithImageLayout_titleText) ?: EMPTY_TITLE
                dateText = getString(R.styleable.PostWithImageLayout_dateText) ?: EMPTY_DATE
                contentText = getString(R.styleable.PostWithImageLayout_contentText) ?: EMPTY_CONTENT
                numberOfLikes = getInt(R.styleable.PostWithImageLayout_numberOfLikes, DEFAULT_NUMBER_OF_LIKES_AND_COMMENTS)
                numberOfComments = getInt(R.styleable.PostWithImageLayout_numberOfComments, DEFAULT_NUMBER_OF_LIKES_AND_COMMENTS)
            } finally {
                recycle()
            }
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = MeasureSpec.getSize(widthMeasureSpec)
        var height = 0

        measureChildWithMargins(logoImageView, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(titleTextView, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(dateTextView, widthMeasureSpec, 0, heightMeasureSpec, height)

        height = max(
            logoImageView.measuredHeightWithMargins,
            titleTextView.measuredHeightWithMargins + dateTextView.measuredHeightWithMargins
        )
        topPanelBackground.measure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(height, MeasureSpec.EXACTLY)
        )

        measureChildWithMargins(numberOfLikesTextView, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(numberOfCommentsTextView, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(likeButton, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(commentButton, widthMeasureSpec, 0, heightMeasureSpec, height)
        measureChildWithMargins(shareButton, widthMeasureSpec, 0, heightMeasureSpec, height)
        val highestButton = maxOf(
            likeButton.measuredHeightWithMargins,
            commentButton.measuredHeightWithMargins,
            shareButton.measuredHeightWithMargins
        )
        bottomPanelBackground.measure(
            widthMeasureSpec,
            MeasureSpec.makeMeasureSpec(highestButton, MeasureSpec.EXACTLY)
        )
        height += highestButton

        measureChildWithMargins(contentTextView, widthMeasureSpec, 0, heightMeasureSpec, height)
        height += contentTextView.measuredHeightWithMargins

        measureChildWithMargins(imageView, widthMeasureSpec, 0, heightMeasureSpec, height)
        height += imageView.measuredHeightWithMargins

        val desiredHeight = View.resolveSize(height, heightMeasureSpec)
        if (desiredHeight < height) {
            imageView.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(desiredHeight - (height - imageView.measuredHeight), MeasureSpec.EXACTLY))
        }

        setMeasuredDimension(desiredWidth, desiredHeight)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        val currentLeft = paddingLeft
        var currentTop = paddingTop

        topPanelBackground.layout(0, 0, measuredWidth, topPanelBackground.measuredHeight)

        logoImageView.layoutIfNotGone(
            currentLeft + logoImageView.marginStart,
            currentTop + logoImageView.marginTop,
            currentLeft + logoImageView.marginStart + logoImageView.measuredWidth,
            currentTop + logoImageView.marginTop + logoImageView.measuredHeight
        )
        titleTextView.layout(
            currentLeft + logoImageView.measuredWidthWithMargins + titleTextView.marginStart,
            currentTop + titleTextView.marginTop,
            currentLeft + logoImageView.measuredWidthWithMargins + titleTextView.marginStart + titleTextView.measuredWidth,
            currentTop + titleTextView.marginTop + titleTextView.measuredHeight
        )
        dateTextView.layout(
            currentLeft + logoImageView.measuredWidthWithMargins + dateTextView.marginStart,
            currentTop + titleTextView.measuredHeightWithMargins + dateTextView.marginTop,
            currentLeft + logoImageView.measuredWidthWithMargins + dateTextView.marginStart + dateTextView.measuredWidth,
            currentTop + titleTextView.measuredHeightWithMargins + dateTextView.marginTop + dateTextView.measuredHeight
        )
        currentTop += topPanelBackground.measuredHeight

        contentTextView.layout(
            currentLeft + contentTextView.marginStart,
            currentTop + contentTextView.marginTop,
            currentLeft + contentTextView.marginStart + contentTextView.measuredWidth,
            currentTop + contentTextView.marginTop + contentTextView.measuredHeight
        )
        currentTop += contentTextView.measuredHeightWithMargins

        //place the image in the center
        var x = measuredWidth / 2
        imageView.layout(
            x - imageView.measuredWidthWithMargins / 2,
            currentTop + imageView.marginTop,
            x + imageView.measuredWidthWithMargins / 2,
            currentTop + imageView.marginTop + imageView.measuredHeight
        )
        currentTop += imageView.measuredHeightWithMargins

        if (currentTop + bottomPanelBackground.measuredHeightWithMargins < measuredHeight) {
            currentTop += measuredHeight - (currentTop + bottomPanelBackground.measuredHeightWithMargins)
        }

        bottomPanelBackground.layout(
            0,
            currentTop,
            measuredWidth,
            currentTop + bottomPanelBackground.measuredHeight
        )

        //place the buttons in the center of screen
        x = measuredWidth / 4
        likeButton.layout(
            x - likeButton.measuredWidth / 2,
            currentTop + likeButton.marginTop,
            x + likeButton.measuredWidth / 2,
            currentTop + likeButton.marginTop + likeButton.measuredHeight
        )
        numberOfLikesTextView.layout(
            x + x / 2 - numberOfLikesTextView.measuredWidth / 2,
            currentTop + likeButton.measuredHeightWithMargins / 2 - numberOfLikesTextView.measuredHeight / 2,
            x + x / 2 + numberOfLikesTextView.measuredWidth / 2,
            currentTop + likeButton.measuredHeightWithMargins / 2 + numberOfLikesTextView.measuredHeight / 2
        )
        commentButton.layout(
            x * 2 - commentButton.measuredWidth / 2,
            currentTop + commentButton.marginTop,
            x * 2 + commentButton.measuredWidth / 2,
            currentTop + commentButton.marginTop + commentButton.measuredHeight
        )
        numberOfCommentsTextView.layout(
            x * 2 + x / 2 - numberOfCommentsTextView.measuredWidth / 2,
            currentTop + shareButton.measuredHeightWithMargins / 2 - numberOfCommentsTextView.measuredHeight / 2,
            x * 2 + x / 2 + numberOfCommentsTextView.measuredWidth / 2,
            currentTop + shareButton.measuredHeightWithMargins / 2 + numberOfCommentsTextView.measuredHeight / 2
        )
        shareButton.layout(
            x * 3 - shareButton.measuredWidth / 2,
            currentTop + shareButton.marginTop,
            x * 3 + shareButton.measuredWidth / 2,
            currentTop + shareButton.marginTop + shareButton.measuredHeight
        )
    }

    override fun generateDefaultLayoutParams() = MarginLayoutParams(
        LayoutParams.WRAP_CONTENT,
        LayoutParams.WRAP_CONTENT
    )

    override fun generateLayoutParams(attrs: AttributeSet?) = MarginLayoutParams(context, attrs)

    companion object {
        const val EMPTY_TITLE = "EMPTY TITLE"
        const val EMPTY_DATE = "EMPTY DATE"
        const val EMPTY_CONTENT = "CONTENT IS EMPTY"
        const val DEFAULT_NUMBER_OF_LIKES_AND_COMMENTS = 0
    }
}

fun View.layoutIfNotGone(l: Int, t: Int, r: Int, b: Int) {
    if (this.visibility == View.GONE) return
    layout(l, t, r, b)
}

val View.measuredWidthWithMargins: Int
    get() = this.marginStart + this.measuredWidth + this.marginEnd

val View.measuredHeightWithMargins: Int
    get() = this.marginTop + this.measuredHeight + this.marginBottom
