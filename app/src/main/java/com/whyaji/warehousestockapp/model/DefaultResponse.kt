package com.whyaji.warehousestockapp.model

data class DefaultResponse<T>(
    val statusCode: Int,
    val message: String,
    val data: T
)
