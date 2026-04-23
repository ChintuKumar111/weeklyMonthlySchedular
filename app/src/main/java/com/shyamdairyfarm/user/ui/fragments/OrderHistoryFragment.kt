package com.shyamdairyfarm.user.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.data.model.OrderHistoryModel
import com.shyamdairyfarm.user.databinding.FragmentOrdersHistoryBinding
import com.shyamdairyfarm.user.ui.adapter.UserOrderHistoryAdapter
import com.shyamdairyfarm.user.ui.viewmodel.OrderHistoryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class OrderHistoryFragment : Fragment() {
    private var _binding: FragmentOrdersHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderHistoryViewModel by viewModel()
    private lateinit var adapter: UserOrderHistoryAdapter

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
        
        setupRecyclerView()
        setupFilterChips()
        setupObservers()
        setupClickListeners()
        
        viewModel.fetchOrderHistory()
    }

    private fun setupRecyclerView() {
        adapter = UserOrderHistoryAdapter { delivery ->
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
        val bundle = Bundle().apply {
            putParcelable("delivery", delivery)
        }
        findNavController().navigate(R.id.action_myOrdersFragment_to_orderDetailsFragment, bundle)
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
