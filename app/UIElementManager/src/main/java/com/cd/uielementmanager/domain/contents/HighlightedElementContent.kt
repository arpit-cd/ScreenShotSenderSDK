package com.cd.uielementmanager.domain.contents

/**
 * Element to be highlighted in a training step
 * Clean domain model without framework dependencies
 */
data class HighlightedElementContent(
    val elementId: String,
    val borderShape: String?, // "circle", "rounded", "rectangle"
    val borderStrokeWidth: Float?, // in pixels
    val borderRadius: Float? = null, // in pixels, only used for "rounded" shape
    val borderColor: String? = null // hex color code (e.g., "#FF0000")
)