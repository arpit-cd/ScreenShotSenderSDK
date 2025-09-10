package com.cd.screenshotsender.domain.use_cases

import android.content.Context
import com.cd.screenshotsender.data.network.HttpClientManager
import com.cd.screenshotsender.data.repository.SendScreenShotRepositoryImpl
import com.cd.screenshotsender.data.entities.PackageNameResponse
import com.cd.screenshotsender.domain.domain_utils.DataResponseStatus
import com.cd.screenshotsender.domain.repository.ISendScreenShotRepository

internal class SendPackageNameUseCase {

    suspend fun invoke(
        packageName: String,
        context: Context
    ): DataResponseStatus<PackageNameResponse> {
        val apiService = HttpClientManager.getApiService(context)
        val repository: ISendScreenShotRepository = SendScreenShotRepositoryImpl(apiService)
        return repository.sendPackageName(packageName)
    }
}