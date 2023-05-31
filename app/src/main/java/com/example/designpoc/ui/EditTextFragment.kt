package com.example.designpoc.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.designpoc.R
import com.example.designpoc.databinding.FragmentEditTextBinding
import com.example.designpoc.utils.shahryView.ShahryInputField.State
import com.example.designpoc.utils.shahryView.ShahryPasswordInputField
import com.example.designpoc.utils.shahryView.appBar.AppBarAnimation.TRANSITION
import com.example.designpoc.utils.shahryView.appBar.configureAppBar

class EditTextFragment : Fragment() {
    private var _binding: FragmentEditTextBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEditTextBinding.inflate(layoutInflater, container, false)
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
            shahryEditText.setHelperText(getString(R.string.help_text_here))
            shahryEditTextPassword.setHelperText(getString(R.string.help_text_here))
            shahryPhoneNumber.observeTextEntered(viewLifecycleOwner)
            shahryPhoneNumber.setHelperText(getString(R.string.help_text_here))
            etBasic.setHelperText("")
            etCreditCard.observeTextEntered(viewLifecycleOwner)

            btnSetError.setOnClickActionListener {
                shahryEditText.render(State.ErrorState(getString(R.string.otp_error)))
                shahryEditTextPassword.render(ShahryPasswordInputField.State.ErrorState(getString(R.string.otp_error)))
                shahryPhoneNumber.renderError(getString(R.string.otp_error))
                etCreditCard.renderError(getString(R.string.otp_error))
                etBasic.renderError(getString(R.string.otp_error))
            }
            btnClearError.setOnClickActionListener {
                shahryEditText.setHelperText(getString(R.string.help_text_here))
                shahryEditTextPassword.setHelperText(getString(R.string.help_text_here))
                shahryEditText.render(State.Initial(true))
                shahryEditTextPassword.render(ShahryPasswordInputField.State.Initial(true))
                shahryPhoneNumber.renderInitial(true)
                etCreditCard.renderInitial(true)
                etBasic.renderInitial(true)
            }
            btnDisable.setOnClickActionListener {
                shahryEditText.setHelperText(getString(R.string.help_text_here))
                shahryEditText.render(State.Initial(false))
                shahryEditTextPassword.render(ShahryPasswordInputField.State.Initial(false))
                shahryPhoneNumber.renderInitial(false)
                etCreditCard.renderInitial(false)
                etBasic.renderInitial(false)
            }
            btnEnabled.setOnClickActionListener {
                shahryEditTextPassword.setHelperText(getString(R.string.help_text_here))
                shahryEditText.setHelperText(getString(R.string.help_text_here))
                shahryEditText.render(State.Initial(true))
                shahryEditTextPassword.render(ShahryPasswordInputField.State.Initial(true))
                shahryPhoneNumber.renderInitial(true)
                etCreditCard.renderInitial(true)
                etBasic.renderInitial(true)
            }
            btnClearFocus.setOnClickActionListener {
                shahryEditText.clearFieldFocus()
                shahryEditTextPassword.clearFieldFocus()
                shahryPhoneNumber.clearFieldFocus()
                etCreditCard.clearFieldFocus()
                etBasic.clearFieldFocus()
            }
            btnGetData.setOnClickActionListener {
                etCreditCard.setOnEditTextClickListener {
                    Toast.makeText(requireContext(), it.filter { chars -> !chars.isWhitespace() }, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}