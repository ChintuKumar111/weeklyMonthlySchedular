package com.shyamdairyfarm.user.ui.fragments.subscriptionTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.data.model.response.SubscriptionResponse
import com.shyamdairyfarm.user.databinding.FragmentCancelledSubscriptionBinding
import com.shyamdairyfarm.user.ui.adapter.SubscriptionStatusAdapter
import com.shyamdairyfarm.user.ui.viewmodel.SubscriptionStatusViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel

class CancelledSubscriptionFragment : Fragment() {

    private lateinit var _binding: FragmentCancelledSubscriptionBinding
    private val binding get() = _binding!!

    // Shared ViewModel between tabs using Koin
    private val viewModel: SubscriptionStatusViewModel by activityViewModel()

    private lateinit var adapter: SubscriptionStatusAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentCancelledSubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = SubscriptionStatusAdapter(
            emptyList(),
            onPauseClick = { item ->
              viewModel.pauseSubscription(item)
            },
            onCancelClick = { item ->
                viewModel.cancelSubscription(item)
            }
        )

        setupRecyclerView()
        // Observe pause list
        viewModel.cancelList.observe(viewLifecycleOwner) { list ->

            if (list.isEmpty()) {

                binding.recyclerSubscriptions.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE

            } else {

                binding.recyclerSubscriptions.visibility = View.VISIBLE
                binding.layoutEmpty.visibility = View.GONE

                adapter.updateList(list)
            }
        }

    }

    private fun setupRecyclerView() {
        binding.recyclerSubscriptions.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerSubscriptions.adapter = adapter
        adapter.updateList(getDummyData())
    }

    private fun getDummyData(): List<SubscriptionResponse> {
        return listOf(
            SubscriptionResponse(
                "FreshyZo ghee 250g",
                "₹120",
                "Weekly",
                "10 Feb 2026",
                "Cancelled on 5 Mar 2026",
                "CANCEL",
                R.drawable.ghee
            )
        )
    }
}