package com.example.freshyzoappmodule.ui.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.DeliveryModel
import com.example.freshyzoappmodule.databinding.FragmentMyOrdersBinding
import com.example.freshyzoappmodule.ui.adapter.DeliveriesAdapter
import com.example.freshyzoappmodule.viewmodel.DeliveriesViewModel

class DeliveriesFragment : Fragment() {

                    // ── View Binding ─────────────────────────────────────────
    private var _binding: FragmentMyOrdersBinding? = null
    private val binding get() = _binding!!

    // ── ViewModel ────────────────────────────────────────────
    private val viewModel: DeliveriesViewModel by viewModels()

    // ── Adapter ──────────────────────────────────────────────
    private lateinit var adapter: DeliveriesAdapter

                    // ── Chip references ──────────────────────────────────────
    private val chipViews: Map<String, TextView>
        get() = mapOf(

            "all" to binding.chipAll,
            "Placed" to binding.chipPlaced,
            "Pending" to binding.chipPending,
            "Cancelled" to binding.chipCancelled
        )

                    // ────────────────────────────────────────────────────────
                    override fun onCreateView(
                        inflater: LayoutInflater,
                        container: ViewGroup?,
                        savedInstanceState: Bundle?
                    ): View {
                        _binding = FragmentMyOrdersBinding.inflate(inflater, container, false)
                        return binding.root
                    }

                    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
                        super.onViewCreated(view, savedInstanceState)

                        setupRecyclerView()
                        setupFilterChips()
                        setupObservers()
                        setupClickListeners()
                    }

                    // ── RecyclerView ─────────────────────────────────────────
                    private fun setupRecyclerView() {
                        adapter = DeliveriesAdapter { delivery ->
                            onDeliveryClicked(delivery)
                        }

                        binding.rvDeliveries.apply {
                            layoutManager = LinearLayoutManager(requireContext())
                            adapter = this@DeliveriesFragment.adapter
                            // Smooth scroll + no flicker on updates
                            itemAnimator = null
                            setHasFixedSize(false)
                        }
                    }

                    // ── Filter chips ─────────────────────────────────────────
                    private fun setupFilterChips() {
                        chipViews.forEach { (filter, chip) ->
                            chip.setOnClickListener {
                                viewModel.applyFilter(filter)
                            }
                        }
                    }

                    // ── Observe LiveData ─────────────────────────────────────
                    private fun setupObservers() {

                        // Filtered list → update RecyclerView
                        viewModel.filteredDeliveries.observe(viewLifecycleOwner) { list ->
                            adapter.submitList(list)

//                            // Show / hide empty state
//                            binding.emptyState.visibility =
//                                if (list.isEmpty()) View.VISIBLE else View.GONE
                            binding.rvDeliveries.visibility =
                                if (list.isEmpty()) View.GONE else View.VISIBLE
                        }

                        // Active filter → update chip styles
                        viewModel.activeFilter.observe(viewLifecycleOwner) { activeFilter ->
                            updateChipStyles(activeFilter)
                        }

                        // Stats → update header strip
                        viewModel.stats.observe(viewLifecycleOwner) { stats ->
                            binding.tvStatAll.text = stats.total.toString()
                            binding.tvStatPlaced.text = stats.placed.toString()
                            binding.tvStatPending.text = stats.pending.toString()
                            binding.tvStatCancelled.text = stats.cancelled.toString()

                        }
                    }

                    // ── Click listeners ───────────────────────────────────────
                    private fun setupClickListeners() {
                        // Back button
                        binding.btnBack.setOnClickListener {
                            requireActivity().onBackPressedDispatcher.onBackPressed()
                        }


                    }

                    // ── Chip visual state ─────────────────────────────────────
                    private fun updateChipStyles(activeFilter: String) {
                        val ctx = requireContext()

                        chipViews.forEach { (filter, chip) ->
                            if (filter == activeFilter) {
                                // Active style
                                chip.setTextColor(ContextCompat.getColor(ctx, R.color.white))
                                chip.setBackgroundResource(
                                    when (filter) {
                                        "Placed" -> R.drawable.bg_chip_active_green
                                        "Pending" -> R.drawable.bg_chip_active_amber
                                        "Cancelled" -> R.drawable.bg_chip_active_coral
                                        else -> R.drawable.bg_chip_active_green // "all"
                                    }
                                )
                            } else {
                                // Inactive style
                                chip.setTextColor(ContextCompat.getColor(ctx, R.color.text_muted))
                                chip.setBackgroundResource(R.drawable.bg_chip_inactive)
                            }
                        }
                    }

                    // ── Card click ────────────────────────────────────────────
                    private fun onDeliveryClicked(delivery: DeliveryModel) {
                        // Navigate to detail screen or show bottom sheet
                        // Example with NavController:
                        // val action = DeliveriesFragmentDirections
                        //     .actionDeliveriesFragmentToDeliveryDetailFragment(delivery.id)
                        // findNavController().navigate(action)

                        Toast.makeText(
                            requireContext(),
                            "Order #${delivery.txnId} — ${delivery.status.name}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }

                    // ── Cleanup ───────────────────────────────────────────────
                    override fun onDestroyView() {
                        super.onDestroyView()
                        _binding = null
                    }

                    companion object {
                        fun newInstance() = DeliveriesFragment()
                    }
                }

