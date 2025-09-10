package com.cd.screenshotsender.data.repository

import com.cd.screenshotsender.domain.repository.ISendScreenShotRepository
import com.cd.screenshotsender.data.entities.PackageNameResponse
import com.cd.screenshotsender.data.network.NetworkCallHelper.networkCall
import com.cd.screenshotsender.data.network.NetworkCallHelper.networkCallForUpload
import com.cd.screenshotsender.data.network.SendScreenShotApiService
import com.cd.screenshotsender.domain.domain_utils.DataResponseStatus
import okhttp3.MultipartBody

internal class SendScreenShotRepositoryImpl(private val apiService: SendScreenShotApiService) :
    ISendScreenShotRepository {


    override suspend fun sendScreenShotWithFlow(
        flowId: Int,
        screenshotPart: MultipartBody.Part
    ): DataResponseStatus<Unit> {
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
}