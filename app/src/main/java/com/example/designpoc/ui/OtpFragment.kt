package com.example.designpoc.ui

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.designpoc.R
import com.example.designpoc.databinding.FragmentOtpBinding
import com.example.designpoc.utils.extensions.hideKeyboard
import com.example.designpoc.utils.shahryView.ShahryInputField.State
import com.example.designpoc.utils.shahryView.ShahryPinView
import com.example.designpoc.utils.shahryView.ShahryPinView.State.Error
import com.example.designpoc.utils.shahryView.ShahryPinView.State.Initial
import com.example.designpoc.utils.shahryView.appBar.AppBarAnimation.TRANSITION
import com.example.designpoc.utils.shahryView.appBar.configureAppBar
import com.google.android.material.internal.ViewUtils.hideKeyboard

class OtpFragment : Fragment() {
    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!

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
                configureAppBar(
                    title = getString(R.string.otp),
                    animationType = TRANSITION,
                    hasNavigationIcon = true,
                    navIcon = R.drawable.ic_back,
                    onNavigationClicked = {
                        requireActivity().onBackPressedDispatcher.onBackPressed()
                    },
                )
            }

            btnSetError.setOnClickActionListener {
                shahryPinView.render(Error(getString(R.string.otp_error)))
                shahryEditText.render(State.ErrorState(getString(R.string.otp_error)))
            }
            btnClearError.setOnClickActionListener {
                shahryPinView.render(Initial())
                shahryEditText.render(State.Initial(true))
            }

            btnClearFields.setOnClickActionListener {
                shahryPinView.render(Initial(true))
                shahryEditText.render(State.Initial(true, getString(R.string.help_text_here)))
            }

            shahryPinView.setOnValueEnteredAction { value ->
                Toast.makeText(requireContext(), value, Toast.LENGTH_SHORT).show()
                requireContext().hideKeyboard(shahryPinView)
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}