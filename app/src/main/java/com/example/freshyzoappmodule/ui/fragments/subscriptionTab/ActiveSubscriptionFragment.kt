package com.example.freshyzoappmodule.ui.fragments.subscriptionTab

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.response.SubscriptionResponse
import com.example.freshyzoappmodule.databinding.FragmentActiveSubscriptionBinding
import com.example.freshyzoappmodule.ui.adapter.SubscriptionStatusAdapter
import com.example.freshyzoappmodule.ui.viewmodel.SubscriptionStatusViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class ActiveSubscriptionFragment : Fragment() {

    private lateinit var _binding: FragmentActiveSubscriptionBinding
    private val binding get() = _binding!!

    // Shared ViewModel between tabs using Koin
    private val viewModel: SubscriptionStatusViewModel by activityViewModel()

    // Adapter
    private val adapter = SubscriptionStatusAdapter(
        emptyList(),
        onPauseClick = { item ->
            viewModel.pauseSubscription(item)
        },
        onCancelClick = { item ->
            viewModel.cancelSubscription(item)
             })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentActiveSubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        // Observe active list

        viewModel.activeList.observe(viewLifecycleOwner) { list ->
            if (list.isEmpty()) {
                binding.recyclerSubscriptions.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
            } else {
                binding.recyclerSubscriptions.visibility = View.VISIBLE
                binding.layoutEmpty.visibility = View.GONE
                adapter.updateList(list)
            }
        }
//
    }

    private fun setupRecyclerView() {
        binding.recyclerSubscriptions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSubscriptions.adapter = adapter
        adapter.updateList(getDummyData())
    }

    private fun getDummyData(): List<SubscriptionResponse> {
        return listOf(
            SubscriptionResponse(
                "FreshyZo Cow Milk 1000 Ml",
                "₹60",
                "Everyday",
                "25 Feb 2026",
                "N/A",
                "ACTIVE",
                R.drawable.milk
            ),
            SubscriptionResponse(
                "FreshyZo Buffalo Milk 500 Ml",
                "₹55",
                "Alternate Day",
                "20 Feb 2026",
                "N/A",
                "ACTIVE",
                R.drawable.milk
            )
        )
    }

}