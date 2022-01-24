package com.mitsuki.ehit.viewmodel

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.mitsuki.ehit.crutch.extensions.string
import com.mitsuki.ehit.model.ehparser.GalleryFavorites
import com.mitsuki.ehit.model.entity.FavouriteCountWrap
import com.mitsuki.ehit.model.entity.Gallery
import com.mitsuki.ehit.model.page.FavouritePageIn
import com.mitsuki.ehit.crutch.di.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository
import com.mitsuki.ehit.R
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class FavouriteViewModel @Inject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {

    private val mListPageIn: FavouritePageIn by lazy { FavouritePageIn() }

    private val countWrap: FavouriteCountWrap by lazy { FavouriteCountWrap() }

    val count get() = countWrap.count

    val searchBarHint: MutableLiveData<String> by lazy { MutableLiveData() }

    val favouriteList: LiveData<PagingData<Gallery>> by lazy {
        repository.favoriteList(mListPageIn, countWrap)
            .cachedIn(viewModelScope)
            .asLiveData()
    }

    fun setFavouriteGroup(tag: Int) {
        mListPageIn.group = tag
        mListPageIn.targetPage = 1

        searchBarHint.postValue(
            if (tag < 0) string(R.string.text_all) else GalleryFavorites.findName(tag)
        )
    }


}