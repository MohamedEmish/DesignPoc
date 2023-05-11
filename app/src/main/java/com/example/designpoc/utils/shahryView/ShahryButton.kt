package com.example.designpoc.utils.shahryView

import android.content.Context
import android.content.res.ColorStateList
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.util.AttributeSet
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.designpoc.R
import com.example.designpoc.databinding.ShahryButtonWidgetBinding
import com.example.designpoc.utils.extensions.dpToPx
import com.example.designpoc.utils.extensions.getColorResource
import com.example.designpoc.utils.extensions.getDrawableResource
import com.example.designpoc.utils.extensions.setSafeOnClickListener
import com.example.designpoc.utils.shahryView.ShahryButton.ButtonType.PRIMARY_LARGE
import com.example.designpoc.utils.shahryView.ShahryButton.ButtonType.PRIMARY_X_SMALL
import com.example.designpoc.utils.shahryView.ShahryButton.State
import com.example.designpoc.utils.shahryView.ShahryButton.State.Initial
import com.example.designpoc.utils.shahryView.ShahryButton.State.Loading
import com.example.designpoc.utils.widget.Widget
import com.google.android.material.button.MaterialButton
import com.google.android.material.progressindicator.CircularProgressIndicatorSpec
import com.google.android.material.progressindicator.IndeterminateDrawable

class ShahryButton(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(
    context,
    attrs
), Widget<State> {

    private val binding by lazy { ShahryButtonWidgetBinding.inflate(LayoutInflater.from(context), this, true) }

    private var actionCallbacks: OnButtonCallbacks? = null

    private var callbacks: OnButtonCallbacks = object : OnButtonCallbacks {
        override fun onClicked() {
            actionCallbacks?.onClicked()
        }
    }

    private var buttonType = PRIMARY_LARGE
    private var buttonColor = context.getColorResource(R.color.black)
    private var buttonDisabledColor = context.getColorResource(R.color.platinum_400)

    private var buttonStrokeColor = context.getColorResource(R.color.black)
    private var buttonStrokeDisabledColor = context.getColorResource(R.color.platinum_400)

    private var buttonText = ""
    private var buttonTextColor = context.getColorResource(R.color.white)
    private var buttonTextDisabledColor = context.getColorResource(R.color.platinum_400)

    private var progressColor = context.getColorResource(R.color.white)
    private var buttonRippleColor = context.getColorResource(R.color.platinum_400)

    private var iconResource = 0

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ShahryButton)

        if (attrs != null) {
            // Button attributes
            val buttonTypes = ButtonType.values()
            buttonType = buttonTypes[attributes.getInt(R.styleable.ShahryButton_buttonType, 0)]
            buttonColor = attributes.getColor(
                R.styleable.ShahryButton_buttonColor,
                when {
                    buttonType.isPrimary -> context.getColorResource(R.color.black)
                    else -> context.getColorResource(R.color.white)
                }
            )
            buttonDisabledColor = attributes.getColor(
                R.styleable.ShahryButton_buttonDisabledColor,
                when {
                    buttonType.isPrimary -> context.getColorResource(R.color.platinum_400)
                    else -> context.getColorResource(R.color.white)
                }
            )
            buttonStrokeColor = attributes.getColor(
                R.styleable.ShahryButton_buttonStrokeColor,
                when {
                    buttonType.isSecondary -> context.getColorResource(R.color.black)
                    else -> context.getColorResource(R.color.white)
                }
            )
            buttonStrokeDisabledColor = attributes.getColor(
                R.styleable.ShahryButton_buttonStrokeDisabledColor,
                when {
                    buttonType.isSecondary -> context.getColorResource(R.color.platinum_400)
                    else -> context.getColorResource(R.color.white)
                }
            )

            buttonText = attributes.getString(R.styleable.ShahryButton_text) ?: ""
            buttonTextColor = attributes.getColor(
                R.styleable.ShahryButton_textColor,
                when {
                    buttonType.isPrimary -> context.getColorResource(R.color.white)
                    else -> context.getColorResource(R.color.black)
                }
            )
            buttonTextDisabledColor = attributes.getColor(
                R.styleable.ShahryButton_textDisabledColor,
                when {
                    buttonType.isPrimary -> context.getColorResource(R.color.white)
                    else -> context.getColorResource(R.color.platinum_400)
                }
            )

            // Progress indicator attributes
            progressColor = attributes.getColor(
                R.styleable.ShahryButton_progressColor,
                when {
                    buttonType.isPrimary -> context.getColorResource(R.color.white)
                    else -> context.getColorResource(R.color.black)
                }
            )

            buttonRippleColor = attributes.getColor(
                R.styleable.ShahryButton_rippleColor,
                context.getColorResource(R.color.platinum_400)
            )

            // Icon attributes
            iconResource = attributes.getResourceId(
                R.styleable.ShahryButton_iconResource, 0
            )
        }

        binding.renderInitial()
        attributes.recycle()
    }

    override fun render(state: State) {
        when (state) {
            is Initial -> binding.renderInitial(state.enabled)
            is Loading -> binding.renderLoading(state.isLoading)
        }
    }

    private fun ShahryButtonWidgetBinding.renderInitial(enabled: Boolean = true) {
        materialButton.apply {
            /** Actions **/
            setSafeOnClickListener {
                callbacks.onClicked()
            }
            isEnabled = enabled

            /** Text style **/
            text = buttonText
            when {
                VERSION.SDK_INT >= VERSION_CODES.M -> setTextAppearance(buttonType.textStyle)
                else -> setTextAppearance(context, buttonType.textStyle)
            }
            setTextColor(
                when {
                    buttonType.isPrimary -> buttonTextColor
                    enabled -> buttonTextColor
                    else -> buttonTextDisabledColor
                }
            )

            /** Background style **/
            backgroundTintList = ColorStateList.valueOf(
                when {
                    buttonType.isPrimary -> if (enabled) buttonColor else buttonDisabledColor
                    buttonType.isSecondary -> buttonColor
                    else -> context.getColorResource(R.color.white)
                }
            )

            if (buttonType.isSecondary) {
                strokeColor = ColorStateList.valueOf(
                    if (enabled) buttonStrokeColor else buttonStrokeDisabledColor
                )
                strokeWidth = context.dpToPx(2)
            }

            rippleColor =
                if (enabled && !buttonType.isText) ColorStateList.valueOf(buttonRippleColor) else rippleColor

            /** Icons **/
            if (buttonType.isText) {
                if (iconResource != 0) {
                    gravity = Gravity.CENTER
                    iconGravity = MaterialButton.ICON_GRAVITY_TEXT_END
                    icon = context.getDrawableResource(iconResource)
                    iconSize = context.dpToPx(16)
                    iconTint = when {
                        enabled -> null
                        else -> ColorStateList.valueOf(buttonTextDisabledColor)
                    }
                }
            }
        }
    }

    private fun ShahryButtonWidgetBinding.renderLoading(isLoading: Boolean) {
        isEnabled = !isLoading
        materialButton.apply {
            icon = when {
                isLoading -> materialButton.showProgress(
                    when {
                        buttonType.isText -> MaterialButton.ICON_GRAVITY_TEXT_END
                        else -> MaterialButton.TEXT_ALIGNMENT_CENTER
                    }
                )

                else -> when {
                    iconResource != 0 -> context.getDrawableResource(iconResource)
                    else -> null
                }
            }
            text = when {
                isLoading && !buttonType.isText -> ""
                else -> buttonText
            }
        }
    }

    private fun MaterialButton.showProgress(gravity: Int): IndeterminateDrawable<CircularProgressIndicatorSpec> {
        val spec = CircularProgressIndicatorSpec(context, null, 0).apply {
            indicatorColors = intArrayOf(progressColor)
            indicatorSize = context.dpToPx(if (buttonType == PRIMARY_X_SMALL || buttonType.isText) 16 else 24)
            trackThickness = context.dpToPx(if (buttonType == PRIMARY_X_SMALL || buttonType.isText) 2 else 3)
        }

        iconGravity = gravity
        return IndeterminateDrawable.createCircularDrawable(context, spec)
    }

    fun addOnButtonCallbackListener(callback: OnButtonCallbacks) {
        actionCallbacks = callback
    }

    sealed class State : Widget.State {
        data class Initial(val enabled: Boolean = true) : State()
        data class Loading(val isLoading: Boolean) : State()
    }

    enum class ButtonType {
        PRIMARY_LARGE,
        PRIMARY_MEDIUM,
        PRIMARY_SMALL,
        PRIMARY_X_SMALL,
        SECONDARY_LARGE,
        SECONDARY_MEDIUM,
        SECONDARY_SMALL,
        GHOST_LARGE,
        GHOST_MEDIUM,
        GHOST_SMALL,
        TEXT_LARGE,
        TEXT_MEDIUM,
        TEXT_SMALL;

        val isPrimary get() = this in listOf(PRIMARY_LARGE, PRIMARY_MEDIUM, PRIMARY_SMALL, PRIMARY_X_SMALL)

        val isSecondary get() = this in listOf(SECONDARY_LARGE, SECONDARY_MEDIUM, SECONDARY_SMALL)

        val isGhost get() = this in listOf(GHOST_LARGE, GHOST_MEDIUM, GHOST_SMALL)

        val isText get() = this in listOf(TEXT_LARGE, TEXT_MEDIUM, TEXT_SMALL)

        val textStyle
            get() = when (this) {
                PRIMARY_LARGE -> R.style.ShahryTitleLarge
                PRIMARY_MEDIUM -> R.style.ShahryTitleMedium
                PRIMARY_SMALL -> R.style.ShahryTitleMedium
                PRIMARY_X_SMALL -> R.style.ShahryLabelSmall
                SECONDARY_LARGE -> R.style.ShahryTitleLarge
                SECONDARY_MEDIUM -> R.style.ShahryTitleMedium
                SECONDARY_SMALL -> R.style.ShahryTitleMedium
                GHOST_LARGE -> R.style.ShahryTitleLarge
                GHOST_MEDIUM -> R.style.ShahryTitleMedium
                GHOST_SMALL -> R.style.ShahryTitleMedium
                TEXT_LARGE -> R.style.ShahryHeadingSmall
                TEXT_MEDIUM -> R.style.ShahryTitleSmall
                TEXT_SMALL -> R.style.ShahryLabelSmall
            }
    }

    interface OnButtonCallbacks {
        fun onClicked()
    }
}