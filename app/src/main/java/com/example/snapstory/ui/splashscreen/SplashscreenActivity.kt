package com.example.snapstory.ui.splashscreen

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.lifecycle.lifecycleScope
import com.example.snapstory.data.local.preferences.UserSessionPreference
import com.example.snapstory.data.local.preferences.dataStore
import com.example.snapstory.ui.main.MainActivity
import com.example.snapstory.ui.onboarding.OnboardingActivity
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashscreenActivity : AppCompatActivity() {

    private lateinit var userSessionPreference: UserSessionPreference
    private val viewModel by viewModels<SplashscreenViewModel>()
    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        installSplashScreen().apply {
            setKeepOnScreenCondition{
                !viewModel.isReady.value
            }
            setOnExitAnimationListener { splashScreenViewProvider ->
                val splashScreenView = splashScreenViewProvider.view
                val fadeOut = ObjectAnimator.ofFloat(splashScreenView, View.ALPHA, 1f, 0f)
                fadeOut.duration = 1000L
                fadeOut.interpolator = DecelerateInterpolator()
                fadeOut.doOnEnd { splashScreenViewProvider.remove() }
                fadeOut.start()
            }
        }

        userSessionPreference = UserSessionPreference.getInstance(dataStore)
        lifecycleScope.launch {
            userSessionPreference.getUserSession().collect { session ->
                if (session.isSessionActive) {
                    navigateToMainActivity()
                } else {
                    navigateToOnboardingActivity()
                }
            }
        }

    }

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun navigateToOnboardingActivity() {
        val intent = Intent(this, OnboardingActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }
}