package com.example.freshyzoappmodule.ui.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.DeliveryModel
import com.example.freshyzoappmodule.databinding.FragmentOrdersHistoryBinding
import com.example.freshyzoappmodule.ui.adapter.OrderHistoryAdapter
import com.example.freshyzoappmodule.viewmodel.OrderHistoryViewModel

class OrderHistoryFragment : Fragment() {
    private var _binding: FragmentOrdersHistoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: OrderHistoryViewModel by viewModels()
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
        setupRecyclerView()
        setupFilterChips()
        setupObservers()
        setupClickListeners()
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
            binding.emptyState.visibility = if (list.isEmpty()) View.VISIBLE else View.GONE
            binding.rvDeliveries.visibility = if (list.isEmpty()) View.GONE else View.VISIBLE
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
    }

    private fun setupClickListeners() {
        binding.btnBack.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun updateChipStyles(activeFilter: String) {
        val ctx = requireContext()
        chipViews.forEach { (filter, chip) ->
            if (filter == activeFilter) {
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

    private fun onDeliveryClicked(delivery: DeliveryModel) {
        val action = OrderHistoryFragmentDirections.actionMyOrdersFragmentToOrderDetailsFragment(delivery)
        findNavController().navigate(action)
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
