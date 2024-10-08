package com.example.snapstory.ui.customview

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import com.example.snapstory.R
import com.google.android.material.textfield.TextInputLayout

class EmailInputEditText : AppCompatEditText, View.OnTouchListener  {

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    private fun init() {
        setOnTouchListener(this)

        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val parentLayout = parent.parent as? TextInputLayout
                return when {
                    s.isNullOrEmpty() -> {
                        parentLayout?.error = context.getString(R.string.email_required)
                    }
                    !isValidEmail(s.toString()) -> {
                        parentLayout?.error =  context.getString(R.string.invalid_email)
                    }
                    else -> {
                        parentLayout?.error =  null
                    }
                }
            }

            override fun afterTextChanged(p0: Editable?) {
            }

        })
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        if (event?.action == MotionEvent.ACTION_DOWN) {
            val parentLayout = parent.parent as? TextInputLayout
            if (text.isNullOrEmpty()) {
                parentLayout?.error = context.getString(R.string.email_required)
            }
        }
        return false
    }

    private fun isValidEmail(email: CharSequence): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

}