package com.example.freshyzoappmodule.ui.Fragments.subscriptionTab

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.SubscriptionResponse
import com.example.freshyzoappmodule.databinding.FragmentPauseSubscriptionBinding
import com.example.freshyzoappmodule.ui.adapter.SubscriptionStatusAdapter
import com.example.freshyzoappmodule.viewmodel.SubscriptionStatusViewModel
import org.koin.androidx.viewmodel.ext.android.activityViewModel
class PauseSubscriptionFragment : Fragment() {

    private lateinit var _binding: FragmentPauseSubscriptionBinding
    private val binding get() = _binding!!

    // Shared ViewModel between tabs using Koin
    private val viewModel: SubscriptionStatusViewModel by activityViewModel()

    // Adapter
    private val adapter = SubscriptionStatusAdapter(
        emptyList(),
        onPauseClick = { item ->
            viewModel.pauseSubscription(item)
            Toast.makeText(requireContext(), "Paused", Toast.LENGTH_SHORT).show()
        },
        onCancelClick = { item ->
            viewModel.cancelSubscription(item)
            Toast.makeText(requireContext(), "cancelled", Toast.LENGTH_SHORT).show()
        }
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPauseSubscriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()

        // Observe pause list
        viewModel.pauseList.observe(viewLifecycleOwner) { list ->

            if (list.isEmpty()) {

                binding.recyclerSubscriptions.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE

            } else {

                binding.recyclerSubscriptions.visibility = View.VISIBLE
                binding.layoutEmpty.visibility = View.GONE

                adapter.updateList(list)
            }
        }

        // For testing UI

    }

    private fun setupRecyclerView() {
        binding.recyclerSubscriptions.layoutManager =
            LinearLayoutManager(requireContext())
        binding.recyclerSubscriptions.adapter = adapter
        adapter.updateList(getDummyData())
    }

    private fun getDummyData(): List<SubscriptionResponse> {
        return listOf(
            SubscriptionResponse(
                "FreshyZo ghee 250g",
                "120",
                "Weekly",
                "10 Feb 2026",
                "Paused",
                "PAUSE",
                R.drawable.ghee
            )
        )
    }
}