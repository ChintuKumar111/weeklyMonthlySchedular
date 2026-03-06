package com.example.freshyzoappmodule.ui.Fragments

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.FragmentAccountBinding
import com.example.freshyzoappmodule.helper.PreferenceHelper

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

        updateProfileUI()

        // Navigation listeners
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

        binding.root.findViewById<View>(R.id.tabFaqs)?.setOnClickListener {
            findNavController().navigate(R.id.action_nav_account_to_faqsFragment)
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

    private fun updateProfileUI() {
        val savedName = PreferenceHelper.getUserName(requireContext())
        val savedImageUri = PreferenceHelper.getProfileImage(requireContext())

        binding.root.findViewById<TextView>(R.id.tvWelcomeUser)?.text = if (savedName != "User Name") {
            "Welcome, $savedName 👋"
        } else {
            "Welcome, User 👋"
        }

        val ivProfile = binding.root.findViewById<ImageView>(R.id.ivAccountProfile)
        if (ivProfile != null && savedImageUri != null) {
            Glide.with(this)
                .load(Uri.parse(savedImageUri))
                .circleCrop()
                .into(ivProfile)
        }
    }

    override fun onResume() {
        super.onResume()
        updateProfileUI()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
