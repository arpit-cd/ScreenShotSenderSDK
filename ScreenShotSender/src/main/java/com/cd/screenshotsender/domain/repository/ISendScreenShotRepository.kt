package com.cd.screenshotsender.domain.repository

import com.cd.screenshotsender.data.entities.PackageNameResponse
import com.cd.screenshotsender.domain.domain_utils.DataResponseStatus
import okhttp3.MultipartBody


internal interface ISendScreenShotRepository {


    suspend fun sendScreenShotWithFlow(
        flowId: Int,
        screenshotPart: MultipartBody.Part
    ): DataResponseStatus<Unit>

    suspend fun sendPackageName(packageName: String): DataResponseStatus<PackageNameResponse>

}