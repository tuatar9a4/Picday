package com.devd.data.repository

import androidx.room.Transaction
import com.devd.commonsystem.utils.getCurrentMonthRangeMillis
import com.devd.commonsystem.utils.getStartEndRangeMillis
import com.devd.data.utils.CallResult
import com.devd.data.utils.SafeNetCall
import com.devd.model.local.CreateDiaryRequest
import com.devd.model.local.DiaryBookEntity
import com.devd.model.local.DiaryBookInfo
import com.devd.model.local.DiaryInfo
import com.devd.model.local.UpdateDiaryRequest
import com.devd.model.remote.DiaryBookBatchSyncReq
import com.devd.network.di.NetworkModule
import com.devd.network.service.DiaryService
import com.devd.room.dao.DiaryBookDao
import com.devd.room.dao.DiaryDao
import com.devd.room.dao.DiaryImageDao
import com.devd.room.dao.DiaryTagDao
import com.devd.room.dao.TagDao
import com.devd.room.entity.DiaryEntity
import com.devd.room.entity.DiaryImageEntity
import com.devd.room.entity.DiaryTagCrossEntity
import com.devd.room.entity.TagEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.map
import java.time.Instant
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date
import javax.inject.Inject

class DiaryBookRepository @Inject constructor(
    @param:NetworkModule.DiaryServer private val diaryService: DiaryService,
    private val diaryBookDao: DiaryBookDao,
    private val diaryDao: DiaryDao,
    private val diaryImageDao: DiaryImageDao,
    private val diaryTagDao: DiaryTagDao,
    private val tagDao: TagDao,
) : SafeNetCall() {

    /* Diary Book */
    suspend fun insertNewDiaryBook(
        bookImage: String,
        bookTitle: String,
        uuid: String,
        bookDescription: String,
        bookPhaseType: Int = 0,
        bookColor: Int = 0
    ) = safeApiCall(Dispatchers.IO) {
        val currentMillis = Date().time
        diaryBookDao.insertDiaryBook(
            DiaryBookEntity(
                title = bookTitle,
                bookImage = bookImage,
                userUuid = uuid,
                description = bookDescription,
                bookPhaseType = bookPhaseType,
                bookColor = bookColor,
                isMajor = !hasDiaryBook(uuid),
                createdAt = currentMillis,
                updatedAt = currentMillis,
            )
        )
    }

    suspend fun fetchAllDiaryBooks(uuid: String) = safeApiCall(Dispatchers.IO) {
        diaryBookDao.selectAllDiaryBook(uuid)
    }.run {
        when (this) {
            is CallResult.Success -> this.res.map { it.transToModel() }
            is CallResult.NetworkError -> emptyList()
        }
    }

    fun fetchAllDairyBooksFlow(uuid: String) = diaryBookDao.selectAllDiaryBookFlow(uuid).map {
        it.map { bookDaoItem -> bookDaoItem.transToModel() }
    }

    suspend fun fetchMajorDiaryBook(uuid: String) =
        safeApiCall(Dispatchers.IO) {
            val diaryBook = diaryBookDao.selectMainDiaryBook(uuid).transToModel()
            val (start, end) = Date().time.getCurrentMonthRangeMillis()
            val monthDatCount = YearMonth.now().lengthOfMonth()
            val curMonthDiaryCount =
                diaryDao.getDiariesByDateRange(diaryBook.bookId, start, end).size
            diaryBook.monthWritePercent = curMonthDiaryCount.toFloat() / monthDatCount
            return@safeApiCall diaryBook
        }.run {
            return@run when (this) {
                is CallResult.Success -> this.res
                is CallResult.NetworkError -> null
            }
        }

    suspend fun fetchBookInfo(bookId: Long) =
        safeApiCall(Dispatchers.IO) { diaryBookDao.selectDiaryBook(bookId).transToModel() }
            .run {
                return@run when (this) {
                    is CallResult.Success -> this.res
                    is CallResult.NetworkError -> null
                }
            }

    suspend fun updateBookInfo(
        updateBook: DiaryBookInfo,
    ): String? =
        safeApiCall(Dispatchers.IO) {
            val originBook = diaryBookDao.selectDiaryBook(updateBook.bookId)
            diaryBookDao.updateDiaryBook(
                originBook.copy(
                    bookImage = updateBook.bookImage ?: originBook.bookImage,
                    title = updateBook.title,
                    description = updateBook.description,
                    bookPhaseType = updateBook.bookPhaseType.ordinal,
                    updatedAt = System.currentTimeMillis()
                )
            )
        }.run {
            return@run when (this) {
                is CallResult.Success -> null
                is CallResult.NetworkError -> this.message

            }
        }

    suspend fun deleteBookInfo(bookId: Long) =
        safeApiCall(Dispatchers.IO) {
            diaryBookDao.softDeleteDiaryBook(bookId.toString())
        }.run {
            return@run when (this) {
                is CallResult.Success -> null
                is CallResult.NetworkError -> this.message
            }
        }

    suspend fun hasDiaryBook(uuid: String) = safeApiCall(Dispatchers.IO) {
        diaryBookDao.selectAllDiaryBook(uuid).isNotEmpty()
    }.run {
        return@run this is CallResult.Success && this.res
    }

    suspend fun changeMajorBook(bookId: Long, uuid: String) = safeApiCall(Dispatchers.IO) {
        val originMainBook = diaryBookDao.selectMainDiaryBook(uuid).copy(isMajor = false)
        val newMainBook = diaryBookDao.selectDiaryBook(bookId).copy(isMajor = true)
        diaryBookDao.updateDiaryBook(originMainBook)
        diaryBookDao.updateDiaryBook(newMainBook)
    }

    /* Diary */
    suspend fun fetchAllDairiesByDiaryBook(diaryBookId: Long) = safeApiCall(Dispatchers.IO) {
        diaryDao.getDiariesByDiaryBook(diaryBookId)
    }.run {
        return@run if (this is CallResult.Success) this.res.map { it.transToModel() } else emptyList()
    }

    suspend fun fetchDairyByDiaryBook(diaryBookId: Long, diaryId: Long) =
        safeApiCall(Dispatchers.IO) {
            diaryDao.getDiariesWithExtras(diaryBookId, diaryId).transToModel()
        }.run {
            return@run if (this is CallResult.Success) this.res else null
        }

    suspend fun fetchMonthDairiesByDiaryBook(
        diaryBookId: Long,
        start: Long,
        end: Long
    ) = safeApiCall(Dispatchers.IO) {
        diaryDao.getDiariesByDateRange(diaryBookId, start, end).map { it.transToModel() }
    }.run {
        return@run if (this is CallResult.Success) this.res else emptyList()
    }

    suspend fun fetchOneDiaryForDate(
        diaryBookId: Long,
        date: Long
    ) = safeApiCall(Dispatchers.IO) {
        val (start, end) = Instant.ofEpochMilli(date)
            .atZone(ZoneId.systemDefault())
            .toLocalDate().getStartEndRangeMillis()

        diaryDao.getDiariesByDateRange(diaryBookId, start, end).firstOrNull()?.transToModel()
    }.run {
        return@run if (this is CallResult.Success) this.res else null
    }

    @Transaction
    suspend fun insertNewDairyWithExtras(
        diaryInfo: CreateDiaryRequest,
    ) {
        val diary = DiaryEntity(
            diaryBookId = diaryInfo.bookId,
            content = diaryInfo.content,
            createdAt = diaryInfo.createDate,
            updatedAt = diaryInfo.updateDate,
        )
        val diaryId = diaryDao.insertDiary(diary)

        val imageRequest = diaryInfo.imageUrls.mapIndexed { index, string ->
            DiaryImageEntity(
                diaryId = diaryId,
                uri = string,
                order = index
            )
        }

        diaryImageDao.deleteImagesByDiary(diaryId)
        diaryImageDao.insertImages(imageRequest)

        diaryTagDao.deleteByDiary(diaryId)

        diaryInfo.tags.forEach { tagName ->
            val tagId = tagDao.getTagByName(tagName)?.id
                ?: tagDao.insertTag(TagEntity(name = tagName))

            diaryTagDao.insertCross(
                DiaryTagCrossEntity(
                    diaryId = diaryId,
                    tagId = tagId
                )
            )
        }
    }

    @Transaction
    suspend fun updateDiaryWithExtras(
        diaryInfo: UpdateDiaryRequest
    ) {
        val diaryExtras = diaryDao.getDiaryById(diaryId = diaryInfo.diaryId)!!
        val diary = diaryExtras.diary
        diary.content = diaryInfo.content
        diary.updatedAt = System.currentTimeMillis()
        diaryDao.updateDiary(diary)

        val imageRequest = diaryInfo.imageUrls.mapIndexed { index, string ->
            DiaryImageEntity(
                diaryId = diary.localId,
                uri = string,
                order = index
            )
        }

        val firstImage = diaryImageDao.getImagesByDiary(diary.localId).firstOrNull()

        if (imageRequest.firstOrNull()?.uri != firstImage?.uri) {
            diaryImageDao.deleteImagesByDiary(diary.localId)
            diaryImageDao.insertImages(imageRequest)
        }

        diaryTagDao.deleteByDiary(diary.localId)
        diaryInfo.tags.forEach { tagName ->
            val tagId = tagDao.getTagByName(tagName)?.id
                ?: tagDao.insertTag(TagEntity(name = tagName))

            diaryTagDao.insertCross(
                DiaryTagCrossEntity(
                    diaryId = diary.localId,
                    tagId = tagId
                )
            )
        }
    }

    suspend fun deleteDiaryWithExtras(
        id: Long,
    ): DiaryInfo? {
        val targetDiary = diaryDao.getDiaryById(diaryId = id) ?: return null
        diaryDao.softDeleteDiary(id)
        return targetDiary.transToModel()
    }

    suspend fun fetchNotSyncDiaryBooks(
        uuid: String
    ): Boolean {
        safeApiCall(Dispatchers.IO) {
            val notSyncBooks = diaryBookDao.selectAllNotSyncDiaryBook(uuid)
            val bookSyncResult =
                diaryService.syncDiaryBooksBatch(DiaryBookBatchSyncReq(notSyncBooks))
            bookSyncResult.syncedItems.forEach {
                diaryBookDao.updateRemoteId(it.remoteId, it.localId)
            }
        }.run {
            return this is CallResult.Success
        }
    }


}