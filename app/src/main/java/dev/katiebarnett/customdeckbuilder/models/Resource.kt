package dev.katiebarnett.customdeckbuilder.models

sealed class Resource<T>(open val data: T?)

data class LoadingResource<T> constructor(override val data: T? = null) : Resource<T>(data)
data class ErrorResource<T> constructor(val error: Throwable?, override val data: T? = null) : Resource<T>(data)
data class SuccessResource<T> constructor(override val data: T) : Resource<T>(data)