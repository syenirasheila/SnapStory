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

        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
        }

        setupAction()
        playAnimation()
    }

    private fun setupAction() {

        binding.btnSignin.setOnClickListener {
            val email = binding.inputEmailSignin.text.toString()
            val password = binding.inputPasswordSignin.text.toString()

            if (!isInputValid(email, password)) {
                showToast(R.string.input_error.toString())
                return@setOnClickListener
            }

            signinViewModel.signinResponse.observe(this) {
                when (it) {
                    is UserResult.Success<*> -> {
                        showLoading(false)
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        showToast(R.string.signin_success.toString())
                    }
                    is UserResult.Loading -> {
                        showLoading(true)
                    }
                    is UserResult.Error -> {
                        showLoading(false)
                        showToast(R.string.signin_failed.toString())
                    }
                }
            }

            signinViewModel.signin (
                email,
                password
            )
        }

        binding.tvSignup.setOnClickListener {
            val intent = Intent(this, SignupActivity::class.java)
            startActivity(intent)
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

    private fun showLoading(isLoading: Boolean) {
        binding.progressIndicatorSignin.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isInputValid(email: String, password: String): Boolean {
        val isEmailValid = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.isNotEmpty() && password.length in 8..10

        return isEmailValid && isPasswordValid
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}