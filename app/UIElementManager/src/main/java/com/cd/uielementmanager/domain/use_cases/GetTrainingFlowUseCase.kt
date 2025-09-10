package com.cd.uielementmanager.domain.use_cases

import android.content.Context
import com.cd.uielementmanager.data.network.HttpClientManager
import com.cd.uielementmanager.data.repository.UIElementsRepositoryImpl
import com.cd.uielementmanager.domain.contents.TrainingFlowContent
import com.cd.uielementmanager.domain.domain_utils.DataResponseStatus
import com.cd.uielementmanager.domain.repository.IUIElementsRepository

/**
 * Use case for fetching training flow data from the server
 * Following clean architecture pattern consistent with SendUIElementsUseCase
 */
internal class GetTrainingFlowUseCase {

    suspend fun invoke(context: Context): DataResponseStatus<TrainingFlowContent> {
        val apiService = HttpClientManager.getApiService(context)
        val repository: IUIElementsRepository = UIElementsRepositoryImpl(apiService)
        return repository.getTrainingFlow()
    }
}