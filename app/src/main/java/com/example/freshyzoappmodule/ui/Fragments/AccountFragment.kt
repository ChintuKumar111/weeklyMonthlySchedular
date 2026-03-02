package com.example.freshyzoappmodule.ui.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.FragmentAccountBinding

class AccountFragment : Fragment() {

    private var _binding: FragmentAccountBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // The cardProfileHero is inside the included layout_account_hero
        binding.root.findViewById<View>(R.id.tabProfile)?.setOnClickListener {
            findNavController().navigate(R.id.action_nav_account_to_profileFragment)
        }

        binding.root.findViewById<View>(R.id.tabAddress)?.setOnClickListener {
            findNavController().navigate(R.id.action_nav_account_to_addressFragment)
        }
        
        binding.btnLogout.setOnClickListener {
            // Handle logout
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
