package com.serah.coparenting

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.serah.coparenting.retrofit.User

class MyViewModel:ViewModel() {
    val viewPagerEnabled = MutableLiveData<Boolean>()
    val currentUser=MutableLiveData<User>()
}