package com.example.freshyzoappmodule.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.api.RetrofitClient
import com.example.freshyzoappmodule.data.model.OrderHistoryModel
import com.example.freshyzoappmodule.data.repository.OrderHistoryRepository
import com.example.freshyzoappmodule.databinding.FragmentOrdersHistoryBinding
import com.example.freshyzoappmodule.ui.adapter.OrderHistoryAdapter
import com.example.freshyzoappmodule.ui.viewmodel.OrderHistoryViewModel
import com.example.freshyzoappmodule.ui.viewmodel.factory.OrderHistoryViewModelFactory
class OrderHistoryFragment : Fragment() {
    private var _binding: FragmentOrdersHistoryBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewModel: OrderHistoryViewModel
    private lateinit var adapter: OrderHistoryAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrdersHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        setupViewModel()
        setupRecyclerView()
        setupFilterChips()
        setupObservers()
        setupClickListeners()
        
        viewModel.fetchOrderHistory()
    }

    private fun setupViewModel() {
        val apiService = RetrofitClient.api
        val repository = OrderHistoryRepository(apiService)
        val factory = OrderHistoryViewModelFactory(repository)
        viewModel = ViewModelProvider(this, factory)[OrderHistoryViewModel::class.java]
    }

    private fun setupRecyclerView() {
        adapter = OrderHistoryAdapter { delivery ->
            onDeliveryClicked(delivery)
        }

        binding.rvDeliveries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = this@OrderHistoryFragment.adapter
            itemAnimator = null
            setHasFixedSize(false)
        }
    }

    private fun setupFilterChips() {
        chipViews.forEach { (filter, chip) ->
            chip.setOnClickListener {
                viewModel.applyFilter(filter)
            }
        }
    }

    private fun setupObservers() {
        viewModel.filteredDeliveries.observe(viewLifecycleOwner) { list ->
            adapter.submitList(list)
            binding.emptyState.visibility = if (list.isNullOrEmpty()) View.VISIBLE else View.GONE
            binding.rvDeliveries.visibility = if (list.isNullOrEmpty()) View.GONE else View.VISIBLE
        }

        viewModel.activeFilter.observe(viewLifecycleOwner) { activeFilter ->
            updateChipStyles(activeFilter)
        }

        viewModel.stats.observe(viewLifecycleOwner) { stats ->
            binding.tvStatAll.text = stats.total.toString()
            binding.tvStatPlaced.text = stats.placed.toString()
            binding.tvStatPending.text = stats.pending.toString()
            binding.tvStatCancelled.text = stats.cancelled.toString()
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            // You can add a progress bar here if needed
            // binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.error.observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateChipStyles(activeFilter: String) {
        val ctx = requireContext()
        chipViews.forEach { (filter, chip) ->
            if (filter.equals(activeFilter, ignoreCase = true) || (filter == "all" && activeFilter == "all")) {
                chip.setTextColor(ContextCompat.getColor(ctx, R.color.white))
                chip.setBackgroundResource(
                    when (filter) {
                        "Placed" -> R.drawable.bg_chip_active_green
                        "Pending" -> R.drawable.bg_chip_active_amber
                        "Cancelled" -> R.drawable.bg_chip_active_coral
                        else -> R.drawable.bg_chip_active_green
                    }
                )
            } else {
                chip.setTextColor(ContextCompat.getColor(ctx, R.color.text_muted))
                chip.setBackgroundResource(R.drawable.bg_chip_inactive)
            }
        }
    }

    private fun onDeliveryClicked(delivery: OrderHistoryModel) {
        // Replace with your actual navigation action
        // val action = OrderHistoryFragmentDirections.actionMyOrdersFragmentToOrderDetailsFragment(delivery)
        // findNavController().navigate(action)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private val chipViews: Map<String, TextView>
        get() = mapOf(
            "all" to binding.chipAll,
            "Placed" to binding.chipPlaced,
            "Pending" to binding.chipPending,
            "Cancelled" to binding.chipCancelled
        )
}
