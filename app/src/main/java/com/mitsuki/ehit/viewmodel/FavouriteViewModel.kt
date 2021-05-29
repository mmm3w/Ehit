package com.mitsuki.ehit.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.ViewModel
import com.mitsuki.ehit.model.repository.RemoteRepository
import com.mitsuki.ehit.model.repository.Repository

class FavouriteViewModel @ViewModelInject constructor(@RemoteRepository var repository: Repository) :
    ViewModel() {



        

}