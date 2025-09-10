package com.cd.uielementmanager.domain.contents

/**
 * Training flow domain model containing all steps for a guided flow
 * Clean domain model without framework dependencies
 */
data class TrainingFlowContent(
    val id: Int,
    val name: String,
    val stepCount: Int,
    val steps: List<TrainingStepContent>
)