package com.shyamdairyfarm.user.ui.fragments.signUp

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.shyamdairyfarm.user.databinding.FragmentSignUpUserBinding
import com.shyamdairyfarm.user.ui.activity.AuthActivity
import com.shyamdairyfarm.user.ui.viewmodel.AuthViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.androidx.viewmodel.ext.android.sharedViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class SignUpUserFragment : Fragment() {

    private var _binding: FragmentSignUpUserBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AuthViewModel by sharedViewModel()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        observeViewModel()
        setOnClickListener()
        enableButton()
        // ✍️ Input listener
        binding.etName.addTextChangedListener {

            println("Name changed $it")

            viewModel.updateName(it.toString())
            enableButton()
        }


        // 🎬 Lottie
        binding.lottieView.playAnimation()
    }

    private fun setOnClickListener() {

        // 🔙 Back
        binding.btnBack.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finishAffinity() // safer for flows
        }
        // ➡️ Button click
        binding.btnNext.setOnClickListener {

            if (!validateName()) return@setOnClickListener

            setNextLoading(true)

            binding.btnNext.postDelayed({

                val action =
                    SignUpUserFragmentDirections
                        .actionSignUpUserFragmentToSignUpAddressMapsFragment()

                findNavController().navigate(action)

            }, 100)
        }
        binding.btnWrongNumber.setOnClickListener {
            viewModel.logout()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finishAffinity()
        }
    }

    private fun validateName(): Boolean {
        val name = binding.etName.text.toString().trim()

        return when {
            name.isEmpty() -> {
                binding.tilName.error = "Name required"
                false
            }

            name.length < 3 -> {
                binding.tilName.error = "Minimum 3 characters"
                false
            }

            !name.matches(Regex("^[a-zA-Z0-9]+([ .,_-][a-zA-Z0-9]+)*$")) -> {
                binding.tilName.error =
                    "Use letters, numbers and . , - _ only (no consecutive symbols)"
                false
            }

            else -> {
                binding.tilName.error = null
                true
            }
        }
    }
    private fun setNextLoading(isLoading: Boolean) {
        binding.btnNext.isEnabled = !isLoading

        if (isLoading) {
            binding.btnNext.text = ""
            binding.progressBarNext.visibility = View.VISIBLE
            binding.btnNext.alpha = 0.7f
        } else {
            binding.btnNext.text = "Next"
            binding.progressBarNext.visibility = View.GONE
            binding.btnNext.alpha = 1f
        }
    }

    fun enableButton(){
        binding.btnNext.isEnabled = validateName()
    }
    private fun observeViewModel() {

        // 👀 Observe error
        viewModel.error.observe(viewLifecycleOwner) { error ->
            binding.tilName.error = error
        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewModel.phoneNumber.collectLatest { phone ->
                binding.txPhoneLabel.text = "+91 ${phone}"
                // use phone here
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}