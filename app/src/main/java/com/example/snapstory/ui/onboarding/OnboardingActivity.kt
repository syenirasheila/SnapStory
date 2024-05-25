package com.example.snapstory.ui.onboarding

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.example.snapstory.databinding.ActivityOnboardingBinding
import com.example.snapstory.ui.signin.SignInActivity
import com.example.snapstory.ui.signup.SignUpActivity


@Suppress("DEPRECATION")
class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        playAnimation()
    }

    private fun setupAction() {
        binding.btnSigninOnboarding.setOnClickListener {
            val intent = Intent(this, SignInActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }

        binding.btnSignupOnboarding.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
            overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
        }
    }

    private fun playAnimation() {
        ObjectAnimator.ofFloat(binding.ivOnboarding, View.TRANSLATION_X, -30f, 30f).apply {
            duration = 6000
            repeatCount = ObjectAnimator.INFINITE
            repeatMode = ObjectAnimator.REVERSE
        }.start()

        val signin = ObjectAnimator.ofFloat(binding.btnSigninOnboarding, View.ALPHA, 1f).setDuration(100)
        val signup = ObjectAnimator.ofFloat(binding.btnSignupOnboarding, View.ALPHA, 1f).setDuration(100)
        val sparkleAccent = ObjectAnimator.ofFloat(binding.ivSparkle, View.ALPHA, 1f).setDuration(100)
        val highlightAccent = ObjectAnimator.ofFloat(binding.ivHighlight, View.ALPHA, 1f).setDuration(100)
        val firstTitle = ObjectAnimator.ofFloat(binding.tvTitleOnboardingFirst, View.ALPHA, 1f).setDuration(100)
        val secondTitle = ObjectAnimator.ofFloat(binding.tvTitleOnboardingSecond, View.ALPHA, 1f).setDuration(100)
        val thirdTitle = ObjectAnimator.ofFloat(binding.tvTitleOnboardingThird, View.ALPHA, 1f).setDuration(100)
        val description = ObjectAnimator.ofFloat(binding.tvDescOnboarding, View.ALPHA, 1f).setDuration(100)

        val accentTogether = AnimatorSet().apply {
            playTogether(sparkleAccent, highlightAccent)
        }

        val btnTogether = AnimatorSet().apply {
            playTogether(signin, signup)
        }

        AnimatorSet().apply {
            playSequentially(
                firstTitle,
                secondTitle,
                thirdTitle,
                description,
                accentTogether,
                btnTogether)
            startDelay = 100
        }.start()
    }

}