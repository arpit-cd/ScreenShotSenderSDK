package com.cd.screenshotsender.presentation.overlay

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.util.TypedValue
import android.view.ContextThemeWrapper
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.ProgressBar
import androidx.core.content.ContextCompat
import com.google.android.material.R
import com.google.android.material.floatingactionbutton.FloatingActionButton

/**
 * View-based implementation of the tracking FAB overlay
 */
internal class ViewBasedTrackingOverlay(context: Context) :
    FrameLayout(context) {

    private var onSendClicked: (() -> Unit)? = null

    constructor(context: Context, onSendClicked: () -> Unit) : this(context) {
        this.onSendClicked = onSendClicked
    }

    private val themedContext =
        ContextThemeWrapper(context, R.style.Theme_Material3_Light)

    private lateinit var sendFab: FloatingActionButton

    private lateinit var progressBar: ProgressBar

    init {
        setupViews()
    }

    private fun setupViews() {
        // Set layout params for the container
        layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)

        val mainContainer = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            gravity = Gravity.CENTER_VERTICAL
            layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT)
        }

        val fabContainer = FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(dpToPx(56), dpToPx(56)).apply {
                marginEnd = dpToPx(8)
            }
        }

        sendFab = FloatingActionButton(themedContext).apply {
            layoutParams = LayoutParams(dpToPx(56), dpToPx(56))
            size = FloatingActionButton.SIZE_NORMAL
            elevation = dpToPx(6).toFloat()
            setImageResource(com.cd.screenshotsender.R.drawable.outline_upload_24)
            imageTintList = ColorStateList.valueOf(Color.WHITE)
            backgroundTintList =
                ContextCompat.getColorStateList(context, android.R.color.holo_blue_dark)
            setOnClickListener { onSendClicked?.invoke() }
        }

        progressBar = ProgressBar(context).apply {
            layoutParams = LayoutParams(dpToPx(32), dpToPx(32)).apply {
                gravity = Gravity.CENTER
            }
            indeterminateTintList = ColorStateList.valueOf(Color.WHITE)
            visibility = GONE
        }

        fabContainer.addView(sendFab)
        fabContainer.addView(progressBar)

        mainContainer.addView(fabContainer)
        addView(mainContainer)
    }

    /**
     * Show loading state in FAB
     */
    fun showLoading() {
        sendFab.setImageDrawable(null) // Hide send icon
        progressBar.visibility = VISIBLE
        sendFab.isEnabled = false
    }

    /**
     * Show success state with checkmark icon
     */
    fun showSuccess() {
        sendFab.setImageResource(android.R.drawable.ic_menu_upload_you_tube) // Checkmark-like icon
        progressBar.visibility = GONE
        sendFab.isEnabled = true
        sendFab.backgroundTintList =
            ContextCompat.getColorStateList(context, android.R.color.holo_green_dark)
    }

    /**
     * Show error state with error icon
     */
    fun showError() {
        sendFab.setImageResource(android.R.drawable.ic_dialog_alert)
        progressBar.visibility = GONE
        sendFab.isEnabled = true
        sendFab.backgroundTintList =
            ContextCompat.getColorStateList(context, android.R.color.holo_red_dark)
    }

    /**
     * Reset FAB to normal state
     */
    fun resetToNormalState() {
        sendFab.setImageResource(com.cd.screenshotsender.R.drawable.outline_upload_24)
        progressBar.visibility = GONE
        sendFab.isEnabled = true
        sendFab.backgroundTintList =
            ContextCompat.getColorStateList(context, android.R.color.holo_blue_dark)
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}