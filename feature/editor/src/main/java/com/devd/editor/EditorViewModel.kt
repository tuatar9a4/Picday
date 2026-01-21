package com.devd.editor

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class EditorViewModel @Inject constructor(

) : ViewModel() {

    var editorText = ""

    fun setDiaryText(text: String) {
        editorText = text
    }
}