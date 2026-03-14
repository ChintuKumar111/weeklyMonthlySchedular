package com.example.freshyzoappmodule.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.freshyzoappmodule.databinding.FragmentComplaintAssistanceCenterBinding

class ComplaintsAssistanceFragment : Fragment() {
    private var _binding: FragmentComplaintAssistanceCenterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {

        _binding = FragmentComplaintAssistanceCenterBinding.inflate(inflater, container, false)
        return binding.root
    }

}