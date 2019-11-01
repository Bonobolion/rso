package com.tigerfallout.room.ui.bulletin

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class BulletinViewModel : ViewModel() {

    private val _text = MutableLiveData<String>().apply {
        value = "This is Bulletin Board Fragment"
    }
    val text: LiveData<String> = _text
}