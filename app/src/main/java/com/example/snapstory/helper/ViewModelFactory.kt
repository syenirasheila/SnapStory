package com.example.snapstory.helper

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.snapstory.di.RepositoryInjection
import com.example.snapstory.ui.main.MainViewModel
import com.example.snapstory.ui.poststory.PostStoryViewModel
import com.example.snapstory.ui.signin.SigninViewModel
import com.example.snapstory.ui.signup.SignupViewModel
import com.example.snapstory.ui.storydetail.StoryDetailViewModel
import com.example.snapstory.ui.storymaps.StoryMapsViewModel

class ViewModelFactory (private val mApplication: Application) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {

            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(RepositoryInjection.provideAuthRepository(mApplication), RepositoryInjection.providePostStoryRepository(mApplication)) as T
            }

            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(RepositoryInjection.provideAuthRepository(mApplication)) as T
            }

            modelClass.isAssignableFrom(SigninViewModel::class.java) -> {
                SigninViewModel(RepositoryInjection.provideAuthRepository(mApplication)) as T
            }
            modelClass.isAssignableFrom(StoryDetailViewModel::class.java) -> {
                StoryDetailViewModel(RepositoryInjection.providePostStoryRepository(mApplication)) as T
            }
            modelClass.isAssignableFrom(PostStoryViewModel::class.java) -> {
                PostStoryViewModel(RepositoryInjection.providePostStoryRepository(mApplication)) as T
            }
            modelClass.isAssignableFrom(StoryMapsViewModel::class.java) -> {
                StoryMapsViewModel(RepositoryInjection.providePostStoryRepository(mApplication)) as T
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