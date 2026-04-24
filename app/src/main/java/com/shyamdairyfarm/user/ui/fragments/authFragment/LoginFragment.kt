package com.shyamdairyfarm.user.ui.fragments.authFragment

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.databinding.FragmentLoginBinding
import com.shyamdairyfarm.user.ui.viewmodel.AuthViewModel
import com.shyamdairyfarm.user.utils.UiState
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val viewModel: AuthViewModel by viewModel()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
        setupTermsText()
        setupClickListeners()
        setupTextWatchers()
        validateInput()
    }

    private fun observeViewModel() {
        viewModel.isSignUpMode.observe(viewLifecycleOwner) { isSignUp ->
            updateUiMode(isSignUp)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.requestOtpState.collect { state ->
                    when (state) {

                        UiState.Idle -> Unit

                        UiState.Loading -> {
                            Toast.makeText(
                                requireContext(),
                                "Sending OTP...",
                                Toast.LENGTH_SHORT
                            ).show()
                        }

                        is UiState.Success -> {

                            val phone = binding.tilPhone.editText
                                ?.text?.toString()?.trim().orEmpty()

                            val action =
                                LoginFragmentDirections
                                    .actionLoginFragmentToOtpFragment(
                                        phone,
                                        viewModel.verificationId.value ?: ""
                                    )

                            findNavController().navigate(action)

                            // ✅ Reset state to avoid re-trigger
                            viewModel.resetOtpRequestState()
                        }

                        is UiState.Error -> {
                            Toast.makeText(
                                requireContext(),
                                state.message,
                                Toast.LENGTH_LONG
                            ).show()

                            // optional reset
                            viewModel.resetOtpRequestState()
                        }
                    }
                }
            }
        }
    }

    private fun setupTextWatchers() {
        binding.tilPhone.editText?.doAfterTextChanged { validateInput() }
        binding.tilFullName.editText?.doAfterTextChanged { validateInput() }
    }

    private fun validateInput() {
        val phone = binding.tilPhone.editText?.text?.toString()?.trim() ?: ""
        val isPhoneValid = phone.length == 10
        val isSignUp = viewModel.isSignUpMode.value ?: false
        val isNameValid = if (isSignUp) {
            binding.tilFullName.editText?.text?.toString()?.trim()?.isNotEmpty() == true
        } else true

        val isValid = isPhoneValid && isNameValid
        binding.btnContinue.isEnabled = isValid
        binding.btnContinue.alpha = if (isValid) 1.0f else 0.6f
    }

    private fun setupClickListeners() {
        binding.btnContinue.setOnClickListener {
            val phone = binding.tilPhone.editText?.text?.toString()?.trim() ?: ""
            viewModel.setPhoneNumber(phone)

            // FOR TESTING ONLY: Skip Firebase and go straight to OtpFragment

//            val action =
//                LoginFragmentDirections.actionLoginFragmentToOtpFragment(phone, "PRODUCTION_ID")
//            val action = LoginFragmentDirections.actionLoginFragmentToOtpFragment(phone, "TEST_VERIFICATION_ID")
            viewModel.requestOtp()
//            findNavController().navigate(action)
        }

        binding.tvLogin.setOnClickListener {
            viewModel.toggleSignUpMode()
            validateInput()
        }
    }

    private fun updateUiMode(isSignUpMode: Boolean) {
        if (isSignUpMode) {
            binding.tilFullName.visibility = View.VISIBLE
            binding.tvLoginPrompt.text = "Already have an account? "
            binding.tvLogin.text = "Login"
        } else {
            binding.tilFullName.visibility = View.GONE
            binding.tvLoginPrompt.text = "Don't have an account? "
            binding.tvLogin.text = "Sign up"
        }
    }

    private fun setupTermsText() {
        val fullText = "By continuing, you agree to our Terms and Privacy Policy"
        val spannable = SpannableString(fullText)
        val primaryColor = ContextCompat.getColor(requireContext(), R.color.primary)

        val termsStart = fullText.indexOf("Terms")
        val termsEnd = termsStart + "Terms".length
        spannable.setSpan(
            ForegroundColorSpan(primaryColor),
            termsStart,
            termsEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireContext(), "Terms of Service", Toast.LENGTH_SHORT).show()
            }
        }, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val privacyStart = fullText.indexOf("Privacy Policy")
        val privacyEnd = privacyStart + "Privacy Policy".length
        spannable.setSpan(
            ForegroundColorSpan(primaryColor),
            privacyStart,
            privacyEnd,
            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireContext(), "Privacy Policy", Toast.LENGTH_SHORT).show()
            }
        }, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        binding.tvTerms.text = spannable
        binding.tvTerms.movementMethod = LinkMovementMethod.getInstance()
        binding.tvTerms.highlightColor = android.graphics.Color.TRANSPARENT
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}