package com.example.freshyzoappmodule.ui.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.FragmentProductSectionBinding
import com.example.freshyzoappmodule.databinding.FragmentWalletBinding


class WalletFragment : Fragment() {


    private lateinit var _binding: FragmentWalletBinding
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentWalletBinding.inflate(inflater, container, false)
       return binding.root
    }



}