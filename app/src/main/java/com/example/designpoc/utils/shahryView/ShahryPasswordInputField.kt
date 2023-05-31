package com.example.designpoc.utils.shahryView

import android.annotation.SuppressLint
import android.content.Context
import android.content.res.ColorStateList
import android.os.Build
import android.os.Build.VERSION_CODES
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import com.example.designpoc.R
import com.example.designpoc.databinding.ShahryPasswordInputBinding
import com.example.designpoc.utils.extensions.getColorResource
import com.example.designpoc.utils.extensions.getDrawableResource
import com.example.designpoc.utils.extensions.hideKeyboard
import com.example.designpoc.utils.extensions.showSoftKeyboard
import com.example.designpoc.utils.shahryView.ShahryPasswordInputField.State
import com.example.designpoc.utils.shahryView.ShahryPasswordInputField.State.ErrorState
import com.example.designpoc.utils.shahryView.ShahryPasswordInputField.State.Initial
import com.example.designpoc.utils.widget.Widget

class ShahryPasswordInputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(
    context, attrs, defStyle
), Widget<State>, TextWatcher {

    private val binding by lazy { ShahryPasswordInputBinding.inflate(LayoutInflater.from(context), this, true) }

    private var textDisabledColor = context.getColorResource(R.color.platinum_400)
    private var iconDisabledColor = 0
    private var textColorRes = 0
    private var iconEndResource = false
    private var iconColor = 0
    private var helperTextEnabled = false
    private var hintTextColorRes = 0
    private var hintText = 0
    private var mListener: TextChangeListener? = null
    private var isFieldError: Boolean? = false
    private var backgroundRes = 0
    private var helperText: String = ""

    private var onTextChanged: (value: String) -> Unit = {}

    private var isTransformationChanged: Boolean = false

    private var textChangeListener = object :
        TextChangeListener {
        override fun onTextChangeListener(
            charSequence: CharSequence?,
        ) {
            if (charSequence.toString().isNotEmpty() && isFieldError == true) {
                binding.renderInitial()
                setHelperText(helperText)
            }
            onTextChanged.invoke(charSequence.toString())
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
        binding.apply {
            tvErrorHelper.isVisible = helperText.isNotEmpty() == true
            tvErrorHelper.text = helperText
        }
    }

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ShahryPasswordInputField)
        if (attrs != null) {
            textColorRes =
                attributes.getColor(
                    R.styleable.ShahryPasswordInputField_passwordTextColor,
                    context.getColorResource(R.color.black)
                )
            iconColor =
                attributes.getColor(R.styleable.ShahryPasswordInputField_passwordIconColor, 0)
            helperTextEnabled = attributes.getBoolean(R.styleable.ShahryInputField_helperTextEnabled, false)

            hintTextColorRes = attributes.getColor(
                R.styleable.ShahryPasswordInputField_passwordHintTextColor,
                context.getColorResource(R.color.platinum_600)
            )

            textDisabledColor = attributes.getColor(
                R.styleable.ShahryPasswordInputField_passwordDisabledTextColor,
                context.getColorResource(R.color.platinum_400)
            )
            iconDisabledColor = attributes.getColor(
                R.styleable.ShahryPasswordInputField_passwordIconDisabledColor,
                context.getColorResource(R.color.platinum_400)
            )
            hintText = attributes.getResourceId(R.styleable.ShahryPasswordInputField_passwordHint, 5)
            iconEndResource = attributes.getBoolean(R.styleable.ShahryPasswordInputField_passwordIconEnabled, false)

            backgroundRes = attributes.getResourceId(
                R.styleable.ShahryPasswordInputField_passwordBackground, R.drawable.ic_background_edit_text
            )
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

    private fun ShahryPasswordInputBinding.renderError(message: String = "") {
        editText.apply {
            background = context.getDrawableResource(
                R.drawable.ic_background_edit_text_error
            )
            setHintTextColor(
                when (hintTextColorRes) {
                    0 -> null
                    else -> ColorStateList.valueOf(context.getColorResource(R.color.error_600))
                }
            )
            setTextColor(
                ColorStateList.valueOf(context.getColorResource(R.color.error_600))
            )
            setCompoundDrawablesWithIntrinsicBounds(
                null, null, if (iconEndResource) {
                    context.getDrawableResource(R.drawable.ic_warning)
                } else {
                    null
                }, null
            )
            if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
                compoundDrawableTintList = ColorStateList.valueOf(context.getColorResource(R.color.error_600))
            }
            addTextChangedListener(this@ShahryPasswordInputField)
            setOnFocusChangeListener { view, hasFocus -> mListener?.onDataEntered(editText, hasFocus) }
            setViewEventListener(textChangeListener, true)
        }

        tvErrorHelper.isVisible = message.isNotEmpty() == true
        tvErrorHelper.text = message
        tvErrorHelper.setTextColor(ColorStateList.valueOf(context.getColorResource(R.color.error_600)))
    }

    @SuppressLint("UseCompatTextViewDrawableApis")
    private fun ShahryPasswordInputBinding.renderInitial(enabled: Boolean = true) {
        editText.apply {
            if (!enabled) clearFocus()
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
            background = context.getDrawableResource(
                R.drawable.ic_background_edit_text
            )
            setTextColor(
                if (enabled) {
                    textColorRes
                } else {
                    textDisabledColor
                }
            )
            setCompoundDrawablesWithIntrinsicBounds(
                null, null,
                when (iconEndResource) {
                    false -> null
                    else -> context.getDrawableResource(R.drawable.eye_close_line)
                }, null
            )
            if (Build.VERSION.SDK_INT >= VERSION_CODES.M) {
                compoundDrawableTintList = ColorStateList.valueOf(
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
            addTextChangedListener(this@ShahryPasswordInputField)
            setViewEventListener(textChangeListener, false)
            setOnFocusChangeListener { view, hasFocus -> mListener?.onDataEntered(editText, hasFocus) }
            transformationMethod = DotsPasswordTransformationMethod()
            text?.length?.let { setSelection(it) }
        }

        if (iconEndResource) {
            setOnEndIconClickListener()
        }
        tvErrorHelper.setTextColor(ColorStateList.valueOf(context.getColorResource(R.color.platinum_600)))
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setOnEndIconClickListener() {
        binding.apply {
            editText.setOnTouchListener { view, event ->
                val DRAWABLE_RIGHT = 2
                if (event.action == MotionEvent.ACTION_UP) {
                    if (event.rawX >= (editText.right - editText.compoundDrawables[DRAWABLE_RIGHT].bounds.width())) {
                        if (isTransformationChanged) {
                            editText.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                context.getDrawableResource(R.drawable.eye_close_line),
                                null
                            )
                            editText.transformationMethod = DotsPasswordTransformationMethod()
                            isTransformationChanged = false
                            if (editText.hasFocus()) {
                                editText.text?.length?.let { editText.setSelection(it) }
                            }
                        } else {
                            editText.setCompoundDrawablesWithIntrinsicBounds(
                                null,
                                null,
                                context.getDrawableResource(R.drawable.eye_open_line),
                                null
                            )
                            editText.transformationMethod = HideReturnsTransformationMethod.getInstance()
                            isTransformationChanged = true
                            if (editText.hasFocus()) {
                                editText.text?.length?.let { editText.setSelection(it) }
                            }
                        }
                        return@setOnTouchListener true
                    }
                }
                return@setOnTouchListener false
            }
        }
    }

    sealed class State : Widget.State {
        data class Initial(val enabled: Boolean = true, val helperText: String = "") : State()
        data class ErrorState(val message: String) : State()
    }

    fun clearFieldFocus() {
        binding.editText.apply {
            mListener?.onDataEntered(this, false)
        }
    }

    //this to be used to retrieve entered data from edit text
    fun setOnEditTextClickListener(onTextChanged: (value: String) -> Unit) {
        this.onTextChanged = onTextChanged
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

    override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

    override fun afterTextChanged(text: Editable?) {
        mListener?.onTextChangeListener(
            text
        )
    }

    class DotsPasswordTransformationMethod : PasswordTransformationMethod() {

        override fun getTransformation(source: CharSequence, view: View): CharSequence {
            return PasswordCharSequence(source)
        }

        inner class PasswordCharSequence(private val source: CharSequence) : CharSequence {

            override val length: Int
                get() = source.length

            override fun get(index: Int): Char = '•'

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                return source.subSequence(startIndex, endIndex)
            }
        }
    }
}