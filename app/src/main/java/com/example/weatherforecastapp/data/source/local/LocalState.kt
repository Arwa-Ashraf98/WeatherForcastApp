package com.example.weatherforecastapp.data.source.local

import com.example.weatherforecastapp.data.models.FavAddress


sealed class LocalState{
    class OnSuccess(val list : List<FavAddress>):LocalState()
    class OnFail(val errorMessage: Throwable ):LocalState()
    object Loading : LocalState()
}
