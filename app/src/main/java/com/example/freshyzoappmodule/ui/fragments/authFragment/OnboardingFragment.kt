package com.example.freshyzoappmodule.ui.fragments.authFragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.OnboardingItem
import com.example.freshyzoappmodule.databinding.FragmentOnboardingBinding
import com.example.freshyzoappmodule.ui.adapter.OnboardingAdapter
import com.google.android.material.tabs.TabLayoutMediator

class OnboardingFragment : Fragment() {

    private var _binding: FragmentOnboardingBinding? = null
    private val binding get() = _binding!!

    private lateinit var onboardingItems: List<OnboardingItem>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOnboardingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupOnboardingData()
        setupViewPager()
        setupClickListeners()
    }

    private fun setupOnboardingData() {
        onboardingItems = listOf(
            OnboardingItem(
                R.drawable.logo,
                "Easily track your orders",
                "Track deliveries, manage subscriptions, and order on the go with our user-friendly app."
            ),
            OnboardingItem(
                R.drawable.logo,
                "Subscription service",
                "Plan and schedule custom deliveries of the products you love. Stay consistent and stress-free!"
            ),
            OnboardingItem(
                R.drawable.logo,
                "Quality assurance",
                "Committed to delivering only the best-quality milk and dairy products for your family's health."
            ),
            OnboardingItem(
                R.drawable.logo,
                "Door-step delivery",
                "Enjoy freshly produced milk delivered to your doorstep, fast and hassle-free."
            )
        )
    }

    private fun setupViewPager() {
        val adapter = OnboardingAdapter(onboardingItems)
        binding.viewPager.adapter = adapter

        binding.dotsIndicator.attachTo(binding.viewPager)

        binding.viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateUIForPage(position)
            }
        })
    }

    private fun updateUIForPage(position: Int) {
        val isFirst = position == 0
        val isLast = position == onboardingItems.size - 1

        // Back button visibility with smooth fade
        binding.btnBackLayout.animate()
            .alpha(if (isFirst) 0f else 1f)
            .setDuration(200)
            .withStartAction {
                if (!isFirst) binding.btnBackLayout.visibility = View.VISIBLE
            }
            .withEndAction {
                if (isFirst) binding.btnBackLayout.visibility = View.GONE
            }
            .start()

        // Skip button hides on last page
        binding.btnSkipLayout.visibility = if (isLast) View.INVISIBLE else View.VISIBLE

        // Button label
        binding.btnNext.text = if (isLast) "Get Started" else "Continue"
    }

    private fun setupClickListeners() {
        binding.btnNext.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current < onboardingItems.size - 1) {
                binding.viewPager.currentItem = current + 1
            } else {
                navigateToLogin()
            }
        }

        binding.btnBackLayout.setOnClickListener {
            val current = binding.viewPager.currentItem
            if (current > 0) {
                binding.viewPager.currentItem = current - 1
            }
        }

        binding.btnSkipLayout.setOnClickListener {
            navigateToLogin()
        }
    }

    private fun navigateToLogin() {
        findNavController().navigate(R.id.action_onboardingFragment_to_loginFragment)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}