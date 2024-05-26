package com.example.snapstory.ui.signup

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.snapstory.R
import com.example.snapstory.databinding.ActivitySignupBinding
import com.example.snapstory.helper.UserResult
import com.example.snapstory.helper.ViewModelFactory
import com.example.snapstory.ui.signin.SigninActivity

class SignupActivity : AppCompatActivity() {

    private lateinit var binding : ActivitySignupBinding
    private val signupViewModel by viewModels<SignupViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvSignin.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }

        setupAction()
        playAnimation()
    }

    private fun setupAction() {

        binding.btnSignup.setOnClickListener {
            val name = binding.inputNameSignup.text.toString()
            val email = binding.inputEmailSignup.text.toString()
            val password = binding.inputPasswordSignup.text.toString()

            if (!isInputValid(name, email, password)) {
                showToast(R.string.fill_all_fields.toString())
                return@setOnClickListener
            }

            signupViewModel.signupResponse.observe(this) {
                when (it) {
                    is UserResult.Success<*> -> {
                        showLoading(false)
                        val intent = Intent(this, SigninActivity::class.java)
                        startActivity(intent)
                        showToast(R.string.signup_success.toString())
                        this.finish()
                    }
                    is UserResult.Loading -> {
                        showLoading(true)
                    }
                    is UserResult.Error -> {
                        showLoading(false)
                        if (it.error == "Email is already taken") {
                            showToast(R.string.email_already_taken.toString())
                        } else {
                            showToast(R.string.signup_failed.toString())
                        }
                    }
                }
            }
            signupViewModel.signup (
                name,
                email,
                password
            )
        }

        binding.tvSignin.setOnClickListener {
            val intent = Intent(this, SigninActivity::class.java)
            startActivity(intent)
        }

    }

    private fun playAnimation() {
        val firstTitle = ObjectAnimator.ofFloat(binding.tvTitleSignupFirst, View.ALPHA, 1f).setDuration(100)
        val secondTitle = ObjectAnimator.ofFloat(binding.tvTitleSignupSecond, View.ALPHA, 1f).setDuration(100)
        val description = ObjectAnimator.ofFloat(binding.tvDescSignup, View.ALPHA, 1f).setDuration(100)
        val inputTextLayout = ObjectAnimator.ofFloat(binding.inputSignupLayout, View.ALPHA, 1f).setDuration(100)
        val btnSignin = ObjectAnimator.ofFloat(binding.btnSignup, View.ALPHA, 1f).setDuration(100)
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
        binding.progressIndicatorSignup.visibility = if (isLoading) View.VISIBLE else View.GONE
    }

    private fun isInputValid(name: String, email: String, password: String): Boolean {
        val isNameValid = name.isNotEmpty()
        val isEmailValid = email.isNotEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
        val isPasswordValid = password.isNotEmpty() && password.length in 8..10

        return isNameValid && isEmailValid && isPasswordValid
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}