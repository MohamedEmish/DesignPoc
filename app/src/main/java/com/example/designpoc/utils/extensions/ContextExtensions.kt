package com.example.designpoc.utils.extensions

import android.app.Activity
import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
import android.view.View
import android.view.inputmethod.InputMethodManager
import androidx.core.content.ContextCompat

fun Context.getDrawableResource(id: Int) = ContextCompat.getDrawable(this, id)

fun Context.getColorResource(id: Int) = ContextCompat.getColor(this, id)

fun Context.dpToPx(dp: Int): Int {
    return TypedValue.applyDimension(
        TypedValue.COMPLEX_UNIT_DIP,
        dp.toFloat(),
        this.resources.displayMetrics
    ).toInt()
}

val Int.dp: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.hideKeyboard(view: View) {
    val imm = this.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(view.windowToken, 0)
}