package com.devd.permission

import android.content.Context
import android.content.pm.PackageManager
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class PermissionHandler (
    private val context: Context,
    private val registry : ActivityResultRegistry
) : DefaultLifecycleObserver, IPermissionHandler {

    private var singlePermissionContinuation: CancellableContinuation<Boolean>? = null
    private lateinit var singlePermissionResultLauncher : ActivityResultLauncher<String>

    private var multiplePermissionContinuation: CancellableContinuation<Map<String, Boolean>>? = null
    private lateinit var multiplePermissionResultLauncher : ActivityResultLauncher<Array<String>>

    override fun onCreate(owner: LifecycleOwner) {
        super.onCreate(owner)
        singlePermissionResultLauncher = registry.register("key", owner, ActivityResultContracts.RequestPermission()) { isGranted: Boolean  ->
            singlePermissionContinuation?.resume(isGranted)
        }

        multiplePermissionResultLauncher = registry.register("key", owner,
            ActivityResultContracts.RequestMultiplePermissions()
        ) { isGranted ->
            multiplePermissionContinuation?.resume(isGranted)
        }
    }

    override fun checkPermission(permissions: Array<String>): Map<String, Boolean> =
        permissions.associateWith { permission ->
            ContextCompat.checkSelfPermission(
                context,
                permission
            ) == PackageManager.PERMISSION_GRANTED
        }

    override fun checkPermission(permission: String): Boolean = ContextCompat.checkSelfPermission(
        context,
        permission
    ) == PackageManager.PERMISSION_GRANTED

    override suspend fun requestPermissionIfNeeded(permission: String) = suspendCancellableCoroutine{ continuation ->
        //권한이 이미 동의된 경우 return
        if(checkPermission(permission)) {
            continuation.resume(true)
            return@suspendCancellableCoroutine
        }

        // 동의하지 않은 권한만 권한 요청
        singlePermissionContinuation = continuation
        singlePermissionResultLauncher.launch(permission)

        continuation.invokeOnCancellation {
            singlePermissionContinuation = null
        }
    }

    override suspend fun requestPermissionIfNeeded(permissions: Array<String>) = suspendCancellableCoroutine{ continuation ->
        // 모든 권한이 동의된 경우 return
        val checkPermissionResult = checkPermission(permissions)
        if(checkPermissionResult.values.all { it }) {
            continuation.resume(checkPermissionResult)
            return@suspendCancellableCoroutine
        }

        // 동의하지 않은 권한만 권한 요청
        val needPermission = checkPermissionResult.filterValues { !it }.keys.toTypedArray()
        multiplePermissionContinuation = continuation
        multiplePermissionResultLauncher.launch(needPermission)

        continuation.invokeOnCancellation {
            multiplePermissionContinuation = null
        }
    }
}