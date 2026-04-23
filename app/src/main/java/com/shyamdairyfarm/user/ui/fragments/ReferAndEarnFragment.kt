package com.shyamdairyfarm.user.ui.fragments

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.shyamdairyfarm.user.databinding.FragmentReferAndEarnBinding

class ReferAndEarnFragment : Fragment() {

    private var _binding: FragmentReferAndEarnBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReferAndEarnBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupListeners()
    }

    private fun setupToolbar() {
        binding.toolbar.setNavigationOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupListeners() {
        binding.btnCopyCode.setOnClickListener {
            val code = binding.tvReferralCode.text.toString()
            val clipboard = requireContext().getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            val clip = ClipData.newPlainText("Referral Code", code)
            clipboard.setPrimaryClip(clip)
            Toast.makeText(requireContext(), "Code copied to clipboard", Toast.LENGTH_SHORT).show()
        }

        binding.btnShare.setOnClickListener {
            val code = binding.tvReferralCode.text.toString()
            val shareText = "Hey! Use my referral code $code to join Freshyzo and get ₹50 off on your first order. Download the app now!"
            
            val intent = Intent(Intent.ACTION_SEND).apply {
                type = "text/plain"
                putExtra(Intent.EXTRA_TEXT, shareText)
            }
            startActivity(Intent.createChooser(intent, "Share via"))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
