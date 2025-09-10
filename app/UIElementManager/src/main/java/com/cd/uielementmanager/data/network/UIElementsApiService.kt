package com.cd.uielementmanager.data.network

import com.cd.uielementmanager.data.entities.PackageNameResponse
import com.cd.uielementmanager.data.entities.TrainingFlowEntity
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Retrofit API interface for UI elements operations
 */
internal interface UIElementsApiService {

    @Multipart
    @POST("api/ui-extraction/guided-flow/upload-snapshot")
    suspend fun uploadUIElementsSnapshot(
        @Part screenshot: MultipartBody.Part,
        @Part("screenName") screenName: RequestBody,
        @Part("timestamp") timestamp: RequestBody,
        @Part("screenInfo") screenInfo: RequestBody,
        @Part("elements") elements: RequestBody
    ): Response<ResponseBody>

    @Multipart
    @POST("https://qa-stock.countrydelight.in/api/cd_training/flows/{flowId}/live-screenshot/")
    suspend fun uploadScreenShot(
        @Path("flowId") flowId: Int,
        @Part screenshot: MultipartBody.Part
    ): Response<ResponseBody>

    @GET("https://qa-stock.countrydelight.in/api/cd_training/active-flows/{packageName}/")
    suspend fun uploadPackageName(
        @Path("packageName") packageName: String
    ): Response<PackageNameResponse>

    @GET("api/training-flows")
    suspend fun getTrainingFlow(): Response<TrainingFlowEntity>
}