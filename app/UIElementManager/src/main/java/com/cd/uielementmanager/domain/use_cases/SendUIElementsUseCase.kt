package com.cd.uielementmanager.domain.use_cases

import android.content.Context
import com.cd.uielementmanager.data.network.HttpClientManager
import com.cd.uielementmanager.data.repository.UIElementsRepositoryImpl
import com.cd.uielementmanager.domain.domain_utils.DataResponseStatus
import com.cd.uielementmanager.domain.repository.IUIElementsRepository
import okhttp3.MultipartBody
import okhttp3.RequestBody

/**
 * Use case for sending UI elements to the server
 * Encapsulates business logic for UI element extraction
 * Following the same pattern as training flow feature
 */
internal class SendUIElementsUseCase {

    suspend fun invoke(
        screenshotPart: MultipartBody.Part,
        screenNamePart: RequestBody,
        timestampPart: RequestBody,
        screenInfoPart: RequestBody,
        elementsPart: RequestBody,
        context: Context
    ): DataResponseStatus<Unit> {
        val apiService = HttpClientManager.getApiService(context)
        val repository: IUIElementsRepository = UIElementsRepositoryImpl(apiService)
        return repository.sendUIElementsWithScreenshot(
            screenshotPart,
            screenNamePart,
            timestampPart,
            screenInfoPart,
            elementsPart
        )
    }
}