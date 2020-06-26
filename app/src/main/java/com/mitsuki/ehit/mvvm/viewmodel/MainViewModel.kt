package com.mitsuki.ehit.mvvm.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.mitsuki.ehit.mvvm.model.MainModel
import com.mitsuki.ehit.mvvm.model.repository.GalleryPagingSource
import com.mitsuki.mvvm.base.BaseViewModel

class MainViewModel @ViewModelInject constructor(model: MainModel) :
    BaseViewModel<MainModel>(model) {


    fun data() = Pager(pagingConfig) {
        GalleryPagingSource(model)
    }.flow


    val pagingConfig = PagingConfig(
        pageSize = 25
    )
}