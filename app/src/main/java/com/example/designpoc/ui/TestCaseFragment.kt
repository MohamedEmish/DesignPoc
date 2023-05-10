package com.example.designpoc.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.designpoc.R
import com.example.designpoc.databinding.FragmentTestCaseBinding

class TestCaseFragment : Fragment() {

    private var _binding: FragmentTestCaseBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentTestCaseBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        with(binding) {
            ivBack.setOnClickListener {
                requireActivity().onBackPressedDispatcher.onBackPressed()
            }

            testItemOne.apply {
                root.outlineSpotShadowColor = ContextCompat.getColor(requireContext(), R.color.card_shadow_5)
            }
            testItemTwo.apply {
                root.outlineSpotShadowColor = ContextCompat.getColor(requireContext(), R.color.card_shadow_10)
            }
        }
    }
    override fun onDestroy() {
        _binding = null
        super.onDestroy()
    }
}