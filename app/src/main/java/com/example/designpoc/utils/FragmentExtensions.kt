package com.example.designpoc.utils

import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.example.designpoc.R

inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> Unit) {
    val fragmentTransaction = beginTransaction()
    fragmentTransaction.func()
    fragmentTransaction.commit()
}

fun FragmentManager.replaceFragment(
    activity: FragmentActivity,
    fragment: Fragment,
    container: Int,
    addToBackStack: Boolean = false,
    tag: String = fragment::class.java.name,
) {
    activity.supportFragmentManager.inTransaction {
        replace(container, fragment)
        setCustomAnimations(
            R.anim.enter_from_right,
            R.anim.exit_to_left,
            R.anim.enter_from_left,
            R.anim.exit_to_right
        )
        takeIf { addToBackStack }?.apply { addToBackStack(tag) }
    }
}

fun View.vibrate() {
    val shake: Animation =
        AnimationUtils.loadAnimation(this.context, R.anim.shake)
    this.startAnimation(shake)
}
