package com.shyamdairyfarm.user.ui.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.databinding.BottomSheetComplaintBinding
import com.shyamdairyfarm.user.databinding.FragmentComplaintAssistanceCenterBinding
import com.google.android.material.bottomsheet.BottomSheetDialog

class ComplaintAssistanceCenterFragment : Fragment() {

    private var _binding: FragmentComplaintAssistanceCenterBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private var currentSheetBinding: BottomSheetComplaintBinding? = null
    private var currentDialog: BottomSheetDialog? = null

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            selectedImageUri = data?.data
            updateImagePreview()
        }
    }

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
    }

    private fun showComplaintBottomSheet(title: String) {
        val dialog = BottomSheetDialog(requireContext())
        currentDialog = dialog
        val sheetBinding = BottomSheetComplaintBinding.inflate(layoutInflater)
        currentSheetBinding = sheetBinding
        dialog.setContentView(sheetBinding.root)

        // Reset state for new sheet
        selectedImageUri = null
        updateImagePreview()

        sheetBinding.tvSheetTitle.text = title

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
            val wrapper = android.view.ContextThemeWrapper(requireContext(), R.style.WhitePopupMenu)
            val popup = PopupMenu(wrapper, it)
            issues.forEach { issue -> popup.menu.add(issue) }
            popup.setOnMenuItemClickListener { item ->
                sheetBinding.tvSelectedIssue.text = item.title
                sheetBinding.tvSelectedIssue.setTextColor(resources.getColor(R.color.black, null))
                true
            }
            popup.show()
        }

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

        sheetBinding.btnSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            pickImageLauncher.launch(intent)
        }

        sheetBinding.btnRemoveImage.setOnClickListener {
            selectedImageUri = null
            updateImagePreview()
        }

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

        dialog.show()
    }

    private fun updateImagePreview() {
        currentSheetBinding?.let { binding ->
            if (selectedImageUri != null) {
                binding.cvImagePreview.visibility = View.VISIBLE
                binding.ivSelectedImage.setImageURI(selectedImageUri)
                binding.btnSelectImage.visibility = View.GONE
            } else {
                binding.cvImagePreview.visibility = View.GONE
                binding.btnSelectImage.visibility = View.VISIBLE
            }
        }
    }

    override fun onDestroyView() {
        currentDialog?.dismiss()
        currentDialog = null
        super.onDestroyView()
        _binding = null
        currentSheetBinding = null
    }
}
