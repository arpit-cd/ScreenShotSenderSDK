package com.cd.uielementmanager.data.repository

import com.cd.uielementmanager.data.entities.HighlightedElementEntity
import com.cd.uielementmanager.data.entities.PackageNameResponse
import com.cd.uielementmanager.data.entities.TrainingFlowEntity
import com.cd.uielementmanager.data.entities.TrainingStepEntity
import com.cd.uielementmanager.data.mappers.TrainingFlowMapper
import com.cd.uielementmanager.data.network.NetworkCallHelper.networkCall
import com.cd.uielementmanager.data.network.NetworkCallHelper.networkCallForUpload
import com.cd.uielementmanager.data.network.UIElementsApiService
import com.cd.uielementmanager.domain.contents.TrainingFlowContent
import com.cd.uielementmanager.domain.domain_utils.DataResponseStatus
import com.cd.uielementmanager.domain.repository.IUIElementsRepository
import com.cd.uielementmanager.presentation.utils.DataUiResponseStatus
import kotlinx.coroutines.delay
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody

/**
 * Repository implementation for UI elements operations using Retrofit
 * Following clean architecture principles
 */
internal class UIElementsRepositoryImpl(private val apiService: UIElementsApiService) :
    IUIElementsRepository {

    override suspend fun sendUIElementsWithScreenshot(
        screenshotPart: MultipartBody.Part,
        screenNamePart: RequestBody,
        timestampPart: RequestBody,
        screenInfoPart: RequestBody,
        elementsPart: RequestBody
    ): DataResponseStatus<Unit> {
        return networkCallForUpload {
            apiService.uploadUIElementsSnapshot(
                screenshot = screenshotPart,
                screenName = screenNamePart,
                timestamp = timestampPart,
                screenInfo = screenInfoPart,
                elements = elementsPart
            )
        }
    }

    override suspend fun sendScreenShotWithFlow(flowId: Int, screenshotPart: MultipartBody.Part): DataResponseStatus<Unit> {
        return networkCallForUpload {
            apiService.uploadScreenShot(
                flowId = flowId,
                screenshot = screenshotPart
            )
        }
    }

    override suspend fun sendPackageName(packageName: String): DataResponseStatus<PackageNameResponse> {
        return networkCall {
            apiService.uploadPackageName(packageName)
        }
    }

    override suspend fun getTrainingFlow(): DataResponseStatus<TrainingFlowContent> {
        // Return dummy response for now
        return try {
            // Simulate network delay
            delay(1000)
            
            val dummyEntity = createDummyTrainingFlowEntity()
            val mapper = TrainingFlowMapper()
            val mappedContent = mapper.mapData(dummyEntity)
            DataResponseStatus.success(mappedContent)
        } catch (e: Exception) {
            DataResponseStatus.failure(
                errorMessage = e.message ?: "Unknown error",
                errorCode = 500
            )
        }
        
        // TODO: Replace with actual API call when ready
        // return networkCall(
        //     mapper = TrainingFlowMapper(),
        //     api = { apiService.getTrainingFlow() }
        // )
    }
    
    private fun createDummyTrainingFlowEntity(): TrainingFlowEntity {
        return TrainingFlowEntity(
            id = 1,
            name = "Complete UI Elements Tutorial",
            stepCount = 12,
            steps = listOf(
                // Navigation elements
                TrainingStepEntity(
                    id = 1,
                    stepNumber = 1,
                    screenName = "ui_elements_screen",
                    highlightedElement = HighlightedElementEntity(
                        elementId = "back_button",
                        borderShape = "circle",
                        borderStrokeWidth = 8f,
                        borderRadius = 12f,
                        borderColor = "#FF5722"
                    ),
                    instructions = listOf(
                        "Welcome to the UI Elements Tutorial!",
                        "This is the back button",
                        "Tap here to go back to the previous screen"
                    )
                ),
                TrainingStepEntity(
                    id = 2,
                    stepNumber = 2,
                    screenName = "ui_elements_screen",
                    highlightedElement = HighlightedElementEntity(
                        elementId = "navigate_simple_button",
                        borderShape = "rounded",
                        borderStrokeWidth = 10f,
                        borderRadius = 16f,
                        borderColor = "#4CAF50"
                    ),
                    instructions = listOf(
                        "This is the navigation menu button",
                        "Tap here to navigate to Simple Elements screen",
                        "Notice the rounded highlight border"
                    )
                ),
                TrainingStepEntity(
                    id = 3,
                    stepNumber = 3,
                    screenName = "ui_elements_screen",
                    highlightedElement = HighlightedElementEntity(
                        elementId = "title",
                        borderShape = "rectangle",
                        borderStrokeWidth = 4f,
                        borderRadius = null,
                        borderColor = "#9C27B0"
                    ),
                    instructions = listOf(
                        "This is the screen title",
                        "It identifies the current screen you're viewing"
                    )
                ),
                
                // Horizontal cards section
                TrainingStepEntity(
                    id = 4,
                    stepNumber = 4,
                    screenName = "ui_elements_screen",
                    highlightedElement = HighlightedElementEntity(
                        elementId = "horizontal_title",
                        borderShape = "rounded",
                        borderStrokeWidth = 5f,
                        borderRadius = 8f,
                        borderColor = "#795548"
                    ),
                    instructions = listOf(
                        "Section headers organize content",
                        "This header introduces the horizontal elements"
                    )
                ),
                TrainingStepEntity(
                    id = 5,
                    stepNumber = 5,
                    screenName = "ui_elements_screen",
                    highlightedElement = HighlightedElementEntity(
                        elementId = "horizontal_card_1",
                        borderShape = "rounded",
                        borderStrokeWidth = 6f,
                        borderRadius = 20f,
                        borderColor = "#2196F3"
                    ),
                    instructions = listOf(
                        "This is the Home card",
                        "Cards display related information together",
                        "This card has a large rounded border"
                    )
                ),
                TrainingStepEntity(
                    id = 6,
                    stepNumber = 6,
                    screenName = "ui_elements_screen",
                    highlightedElement = HighlightedElementEntity(
                        elementId = "horizontal_card_2",
                        borderShape = "circle",
                        borderStrokeWidth = 12f,
                        borderRadius = null,
                        borderColor = "#FF9800"
                    ),
                    instructions = listOf(
                        "The Favorites card uses a circular highlight",
                        "Different shapes help distinguish elements",
                        "Notice the thick orange border"
                    )
                ),
                TrainingStepEntity(
                    id = 7,
                    stepNumber = 7,
                    screenName = "ui_elements_screen",
                    highlightedElement = HighlightedElementEntity(
                        elementId = "horizontal_card_3",
                        borderShape = "rectangle",
                        borderStrokeWidth = 8f,
                        borderRadius = null,
                        borderColor = "#E91E63"
                    ),
                    instructions = listOf(
                        "The Settings card has a rectangular border",
                        "Sharp corners create a different visual emphasis"
                    )
                ),
                
                // Vertical buttons section
                TrainingStepEntity(
                    id = 8,
                    stepNumber = 8,
                    screenName = "ui_elements_screen",
                    highlightedElement = HighlightedElementEntity(
                        elementId = "vertical_title",
                        borderShape = "rounded",
                        borderStrokeWidth = 5f,
                        borderRadius = 8f,
                        borderColor = "#607D8B"
                    ),
                    instructions = listOf(
                        "Here's another section header",
                        "This one introduces the vertical button elements"
                    )
                ),
                TrainingStepEntity(
                    id = 9,
                    stepNumber = 9,
                    screenName = "ui_elements_screen",
                    highlightedElement = HighlightedElementEntity(
                        elementId = "vertical_button_1",
                        borderShape = "rounded",
                        borderStrokeWidth = 6f,
                        borderRadius = 24f,
                        borderColor = "#00BCD4"
                    ),
                    instructions = listOf(
                        "Primary actions use filled buttons",
                        "The 'Add New Item' button is prominently displayed",
                        "Large corner radius creates a pill-shaped highlight"
                    )
                ),
                TrainingStepEntity(
                    id = 10,
                    stepNumber = 10,
                    screenName = "ui_elements_screen",
                    highlightedElement = HighlightedElementEntity(
                        elementId = "vertical_button_2",
                        borderShape = "rounded",
                        borderStrokeWidth = 4f,
                        borderRadius = 12f,
                        borderColor = "#8BC34A"
                    ),
                    instructions = listOf(
                        "Outlined buttons indicate secondary actions",
                        "The 'Edit Existing' button is less prominent",
                        "Medium corner radius for balanced appearance"
                    )
                ),
                TrainingStepEntity(
                    id = 11,
                    stepNumber = 11,
                    screenName = "ui_elements_screen",
                    highlightedElement = HighlightedElementEntity(
                        elementId = "vertical_button_3",
                        borderShape = "rectangle",
                        borderStrokeWidth = 10f,
                        borderRadius = null,
                        borderColor = "#F44336"
                    ),
                    instructions = listOf(
                        "Destructive actions use warning colors",
                        "The 'Delete Item' button uses error styling",
                        "Red highlight draws attention to dangerous actions"
                    )
                ),
                
                // Info card
                TrainingStepEntity(
                    id = 12,
                    stepNumber = 12,
                    screenName = "ui_elements_screen",
                    highlightedElement = HighlightedElementEntity(
                        elementId = "info_card",
                        borderShape = "rounded",
                        borderStrokeWidth = 8f,
                        borderRadius = 16f,
                        borderColor = "#3F51B5"
                    ),
                    instructions = listOf(
                        "Information cards provide additional context",
                        "This completes our UI Elements tutorial!",
                        "Tap anywhere on the highlighted area to finish"
                    )
                )
            )
        )
    }
}