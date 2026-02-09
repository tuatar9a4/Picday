package com.devd.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@Composable
fun rememberPermissionHandler(): ComposablePermissionHandler {
    val context = LocalContext.current

    return remember {
        ComposablePermissionHandler(context)
    }.apply {
        RegisterLaunchers()
    }
}

class ComposablePermissionHandler(
    private val context: Context
) : IPermissionHandler {

    private var singlePermissionContinuation: CancellableContinuation<Boolean>? = null
    private var multiplePermissionContinuation: CancellableContinuation<Map<String, Boolean>>? =
        null

    private lateinit var singlePermissionLauncher: ActivityResultLauncher<String>
    private lateinit var multiplePermissionLauncher: ActivityResultLauncher<Array<String>>

    @Composable
    fun RegisterLaunchers() {
        singlePermissionLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.RequestPermission()
            ) { granted ->
                singlePermissionContinuation?.resume(granted)
                singlePermissionContinuation = null
            }

        multiplePermissionLauncher =
            rememberLauncherForActivityResult(
                ActivityResultContracts.RequestMultiplePermissions()
            ) { result ->
                multiplePermissionContinuation?.resume(result)
                multiplePermissionContinuation = null
            }
    }

    override fun checkPermission(permission: String): Boolean =
        ContextCompat.checkSelfPermission(
            context,
            permission
        ) == PackageManager.PERMISSION_GRANTED

    override fun checkPermission(permissions: Array<String>): Map<String, Boolean> =
        permissions.associateWith { checkPermission(it) }

    override suspend fun requestPermissionIfNeeded(
        permission: String
    ): Boolean = suspendCancellableCoroutine { continuation ->

        if (checkPermission(permission)) {
            continuation.resume(true)
            return@suspendCancellableCoroutine
        }

        singlePermissionContinuation = continuation
        singlePermissionLauncher.launch(permission)

        continuation.invokeOnCancellation {
            singlePermissionContinuation = null
        }
    }

    override suspend fun requestPermissionIfNeeded(
        permissions: Array<String>
    ): Map<String, Boolean> = suspendCancellableCoroutine { continuation ->

        val checkResult = checkPermission(permissions)
        if (checkResult.values.all { it }) {
            continuation.resume(checkResult)
            return@suspendCancellableCoroutine
        }

        val needPermissions = checkResult.filterValues { !it }.keys.toTypedArray()

        multiplePermissionContinuation = continuation
        multiplePermissionLauncher.launch(needPermissions)

        continuation.invokeOnCancellation {
            multiplePermissionContinuation = null
        }
    }
}