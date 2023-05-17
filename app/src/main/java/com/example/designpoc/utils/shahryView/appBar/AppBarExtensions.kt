package com.example.designpoc.utils.shahryView.appBar

import android.graphics.Color
import android.view.MenuItem
import androidx.annotation.DrawableRes
import androidx.annotation.MenuRes
import androidx.core.view.isVisible
import com.example.designpoc.R
import com.example.designpoc.databinding.AppbarLayoutBinding
import com.example.designpoc.utils.extensions.getDrawableResource
import com.example.designpoc.utils.shahryView.appBar.AppBarAnimation.FADE
import com.example.designpoc.utils.shahryView.appBar.AppBarAnimation.TRANSITION
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener
import kotlin.math.abs

fun AppbarLayoutBinding.setTitle(title: String) {
    toolbar.title = title
    tvExpandedTitle.text = title
    setTitleStyle()
}

fun AppbarLayoutBinding.setAppBarTransitionAnimation() {
    collapsingToolbar.isTitleEnabled = true
    tvExpandedTitle.isVisible = false
}

fun AppbarLayoutBinding.setAppBarFadeAnimation() {
    collapsingToolbar.isTitleEnabled = false
    tvExpandedTitle.isVisible = true
    appBar.addOnOffsetChangedListener(object : OnOffsetChangedListener {
        var scrollRange = -1
        override fun onOffsetChanged(appBarLayout: AppBarLayout, verticalOffset: Int) {
            if (scrollRange == -1) {
                scrollRange = appBarLayout.totalScrollRange
            }
            if (verticalOffset == 0) {
                toolbar.setTitleTextColor(Color.TRANSPARENT)
            } else {
                val alpha = abs(verticalOffset).toFloat() / scrollRange
                var color = Color.BLACK
                color = color and 0x00ffffff or ((alpha * 255).toInt().toByte().toInt() shl 24)
                toolbar.setTitleTextColor(color)
                var topColor = Color.BLACK
                topColor = topColor and 0x00ffffff or (((1 - alpha) * 255).toInt().toByte().toInt() shl 24)
                tvExpandedTitle.setTextColor(topColor)
            }
            if (scrollRange + verticalOffset == 0) {
                tvExpandedTitle.setTextColor(Color.TRANSPARENT)
            }
        }
    })
}

fun AppbarLayoutBinding.setTitleStyle() {
    collapsingToolbar.apply {
        setExpandedTitleTextAppearance(R.style.ShahryHeadingLarge)
        setCollapsedTitleTextAppearance(R.style.ShahryTitleLarge)
    }
}

fun AppbarLayoutBinding.configureAppBar(
    title: String,
    animationType: AppBarAnimation = TRANSITION,
    hasNavigationIcon: Boolean = true,
    @DrawableRes navIcon: Int = R.drawable.ic_back,
    @MenuRes menuRes: Int = 0,
    onNavigationClicked: () -> Unit,
    onMenuItemClicked: (menuItem: MenuItem) -> Unit,
) {
    setTitle(title)
    toolbar.apply {
        navigationIcon = when {
            hasNavigationIcon -> {
                setNavigationOnClickListener { onNavigationClicked.invoke() }
                context.getDrawableResource(navIcon)
            }

            else -> null
        }


        if (menuRes != 0) {
            inflateMenu(menuRes)
            setOnMenuItemClickListener { menuItem ->
                onMenuItemClicked.invoke(menuItem)
                true
            }
        }
    }

    when (animationType) {
        FADE -> setAppBarFadeAnimation()
        TRANSITION -> setAppBarTransitionAnimation()
    }
}
