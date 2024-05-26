package com.example.snapstory.helper

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snapstory.di.RepositoryInjection
import com.example.snapstory.ui.main.MainViewModel
import com.example.snapstory.ui.signin.SigninViewModel
import com.example.snapstory.ui.signup.SignupViewModel

class ViewModelFactory (private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(RepositoryInjection.provideAuthRepository(mApplication)) as T
            }

            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(RepositoryInjection.provideAuthRepository(mApplication)) as T
            }

            modelClass.isAssignableFrom(SigninViewModel::class.java) -> {
                SigninViewModel(RepositoryInjection.provideAuthRepository(mApplication)) as T
            }

        else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(application: Application): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(application)
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}