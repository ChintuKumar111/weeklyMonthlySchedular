package com.example.freshyzoappmodule.ui.fragments.authFragment

import android.app.Activity
import android.content.ActivityNotFoundException
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.os.CountDownTimer
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.FragmentOtpBinding
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.PhoneAuthCredential
import com.google.firebase.auth.PhoneAuthProvider

class OtpFragment : Fragment() {
    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private val args: OtpFragmentArgs by navArgs()
    private var countDownTimer: CountDownTimer? = null
    private lateinit var auth: FirebaseAuth
    private val smsConsentLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            val message = result.data?.getStringExtra(SmsRetriever.EXTRA_SMS_MESSAGE)
            Log.d("OtpFragment", "SMS Message received: $message")
            message?.let { extractOtpAndFill(it) }
        }
    }

    private val smsVerificationReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            // Handle internal broadcast from SmsReceiver.kt (Silent Auto-fill)
            if ("OTP_RECEIVED" == intent?.action) {
                val otp = intent.getStringExtra("otp")
                otp?.let {
                    binding.pinView.setText(it)
                    Log.d("OtpFragment", "OTP Filled from internal receiver: $it")
                    verifyFirebaseOtp(it)
                }
                return
            }

            // Handle Google Play Services SMS Retriever/User Consent
            if (SmsRetriever.SMS_RETRIEVED_ACTION == intent?.action) {
                val extras = intent.extras
                val smsRetrieverStatus = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    extras?.getParcelable(SmsRetriever.EXTRA_STATUS, Status::class.java)
                } else {
                    @Suppress("DEPRECATION")
                    extras?.get(SmsRetriever.EXTRA_STATUS) as? Status
                }

                when (smsRetrieverStatus?.statusCode) {
                    CommonStatusCodes.SUCCESS -> {
                        // Check if it's the silent SMS Retriever API first (needs hash in SMS)
                        val message = extras?.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                        if (message != null) {
                            extractOtpAndFill(message)
                        } else {
                            // Fallback to User Consent API (shows popup)
                            val consentIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                                extras?.getParcelable(SmsRetriever.EXTRA_CONSENT_INTENT, Intent::class.java)
                            } else {
                                @Suppress("DEPRECATION")
                                extras?.getParcelable<Intent>(SmsRetriever.EXTRA_CONSENT_INTENT)
                            }
                            
                            try {
                                consentIntent?.let { smsConsentLauncher.launch(it) }
                            } catch (e: ActivityNotFoundException) {
                                Log.e("OtpFragment", "ActivityNotFoundException", e)
                            }
                        }
                    }
                    CommonStatusCodes.TIMEOUT -> {
                        Log.d("OtpFragment", "SMS Retriever Timeout")
                    }
                }
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOtpBinding.inflate(inflater, container, false)
        auth = FirebaseAuth.getInstance()
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Start both for maximum compatibility
        startSmsRetriever()
        startSmsUserConsent()
        registerSmsReceiver()

        val phoneNumber = args.phoneNumber
        binding.tvOtpSent.text = getString(R.string.otp_sent_to_v2, phoneNumber)

        setupClickListeners()
        startTimer()
    }

    private fun startSmsRetriever() {
        SmsRetriever.getClient(requireActivity()).startSmsRetriever()
            .addOnSuccessListener { Log.d("OtpFragment", "SmsRetriever started") }
            .addOnFailureListener { Log.e("OtpFragment", "SmsRetriever failed") }
    }

    private fun startSmsUserConsent() {
        SmsRetriever.getClient(requireActivity()).startSmsUserConsent(null)
            .addOnSuccessListener { Log.d("OtpFragment", "SmsUserConsent started") }
            .addOnFailureListener { Log.e("OtpFragment", "SmsUserConsent failed") }
    }

    private fun registerSmsReceiver() {
        val intentFilter = IntentFilter()
        intentFilter.addAction(SmsRetriever.SMS_RETRIEVED_ACTION)
        intentFilter.addAction("OTP_RECEIVED")
        
        ContextCompat.registerReceiver(
            requireContext(),
            smsVerificationReceiver,
            intentFilter,
            ContextCompat.RECEIVER_EXPORTED
        )
    }

    private fun extractOtpAndFill(message: String) {
        val otpRegex = Regex("(\\d{6})")
        val match = otpRegex.find(message)
        match?.let {
            val otp = it.value
            binding.pinView.setText(otp)
            Log.d("OtpFragment", "OTP Filled: $otp")
            
            if (args.verificationId == "TEST_VERIFICATION_ID") {
                handleTestLoginSuccess()
            } else {
                verifyFirebaseOtp(otp)
            }
        }
    }

    private fun setupClickListeners() {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.tvResend.setOnClickListener {
            binding.tvResend.visibility = View.GONE
            binding.tvTimer.visibility = View.VISIBLE
            startTimer()
            startSmsRetriever()
            startSmsUserConsent()
        }

        binding.btnVerify.setOnClickListener {
            val otp = binding.pinView.text.toString()
            if (otp.length == 6) {
                if (args.verificationId == "TEST_VERIFICATION_ID") {
                    handleTestLoginSuccess()
                } else {
                    verifyFirebaseOtp(otp)
                }
            } else {
                binding.pinView.error = getString(R.string.enter_6_digit_otp)
            }
        }
    }

    private fun handleTestLoginSuccess() {
        Log.d("OtpFragment", "Test Login Successful")
        Toast.makeText(requireContext(), "Test Login Successful", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireActivity(), com.example.freshyzoappmodule.ui.activity.HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        requireActivity().finish()
    }

    private fun verifyFirebaseOtp(otp: String) {
        val verificationId = args.verificationId
        if (verificationId == "TEST_VERIFICATION_ID") return

        if (verificationId.isEmpty()) {
            Toast.makeText(requireContext(), "Verification ID not found", Toast.LENGTH_SHORT).show()
            return
        }
        val credential = PhoneAuthProvider.getCredential(verificationId, otp)
        signInWithPhoneAuthCredential(credential)
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth.signInWithCredential(credential)
            .addOnCompleteListener(requireActivity()) { task ->
                if (task.isSuccessful) {
                    Toast.makeText(requireContext(), "Login Successful", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireActivity(), com.example.freshyzoappmodule.ui.activity.HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    requireActivity().finish()
                } else {
                    Toast.makeText(requireContext(), "Invalid OTP", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun startTimer() {
        countDownTimer?.cancel()
        countDownTimer = object : CountDownTimer(60000, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                val secondsRemaining = millisUntilFinished / 1000
                binding.tvTimer.text = getString(R.string.resend_otp_in, secondsRemaining.toInt())
            }

            override fun onFinish() {
                binding.tvTimer.visibility = View.GONE
                binding.tvResend.visibility = View.VISIBLE
            }
        }.start()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            requireActivity().unregisterReceiver(smsVerificationReceiver)
        } catch (e: Exception) {
        }
        countDownTimer?.cancel()
        _binding = null
    }
}