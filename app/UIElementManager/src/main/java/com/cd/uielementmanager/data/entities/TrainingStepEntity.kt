package com.cd.uielementmanager.data.entities

import com.google.gson.annotations.SerializedName

/**
 * Data layer entity for training step - represents API response structure
 */
internal data class TrainingStepEntity(
    @SerializedName("id")
    val id: Int,
    @SerializedName("step_number")
    val stepNumber: Int,
    @SerializedName("screen_name")
    val screenName: String,
    @SerializedName("highlighted_element")
    val highlightedElement: HighlightedElementEntity,
    @SerializedName("instructions")
    val instructions: List<String>
)
