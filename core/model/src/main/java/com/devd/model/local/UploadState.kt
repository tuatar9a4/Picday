package com.devd.model.local

sealed class UploadState

data object StartUpload : UploadState()
data class Uploading(val progress: Int) : UploadState()
data object SuccessUpload : UploadState()
data object FailUpload : UploadState()
