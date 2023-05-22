package com.example.designpoc.utils.shahryView

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.example.designpoc.R
import com.example.designpoc.databinding.ShahryPinViewWidgetBinding
import com.example.designpoc.utils.extensions.getColorResource
import com.example.designpoc.utils.extensions.showSoftKeyboard
import com.example.designpoc.utils.shahryView.ShahryPinView.State
import com.example.designpoc.utils.shahryView.ShahryPinView.State.Error
import com.example.designpoc.utils.shahryView.ShahryPinView.State.Filled
import com.example.designpoc.utils.widget.Widget

class ShahryPinView(
    context: Context,
    attrs: AttributeSet? = null,
) : FrameLayout(
    context,
    attrs
), Widget<State> {

    private val binding by lazy { ShahryPinViewWidgetBinding.inflate(LayoutInflater.from(context), this, true) }

    private var onValueEntered: (value: String) -> Unit = {}

    private val pinViewListener = object : PinView.PinViewEventListener {
        override fun onDataEntered(pinView: PinView?, fromUser: Boolean) {
            pinView?.value?.let {
                onValueEntered.invoke(it)
                binding.renderFilled()
                binding.pinView.clearFocus()
            }
        }

        override fun onTextChangeListener(charSequence: CharSequence?, start: Int, end: Int, count: Int) {
            if (binding.tvHint.text.isNotEmpty()) {
                binding.renderInitial()
            }
        }

    }
    private var pinViewHint = 0

    init {
        val attributes = context.obtainStyledAttributes(attrs, R.styleable.ShahryPinView)

        if (attrs != null) {
            pinViewHint = attributes.getResourceId(
                R.styleable.ShahryPinView_pinViewHint,
                5
            )
        }
        binding.pinView.apply {
            setPinViewEventListener(pinViewListener)
            requestFocus()
            showSoftKeyboard()
        }
        binding.renderInitial()
        attributes.recycle()
    }

    override fun render(state: State) {
        when (state) {
            is State.Initial -> binding.renderInitial(state.clearFields)
            is Error -> binding.renderError(state.message)
            is Filled -> binding.renderFilled()
        }
    }

    private fun ShahryPinViewWidgetBinding.renderInitial(clearFields: Boolean = false) {
        pinView.apply {
            setPinBackgroundRes(R.drawable.pin_background)
            if (clearFields && pinView.value.isNotEmpty()) {
                pinView.clearValue()
            }
            setTextColor(
                context.getColorResource(R.color.black)
            )
        }

        tvHint.apply {
            text = when (pinViewHint) {
                0 -> ""
                else -> context.getString(pinViewHint)
            }
            setTextColor(
                ColorStateList.valueOf(
                    context.getColorResource(R.color.platinum_600)
                )
            )
            visibility = when (pinViewHint) {
                0 -> INVISIBLE
                else -> VISIBLE
            }
        }
    }

    private fun ShahryPinViewWidgetBinding.renderError(message: String) {
        pinView.apply {
            setPinBackgroundRes(R.drawable.pin_error_background)
            setTextColor(
                context.getColorResource(R.color.error_600)
            )
        }

        tvHint.apply {
            text = message
            setTextColor(
                ColorStateList.valueOf(
                    context.getColorResource(R.color.error_600)
                )
            )
            visibility = VISIBLE
        }
    }

    private fun ShahryPinViewWidgetBinding.renderFilled() {
        pinView.apply {
            setPinBackgroundRes(R.drawable.pin_background)
            setTextColor(
                context.getColorResource(R.color.black)
            )
        }

        tvHint.apply {
            text = ""
            setTextColor(
                ColorStateList.valueOf(
                    context.getColorResource(R.color.platinum_600)
                )
            )
            visibility = INVISIBLE
        }
    }

    fun setOnValueEnteredAction(onValueEntered: (value: String) -> Unit) {
        this.onValueEntered = onValueEntered
    }

    sealed class State : Widget.State {
        data class Initial(val clearFields: Boolean = false) : State()
        data class Error(val message: String) : State()
        object Filled : State()
    }
}