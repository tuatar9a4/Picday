package com.devd.data.repository

import com.devd.data.utils.CallResult
import com.devd.data.utils.SafeNetCall
import com.devd.room.DiaryBookEntity
import com.devd.room.dao.DiaryBookDao
import kotlinx.coroutines.Dispatchers
import java.util.Date
import javax.inject.Inject

class DiaryBookRepository @Inject constructor(
    private val diaryBookDao: DiaryBookDao
) : SafeNetCall() {


    suspend fun insertNewDiaryBook(
        bookTitle: String,
        uuid: String,
        bookDescription: String
    ) = safeApiCall(Dispatchers.IO) {
        val isFirstBook = diaryBookDao.selectAllDiaryBook(uuid).isEmpty()
        val currentMillis = Date().time
        diaryBookDao.insertDiaryBook(
            DiaryBookEntity(
                title = bookTitle,
                userLocalUUId = uuid,
                description = bookDescription,
                isMain = isFirstBook,
                createdAt = currentMillis,
                updatedAt = currentMillis
            )
        )
    }


    suspend fun fetchAllDairies(uuid: String) =
        safeApiCall(Dispatchers.IO) { diaryBookDao.selectAllDiaryBook(uuid) }.run {
            return@run when (this) {
                is CallResult.Success -> this.res.map { it.transToModel() }
                is CallResult.NetworkError -> emptyList()
            }
        }

    suspend fun hasDiaryBook(uuid: String) = safeApiCall(Dispatchers.IO) {
        diaryBookDao.selectAllDiaryBook(uuid).isNotEmpty()
    }.run {
        return@run this is CallResult.Success && this.res
    }

}