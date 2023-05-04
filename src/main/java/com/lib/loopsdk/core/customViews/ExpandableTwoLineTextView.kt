package com.lib.loopsdk.core.customViews

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.content.Context
import android.util.AttributeSet
import android.util.TypedValue
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.core.content.ContextCompat
import com.lib.loopsdk.R
import java.util.*


/**
 * Copyright (C) 2017 Cliff Ophalvens (Blogc.at)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @author Cliff Ophalvens (Blogc.at)
 */
class ExpandableTwoLineTextView(context: Context, attrs: AttributeSet?) :
    CustomTVForExpandableTwoLineTV(context, attrs) {
    private val onExpandListeners: MutableList<OnExpandListener>
    /**
     * Returns the current [TimeInterpolator] for expanding.
     * @return the current interpolator, null by default.
     */
    /**
     * Sets a [TimeInterpolator] for expanding.
     * @param expandInterpolator the interpolator
     */
    private var expandInterpolator: TimeInterpolator
    /**
     * Returns the current [TimeInterpolator] for collapsing.
     * @return the current interpolator, null by default.
     */
    /**
     * Sets a [TimeInterpolator] for collpasing.
     * @param collapseInterpolator the interpolator
     */
    private var collapseInterpolator: TimeInterpolator
    private val maxLinesCurrent: Int
    private var animationDuration: Long
    private var animating = false

    private var ellipseColor: Int = ContextCompat.getColor(context, R.color.white)
    private var isShowingFRTFull: Boolean = false

    constructor(context: Context) : this(context, null) {
        maxLines = 2
//        ellipsize = TextUtils.TruncateAt.END
        ellipseColor = if (isShowingFRTFull)
            ContextCompat.getColor(context, R.color.white)
        else
            ContextCompat.getColor(context, R.color.white)
    }

    init {
        maxLines = 2
//        ellipsize = TextUtils.TruncateAt.END
        setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 3.5f,  resources.displayMetrics), 1.0f)
        setLineSpacing()
    }

    companion object {
    }

    fun setLineSpacing(size: Float = 3.0f) {
        setLineSpacing(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, size,  resources.displayMetrics), 1.0f)
    }

    /**
     * Is this [ExpandableTwoLineTextView] expanded or not?
     * @return true if expanded, false if collapsed.
     */
    var isExpanded = false
        private set
    private var collapsedHeight = 0
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        // if this TextView is collapsed and maxLines = 0,
        // than make its height equals to zero
        var heightMeasureSpec = heightMeasureSpec
        if (maxLinesCurrent == 0 && !isExpanded && !animating) {
            heightMeasureSpec = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.EXACTLY)
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
    }
    //region public helper methods
    /**
     * Toggle the expanded state of this [ExpandableTwoLineTextView].
     * @return true if toggled, false otherwise.
     */
    fun toggle(): Boolean {
        return if (isExpanded) collapse() else expand()
    }

    /**
     * Expand this [ExpandableTwoLineTextView].
     * @return true if expanded, false otherwise.
     */
    fun expand(): Boolean {
        if (!isExpanded && !animating && maxLinesCurrent >= 2) {
            // notify listener
            notifyOnExpand()

            // measure collapsed height
            measure(
                MeasureSpec.makeMeasureSpec(this.measuredWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            collapsedHeight = this.measuredHeight

            // indicate that we are now animating
            animating = true

            // set maxLines to MAX Integer, so we can calculate the expanded height
            setMaxLines(Int.MAX_VALUE)

            // measure expanded height
            measure(
                MeasureSpec.makeMeasureSpec(this.measuredWidth, MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED)
            )
            val expandedHeight = this.measuredHeight

            // animate from collapsed height to expanded height
            val valueAnimator = ValueAnimator.ofInt(collapsedHeight, expandedHeight)
            valueAnimator.addUpdateListener { animation ->
                this@ExpandableTwoLineTextView.height = animation.animatedValue as Int
            }

            // wait for the animation to end
            valueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // reset min & max height (previously set with setHeight() method)
                    this@ExpandableTwoLineTextView.maxHeight = Int.MAX_VALUE
                    this@ExpandableTwoLineTextView.minHeight = 0

                    // if fully expanded, set height to WRAP_CONTENT, because when rotating the device
                    // the height calculated with this ValueAnimator isn't correct anymore
                    var layoutParams = this@ExpandableTwoLineTextView.layoutParams
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    layoutParams = layoutParams

                    // keep track of current status
                    isExpanded = true
                    animating = false
                }
            })

            // set interpolator
            valueAnimator.interpolator = expandInterpolator

            // start the animation
            valueAnimator
                .setDuration(animationDuration)
                .start()
            return true
        }
        return false
    }

    /**
     * Collapse this [TextView].
     * @return true if collapsed, false otherwise.
     */
    fun collapse(): Boolean {
        if (isExpanded && !animating && maxLinesCurrent >= 0) {
            // notify listener
            notifyOnCollapse()

            // measure expanded height
            val expandedHeight = this.measuredHeight

            // indicate that we are now animating
            animating = true

            // animate from expanded height to collapsed height
            val valueAnimator = ValueAnimator.ofInt(
                expandedHeight,
                collapsedHeight
            )
            valueAnimator.addUpdateListener { animation ->
                this@ExpandableTwoLineTextView.height = animation.animatedValue as Int
            }

            // wait for the animation to end
            valueAnimator.addListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator) {
                    // keep track of current status
                    isExpanded = false
                    animating = false

                    // set maxLines back to original value
                    maxLines = maxLinesCurrent

                    // if fully collapsed, set height back to WRAP_CONTENT, because when rotating the device
                    // the height previously calculated with this ValueAnimator isn't correct anymore
                    var layoutParams = this@ExpandableTwoLineTextView.layoutParams
                    layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    layoutParams = layoutParams
                }
            })

            // set interpolator
            valueAnimator.interpolator = collapseInterpolator

            // start the animation
            valueAnimator
                .setDuration(animationDuration)
                .start()
            return true
        }
        return false
    }
    //endregion

    //region public getters and setters
    /**
     * Sets the duration of the expand / collapse animation.
     * @param animationDuration duration in milliseconds.
     */
    fun setAnimationDuration(animationDuration: Long) {
        this.animationDuration = animationDuration
    }

    /**
     * Adds a listener which receives updates about this [ExpandableTwoLineTextView].
     * @param onExpandListener the listener.
     */
    fun addOnExpandListener(onExpandListener: OnExpandListener) {
        onExpandListeners.add(onExpandListener)
    }

    /**
     * Removes a listener which receives updates about this [ExpandableTwoLineTextView].
     * @param onExpandListener the listener.
     */
    fun removeOnExpandListener(onExpandListener: OnExpandListener?) {
        onExpandListeners.remove(onExpandListener)
    }

    /**
     * Sets a [TimeInterpolator] for expanding and collapsing.
     * @param interpolator the interpolator
     */
    fun setInterpolator(interpolator: TimeInterpolator) {
        expandInterpolator = interpolator
        collapseInterpolator = interpolator
    }
    //endregion
    /**
     * This method will notify the listener about this view being expanded.
     */
    private fun notifyOnCollapse() {
        for (onExpandListener in onExpandListeners) {
            onExpandListener.onCollapse(this)
        }
    }

    /**
     * This method will notify the listener about this view being collapsed.
     */
    private fun notifyOnExpand() {
        for (onExpandListener in onExpandListeners) {
            onExpandListener.onExpand(this)
        }
    }
    //region public interfaces
    /**
     * Interface definition for a callback to be invoked when
     * a [ExpandableTwoLineTextView] is expanded or collapsed.
     */
    interface OnExpandListener {
        /**
         * The [ExpandableTwoLineTextView] is being expanded.
         * @param view the textview
         */
        fun onExpand(view: ExpandableTwoLineTextView)

        /**
         * The [ExpandableTwoLineTextView] is being collapsed.
         * @param view the textview
         */
        fun onCollapse(view: ExpandableTwoLineTextView)
    }

    /**
     * Simple implementation of the [OnExpandListener] interface with stub
     * implementations of each method. Extend this if you do not intend to override
     * every method of [OnExpandListener].
     */
    class SimpleOnExpandListener : OnExpandListener {
        override fun onExpand(view: ExpandableTwoLineTextView) {
            // empty implementation
        }

        override fun onCollapse(view: ExpandableTwoLineTextView) {
            // empty implementation
        }
    } //endregion

    init {
        animationDuration = 750L

        // keep the original value of maxLines
        maxLinesCurrent = maxLines

        // create bucket of OnExpandListener instances
        onExpandListeners = ArrayList()

        // create default interpolators
        expandInterpolator = AccelerateDecelerateInterpolator()
        collapseInterpolator = AccelerateDecelerateInterpolator()
    }
}