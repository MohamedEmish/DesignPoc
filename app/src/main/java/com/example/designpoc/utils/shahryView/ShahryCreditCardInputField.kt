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
import com.example.designpoc.databinding.ShahryCreditCardInputBinding
import com.example.designpoc.utils.extensions.getColorResource
import com.example.designpoc.utils.extensions.getDrawableResource
import com.example.designpoc.utils.extensions.hideKeyboard
import com.example.designpoc.utils.extensions.showSoftKeyboard
import com.example.designpoc.utils.vibrate

class ShahryCreditCardInputField @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(
    context, attrs, defStyle
), TextWatcher {

    private val binding by lazy { ShahryCreditCardInputBinding.inflate(LayoutInflater.from(context), this, true) }

    private var textDisabledColor = context.getColorResource(R.color.platinum_400)
    private var iconDisabledColor = 0
    private var textColorRes = 0
    private var iconEndEnabled = false
    private var iconColor = 0
    private var helperTextEnabled = false
    private var hintTextColorRes = 0
    private var hintText = 0
    private var mListener: TextChangeListener? = null
    private var isFieldError: Boolean? = false
    private var helperText: String = ""

    private val textEntered = MutableLiveData("")

    private var lifecycleOwner: LifecycleOwner? = null

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
        binding.apply {
            tvErrorHelper.isVisible = helperText.isNotEmpty() == true
            tvErrorHelper.text = helperText
        }
    }

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ShahryCreditCardInputField)
        if (attrs != null) {
            textColorRes =
                attributes.getColor(
                    R.styleable.ShahryCreditCardInputField_creditCardTextColor,
                    context.getColorResource(R.color.black)
                )
            iconColor =
                attributes.getColor(
                    R.styleable.ShahryCreditCardInputField_creditCardIconColor,
                    context.getColorResource(R.color.black)
                )

            hintTextColorRes = attributes.getColor(
                R.styleable.ShahryCreditCardInputField_creditCardHintTextColor,
                context.getColorResource(R.color.platinum_600)
            )

            textDisabledColor = attributes.getColor(
                R.styleable.ShahryCreditCardInputField_creditCardDisabledTextColor,
                context.getColorResource(R.color.platinum_400)
            )
            iconDisabledColor = attributes.getColor(
                R.styleable.ShahryCreditCardInputField_creditCardIconDisabledColor,
                context.getColorResource(R.color.platinum_400)
            )
            hintText = attributes.getResourceId(R.styleable.ShahryCreditCardInputField_creditCardHint, 5)
            iconEndEnabled = attributes.getBoolean(R.styleable.ShahryCreditCardInputField_creditCardIconEnabled, false)

        }
        binding.container.background = context.getDrawableResource(R.drawable.ic_background_normal)
        renderInitial()
        attributes.recycle()
    }

    fun renderInitial(
        enabled: Boolean = true,
    ) {
        binding.apply {
            observeTextEntered()
            if (!enabled) {
                editText.clearFocus()
                editText.isEnabled = false
                container.isClickable = false
            } else {
                editText.isEnabled = true
                container.isClickable = true
            }
            editText.apply {
                filters = arrayOf(
                    LengthFilter(19),
                    CreditCardInputFilter()
                )
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

                addTextChangedListener(this@ShahryCreditCardInputField)
                setViewEventListener(textChangeListener, false)
                setOnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        container.background = context.getDrawableResource(
                            R.drawable.ic_background_normal_focused
                        )
                    } else {
                        container.background = context.getDrawableResource(
                            R.drawable.ic_background_normal
                        )
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
            btnEndIcon.isVisible = iconEndEnabled && !isPhoneNumberMatching(editText.text.toString())
            btnEndIcon.background = context.getDrawableResource(R.drawable.ic_info)
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

                addTextChangedListener(this@ShahryCreditCardInputField)
                setOnFocusChangeListener { view, hasFocus ->
                    if (hasFocus) {
                        container.background = context.getDrawableResource(
                            R.drawable.ic_background_error_focused
                        )
                    } else {
                        container.background = context.getDrawableResource(
                            R.drawable.ic_background_error_normal
                        )
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

            btnEndIcon.isVisible = iconEndEnabled
            btnEndIcon.background = context.getDrawableResource(R.drawable.ic_warning)
            btnEndIcon.backgroundTintList = ColorStateList.valueOf(context.getColorResource(R.color.error_600))
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
                .isNotBlank() && isFieldError == false
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
                    if (text.isNotEmpty() && text.toString().startsWith("4")) {
                        binding.ivCardImage.background = context.getDrawableResource(R.drawable.ic_visa_card)
                    } else if (text.length >= 2 && text.toString().startsWith("51")
                               || text.toString().startsWith("52")
                               || text.toString().startsWith("53")
                               || text.toString().startsWith("54")
                               || text.toString().startsWith("55")
                    ) {
                        binding.ivCardImage.background = context.getDrawableResource(R.drawable.ic_master_card)
                    } else if (text.length >= 2 && text.toString().startsWith("50")) {
                        binding.ivCardImage.background = context.getDrawableResource(R.drawable.ic_meeza)
                    } else {
                        binding.ivCardImage.background = context.getDrawableResource(R.drawable.ic_card_placeholder)
                    }
                }
            }
        }
    }

    class CreditCardInputFilter : InputFilter {
        override fun filter(
            source: CharSequence,
            start: Int,
            end: Int,
            dest: Spanned,
            dstart: Int,
            dend: Int,
        ): CharSequence? {
            if ((dest != null) and (dest.toString().trim().length > 19)) {
                return null
            }
            return if (source.length == 1 && (dstart == 4 || dstart == 9 || dstart == 14)) {
                " ".plus(source.toString())
            } else {
                null
            }
        }
    }
}