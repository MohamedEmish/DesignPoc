package com.example.designpoc.utils.widget

import com.example.designpoc.utils.widget.Widget.State

interface Widget<s: State> {
    interface State

    fun render(state: s)
}