package com.example.designpoc.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.designpoc.R
import com.example.designpoc.R.string
import com.example.designpoc.databinding.FragmentOtpBinding
import com.example.designpoc.utils.shahryView.ShahryButton
import com.example.designpoc.utils.shahryView.ShahryPinView
import com.example.designpoc.utils.shahryView.ShahryPinView.State.Error
import com.example.designpoc.utils.shahryView.ShahryPinView.State.Initial

class OtpFragment: Fragment() {
    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!

    private var buttonsEnabled = true
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentOtpBinding.inflate(layoutInflater, container, false)
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
                    title = getString(R.string.otp)
                }

                collapsingToolbar.apply {
                    setExpandedTitleTextAppearance(R.style.ShahryHeadingLarge)
                    setCollapsedTitleTextAppearance(R.style.ShahryTitleLarge)
                }
            }
            btnSetError.addOnButtonCallbackListener(object : ShahryButton.OnButtonCallbacks{
                override fun onClicked() {
                    shahryPinView.render(Error (getString(string.otp_error)))
                }
            })
            btnClearError.addOnButtonCallbackListener(object : ShahryButton.OnButtonCallbacks{
                override fun onClicked() {
                    shahryPinView.render(Initial())
                }

            })
            btnClearFields.addOnButtonCallbackListener(object : ShahryButton.OnButtonCallbacks{
                override fun onClicked() {
                    shahryPinView.render(Initial(true))
                }
            })

            shahryPinView.addCallbackListener(object : ShahryPinView.OnButtonCallbacks {
                override fun onValueEntered(value: String) {
                    Toast.makeText(requireContext(), value, Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}