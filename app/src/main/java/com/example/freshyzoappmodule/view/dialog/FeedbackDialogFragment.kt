package com.example.freshyzoappmodule.view.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.example.freshyzoappmodule.databinding.DialogFeedbackBinding

class FeedbackDialogFragment : DialogFragment() {

    private var _binding: DialogFeedbackBinding? = null
    private val binding get() = _binding!!

    private var feedbackListener: ((String) -> Unit)? = null
    private var selectedFeedback: String? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = DialogFeedbackBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Initial state
        binding.btnSubmit.isEnabled = false
        binding.emojiSad.alpha = 0.5f
        binding.emojiNeutral.alpha = 0.5f
        binding.emojiHappy.alpha = 0.5f

        binding.emojiSad.setOnClickListener {
            selectedFeedback = "normal"
            updateEmojiSelection(it)
        }

        binding.emojiNeutral.setOnClickListener {
            selectedFeedback = "happy"
            updateEmojiSelection(it)
        }

        binding.emojiHappy.setOnClickListener {
            selectedFeedback = "excellent"
            updateEmojiSelection(it)
        }

        binding.btnSubmit.setOnClickListener {
            selectedFeedback?.let { feedback ->
                feedbackListener?.invoke(feedback)
            }
            dismiss()
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun updateEmojiSelection(selectedView: View) {
        // Reset all emojis
        binding.emojiSad.alpha = 0.5f
        binding.emojiNeutral.alpha = 0.5f
        binding.emojiHappy.alpha = 0.5f

        // Highlight the selected one
        selectedView.alpha = 1.0f
        binding.btnSubmit.isEnabled = true
    }

    fun setFeedbackListener(listener: (String) -> Unit) {
        feedbackListener = listener
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
