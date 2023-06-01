package com.example.designpoc.utils.shahryView

import android.content.Context
import android.content.res.ColorStateList
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.Spanned
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.MutableLiveData
import com.example.designpoc.R
import com.example.designpoc.databinding.ShahryPhoneNumberInputBinding
import com.example.designpoc.utils.extensions.getColorResource
import com.example.designpoc.utils.extensions.getDrawableResource
import com.example.designpoc.utils.extensions.hideKeyboard
import com.example.designpoc.utils.extensions.showSoftKeyboard
import com.example.designpoc.utils.vibrate

class ShahryPhoneNumberInputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(
    context, attrs, defStyle
), TextWatcher {

    private val binding by lazy { ShahryPhoneNumberInputBinding.inflate(LayoutInflater.from(context), this, true) }

    private var textDisabledColor = context.getColorResource(R.color.platinum_400)
    private var iconDisabledColor = 0
    private var textColorRes = 0
    private var iconEndResource = false
    private var iconColor = 0
    private var helperTextEnabled = false
    private var hintTextColorRes = 0
    private var hintText = 0
    private var focusedHintText = 0
    private var mListener: TextChangeListener? = null
    private var isFieldError: Boolean? = false
    private var helperText: String = ""
    private var isEnteredPhoneEmpty: Boolean = false
    private var isFieldEnabled: Boolean = false

    private val textEntered = MutableLiveData("")

    private var lifecycleOwner: LifecycleOwner? = null

    private var textChangeListener = object :
        TextChangeListener {
        override fun onTextChangeListener(
            charSequence: CharSequence?,
        ) {
            if (charSequence.toString().isEmpty() && isFieldError == true) {
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
        binding.apply {
            tvErrorHelper.isVisible = helperText.isNotEmpty() == true
            tvErrorHelper.text = helperText
        }
    }

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ShahryPhoneNumberInputField)
        if (attrs != null) {
            textColorRes =
                attributes.getColor(
                    R.styleable.ShahryPhoneNumberInputField_phoneTextColor,
                    context.getColorResource(R.color.black)
                )
            iconColor =
                attributes.getColor(
                    R.styleable.ShahryPhoneNumberInputField_phoneIconColor,
                    context.getColorResource(R.color.black)
                )

            hintTextColorRes = attributes.getColor(
                R.styleable.ShahryPhoneNumberInputField_phoneHintTextColor,
                context.getColorResource(R.color.platinum_600)
            )

            textDisabledColor = attributes.getColor(
                R.styleable.ShahryPhoneNumberInputField_phoneDisabledTextColor,
                context.getColorResource(R.color.platinum_400)
            )
            iconDisabledColor = attributes.getColor(
                R.styleable.ShahryPhoneNumberInputField_phoneIconDisabledColor,
                context.getColorResource(R.color.platinum_400)
            )
            hintText = attributes.getResourceId(R.styleable.ShahryPhoneNumberInputField_phoneHint, 5)
            focusedHintText = attributes.getResourceId(
                R.styleable.ShahryPhoneNumberInputField_phoneFocusedHint,
                R.string.phone_number_hint
            )
            iconEndResource = attributes.getBoolean(R.styleable.ShahryPhoneNumberInputField_phoneIconEnabled, false)

        }
        binding.container.background = context.getDrawableResource(R.drawable.ic_background_normal)
        renderInitial()
        attributes.recycle()
    }

    fun renderInitial(
        enabled: Boolean = true,
    ) {
        this.isFieldEnabled= enabled
        binding.apply {
            observeTextEntered()
            if (!enabled) {
                editText.clearFocus()
                editText.isEnabled = false
                container.isClickable = false
                tvCountryCode.setTextColor(textDisabledColor)
            } else {
                editText.isEnabled = true
                container.isClickable = true
                tvCountryCode.setTextColor(textColorRes)
            }
            editText.apply {
                hint = context.getString(hintText)
                filters = arrayOf(
                    LengthFilter(12),
                    PhoneNumberInputFilter()
                )
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

                addTextChangedListener(this@ShahryPhoneNumberInputField)
                setViewEventListener(textChangeListener, false)
                setOnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        container.background = context.getDrawableResource(
                            R.drawable.ic_background_normal_focused
                        )
                        if (text?.isEmpty() == true) {
                            hint = context.getString(focusedHintText)
                        }
                    } else {
                        container.background = context.getDrawableResource(
                            R.drawable.ic_background_normal
                        )
                        if (text?.isEmpty() == true) {
                            hint = context.getString(hintText)
                        }
                    }
                    mListener?.onDataEntered(editText, hasFocus)
                }
            }

            if (editText.hasFocus()) {
                container.background = context.getDrawableResource(
                    R.drawable.ic_background_normal_focused
                )
            } else {
                container.background = context.getDrawableResource(
                    R.drawable.ic_background_normal
                )
            }
            btnEndIcon.isVisible = iconEndResource && !isPhoneNumberMatching(editText.text.toString())
            btnEndIcon.background = context.getDrawableResource(R.drawable.ic_info)
            btnSuccessIcon.isVisible = iconEndResource && isPhoneNumberMatching(editText.text.toString())
            btnEndIcon.backgroundTintList = ColorStateList.valueOf(
                if (!enabled) {
                    iconDisabledColor
                } else {
                    iconColor
                }
            )
            binding.btnClearIcon.setOnClickListener {
                binding.editText.setText("")
            }
            tvErrorHelper.setTextColor(ColorStateList.valueOf(context.getColorResource(R.color.platinum_600)))
        }
    }

    fun renderError(message: String = "") {
        binding.apply {
            observeTextEntered()
            root.vibrate()
            editText.apply {
                setHintTextColor(
                    when (hintTextColorRes) {
                        0 -> null
                        else -> ColorStateList.valueOf(context.getColorResource(R.color.error_600))
                    }
                )
                setTextColor(
                    ColorStateList.valueOf(context.getColorResource(R.color.error_600))
                )

                addTextChangedListener(this@ShahryPhoneNumberInputField)
                setOnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        container.background = context.getDrawableResource(
                            R.drawable.ic_background_error_focused
                        )
                        if (text?.isEmpty() == true) {
                            hint = context.getString(focusedHintText)
                        }
                    } else {
                        container.background = context.getDrawableResource(
                            R.drawable.ic_background_error_normal
                        )
                        if (text?.isEmpty() == true) {
                            hint = context.getString(hintText)
                        }
                    }
                    mListener?.onDataEntered(editText, hasFocus)
                }

                if (message.isNotEmpty() && editText.hasFocus()) {
                    container.background = context.getDrawableResource(
                        R.drawable.ic_background_error_focused
                    )
                } else {
                    container.background = context.getDrawableResource(
                        R.drawable.ic_background_error_normal
                    )
                }
                setViewEventListener(textChangeListener, true)
            }

            btnEndIcon.isVisible = iconEndResource
            btnEndIcon.background = context.getDrawableResource(R.drawable.ic_warning)
            btnEndIcon.backgroundTintList = ColorStateList.valueOf(context.getColorResource(R.color.error_600))
            btnSuccessIcon.isVisible = false
            btnClearIcon.isVisible = false

            tvErrorHelper.isVisible = message.isNotEmpty() == true
            tvErrorHelper.text = message
            tvErrorHelper.setTextColor(ColorStateList.valueOf(context.getColorResource(R.color.error_600)))
        }
    }

    private fun isPhoneNumberMatching(phoneNumber: String): Boolean {
        return phoneNumber.isNotEmpty() && phoneNumber.isNotBlank() && phoneNumber.length == 10
    }

    fun clearFieldFocus() {
        binding.editText.apply {
            mListener?.onDataEntered(this, false)
        }
    }

    //this to be used to retrieve entered data from edit text
    fun setOnEditTextClickListener(onTextChanged: (value: String) -> Unit) {
        onTextChanged.invoke(binding.editText.text.toString().filter{!it.isWhitespace()})
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
        textEntered.postValue(text.toString())
        if (binding.editText.text.toString().isNotEmpty() && binding.editText.text
                .toString()
                .isNotBlank() && isFieldError == false && isFieldEnabled
        ) {
            binding.btnClearIcon.isVisible = true
            binding.btnEndIcon.isVisible = false
        } else {
            binding.btnClearIcon.isVisible = false
            binding.btnEndIcon.isVisible = true
        }
        mListener?.onTextChangeListener(text)
    }

    fun observeTextEntered(lifecycle: LifecycleOwner? = null) {
        lifecycleOwner = lifecycle
        if (lifecycle != null) {
            textEntered.observe(lifecycle) { text ->
                if (text != null) {
                    if (text.toString().startsWith("0") && !isEnteredPhoneEmpty) {
                        binding.editText.apply {
                            setText(text.toString().replace("0", ""))
                        }
                        isEnteredPhoneEmpty = true
                    } else if (text.toString().startsWith("2")
                               || text.toString().startsWith("3")
                               || text.toString().startsWith("4")
                               || text.toString().startsWith("5")
                               || text.toString().startsWith("6")
                               || text.toString().startsWith("7")
                               || text.toString().startsWith("8")
                               || text.toString().startsWith("9")
                    ) {
                        if (binding.editText.text.toString().length > 1) {
                            binding.editText.apply {
                                setText(
                                    text
                                        .toString()
                                        .replace(text.toString()[1].toString(), "")
                                )
                                this.text?.length?.let { setSelection(it) }
                            }
                        }
                        renderError("Invalid phone number")
                        isEnteredPhoneEmpty = false
                    } else {
                        isEnteredPhoneEmpty = false
                    }
                }
            }
        }
    }

    class PhoneNumberInputFilter : InputFilter {
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int,
        ): CharSequence? {
            if ((dest != null) and (dest.toString().trim().length > 12)) {
                return null
            }
            return if (source.length == 1 && (dstart == 3 || dstart == 7)) {
                " ".plus(source.toString())
            } else {
                null
            }
        }
    }

}