package com.example.freshyzoappmodule.ui.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.example.freshyzoappmodule.ui.Fragments.subscriptionTab.ActiveSubscriptionFragment
import com.example.freshyzoappmodule.ui.Fragments.subscriptionTab.CancelledSubscriptionFragment
import com.example.freshyzoappmodule.ui.Fragments.subscriptionTab.PauseSubscriptionFragment

class SubscriptionPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = listOf(
        ActiveSubscriptionFragment(),
        PauseSubscriptionFragment(),
        CancelledSubscriptionFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}