package com.example.freshyzoappmodule.ui.fragments.authFragment

import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.FragmentLoginBinding
import com.google.firebase.FirebaseException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthOptions
import com.google.firebase.auth.PhoneAuthProvider
import java.util.concurrent.TimeUnit

class LoginFragment : Fragment() {

    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private var isSignUpMode = false
    private lateinit var auth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        updateUiMode()
        setupTermsText()
        setupClickListeners()
        setupTextWatchers()
        validateInput()
    }

    private fun setupTextWatchers() {
        binding.tilPhone.editText?.doAfterTextChanged { validateInput() }
        binding.tilFullName.editText?.doAfterTextChanged { validateInput() }
    }

    private fun validateInput() {
        val phone = binding.tilPhone.editText?.text?.toString()?.trim() ?: ""
        val isPhoneValid = phone.length == 10
        val isNameValid = if (isSignUpMode) {
            binding.tilFullName.editText?.text?.toString()?.trim()?.isNotEmpty() == true
        } else true

        val isValid = isPhoneValid && isNameValid
        binding.btnContinue.isEnabled = isValid
        binding.btnContinue.alpha = if (isValid) 1.0f else 0.6f
    }

    private fun setupClickListeners() {
        binding.btnContinue.setOnClickListener {
            val phone = binding.tilPhone.editText?.text?.toString()?.trim() ?: ""
            // FOR TESTING ONLY: Skip Firebase and go straight to OtpFragment
            val action = LoginFragmentDirections.actionLoginFragmentToOtpFragment(phone, "TEST_VERIFICATION_ID")
            findNavController().navigate(action)
            
            // Commenting out Firebase OTP code
            /*
            startFirebaseOtp(phone)
            */
        }

        binding.tvLogin.setOnClickListener {
            isSignUpMode = !isSignUpMode
            updateUiMode()
            validateInput()
        }
    }

//    private fun startFirebaseOtp(phone: String) {
//        binding.btnContinue.isEnabled = false
//        binding.btnContinue.text = "Sending..."
//        Log.d("FirebaseAuth", "Starting verification for: +91$phone")
//
//        val options = PhoneAuthOptions.newBuilder(auth)
//            .setPhoneNumber("+91$phone")
//            .setTimeout(60L, TimeUnit.SECONDS)
//            .setActivity(requireActivity())
//            .setCallbacks(object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
//                override fun onVerificationCompleted(credential: PhoneAuthCredential) {
//                    Log.d("FirebaseAuth", "Verification Completed automatically. Code: ${credential.smsCode}")
//                    binding.btnContinue.isEnabled = true
//                    binding.btnContinue.text = "Continue"
//
//                    // If SMS code is available, you can use it to sign in automatically
//                    credential.smsCode?.let {
//                        Log.d("FirebaseAuth", "Auto-retrieved SMS code: $it")
//                    }
//                }
//
//                override fun onVerificationFailed(e: FirebaseException) {
//                    Log.e("FirebaseAuth", "Verification Failed", e)
//                    binding.btnContinue.isEnabled = true
//                    binding.btnContinue.text = "Continue"
//                    Toast.makeText(requireContext(), "Verification Failed: ${e.message}", Toast.LENGTH_LONG).show()
//                }
//
//                override fun onCodeSent(verificationId: String, token: PhoneAuthProvider.ForceResendingToken) {
//                    Log.d("FirebaseAuth", "onCodeSent: $verificationId")
//                    binding.btnContinue.isEnabled = true
//                    binding.btnContinue.text = "Continue"
//                    Toast.makeText(requireContext(), "OTP Sent", Toast.LENGTH_SHORT).show()
//
//                    val action = LoginFragmentDirections.actionLoginFragmentToOtpFragment(phone, verificationId)
//                    findNavController().navigate(action)
//                }
//            })
//            .build()
//        PhoneAuthProvider.verifyPhoneNumber(options)
//    }

    private fun updateUiMode() {
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
        spannable.setSpan(ForegroundColorSpan(primaryColor), termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannable.setSpan(object : ClickableSpan() {
            override fun onClick(widget: View) {
                Toast.makeText(requireContext(), "Terms of Service", Toast.LENGTH_SHORT).show()
            }
        }, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        val privacyStart = fullText.indexOf("Privacy Policy")
        val privacyEnd = privacyStart + "Privacy Policy".length
        spannable.setSpan(ForegroundColorSpan(primaryColor), privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
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