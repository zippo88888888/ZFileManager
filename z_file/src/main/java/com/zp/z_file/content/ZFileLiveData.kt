package com.zp.z_file.content

import androidx.lifecycle.MutableLiveData

internal class ZFileLiveData : MutableLiveData<Boolean>() {

    private object BUILDER {
        val builder = ZFileLiveData()
    }

    companion object {
        fun getInstance() = BUILDER.builder
    }

}