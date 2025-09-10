package com.cd.screenshotsender.data.network

import com.cd.screenshotsender.data.entities.PackageNameResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path


internal interface SendScreenShotApiService {

    @Multipart
    @POST("flows/{flowId}/live-screenshot/")
    suspend fun uploadScreenShot(
        @Path("flowId") flowId: Int,
        @Part screenshot: MultipartBody.Part
    ): Response<ResponseBody>

    @GET("active-flows/{packageName}/")
    suspend fun uploadPackageName(
        @Path("packageName") packageName: String
    ): Response<PackageNameResponse>

}