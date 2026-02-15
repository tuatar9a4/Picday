package com.devd.model.local

sealed class UploadState

data object StartUpload : UploadState()
data class Uploading(val progress: Int) : UploadState()
data class SuccessUpload(val uploadFileName: String) : UploadState()
data class FailUpload(val errorMessage: String) : UploadState()
