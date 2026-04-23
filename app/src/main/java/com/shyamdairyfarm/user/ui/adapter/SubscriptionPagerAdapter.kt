package com.shyamdairyfarm.user.ui.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.shyamdairyfarm.user.ui.fragments.subscriptionTab.ActiveSubscriptionFragment
import com.shyamdairyfarm.user.ui.fragments.subscriptionTab.CancelledSubscriptionFragment
import com.shyamdairyfarm.user.ui.fragments.subscriptionTab.PauseSubscriptionFragment

class SubscriptionPagerAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = listOf(
        ActiveSubscriptionFragment(),
        PauseSubscriptionFragment(),
        CancelledSubscriptionFragment()
    )

    override fun getItemCount(): Int = fragments.size

    override fun createFragment(position: Int): Fragment = fragments[position]
}