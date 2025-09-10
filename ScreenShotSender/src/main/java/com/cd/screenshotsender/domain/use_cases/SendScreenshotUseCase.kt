package com.cd.screenshotsender.domain.use_cases

import android.content.Context
import com.cd.screenshotsender.data.network.HttpClientManager
import com.cd.screenshotsender.data.repository.SendScreenShotRepositoryImpl
import com.cd.screenshotsender.domain.domain_utils.DataResponseStatus
import com.cd.screenshotsender.domain.repository.ISendScreenShotRepository
import okhttp3.MultipartBody

internal class SendScreenshotUseCase {

    suspend fun invoke(
        flowId: Int,
        screenshotPart: MultipartBody.Part,
        context: Context
    ): DataResponseStatus<Unit> {
        val apiService = HttpClientManager.getApiService(context)
        val repository: ISendScreenShotRepository = SendScreenShotRepositoryImpl(apiService)
        return repository.sendScreenShotWithFlow(flowId, screenshotPart)
    }
}