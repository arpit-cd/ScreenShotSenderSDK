package com.cd.uielementmanager.domain.contents

/**
 * Individual step in a training flow
 * Clean domain model without framework dependencies
 */
data class TrainingStepContent(
    val id: Int,
    val stepNumber: Int,
    val screenName: String,
    val highlightedElementContent: HighlightedElementContent,
    val instructions: List<String>
)