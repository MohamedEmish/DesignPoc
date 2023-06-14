package com.example.designpoc.ui

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.designpoc.R
import com.example.designpoc.databinding.FragmentMainBinding
import com.example.designpoc.utils.replaceFragment
import com.yariksoffice.lingver.Lingver

class MainFragment : Fragment() {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentMainBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            btnArabic.setOnClickActionListener {
                handleCheckedLanguage("ar")
            }
            btnEnglish.setOnClickActionListener {
                handleCheckedLanguage("en")
            }
            btnTestCase.setOnClickActionListener {
                parentFragmentManager.replaceFragment(
                    requireActivity(),
                    TestCaseFragment(),
                    R.id.fragmentContainer,
                    true
                )
            }
            btnDesignSystem.setOnClickActionListener {
                parentFragmentManager.replaceFragment(
                    requireActivity(),
                    DesignSystemFragment(),
                    R.id.fragmentContainer,
                    true
                )
            }
            btnButtons.setOnClickActionListener {
                parentFragmentManager.replaceFragment(
                    requireActivity(),
                    ButtonsFragment(),
                    R.id.fragmentContainer,
                    true
                )
            }
            btnOtp.setOnClickActionListener {
                parentFragmentManager.replaceFragment(
                    requireActivity(),
                    OtpFragment(),
                    R.id.fragmentContainer,
                    true
                )
            }

            btnEditText.setOnClickActionListener {
                parentFragmentManager.replaceFragment(
                    requireActivity(),
                    EditTextFragment(),
                    R.id.fragmentContainer,
                    true
                )
            }
            btnEnglish.setOnClickListener { }
        }
    }

    private fun handleCheckedLanguage(language: String) {
        val oldLocale = Lingver.getInstance().getLanguage()
        Lingver.getInstance().setLocale(requireContext(), language)
        if (oldLocale != Lingver.getInstance().getLanguage()) {
            val intent = Intent(
                requireActivity(), MainActivity::class.java
            ).apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            startActivity(intent)
        }
    }

    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}