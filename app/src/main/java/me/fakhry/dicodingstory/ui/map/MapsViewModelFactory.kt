package me.fakhry.dicodingstory.ui.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import me.fakhry.dicodingstory.di.Injection
import me.fakhry.dicodingstory.repository.MapsRepository

class MapsViewModelFactory private constructor(private val mapsRepository: MapsRepository) :
    ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MapsViewModel::class.java)) {
            return MapsViewModel(mapsRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }

    companion object {
        @Volatile
        private var instance: MapsViewModelFactory? = null

        fun getInstance(): MapsViewModelFactory =
            instance ?: synchronized(this) {
                instance ?: MapsViewModelFactory(Injection.provideRepository())
            }.also { instance = it }
    }
}