package com.example.designpoc.utils.shahryView

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Rect
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.text.Editable
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.text.TextWatcher
import android.text.method.TransformationMethod
import android.util.AttributeSet
import android.view.Gravity
import android.view.KeyEvent
import android.view.View
import android.view.View.OnFocusChangeListener
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import androidx.annotation.ColorInt
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import androidx.core.view.setMargins
import com.example.designpoc.R
import java.util.*

/**
 * This class implements a pinview for android.
 * It can be used as a widget in android to take passwords/OTP/pins etc.
 * It is extended from a LinearLayout, implements TextWatcher, FocusChangeListener and OnKeyListener.
 * Supports drawableItem/selectors as a background for each pin box.
 * A listener is wired up to monitor complete data entry.
 * Can toggle cursor visibility.
 * Supports numpad and text keypad.
 * Flawless focus change to the consecutive pinbox.
 * Date : 11/01/17
 *
 * @author Krishanu
 * @author Pavan
 * @author Koushik
 */
internal class PinView @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) :
    LinearLayout(context, attrs, defStyleAttr), TextWatcher, OnFocusChangeListener, View.OnKeyListener {
    private val deviceDensity = getContext().resources.displayMetrics.density

    /**
     * Attributes
     */
    private var mPinLength = 6
    private val editTextList: MutableList<EditText> = mutableListOf()
    private var mPinWidth = 50
    private var mTextSize = 12
    private var mPinHeight = 50
    private var mSplitWidth = 20
    private var mCursorVisible = false
    private var mDelPressed = false
    private var mTextColor: Int = R.color.black

    @get:DrawableRes
    @DrawableRes
    var pinBackground = R.drawable.pin_background
        private set
    private var mPassword = false
    private var mHint: String? = ""
    private var inputType = InputType.TEXT
    private var finalNumberPin = false
    private var mListener: PinViewEventListener? = null
    private var fromSetValue = false
    private var mForceKeyboard = true

    enum class InputType {
        TEXT,
        NUMBER
    }

    /**
     * Interface for onDataEntered event.
     */
    interface PinViewEventListener {
        fun onDataEntered(pinView: PinView?, fromUser: Boolean)
        fun onTextChangeListener(charSequence: CharSequence?, start: Int, end: Int, count: Int)
    }

    private var mClickListener: OnClickListener? = null
    private var currentFocus: View? = null
    private var filters = arrayOfNulls<InputFilter>(1)
    private var params: LayoutParams? = null

    /**
     * A method to take care of all the initialisations.
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private fun init(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        removeAllViews()
        mPinHeight *= deviceDensity.toInt()
        mPinWidth *= deviceDensity.toInt()
        mSplitWidth *= deviceDensity.toInt()
        setWillNotDraw(false)
        initAttributes(context, attrs, defStyleAttr)
        params = LayoutParams(mPinWidth, mPinHeight)
        orientation = HORIZONTAL
        createEditTexts()
        super.setOnClickListener {
            var focused = false
            for (editText in editTextList) {
                if (editText.length() == 0) {
                    editText.requestFocus()
                    openKeyboard()
                    focused = true
                    break
                }
            }
            if (!focused && editTextList.size > 0) { // Focus the last view
                editTextList[editTextList.size - 1].requestFocus()
            }
            mClickListener?.onClick(this@PinView)

        }
        // Bring up the keyboard
        val firstEditText: View = editTextList[0]
        firstEditText.postDelayed({ openKeyboard() }, 200)
        updateEnabledState()
    }

    /**
     * Creates editTexts and adds it to the pinview based on the pinLength specified.
     */
    private fun createEditTexts() {
        removeAllViews()
        editTextList.clear()
        var editText: EditText
        for (i in 0 until mPinLength) {
            editText = EditText(context)
            editText.textSize = mTextSize.toFloat()
            editText.setTextColor(ContextCompat.getColor(this.context, mTextColor))
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                editText.setTextAppearance(R.style.ShahryBodyLarge)
            }
            editTextList.add(i, editText)
            this.addView(editText)
            generateOneEditText(editText, "" + i)
        }
        setTransformation()
    }

    /**
     * This method gets the attribute values from the XML, if not found it takes the default values.
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    private fun initAttributes(context: Context, attrs: AttributeSet?, defStyleAttr: Int) {
        if (attrs != null) {
            val array = context.obtainStyledAttributes(attrs, R.styleable.PinView, defStyleAttr, 0)
            pinBackground = array.getResourceId(R.styleable.PinView_pinBackground, pinBackground)
            mPinLength = array.getInt(R.styleable.PinView_pinLength, mPinLength)
            mPinHeight = array.getDimension(R.styleable.PinView_pinHeight, mPinHeight.toFloat()).toInt()
            mPinWidth = array.getDimension(R.styleable.PinView_pinWidth, mPinWidth.toFloat()).toInt()
            mSplitWidth = array.getDimension(R.styleable.PinView_splitWidth, mSplitWidth.toFloat()).toInt()
            mTextSize = array.getDimension(R.styleable.PinView_textSize, mTextSize.toFloat()).toInt()
            mCursorVisible = array.getBoolean(R.styleable.PinView_cursorVisible, mCursorVisible)
            mPassword = array.getBoolean(R.styleable.PinView_password, mPassword)
            mForceKeyboard = array.getBoolean(R.styleable.PinView_forceKeyboard, mForceKeyboard)
            mHint = array.getString(R.styleable.PinView_hint)
            mTextColor = array.getResourceId(R.styleable.PinView_pinTextColor, mTextColor)
            val its = InputType.values()
            inputType = its[array.getInt(R.styleable.PinView_inputType, 0)]
            array.recycle()
        }
    }

    /**
     * Takes care of styling the editText passed in the param.
     * tag is the index of the editText.
     *
     * @param styleEditText
     * @param tag
     */
    @SuppressLint("ClickableViewAccessibility")
    private fun generateOneEditText(styleEditText: EditText, tag: String) {
        params?.setMargins(mSplitWidth / 2)
        filters[0] = LengthFilter(1)
        styleEditText.apply {
            filters = filters
            layoutParams = params
            gravity = Gravity.CENTER
            if (VERSION.SDK_INT >= VERSION_CODES.M) {
                foregroundGravity = Gravity.CENTER
            }
            textAlignment = TEXT_ALIGNMENT_CENTER
            isCursorVisible = mCursorVisible
            if (!mCursorVisible) {
                isClickable = false
                hint = mHint
                setOnTouchListener { _, _ -> // When back space is pressed it goes to delete mode and when u click on an edit Text it should get out of the delete mode
                    mDelPressed = false
                    false
                }
            }
            setBackgroundResource(pinBackground)
            setPadding(0, 0, 0, 0)
            this.tag = tag
            inputType = keyboardInputType
            addTextChangedListener(this@PinView)
            onFocusChangeListener = this@PinView
            setOnKeyListener(this@PinView)
        }
    }

    private val keyboardInputType: Int
        get() {
            return when (inputType) {
                InputType.NUMBER -> android.text.InputType.TYPE_CLASS_NUMBER or android.text.InputType.TYPE_NUMBER_VARIATION_PASSWORD
                InputType.TEXT -> android.text.InputType.TYPE_CLASS_TEXT
            }
        }
    /**
     * Returns the value of the Pinview
     *
     * @return
     */
    /**
     * Sets the value of the PinView
     *
     * @param
     */
    var value: String
        get() {
            val sb = StringBuilder()
            for (et in editTextList) {
                sb.append(et.text.toString())
            }
            return sb.toString()
        }
        set(value) {
            val regex = "\\d*" // Allow empty string to clear the fields
            fromSetValue = true
            if (inputType == InputType.NUMBER && !value.matches(regex.toRegex())) return
            var lastTagHavingValue = -1
            for (i in editTextList.indices) {
                if (value.length > i) {
                    lastTagHavingValue = i
                    editTextList[i].setText(value[i].toString())
                } else {
                    editTextList[i].setText("")
                }
            }
            if (mPinLength > 0) {
                if (lastTagHavingValue < mPinLength - 1) {
                    currentFocus = editTextList[lastTagHavingValue + 1]
                } else {
                    currentFocus = editTextList[mPinLength - 1]
                    if (inputType == InputType.NUMBER || mPassword) finalNumberPin = true
                    mListener?.onDataEntered(this, false)
                }
                currentFocus?.requestFocus()
            }
            fromSetValue = false
            updateEnabledState()
        }

    @Suppress("DEPRECATION")
    private fun openKeyboard() {
        if (mForceKeyboard) {
            val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
        }
    }

    /**
     * Clears the values in the Pinview
     */
    fun clearValue() {
        value = ""
    }

    override fun onFocusChange(view: View, isFocused: Boolean) {
        if (isFocused && !mCursorVisible) {
            if (mDelPressed) {
                currentFocus = view
                mDelPressed = false
                return
            }
            for (editText in editTextList) {
                if (editText.length() == 0) {
                    if (editText !== view) {
                        editText.requestFocus()
                    } else {
                        currentFocus = view
                    }
                    return
                }
            }
            if (editTextList[editTextList.size - 1] !== view) {
                editTextList[editTextList.size - 1].requestFocus()
            } else {
                currentFocus = view
            }
        } else if (isFocused) {
            currentFocus = view
        } else {
            view.clearFocus()
        }
    }

    /**
     * Handles the character transformation for password inputs.
     */
    private fun setTransformation() {
        if (mPassword) {
            for (editText in editTextList) {
                editText.removeTextChangedListener(this)
                editText.transformationMethod = PinTransformationMethod()
                editText.addTextChangedListener(this)
            }
        } else {
            for (editText in editTextList) {
                editText.removeTextChangedListener(this)
                editText.transformationMethod = null
                editText.addTextChangedListener(this)
            }
        }
    }

    override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {}

    /**
     * Fired when text changes in the editTexts.
     * Backspace is also identified here.
     *
     * @param charSequence
     * @param start
     * @param i1
     * @param count
     */
    override fun onTextChanged(charSequence: CharSequence, start: Int, i1: Int, count: Int) {
        mListener?.onTextChangeListener(charSequence, start, i1, count)
        if (charSequence.length == 1 && currentFocus != null) {
            val currentTag = indexOfCurrentFocus
            if (currentTag < mPinLength - 1) {
                var delay: Long = 1
                if (mPassword) delay = 25
                postDelayed({
                    val nextEditText = editTextList[currentTag + 1]
                    nextEditText.isEnabled = true
                    nextEditText.requestFocus()
                }, delay)
            }
            if (currentTag == mPinLength - 1 && inputType == InputType.NUMBER || currentTag == mPinLength - 1 && mPassword) {
                finalNumberPin = true
            }
        } else if (charSequence.isEmpty()) {
            val currentTag = indexOfCurrentFocus
            mDelPressed = true
            //For the last cell of the non password text fields. Clear the text without changing the focus.
            if (editTextList[currentTag].text.isNotEmpty()) editTextList[currentTag].setText("")
        } else if (charSequence.length > 1) {
            val currentTag = indexOfCurrentFocus
            //For the last cell of the non password text fields. Clear the text without changing the focus.
            if (editTextList[currentTag].text.isNotEmpty())
                editTextList[currentTag].setText(charSequence.last().toString())
        }
        for (index in 0 until mPinLength) {
            if (editTextList[index].text.isEmpty()) break
            if (!fromSetValue && index + 1 == mPinLength && indexOfCurrentFocus == mPinLength - 1) mListener?.onDataEntered(this, true)
        }
        updateEnabledState()
    }

    /**
     * Disable views ahead of current focus, so a selector can change the drawing of those views.
     */
    private fun updateEnabledState() {
        val currentTag = 0.coerceAtLeast(indexOfCurrentFocus)
        for (index in editTextList.indices) {
            val editText = editTextList[index]
            editText.isEnabled = editText.text.isNotEmpty() || index <= currentTag
        }
    }

    override fun afterTextChanged(editable: Editable) {}

    /**
     * Monitors keyEvent.
     *
     * @param view
     * @param i
     * @param keyEvent
     * @return
     */
    override fun onKey(view: View, i: Int, keyEvent: KeyEvent): Boolean {
        if (keyEvent.action == KeyEvent.ACTION_UP && i == KeyEvent.KEYCODE_DEL) {
            // Perform action on Del press
            val currentTag = indexOfCurrentFocus
            //Last tile of the number pad. Clear the edit text without changing the focus.
            if (inputType == InputType.NUMBER && currentTag == mPinLength - 1 && finalNumberPin ||
                mPassword && currentTag == mPinLength - 1 && finalNumberPin
            ) {
                if (editTextList[currentTag].length() > 0) {
                    editTextList[currentTag].setText("")
                }
                finalNumberPin = false
            } else if (currentTag > 0) {
                mDelPressed = true
                if (editTextList[currentTag].length() == 0) {
                    //Takes it back one tile
                    editTextList[currentTag - 1].requestFocus()
                    //Clears the tile it just got to
                    editTextList[currentTag].setText("")
                } else {
                    //If it has some content clear it first
                    editTextList[currentTag].setText("")
                }
            } else {
                //For the first cell
                if (editTextList[currentTag].text.isNotEmpty()) editTextList[currentTag].setText("")
            }
            return true
        }
        return false
    }

    /**
     * A class to implement the transformation mechanism
     */
    private inner class PinTransformationMethod : TransformationMethod {
        private val bullet = '\u2022'
        override fun getTransformation(source: CharSequence, view: View): CharSequence {
            return PasswordCharSequence(source)
        }

        override fun onFocusChanged(
            view: View,
            sourceText: CharSequence,
            focused: Boolean,
            direction: Int,
            previouslyFocusedRect: Rect,
        ) {
        }

        private inner class PasswordCharSequence(private val source: CharSequence) : CharSequence {
            override val length: Int
                get() = source.length

            override fun get(index: Int): Char {
                return bullet
            }

            override fun subSequence(startIndex: Int, endIndex: Int): CharSequence {
                return PasswordCharSequence(source.subSequence(startIndex, endIndex))
            }
        }
    }

    /**
     * Getters and Setters
     */
    private val indexOfCurrentFocus: Int
        get() = editTextList.indexOf(currentFocus)
    var hint: String?
        get() = mHint
        set(mHint) {
            this.mHint = mHint
            for (editText in editTextList) editText.hint = mHint
        }

    override fun setOnClickListener(l: OnClickListener?) {
        mClickListener = l
    }

    fun setPinViewEventListener(listener: PinViewEventListener?) {
        mListener = listener
    }

    fun setPinBackgroundRes(@DrawableRes res: Int) {
        pinBackground = res
        editTextList.forEach { it.setBackgroundResource(res) }
    }

    fun setPinLength(length: Int) {
        mPinLength = length
        createEditTexts()
    }

    fun setTextColor(@ColorInt color: Int) {
        if (editTextList.isEmpty()) {
            return
        }
        for (edt in editTextList) {
            edt.setTextColor(color)
        }
    }

    fun setIsFocusable(focusable: Boolean) {
        editTextList.forEach {
            it.isFocusable = focusable
            if (focusable) {
                it.isEnabled = true
                it.isFocusableInTouchMode = true
            }
        }
    }

    init {
        gravity = Gravity.CENTER
        init(context, attrs, defStyleAttr)
    }
}