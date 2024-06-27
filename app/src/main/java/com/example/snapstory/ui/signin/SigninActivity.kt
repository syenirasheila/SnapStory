package com.example.snapstory.ui.signin

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.snapstory.R
import com.example.snapstory.databinding.ActivitySigninBinding
import com.example.snapstory.helper.UserResult
import com.example.snapstory.helper.ViewModelFactory
import com.example.snapstory.ui.main.MainActivity
import com.example.snapstory.ui.signup.SignupActivity

class SigninActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySigninBinding
    private val signinViewModel by viewModels<SigninViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySigninBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAction()
        playAnimation()
    }

    private fun setupAction() {

        binding.btnSignin.setOnClickListener {
            val email = binding.inputEmailSignin.text.toString()
            val password = binding.inputPasswordSignin.text.toString()

            if (!isInputValid(email, password)) {
                showToast(getString(R.string.input_error))
                return@setOnClickListener
            }

            signinViewModel.signin(email,password).observe(this) {
                when (it) {
                    is UserResult.Success -> {
                        showLoading(false)
                        showToast(getString(R.string.signin_success))
                        navigateToMainActivity()
                    }
                    is UserResult.Loading -> {
                        showLoading(true)
                    }
                    is UserResult.Error -> {
                        showLoading(false)
                        when (it.error) {
                            "User not found" -> {
                                showToast(getString(R.string.email_notfound))
                            }
                            "Invalid password" -> {
                                showToast(getString(R.string.invalid_password_signin))
                            }
                            else -> {
                                showToast(getString(R.string.signin_failed))
                            }
                        }
                    }
                }
            }
        }

        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java).apply {
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK
            }
            startActivity(intent)
            finish()
        }
    }

    private fun playAnimation() {
        val firstTitle = ObjectAnimator.ofFloat(binding.tvTitleSigninFirst, View.ALPHA, 1f).setDuration(100)
        val secondTitle = ObjectAnimator.ofFloat(binding.tvTitleSigninSecond, View.ALPHA, 1f).setDuration(100)
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

    private fun navigateToMainActivity() {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        startActivity(intent)
        finish()
    }

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicator.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isInputValid(email: String, password: String): Boolean {
        val isEmailValid = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.isNotEmpty() && password.length >= 8

        return isEmailValid && isPasswordValid
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}