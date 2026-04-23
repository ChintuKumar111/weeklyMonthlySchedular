package com.shyamdairyfarm.user.ui.fragments.authFragment

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
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.databinding.FragmentOtpBinding
import com.shyamdairyfarm.user.ui.viewmodel.AuthViewModel
import com.google.android.gms.auth.api.phone.SmsRetriever
import com.google.android.gms.common.api.CommonStatusCodes
import com.google.android.gms.common.api.Status
import com.google.firebase.auth.PhoneAuthProvider
import org.koin.androidx.viewmodel.ext.android.viewModel
import kotlin.concurrent.thread

class OtpFragment : Fragment() {

    private var _binding: FragmentOtpBinding? = null
    private val binding get() = _binding!!
    private val args: OtpFragmentArgs by navArgs()
    private var countDownTimer: CountDownTimer? = null
    private val viewModel: AuthViewModel by viewModel()

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
            if ("OTP_RECEIVED" == intent?.action) {
                val otp = intent.getStringExtra("otp")
                otp?.let {
                    binding.pinView.setText(it)
                    Log.d("OtpFragment", "OTP Filled from internal receiver: $it")
                    verifyOtp(it)
                }
                return
            }

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
                        val message = extras?.getString(SmsRetriever.EXTRA_SMS_MESSAGE)
                        if (message != null) {
                            extractOtpAndFill(message)
                        } else {
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
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.setPhoneNumber(args.phoneNumber)
        viewModel.setVerificationId(args.verificationId)

        observeViewModel()
        startSmsRetriever()
        startSmsUserConsent()
        registerSmsReceiver()

        binding.tvOtpSent.text = getString(R.string.otp_sent_to_v2, args.phoneNumber)

        setupClickListeners()
        startTimer()
    }

    private fun observeViewModel() {
        viewModel.authState.observe(viewLifecycleOwner) { state ->
            when (state) {
                is AuthViewModel.AuthState.Loading -> {
                    binding.btnVerify.isEnabled = false
                    binding.loadingOverlay.visibility = View.VISIBLE
                }
                is AuthViewModel.AuthState.Success -> {
                    // Keep loader visible during transition
                    binding.loadingOverlay.visibility = View.VISIBLE
                    handleLoginSuccess()
                }
                is AuthViewModel.AuthState.Error -> {
                    binding.btnVerify.isEnabled = true
                    binding.loadingOverlay.visibility = View.GONE
                    Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                }
                else -> {
                    binding.btnVerify.isEnabled = true
                    binding.loadingOverlay.visibility = View.GONE
                }
            }
        }
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
            ContextCompat.RECEIVER_NOT_EXPORTED
        )
    }

    private fun extractOtpAndFill(message: String) {
        val otpRegex = Regex("(\\d{6})")
        val match = otpRegex.find(message)
        match?.let {
            val otp = it.value
            binding.pinView.setText(otp)
            verifyOtp(otp)
        }
    }

    private fun verifyOtp(otp: String) {
        if (args.verificationId == "TEST_VERIFICATION_ID") {
            binding.loadingOverlay.visibility = View.VISIBLE
            handleLoginSuccess()
        } else {
            val credential = PhoneAuthProvider.getCredential(args.verificationId, otp)
            viewModel.signInWithCredential(credential)
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
                verifyOtp(otp)
            } else {
                binding.pinView.error = getString(R.string.enter_6_digit_otp)
            }
        }
    }

    private fun handleLoginSuccess() {
        // Run navigation logic on a background thread if there's any data processing,
        // but startActivity must be on main thread. Using thread + runOnUiThread 
        // to simulate a non-blocking flow and ensuring the loader stays up.
        thread {
            // Optional: Add small artificial delay if you want the loader to be seen
            // Thread.sleep(300) 
            
            activity?.runOnUiThread {
                if (isAdded) {
                    val intent = Intent(requireActivity(), com.shyamdairyfarm.user.ui.activity.HomeActivity::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                    
                    // Crossfade hides the activity switching gap
                    requireActivity().overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out)
                    requireActivity().finish()
                }
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
