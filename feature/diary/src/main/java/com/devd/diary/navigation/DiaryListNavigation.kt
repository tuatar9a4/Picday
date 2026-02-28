package com.devd.diary.navigation

import android.net.Uri
import android.os.Build
import android.os.Bundle
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.devd.commonsystem.utils.LocalAnimatedVisibilityScope
import com.devd.diary.DiaryListScreenRoute
import com.devd.model.local.DiaryInfo
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.reflect.typeOf

@Serializable
data class DiaryListRoute(
    val initList: List<DiaryInfo>,
    val startPos : Int,
)

val DiaryInfoListType = object : NavType<List<DiaryInfo>>(isNullableAllowed = false) {
    override fun put(bundle: Bundle, key: String, value: List<DiaryInfo>) {
        bundle.putParcelableArrayList(key, ArrayList(value))
    }

    override fun get(bundle: Bundle, key: String): List<DiaryInfo>? {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            bundle.getParcelableArrayList(key, DiaryInfo::class.java)
        } else {
            @Suppress("DEPRECATION")
            bundle.getParcelableArrayList(key)
        }
    }

    override fun parseValue(value: String): List<DiaryInfo> {
        return Json.decodeFromString(Uri.decode(value))
    }

    override fun serializeAsValue(value: List<DiaryInfo>): String {
        return Uri.encode(Json.encodeToString(value))
    }
}

fun NavGraphBuilder.diaryListScreen(
    modifier: Modifier = Modifier,
    backListener : () -> Unit
) {
    composable<DiaryListRoute>(
        typeMap = mapOf(typeOf<List<DiaryInfo>>() to DiaryInfoListType)
    ) { backstackEntry ->
        CompositionLocalProvider(
            LocalAnimatedVisibilityScope provides this
        ) {
            val route = backstackEntry.toRoute<DiaryListRoute>()

            DiaryListScreenRoute(
                modifier = modifier,
                initList = route.initList,
                startPos = route.startPos,
                onBackClick = backListener
            )
        }
    }
}