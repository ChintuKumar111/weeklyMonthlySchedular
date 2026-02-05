package com.example.freshyzoappmodule.cartpreview.freetrial

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.BottomSheetFreeTrialBinding
import com.google.android.material.bottomsheet.BottomSheetDialogFragment

class FreeTrialBottomSheet : BottomSheetDialogFragment() {


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


