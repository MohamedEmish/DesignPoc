package com.example.designpoc.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.fragment.app.Fragment
import com.example.designpoc.R
import com.example.designpoc.databinding.FragmentButtonsBinding
import com.example.designpoc.utils.shahryView.ShahryButton
import com.google.android.material.appbar.AppBarLayout.OnOffsetChangedListener

class ButtonsFragment : Fragment() {
    private var _binding: FragmentButtonsBinding? = null
    private val binding get() = _binding!!

    private var buttonsEnabled = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentButtonsBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            appBarLayout.apply {
                toolbar.apply {
                    setNavigationOnClickListener {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    }
                    setOnMenuItemClickListener { menuItem ->
                        when (menuItem.itemId) {
                            R.id.ivEnable -> {
                                buttonsEnabled = !buttonsEnabled
                                binding.llRoot.children.forEach {
                                    if (it is ShahryButton) {
                                        it.apply {
                                            render(ShahryButton.State.Initial(buttonsEnabled))
                                        }
                                    }
                                }
                                true
                            }

                            else -> false
                        }
                    }
                    title = getString(R.string.buttons)
                    inflateMenu(R.menu.buttons_toolbar_menu)
                }

                collapsingToolbar.apply {
                    setExpandedTitleTextAppearance(R.style.ShahryHeadingLarge)
                    setCollapsedTitleTextAppearance(R.style.ShahryTitleLarge)
                }
            }

            binding.llRoot.children.forEach {
                if (it is ShahryButton) {
                    it.apply {
                        addOnButtonCallbackListener(object : ShahryButton.OnButtonCallbacks {
                            override fun onClicked() {
                                click()
                            }
                        })
                    }
                }
            }
        }
    }

    private fun ShahryButton.click() {
        render(ShahryButton.State.Loading(true))
        Handler(Looper.getMainLooper()).postDelayed({
            render(ShahryButton.State.Loading(false))
        }, 3000)
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}