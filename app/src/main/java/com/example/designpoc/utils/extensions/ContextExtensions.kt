package com.example.designpoc.utils.extensions

import android.content.Context
import android.content.res.Resources
import android.util.TypedValue
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