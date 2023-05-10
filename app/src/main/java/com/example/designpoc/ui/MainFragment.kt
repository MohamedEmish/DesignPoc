package com.example.designpoc.ui

import android.Manifest.permission.CAMERA
import android.Manifest.permission.RECORD_AUDIO
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import com.example.designpoc.R
import com.example.designpoc.databinding.FragmentMainBinding
import com.example.designpoc.utils.permission.PermissionResult.DENIED
import com.example.designpoc.utils.permission.PermissionResult.GRANTED
import com.example.designpoc.utils.permission.PermissionResult.RATIONAL_REASON
import com.example.designpoc.utils.permission.registerMultiplePermissionLauncher
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
            btnArabic.setOnClickListener {
                handleCheckedLanguage("ar")
            }
            btnEnglish.setOnClickListener {
                handleCheckedLanguage("en")
            }
            btnTestCase.setOnClickListener {
                parentFragmentManager.replaceFragment(
                    requireActivity(),
                    TestCaseFragment(),
                    R.id.fragmentContainer,
                    true
                )
            }
            btnDesignSystem.setOnClickListener {
                parentFragmentManager.replaceFragment(
                    requireActivity(),
                    DesignSystemFragment(),
                    R.id.fragmentContainer,
                    true
                )
            }
            btnButtons.setOnClickListener {
                parentFragmentManager.replaceFragment(
                    requireActivity(),
                    ButtonsFragment(),
                    R.id.fragmentContainer,
                    true
                )
            }
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