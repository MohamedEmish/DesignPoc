package com.example.designpoc.utils.shahryView

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.designpoc.R
import com.example.designpoc.databinding.ShahryTextInputBinding
import com.example.designpoc.utils.extensions.getColorResource
import com.example.designpoc.utils.extensions.getDrawableResource
import com.example.designpoc.utils.extensions.hideKeyboard
import com.example.designpoc.utils.extensions.showSoftKeyboard
import com.example.designpoc.utils.shahryView.ShahryInputField.State
import com.example.designpoc.utils.shahryView.ShahryInputField.State.ErrorState
import com.example.designpoc.utils.shahryView.ShahryInputField.State.Initial
import com.example.designpoc.utils.vibrate
import com.example.designpoc.utils.widget.Widget

class ShahryInputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(
    context, attrs, defStyle
), Widget<State>, TextWatcher {

    private val binding by lazy { ShahryTextInputBinding.inflate(LayoutInflater.from(context), this, true) }

    private var textDisabledColor = context.getColorResource(R.color.platinum_400)
    private var iconDisabledColor = context.getColorResource(R.color.platinum_400)
    private var textColorRes = context.getColorResource(R.color.black)
    private var iconEndResource = 0
    private var iconColor = 0
    private var helperTextEnabled = false
    private var hintTextColorRes = 0
    private var hintText = 0
    private var mListener: TextChangeListener? = null
    private var isFieldError: Boolean? = false
    private var backgroundRes = R.drawable.ic_background_edit_text
    private var inputType = InputType.TEXT
    private var helperText: String = ""

    private var textChangeListener = object : TextChangeListener {
        override fun onTextChangeListener(
            charSequence: CharSequence?,
        ) {
            if (charSequence.toString().isNotEmpty() && isFieldError == true) {
                binding.renderInitial()
            }
        }

        override fun onDataEntered(view: View, hasFocus: Boolean) {
            setEditTextPadding(hasFocus)
            if (hasFocus) {
                view.showSoftKeyboard()
            } else {
                view.clearFocus()
                context.hideKeyboard(view)
            }
        }
    }

    fun setHelperText(helperText: String) {
        this.helperText = helperText
        binding.apply {
            tvErrorHelper.isVisible = helperText.isNotEmpty() == true
            tvErrorHelper.text = helperText
        }
    }

    private fun setEditTextPadding(hasFocus: Boolean = false) {
        if (hasFocus || (binding.editText.text?.isNotBlank() == true && binding.editText.text?.isNotEmpty() == true)) {
            binding.inputLayout.setPaddingRelative(
                0,
                context.resources.getDimensionPixelOffset(R.dimen.spacing_special_10), // 11
                0,
                0
            )
            binding.editText.setPaddingRelative(
                context.resources.getDimensionPixelOffset(R.dimen.spacing_normal_16),
                context.resources.getDimensionPixelOffset(R.dimen.spacing_special_minus_24),
                context.resources.getDimensionPixelOffset(R.dimen.spacing_special_48),
                context.resources.getDimensionPixelOffset(R.dimen.spacing_special_minus_15) //14
            )
        } else {
            binding.inputLayout.setPaddingRelative(
                0,
                0,
                0,
                context.resources.getDimensionPixelOffset(R.dimen.spacing_x_small_4)
            )
            binding.editText.setPaddingRelative(
                context.resources.getDimensionPixelOffset(R.dimen.spacing_normal_16),
                0,
                context.resources.getDimensionPixelOffset(R.dimen.spacing_special_42),
                context.resources.getDimensionPixelOffset(R.dimen.spacing_x_small_4),
            )
        }
    }

    enum class InputType {
        TEXT,
        EMAIL,
        NUMBER,
        NUMBER_DECIMAL,
        PHONE,
        DATE_TIME,
        DATE,
        TIME
    }

    private val fieldInputType: Int
        get() {
            return when (inputType) {
                InputType.NUMBER -> android.text.InputType.TYPE_CLASS_NUMBER
                InputType.EMAIL -> android.text.InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS
                InputType.NUMBER_DECIMAL -> android.text.InputType.TYPE_NUMBER_FLAG_DECIMAL
                InputType.PHONE -> android.text.InputType.TYPE_CLASS_PHONE
                InputType.DATE_TIME -> android.text.InputType.TYPE_CLASS_DATETIME
                InputType.DATE -> android.text.InputType.TYPE_DATETIME_VARIATION_DATE
                InputType.TIME -> android.text.InputType.TYPE_DATETIME_VARIATION_TIME
                InputType.TEXT -> android.text.InputType.TYPE_CLASS_TEXT
            }
        }

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ShahryInputField)
        if (attrs != null) {
            textColorRes =
                attributes.getColor(
                    R.styleable.ShahryInputField_enteredTextColor,
                    context.getColorResource(R.color.black)
                )
            iconColor =
                attributes.getColor(R.styleable.ShahryInputField_iconColor, 0)
            helperTextEnabled = attributes.getBoolean(R.styleable.ShahryInputField_helperTextEnabled, false)

            hintTextColorRes = attributes.getColor(
                R.styleable.ShahryInputField_hintTextColor, context.getColorResource(R.color.platinum_600)
            )

            textDisabledColor = attributes.getColor(
                R.styleable.ShahryInputField_disabledTextColor, context.getColorResource(R.color.platinum_400)
            )
            iconDisabledColor = attributes.getColor(
                R.styleable.ShahryInputField_iconDisabledColor, context.getColorResource(R.color.platinum_400)
            )
            hintText = attributes.getResourceId(R.styleable.ShahryInputField_editTextHint, 5)
            iconEndResource = attributes.getResourceId(R.styleable.ShahryInputField_iconEndResource, 0)

            backgroundRes = attributes.getResourceId(
                R.styleable.ShahryInputField_fieldBackground, R.drawable.ic_background_edit_text
            )

            inputType = InputType.values()[attributes.getInt(R.styleable.ShahryInputField_editTextInputType, 0)]
        }
        binding.renderInitial()
        attributes.recycle()
    }

    override fun render(state: State) {
        when (state) {
            is Initial -> {
                binding.renderInitial(state.enabled)
            }
            is ErrorState -> {
                binding.renderError(state.message)
            }
        }
    }

    private fun ShahryTextInputBinding.renderInitial(enabled: Boolean = true) {
        setEditTextPadding()
        inputLayout.apply {
            background = context.getDrawableResource(backgroundRes)

            isEnabled = enabled
            isClickable = enabled
            isFocusable = enabled

            hint = when (hintText) {
                0 -> ""
                else -> context.getString(hintText)
            }
            hintTextColor = ColorStateList.valueOf(
                if (enabled) {
                    context.getColorResource(R.color.platinum_600)
                } else {
                    textDisabledColor
                }
            )
        }

        btnIcon.apply {
            isVisible = iconEndResource != 0
            background = if (iconEndResource != 0) {
                context.getDrawableResource(iconEndResource)
            } else {
                null
            }

            backgroundTintList =
                ColorStateList.valueOf(
                    if (enabled) {
                        when (iconColor) {
                            0 -> context.getColorResource(R.color.black)
                            else -> iconColor
                        }
                    } else {
                        iconDisabledColor
                    }
                )
        }

        editText.apply {
            isEnabled = enabled
            isClickable = enabled
            isFocusable = enabled
            setTextColor(
                if (enabled) {
                    textColorRes
                } else {
                    textDisabledColor
                }
            )
            inputType = fieldInputType
            addTextChangedListener(this@ShahryInputField)
            setViewEventListener(textChangeListener, false)
            setOnFocusChangeListener { view, hasFocus -> mListener?.onDataEntered(view, hasFocus) }
        }

        tvErrorHelper.setTextColor(ColorStateList.valueOf(context.getColorResource(R.color.platinum_600)))
    }

    private fun ShahryTextInputBinding.renderError(message: String = "") {
        inputLayout.apply {
            root.vibrate()
            background = context.getDrawableResource(
                R.drawable.ic_background_edit_text_error
            )
            hintTextColor = when (hintTextColorRes) {
                0 -> null
                else -> ColorStateList.valueOf(context.getColorResource(R.color.error_600))
            }
        }

        btnIcon.apply {
            isVisible = iconEndResource != 0
            isClickable = false

            background = if (iconEndResource != 0) {
                context.getDrawableResource(R.drawable.ic_warning)
            } else {
                null
            }

            backgroundTintList = null
        }

        editText.apply {
            setTextColor(textColorRes)
            inputType = fieldInputType
            addTextChangedListener(this@ShahryInputField)
            setOnFocusChangeListener { view, hasFocus -> mListener?.onDataEntered(view, hasFocus) }
            setViewEventListener(textChangeListener, true)
        }

        tvErrorHelper.isVisible = message.isNotEmpty() == true
        tvErrorHelper.text = message
        tvErrorHelper.setTextColor(ColorStateList.valueOf(context.getColorResource(R.color.error_600)))
    }

    fun clearFieldFocus() {
        binding.editText.apply {
            mListener?.onDataEntered(this, false)
        }
    }

    sealed class State : Widget.State {
        data class Initial(val enabled: Boolean = true, val helperText: String = "") : State()
        data class ErrorState(val message: String) : State()
    }

    private fun setViewEventListener(listener: TextChangeListener?, isError: Boolean) {
        mListener = listener
        isFieldError = isError
    }

    //this to be used to retrieve entered data from edit text
    fun setOnEditTextClickListener(onTextChanged: (value: String) -> Unit) {
        onTextChanged.invoke(binding.editText.text.toString())
    }

    interface TextChangeListener {
        fun onTextChangeListener(
            charSequence: CharSequence?,
        )

        fun onDataEntered(view: View, hasFocus: Boolean)
    }

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(text: Editable?) {
        mListener?.onTextChangeListener(
            text
        )
    }
}