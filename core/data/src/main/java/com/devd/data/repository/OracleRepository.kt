package com.devd.data.repository

import com.devd.data.di.RepositoryModule
import com.devd.data.utils.SafeNetCall
import com.devd.data.utils.StreamRequestBody
import com.devd.model.local.FailUpload
import com.devd.model.local.StartUpload
import com.devd.model.local.SuccessUpload
import com.devd.model.local.Uploading
import com.devd.network.di.NetworkModule
import com.devd.network.service.OracleService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flowOn
import java.io.File
import javax.inject.Inject

class OracleRepository @Inject constructor(
    @param:RepositoryModule.OciKey private val ociBuketKey: String,
    @param:NetworkModule.OciServer private val oracleService: OracleService
) : SafeNetCall() {

    fun uploadImageFile(
        header: String,
        file: File
    ) = callbackFlow {
        trySend(StartUpload)
        runCatching {
//            val requestBody = MultipartBody.Part.createFormData(
//                "image",
//                file.name,
//                StreamRequestBody(
//                    file = file,
//                    contentType = "image/jpeg",
//                ) {
//                    trySend(Uploading(it))
//                }
//            )

            val requestBody = StreamRequestBody(
                file = file,
                contentType = "image/jpeg",
            ) {
                trySend(Uploading(it))
            }
            oracleService.uploadFile(
                key = ociBuketKey,
                header = header,
                fileName = file.name,
                file = requestBody
            )
        }.onSuccess {
            trySend(SuccessUpload)
        }.onFailure { e ->
            e.printStackTrace()
            trySend(FailUpload)
        }

        close()
    }.flowOn(Dispatchers.IO).distinctUntilChanged()

}