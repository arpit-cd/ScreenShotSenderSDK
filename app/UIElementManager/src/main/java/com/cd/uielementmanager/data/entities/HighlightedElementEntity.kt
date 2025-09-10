package com.cd.uielementmanager.data.entities

import com.google.gson.annotations.SerializedName

/**
 * Data layer entity for highlighted element - represents API response structure
 */
internal data class HighlightedElementEntity(
    @SerializedName("element_id")
    val elementId: String,
    @SerializedName("border_shape")
    val borderShape: String, // "circle", "rounded", "rectangle"
    @SerializedName("border_stroke_width")
    val borderStrokeWidth: Float, // in pixels
    @SerializedName("border_radius")
    val borderRadius: Float? = null, // in pixels, only used for "rounded" shape
    @SerializedName("border_color")
    val borderColor: String? = null // hex color code (e.g., "#FF0000")
)