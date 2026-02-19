package com.devd.data.repository

import androidx.room.Transaction
import com.devd.commonsystem.utils.getCurrentMonthRangeMillis
import com.devd.data.utils.CallResult
import com.devd.data.utils.SafeNetCall
import com.devd.model.local.CreateDiaryRequest
import com.devd.model.local.UpdateDiaryRequest
import com.devd.room.dao.DiaryBookDao
import com.devd.room.dao.DiaryDao
import com.devd.room.dao.DiaryImageDao
import com.devd.room.dao.DiaryTagDao
import com.devd.room.dao.TagDao
import com.devd.room.entity.DiaryBookEntity
import com.devd.room.entity.DiaryEntity
import com.devd.room.entity.DiaryImageEntity
import com.devd.room.entity.DiaryTagCrossEntity
import com.devd.room.entity.TagEntity
import kotlinx.coroutines.Dispatchers
import java.time.LocalDate
import java.time.YearMonth
import java.util.Date
import javax.inject.Inject

class DiaryBookRepository @Inject constructor(
    private val diaryBookDao: DiaryBookDao,
    private val diaryDao: DiaryDao,
    private val diaryImageDao: DiaryImageDao,
    private val diaryTagDao: DiaryTagDao,
    private val tagDao: TagDao,
) : SafeNetCall() {

    /* Diary Book */

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
                isMajor = isFirstBook,
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

    suspend fun fetchMajorDiaryBook(uuid: String) =
        safeApiCall(Dispatchers.IO) {
            val diaryBook = diaryBookDao.selectMainDiaryBook(uuid).transToModel()
            val (start, end) = LocalDate.now().getCurrentMonthRangeMillis()
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


    suspend fun hasDiaryBook(uuid: String) = safeApiCall(Dispatchers.IO) {
        diaryBookDao.selectAllDiaryBook(uuid).isNotEmpty()
    }.run {
        return@run this is CallResult.Success && this.res
    }

    /* Diary */

    suspend fun fetchDairiesByDiaryBook(diaryBookId: Long, diaryId: Long) =
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

    @Transaction
    suspend fun saveDairyWithExtras(
        diaryInfo: CreateDiaryRequest,
    ) {
        val createTime = System.currentTimeMillis()
        val diary = DiaryEntity(
            diaryBookId = diaryInfo.bookId,
            content = diaryInfo.content,
            createdAt = createTime,
            updatedAt = createTime,
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
    ){
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

        val firstImage =  diaryImageDao.getImagesByDiary(diary.localId).firstOrNull()

        if(imageRequest.firstOrNull()?.uri != firstImage?.uri){
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


}