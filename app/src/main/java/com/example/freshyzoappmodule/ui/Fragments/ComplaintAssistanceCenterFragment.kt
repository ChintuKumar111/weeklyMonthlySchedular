package com.example.freshyzoappmodule.ui.Fragments

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.BottomSheetComplaintBinding
import com.example.freshyzoappmodule.databinding.FragmentComplaintAssistanceCenterBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ComplaintAssistanceCenterFragment : Fragment() {

    private var _binding: FragmentComplaintAssistanceCenterBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComplaintAssistanceCenterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        setupClickListeners()
    }

    private fun setupClickListeners() {
        binding.tabAppRelatedIssue.setOnClickListener {
            showComplaintBottomSheet("App Related Issue")
        }

        binding.tabProductRelatedIssue.setOnClickListener {
            showComplaintBottomSheet("Product Related Issue")
        }

        binding.tabRechargeRelatedIssue.setOnClickListener {
            showComplaintBottomSheet("Recharge Related Issue")
        }

        binding.tabDeliveryRelatedIssue.setOnClickListener {
            showComplaintBottomSheet("Delivery Related Issue")
        }

        binding.tabOtherIssue.setOnClickListener {
            showComplaintBottomSheet("Other Issue")
        }

        binding.tabPreviousComplaints.setOnClickListener {
            findNavController().navigate(R.id.action_complaintAssistanceCenterFragment_to_complaintHistoryFragment)
        }
        binding.tabOtherIssue.setOnClickListener {

        }
    }

    private fun showComplaintBottomSheet(title: String) {
        val dialog = BottomSheetDialog(requireContext())
        val sheetBinding = BottomSheetComplaintBinding.inflate(layoutInflater)
        dialog.setContentView(sheetBinding.root)

        sheetBinding.tvSheetTitle.text = title

        // Dropdown options based on category
        val issues = when (title) {
            "App Related Issue" -> arrayOf(
                "App crashing or freezing",
                "Login or account issues",
                "Bug or technical glitch",
                "Feature not working",
                "Poor UI/UX experience",
                "Security or privacy concern",
                "Other"
            )
            "Product Related Issue" -> arrayOf("Damaged Product", "Quality Issue", "Wrong Item", "Missing Item", "Other")
            "Recharge Related Issue" -> arrayOf("Payment Failed", "Balance not updated", "Refund Issue", "Other")
            "Delivery Related Issue" -> arrayOf("Late Delivery", "Partner Behavior", "Not Delivered", "Other")
            else -> arrayOf("General Inquiry", "Feedback", "Other")
        }

        sheetBinding.llIssueType.setOnClickListener {
            val popup = PopupMenu(requireContext(), it)
            issues.forEach { issue -> popup.menu.add(issue) }
            popup.setOnMenuItemClickListener { item ->
                sheetBinding.tvSelectedIssue.text = item.title
                sheetBinding.tvSelectedIssue.setTextColor(resources.getColor(R.color.black, null))
                true
            }
            popup.show()
        }

        // Description character count logic
        sheetBinding.etDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val length = s?.length ?: 0
                sheetBinding.tvCharCount.text = "$length/300"
                if (length > 300) {
                    sheetBinding.tvCharCount.setTextColor(resources.getColor(android.R.color.holo_red_dark, null))
                } else {
                    sheetBinding.tvCharCount.setTextColor(resources.getColor(com.google.android.material.R.color.material_grey_800, null))
                }
            }
            override fun afterTextChanged(s: Editable?) {}
        })

        sheetBinding.btnSubmit.setOnClickListener {
            val issueType = sheetBinding.tvSelectedIssue.text.toString()
            val description = sheetBinding.etDescription.text.toString().trim()

            if (issueType == "Select issue type") {
                Toast.makeText(requireContext(), "Please select an issue type", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                Toast.makeText(requireContext(), "Please describe your issue", Toast.LENGTH_SHORT).show()
            } else if (description.length > 300) {
                Toast.makeText(requireContext(), "Description too long", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(requireContext(), "Complaint submitted successfully", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
        }

        sheetBinding.btnSelectImage.setOnClickListener {
            Toast.makeText(requireContext(), "Image selection coming soon", Toast.LENGTH_SHORT).show()
        }

        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
