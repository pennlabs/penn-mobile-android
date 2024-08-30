package com.pennapps.labs.pennmobile.components.floatingbottombar

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.text.Spannable
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.DisplayMetrics
import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.annotation.FloatRange
import androidx.annotation.Px
import androidx.annotation.VisibleForTesting
import androidx.appcompat.widget.AppCompatImageView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.AccessibilityDelegateCompat
import androidx.core.view.ViewCompat.setAccessibilityDelegate
import androidx.core.view.accessibility.AccessibilityNodeInfoCompat
import com.google.android.material.textview.MaterialTextView
import com.pennapps.labs.pennmobile.R
import com.pennapps.labs.pennmobile.components.floatingbottombar.utils.DrawableHelper
import com.pennapps.labs.pennmobile.components.floatingbottombar.utils.createChain

internal open class ExpandableItemViewController(
    internal val menuItem: ExpandableBottomBarMenuItem,
    private val itemView: View,
    private val textView: TextView,
    private val iconView: ImageView,
    private val backgroundCornerRadius: Float,
    private val backgroundOpacity: Float,
    private val imageView: ImageView,
    private val highlight: ImageView,
) {
    fun setAccessibleWith(
        prev: ExpandableItemViewController?,
        next: ExpandableItemViewController?,
    ) {
        setAccessibilityDelegate(
            itemView,
            object : AccessibilityDelegateCompat() {
                override fun onInitializeAccessibilityNodeInfo(
                    host: View?,
                    info: AccessibilityNodeInfoCompat?,
                ) {
                    info?.setTraversalAfter(prev?.itemView)
                    info?.setTraversalBefore(next?.itemView)
                    super.onInitializeAccessibilityNodeInfo(host, info)
                }
            },
        )
    }

    fun deselect() {
        itemView.background = null
        textView.visibility = View.INVISIBLE
        textView.isSelected = false
        iconView.isSelected = false
        itemView.isSelected = false
        imageView.visibility = View.GONE
        highlight.visibility = View.INVISIBLE
    }

    fun select() {
        textView.visibility = View.VISIBLE
        textView.isSelected = true
        iconView.isSelected = true
        itemView.isSelected = true
        imageView.visibility = View.VISIBLE
        highlight.visibility = View.VISIBLE
        // itemView.background = createHighlightedMenuShape()
    }

    @VisibleForTesting
    internal open fun createHighlightedMenuShape(): Drawable =
        DrawableHelper.createShapeDrawable(
            menuItem.activeColor,
            backgroundCornerRadius,
            backgroundOpacity,
        )

    fun attachTo(
        parent: ConstraintLayout,
        previousIconId: Int,
        nextIconId: Int,
        menuItemHorizontalMarginLeft: Int,
        menuItemHorizontalMarginRight: Int,
        menuItemVerticalMarginTop: Int,
        menuItemVerticalMarginBottom: Int,
    ) {
        textView.setTextAppearance(R.style.fontBottomBar)
        val activity = parent.context as Activity
        val displayMetrics = DisplayMetrics()
        activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

        var width = displayMetrics.widthPixels
        var height = displayMetrics.heightPixels

        val lp =
            ConstraintLayout.LayoutParams(
                ConstraintLayout.LayoutParams.MATCH_CONSTRAINT,
                ConstraintLayout.LayoutParams.MATCH_PARENT,
            )

        lp.setMargins(
            menuItemHorizontalMarginLeft,
            menuItemVerticalMarginTop,
            menuItemHorizontalMarginRight,
            menuItemVerticalMarginBottom,
        )

        parent.addView(itemView, lp)

        val cl = ConstraintSet()
        cl.clone(parent)

        cl.connect(itemView.id, ConstraintSet.TOP, parent.id, ConstraintSet.TOP)
        cl.connect(itemView.id, ConstraintSet.BOTTOM, parent.id, ConstraintSet.BOTTOM)

        if (previousIconId == itemView.id) {
            cl.connect(itemView.id, ConstraintSet.START, parent.id, ConstraintSet.START)
        } else {
            cl.connect(itemView.id, ConstraintSet.START, previousIconId, ConstraintSet.END)
            cl.createChain(previousIconId, itemView.id, ConstraintSet.CHAIN_PACKED)
        }

        if (nextIconId == itemView.id) {
            cl.connect(itemView.id, ConstraintSet.END, parent.id, ConstraintSet.END)
        } else {
            cl.connect(itemView.id, ConstraintSet.END, nextIconId, ConstraintSet.START)
            cl.createChain(itemView.id, nextIconId, ConstraintSet.CHAIN_PACKED)
        }

        cl.applyTo(parent)
    }

    class Builder(
        private val menuItem: ExpandableBottomBarMenuItem,
    ) {
        @Px
        private var itemVerticalPadding: Int = 0

        @Px
        private var itemHorizontalPadding: Int = 0

        @Px
        @SuppressLint("SupportAnnotationUsage")
        private var backgroundCornerRadius: Float = 0.0f

        @FloatRange(from = 0.0, to = 1.0)
        private var backgroundOpacity: Float = 1.0f

        private lateinit var backgroundColorSelector: ColorStateList
        private lateinit var onItemClickListener: (View) -> Unit

        fun itemMargins(
            @Px itemHorizontalPadding: Int,
            @Px itemVerticalPadding: Int,
        ): Builder {
            this.itemVerticalPadding = itemVerticalPadding
            this.itemHorizontalPadding = itemHorizontalPadding
            return this
        }

        fun itemBackground(
            backgroundCornerRadius: Float,
            @FloatRange(from = 0.0, to = 1.0) backgroundOpacity: Float,
        ): Builder {
            this.backgroundCornerRadius = backgroundCornerRadius
            this.backgroundOpacity = backgroundOpacity
            return this
        }

        fun itemsColors(backgroundColorSelector: ColorStateList): Builder {
            this.backgroundColorSelector = backgroundColorSelector
            return this
        }

        fun onItemClickListener(onItemClickListener: (View) -> Unit): Builder {
            this.onItemClickListener = onItemClickListener
            return this
        }

        fun build(context: Context): ExpandableItemViewController {
            val itemView =
                LinearLayout(context).apply {
                    id = menuItem.itemId
                    orientation = LinearLayout.VERTICAL
                    setPadding(
                        itemHorizontalPadding,
                        itemVerticalPadding,
                        itemHorizontalPadding,
                        itemVerticalPadding,
                    )
                    contentDescription =
                        context.resources.getString(
                            R.string.accessibility_item_description,
                            menuItem.text,
                        )
                    isFocusable = true
                    gravity = Gravity.BOTTOM
                }

            val iconView =
                AppCompatImageView(context).apply {
                    setImageDrawable(
                        DrawableHelper.createDrawable(
                            context,
                            menuItem.iconId,
                            backgroundColorSelector,
                        ),
                    )
                }

            val textView =
                MaterialTextView(context).apply {
                    val rawText = SpannableString(menuItem.text)
                    rawText.setSpan(
                        StyleSpan(Typeface.NORMAL),
                        0,
                        rawText.length,
                        Spannable.SPAN_PARAGRAPH,
                    )
                    text = rawText
                    gravity = Gravity.CENTER
                    visibility = View.INVISIBLE
                    textSize = 11.5F
                    setTextColor(backgroundColorSelector)
                }

            val itemLayoutParams =
                LinearLayout
                    .LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                    ).apply {
                        gravity = Gravity.CENTER
                        setMargins(0, 0, 0, 0)
                    }

            val textLayoutParams =
                LinearLayout
                    .LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                    ).apply {
                        gravity = Gravity.CENTER
                        setMargins(0, 24, 0, 12)
                    }

            val indicatorLayoutParams =
                LinearLayout
                    .LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                    ).apply {
                        gravity = Gravity.CENTER
                        setMargins(0, 0, 0, 0)
                    }

            val imageView =
                ImageView(context).apply {
                    val indicator =
                        ContextCompat
                            .getDrawable(context, R.drawable.ic_bottom_bar_indicator)
                    setImageDrawable(indicator)
                    visibility = View.GONE
                }

            with(itemView) {
                addView(iconView, itemLayoutParams)
                addView(textView, textLayoutParams)
                addView(imageView, indicatorLayoutParams)
                setOnClickListener(onItemClickListener)
            }

            val highlight =
                ImageView(context).apply {
                    val indicator =
                        ContextCompat
                            .getDrawable(context, R.drawable.ic_bottom_bar_highlight)
                    setImageDrawable(indicator)
                    visibility = View.GONE
                }

            val highlightLayoutParams =
                FrameLayout
                    .LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        Gravity.BOTTOM,
                    ).apply {
                        setMargins(0, 0, 0, 0)
                    }

            val frameView =
                FrameLayout(context).apply {
                    id = menuItem.itemId
                    isFocusable = true
                    addView(itemView)
                    addView(highlight, highlightLayoutParams)
                }

            return ExpandableItemViewController(
                menuItem,
                frameView,
                textView,
                iconView,
                backgroundCornerRadius,
                backgroundOpacity,
                imageView,
                highlight,
            )
        }
    }
}
