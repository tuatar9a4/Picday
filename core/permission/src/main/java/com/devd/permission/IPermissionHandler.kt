package com.devd.permission

interface IPermissionHandler {
    fun checkPermission(permissions: Array<String>): Map<String, Boolean>
    fun checkPermission(permission: String): Boolean

    suspend fun requestPermissionIfNeeded(permission: String): Boolean
    suspend fun requestPermissionIfNeeded(permissions: Array<String>): Map<String, Boolean>
}