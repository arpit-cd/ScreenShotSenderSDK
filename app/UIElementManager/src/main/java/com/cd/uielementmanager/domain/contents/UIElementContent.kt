package com.cd.uielementmanager.domain.contents

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.IntSize

/**
 * UI element entity for API communication and tracking
 * This is the single source of truth for UI element data
 */
data class UIElementContent(
    val tag: String,
    val bounds: BoundsContent,
) {
    // Helper properties for easy access
    val x: Float get() = bounds.position.x
    val y: Float get() = bounds.position.y
    val width: Int get() = bounds.size.width
    val height: Int get() = bounds.size.height


    /**
     * Element size as IntSize
     */
    val size: IntSize get() = IntSize(width, height)

    /**
     * Screen position (top-left corner) as Offset
     */
    val screenPosition: Offset get() = Offset(x, y)

    /**
     * Extract element type from tag
     * Tag format: "screenName.elementType.semanticId"
     */
    val elementType: String
        get() {
            val parts = tag.split(".")
            return if (parts.size >= 2) parts[1] else "unknown"
        }
}