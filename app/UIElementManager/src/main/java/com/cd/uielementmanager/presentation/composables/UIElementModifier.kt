package com.cd.uielementmanager.presentation.composables

import androidx.compose.runtime.Composable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.LayoutCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.testTag

/**
 * Composition local for accessing the UIElementTrackerViewModel
 * Following clean architecture pattern
 */
val LocalUIElementViewModel = compositionLocalOf<UIElementViewModel?> { null }

/**
 * Modifier extension to track a composable for UI element extraction
 * Updated to use new clean architecture models with explicit screen validation
 *
 * @param screenName The screen this element belongs to
 * @param tag Simple string identifier for this UI element (e.g., "home.submit-button", "btn_login", etc.)
 */
@Composable
fun Modifier.trackElement(screenName: String, tag: String): Modifier {
    val elementTracker = LocalUIElementViewModel.current

    if (elementTracker == null) {
        return this
    }

    return this
        .semantics {
            testTag = "ui_element_${screenName}_$tag"
        }
        .onGloballyPositioned { coordinates ->
            if (coordinates.isAttached) {
                val bounds = coordinates.boundsInWindow()
                elementTracker.registerElement(screenName, tag, bounds)
            }
        }
}


/**
 * Helper extension to get bounds in window coordinates
 */
private fun LayoutCoordinates.boundsInWindow(): Rect {
    val position = positionInWindow()
    val size = size
    return Rect(
        offset = position,
        size = Size(width = size.width.toFloat(), height = size.height.toFloat())
    )
}
