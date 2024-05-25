package com.example.snapstory.ui.splashscreen

import android.animation.ObjectAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.view.animation.DecelerateInterpolator
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.animation.doOnEnd
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import com.example.snapstory.ui.onboarding.OnboardingActivity

@SuppressLint("CustomSplashScreen")
class SplashscreenActivity : AppCompatActivity() {


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

    }

    override fun onResume() {
        super.onResume()

        Handler(Looper.getMainLooper()).postDelayed({
            goToOnboardingActivity()
        }, 2000L)

    }

    override fun onPause() {
        super.onPause()
        Handler(Looper.getMainLooper()).removeCallbacksAndMessages(null)
    }

    private fun goToOnboardingActivity() {
        Intent(this, OnboardingActivity::class.java).also {
            startActivity(it)
            finish()
        }
    }
}