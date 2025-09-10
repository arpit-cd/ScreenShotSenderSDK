package com.cd.screenshotsender.data.entities

import com.google.gson.annotations.SerializedName

internal data class PackageNameResponse(
    @SerializedName("flow_id")
    val flowId: Int?
)