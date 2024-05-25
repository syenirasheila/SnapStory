package com.example.snapstory.ui.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.snapstory.databinding.ActivitySigninBinding


class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        playAnimation()
    }

    private fun playAnimation() {
        val firstTitle =
            ObjectAnimator.ofFloat(binding.tvTitleSigninFirst, View.ALPHA, 1f).setDuration(100)
        val secondTitle =
            ObjectAnimator.ofFloat(binding.tvTitleSigninSecond, View.ALPHA, 1f).setDuration(100)
        val description = ObjectAnimator.ofFloat(binding.tvDescSignin, View.ALPHA, 1f).setDuration(100)
        val inputTextLayout = ObjectAnimator.ofFloat(binding.inputSigninLayout, View.ALPHA, 1f).setDuration(100)
        val btnSignin = ObjectAnimator.ofFloat(binding.btnSignin, View.ALPHA, 1f).setDuration(100)
        val footer = ObjectAnimator.ofFloat(binding.footerLayout, View.ALPHA, 1f).setDuration(100)

        val together = AnimatorSet().apply {
            playTogether(inputTextLayout, btnSignin)
        }

        AnimatorSet().apply {
            playSequentially(
                firstTitle,
                secondTitle,
                description,
                together,
                footer
            )
            startDelay = 100
        }.start()
    }
}