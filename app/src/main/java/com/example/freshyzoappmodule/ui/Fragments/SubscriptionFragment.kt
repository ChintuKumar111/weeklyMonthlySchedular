package com.example.freshyzoappmodule.ui.Fragments

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.freshyzoappmodule.databinding.FragmentSubscriptionBinding
import com.example.freshyzoappmodule.ui.adapter.SubscriptionPagerAdapter
import com.google.android.material.tabs.TabLayoutMediator

class SubscriptionFragment : Fragment() {
private lateinit var _binding: FragmentSubscriptionBinding
    private val binding get() = _binding!!
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,savedInstanceState: Bundle?): View? {
        _binding = FragmentSubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    @SuppressLint("SuspiciousIndentation")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

 binding.viewPager.adapter = SubscriptionPagerAdapter(this)


// Link TabLayout and ViewPager2
        TabLayoutMediator(binding.tabLayout, binding.viewPager) { tab, position ->
            tab.text = when (position) {
                0 -> "Active"
                1 -> "Pause"
                else -> "Cancel"
            }
        }.attach()
    }

}