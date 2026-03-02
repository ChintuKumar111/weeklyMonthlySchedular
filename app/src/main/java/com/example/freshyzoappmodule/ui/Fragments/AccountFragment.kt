package com.example.freshyzoappmodule.ui.Fragments

import android.content.Intent
import android.net.Uri
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
        binding.root.findViewById<View>(R.id.tabComplaintsNAssistance)?.setOnClickListener {
            findNavController().navigate(R.id.action_nav_account_to_complaintAssistanceFragment)
        }

        binding.root.findViewById<View>(R.id.tabTestReport)?.setOnClickListener {
            findNavController().navigate(R.id.action_nav_account_to_testReportFragment)
        }

        binding.root.findViewById<View>(R.id.tabAboutUs)?.setOnClickListener {
            val url = "https://freshyzo.com/about-us/"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)

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
