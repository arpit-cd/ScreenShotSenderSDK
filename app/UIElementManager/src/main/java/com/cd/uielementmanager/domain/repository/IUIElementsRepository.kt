package com.cd.uielementmanager.domain.repository

import com.cd.uielementmanager.data.entities.PackageNameResponse
import com.cd.uielementmanager.domain.contents.TrainingFlowContent
import com.cd.uielementmanager.domain.domain_utils.DataResponseStatus
import com.cd.uielementmanager.presentation.utils.DataUiResponseStatus
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody

/**
 * Repository interface for UI elements operations
 * Following clean architecture principles - domain layer defines the contract
 */
internal interface IUIElementsRepository {

    /**
     * Send UI elements with screenshot to the server
     *
     * @param screenshotPart Screenshot file as multipart
     * @param screenNamePart Screen name as request body
     * @param timestampPart Timestamp as request body
     * @param screenInfoPart Screen information as request body
     * @param elementsPart UI elements data as request body
     * @return DataResponseStatus with extraction response
     */
    suspend fun sendUIElementsWithScreenshot(
        screenshotPart: MultipartBody.Part,
        screenNamePart: RequestBody,
        timestampPart: RequestBody,
        screenInfoPart: RequestBody,
        elementsPart: RequestBody
    ): DataResponseStatus<Unit>

    suspend fun sendScreenShotWithFlow(
        flowId: Int,
        screenshotPart: MultipartBody.Part
    ): DataResponseStatus<Unit>

    suspend fun sendPackageName(packageName: String): DataResponseStatus<PackageNameResponse>
    
    /**
     * Get training flow data by ID
     *
     * @return DataResponseStatus with TrainingFlow data
     */
    suspend fun getTrainingFlow(): DataResponseStatus<TrainingFlowContent>

}