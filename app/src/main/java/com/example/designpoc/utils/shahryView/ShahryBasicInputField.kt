package com.example.designpoc.utils.shahryView

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.designpoc.R
import com.example.designpoc.databinding.ShahryBasicInputTextBinding
import com.example.designpoc.utils.extensions.getColorResource
import com.example.designpoc.utils.extensions.getDrawableResource
import com.example.designpoc.utils.extensions.hideKeyboard
import com.example.designpoc.utils.extensions.showSoftKeyboard
import com.example.designpoc.utils.vibrate

class ShahryBasicInputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(
    context, attrs, defStyle
), TextWatcher {

    private val binding by lazy { ShahryBasicInputTextBinding.inflate(LayoutInflater.from(context), this, true) }

    private var textDisabledColor = context.getColorResource(R.color.platinum_400)
    private var textColorRes = 0
    private var hintTextColorRes = 0
    private var hintText = 0
    private var mListener: TextChangeListener? = null
    private var isFieldError: Boolean? = false
    private var helperText: String = ""

    private var textChangeListener = object :
        TextChangeListener {
        override fun onTextChangeListener(
            charSequence: CharSequence?,
        ) {
            if (charSequence.toString().isNotEmpty() && isFieldError == true) {
                renderInitial()
                setHelperText(helperText)
            }
        }

        override fun onDataEntered(view: View, hasFocus: Boolean) {
            if (hasFocus) {
                (view as EditText).isCursorVisible = true
                showSoftKeyboard()
            } else {
                view.clearFocus()
                context.hideKeyboard(view)
                (view as EditText).isCursorVisible = false
            }
        }
    }

    fun setHelperText(helperText: String) {
        this.helperText = helperText
    }

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ShahryBasicInputField)
        if (attrs != null) {
            textColorRes =
                attributes.getColor(
                    R.styleable.ShahryBasicInputField_basicTextColor,
                    context.getColorResource(R.color.black)
                )

            hintTextColorRes = attributes.getColor(
                R.styleable.ShahryBasicInputField_basicHintTextColor,
                context.getColorResource(R.color.platinum_600)
            )

            textDisabledColor = attributes.getColor(
                R.styleable.ShahryBasicInputField_basicDisabledTextColor,
                context.getColorResource(R.color.platinum_400)
            )
            hintText = attributes.getResourceId(R.styleable.ShahryBasicInputField_basicHint, 5)

        }
        binding.editText.background = context.getDrawableResource(R.drawable.ic_background_normal)
        renderInitial()
        attributes.recycle()
    }

    fun renderInitial(
        enabled: Boolean = true,
    ) {
        binding.apply {
            if (!enabled) {
                editText.clearFocus()
                editText.isEnabled = false
                root.isClickable = false
            } else {
                editText.isEnabled = true
                root.isClickable = true
            }
            editText.apply {
                background = context.getDrawableResource(R.drawable.ic_background_edit_text)
                hint = context.getString(hintText)
                setHintTextColor(
                    ColorStateList.valueOf(
                        if (enabled) {
                            context.getColorResource(R.color.platinum_600)
                        } else {
                            textDisabledColor
                        }
                    )
                )
                setTextColor(
                    if (enabled) {
                        textColorRes
                    } else {
                        textDisabledColor
                    }
                )

                addTextChangedListener(this@ShahryBasicInputField)
                setViewEventListener(textChangeListener, false)
                setOnFocusChangeListener { view, hasFocus ->
                    mListener?.onDataEntered(editText, hasFocus)
                }
            }
            binding.tvErrorHelper.apply {
                isVisible = helperText.isNotEmpty() == true
                text = helperText
                setTextColor(ColorStateList.valueOf(context.getColorResource(R.color.platinum_600)))
            }
        }
    }

    fun renderError(message: String = "") {
        binding.apply {
            root.vibrate()
            editText.apply {
                background = context.getDrawableResource(R.drawable.ic_background_edit_text_error)
                hint = context.getString(hintText)
                setHintTextColor(
                    when (hintTextColorRes) {
                        0 -> null
                        else -> ColorStateList.valueOf(context.getColorResource(R.color.error_600))
                    }
                )
                setTextColor(
                    ColorStateList.valueOf(context.getColorResource(R.color.error_600))
                )

                addTextChangedListener(this@ShahryBasicInputField)
                setOnFocusChangeListener { view, hasFocus ->
                    mListener?.onDataEntered(editText, hasFocus)
                }
                setViewEventListener(textChangeListener, true)
            }

            tvErrorHelper.isVisible = message.isNotEmpty() == true
            tvErrorHelper.text = message
            tvErrorHelper.setTextColor(ColorStateList.valueOf(context.getColorResource(R.color.error_600)))
        }
    }


    fun clearFieldFocus() {
        binding.editText.apply {
            mListener?.onDataEntered(this, false)
        }
    }

    //this to be used to retrieve entered data from edit text
    fun setOnEditTextClickListener(onTextChanged: (value: String) -> Unit) {
        onTextChanged.invoke(binding.editText.text.toString())
    }

    private fun setViewEventListener(
        listener: TextChangeListener?,
        isError: Boolean,
    ) {
        mListener = listener
        isFieldError = isError
    }

    interface TextChangeListener {
        fun onTextChangeListener(
            charSequence: CharSequence?,
        )

        fun onDataEntered(view: View, hasFocus: Boolean)
    }

    override fun beforeTextChanged(text: CharSequence?, start: Int, end: Int, count: Int) {
    }

    override fun onTextChanged(text: CharSequence?, p1: Int, p2: Int, p3: Int) {

    }

    override fun afterTextChanged(text: Editable?) {
        mListener?.onTextChangeListener(text)
    }
}