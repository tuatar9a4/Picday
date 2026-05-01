package com.devd.permission

import android.Manifest


object Consts{
    val CAMERA_PERMISSION = arrayOf(
        Manifest.permission.CAMERA
    )
    val ALARM_PERMISSION = arrayOf(
        Manifest.permission.POST_NOTIFICATIONS
    )
//    fun getFilePermissionList() : Array<String>{
//        val fileListPermission = arrayListOf<String>()
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.Q){
//            fileListPermission.add(Manifest.permission.WRITE_EXTERNAL_STORAGE)
//        }
//        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.S_V2){
//            fileListPermission.add(Manifest.permission.READ_EXTERNAL_STORAGE)
//        }
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            fileListPermission.add(Manifest.permission.READ_MEDIA_IMAGES)
//            fileListPermission.add(Manifest.permission.READ_MEDIA_AUDIO)
//            fileListPermission.add(Manifest.permission.READ_MEDIA_VIDEO)
//        }
//        return fileListPermission.toTypedArray()
//    }
}