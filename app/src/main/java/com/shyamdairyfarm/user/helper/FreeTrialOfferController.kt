package com.shyamdairyfarm.user.helper

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.shyamdairyfarm.user.databinding.BottomSheetFreeTrialBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FreeTrialOfferController : BottomSheetDialogFragment() {

    private var _binding: BottomSheetFreeTrialBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = BottomSheetFreeTrialBinding.inflate(inflater, container, false)

        binding.ivClose.setOnClickListener {
            dismiss()
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}