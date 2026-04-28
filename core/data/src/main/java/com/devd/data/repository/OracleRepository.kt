package com.devd.data.repository

import com.devd.data.utils.CallResult
import com.devd.data.utils.SafeNetCall
import com.devd.data.utils.StreamRequestBody
import com.devd.model.local.FailUpload
import com.devd.model.local.StartUpload
import com.devd.model.local.SuccessUpload
import com.devd.model.local.Uploading
import com.devd.network.di.NetworkModule
import com.devd.network.service.DiaryService
import com.devd.network.service.OracleService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import timber.log.Timber
import java.io.File
import javax.inject.Inject

class OracleRepository @Inject constructor(
    @param:NetworkModule.OciServer private val oracleService: OracleService,
    @param:NetworkModule.DiaryServer private val diaryService: DiaryService,
) : SafeNetCall() {

    fun uploadImageFile(
        uploadUrl: String,
        file: File
    ) = callbackFlow {
        trySend(StartUpload)
        runCatching {
            val requestBody = StreamRequestBody(
                file = file,
                contentType = "image/jpeg",
            ) {
                trySend(Uploading(it))
            }
            oracleService.uploadFile(
                url = uploadUrl,
                file = requestBody
            )
        }.onSuccess {
            trySend(SuccessUpload(file.name))
        }.onFailure { e ->
            e.printStackTrace()
            trySend(FailUpload(e.localizedMessage ?: "Fail"))
        }
        close()
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

    suspend fun fetchUploadUrl(
        uuid: String,
        fileName: String,
    ) = safeApiCall(Dispatchers.IO) {
        Timber.d("CheckUrl  uuid => ${uuid} | fileName : $fileName")
        diaryService.fetchUploadImageUrl(uuid, fileName)
    }.run {
        when (this) {
            is CallResult.Success -> this.res
            is CallResult.NetworkError -> null
        }
    }

    suspend fun deleteBucketImage(
        fileNames: List<String>,
    ) = safeApiCall(Dispatchers.IO) {
        diaryService.deleteBucketImage(fileNames)
    }

}