package com.example.designpoc.utils.shahryView

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import androidx.constraintlayout.widget.ConstraintLayout
import com.example.designpoc.R
import com.example.designpoc.databinding.ShahryButtonWidgetBinding
import com.example.designpoc.databinding.ShahryTextInputBinding
import com.example.designpoc.utils.extensions.getColorResource

class ShahryInputField(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0,
) : ConstraintLayout(
    context, attrs, defStyle
) {

    private val binding by lazy { ShahryTextInputBinding.inflate(LayoutInflater.from(context), this, true) }

    private var textDisabledColor = context.getColorResource(R.color.platinum_400)
    private var iconDisabledColor = context.getColorResource(R.color.platinum_400)
    private var textColor = context.getColorResource(R.color.black)
    private var iconEndResource = 0
    private var iconColor = context.getColorResource(R.color.black)
    private var isFieldEnabled = true
    private var errorEnabled = false
    private var helperTextEnabled = false
    private var hintTextColor = context.getColorResource(R.color.platinum_600)
    private var hint = ""
    private var passwordToggleEnabled = false
    private var passwordToggleDrawable = 0
    private var background = 0
    private var inputType = 0x00000001

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ShahryInputField)
        isFieldEnabled = attributes.getBoolean(R.styleable.ShahryInputField_isFieldEnabled, false)
        textColor = attributes.getColor(R.styleable.ShahryInputField_textColor, context.getColorResource(R.color.black))
        iconColor = attributes.getColor(R.styleable.ShahryInputField_iconColor, context.getColorResource(R.color.black))
        errorEnabled = attributes.getBoolean(R.styleable.ShahryInputField_errorEnabled, false)
        helperTextEnabled = attributes.getBoolean(R.styleable.ShahryInputField_helperTextEnabled, false)

        hintTextColor = attributes.getColor(
            R.styleable.ShahryInputField_hintTextColor, when {
                isFieldEnabled -> context.getColorResource(R.color.platinum_400)
                errorEnabled -> context.getColorResource(R.color.error_600)
                else -> context.getColorResource(R.color.platinum_600)
            }
        )

        textDisabledColor = attributes.getColor(
            R.styleable.ShahryInputField_textDisabledColor, when (isFieldEnabled) {
                true -> context.getColorResource(R.color.platinum_400)
                else -> textColor
            }
        )
        iconDisabledColor = attributes.getColor(
            R.styleable.ShahryInputField_iconDisabledColor, when (isFieldEnabled) {
                true -> context.getColorResource(R.color.platinum_400)
                else -> iconColor
            }
        )
        hint = attributes.getString(R.styleable.ShahryInputField_hint) ?: ""
        passwordToggleEnabled = attributes.getBoolean(R.styleable.ShahryInputField_passwordToggleEnabled, false)
        passwordToggleDrawable = attributes.getResourceId(R.styleable.ShahryInputField_passwordToggleDrawable,0)
        iconEndResource = attributes.getResourceId(R.styleable.ShahryInputField_iconEndResource, 0)

        attributes.recycle()
    }

}