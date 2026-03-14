package com.example.freshyzoappmodule.ui.fragments

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.FragmentProfileBinding
import com.example.freshyzoappmodule.data.manager.PreferenceManager
import java.util.Calendar
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var selectedImageUri: Uri? = null
    private var datePickerDialog: DatePickerDialog? = null

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data: Intent? = result.data
            selectedImageUri = data?.data
            selectedImageUri?.let {
                Glide.with(this)
                    .load(it)
                    .circleCrop()
                    .into(binding.ivAvatar)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        loadProfileData()

        binding.ivAvatar.setOnClickListener {
            openGallery()
        }

        binding.etDob.setOnClickListener {
            showDatePicker()
        }
        
        binding.cardDob.setOnClickListener {
            showDatePicker()
        }

        binding.btnSave.setOnClickListener {
            saveProfileChanges()
        }
    }

    private fun loadProfileData() {
        val savedName = PreferenceManager.getUserName(requireContext())
        val savedImageUri = PreferenceManager.getProfileImage(requireContext())
        val savedDob = PreferenceManager.getUserDob(requireContext())

        binding.etFirstName.setText(savedName)
        binding.tvName.text = savedName
        binding.etDob.setText(savedDob)

        if (savedImageUri != null) {
            Glide.with(this)
                .load(Uri.parse(savedImageUri))
                .placeholder(R.drawable.milk_)
                .circleCrop()
                .into(binding.ivAvatar)
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        
        // Try to parse current date from text
        val currentText = binding.etDob.text.toString()
        if (currentText.isNotEmpty()) {
            try {
                val parts = currentText.split(" / ")
                if (parts.size == 3) {
                    calendar.set(Calendar.DAY_OF_MONTH, parts[0].toInt())
                    calendar.set(Calendar.MONTH, parts[1].toInt() - 1)
                    calendar.set(Calendar.YEAR, parts[2].toInt())
                }
            } catch (e: Exception) {}
        }

        val dialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = String.format(Locale.getDefault(), "%02d / %02d / %d", dayOfMonth, month + 1, year)
                binding.etDob.setText(selectedDate)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog = dialog
        dialog.show()
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "image/*"
        }
        pickImageLauncher.launch(intent)
    }

    private fun saveProfileChanges() {
        val newName = binding.etFirstName.text.toString().trim()
        val newDob = binding.etDob.text.toString().trim()

        if (newName.isEmpty()) {
            Toast.makeText(requireContext(), "Name cannot be empty", Toast.LENGTH_SHORT).show()
            return
        }

        PreferenceManager.saveUserName(requireContext(), newName)
        PreferenceManager.saveUserDob(requireContext(), newDob)
        binding.tvName.text = newName

        selectedImageUri?.let { uri ->
            try {
                requireContext().contentResolver.takePersistableUriPermission(
                    uri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            } catch (e: Exception) {
                e.printStackTrace()
            }
            PreferenceManager.saveProfileImage(requireContext(), uri.toString())
        }
        Toast.makeText(requireContext(), "Changes saved successfully", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        datePickerDialog?.dismiss()
        datePickerDialog = null
        super.onDestroyView()
        _binding = null
    }
}
