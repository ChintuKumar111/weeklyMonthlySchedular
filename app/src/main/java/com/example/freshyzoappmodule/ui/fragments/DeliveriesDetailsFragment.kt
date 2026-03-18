package com.example.freshyzoappmodule.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.data.model.Delivery
import com.example.freshyzoappmodule.databinding.FragmentDeliveriesDetailsBinding
import com.example.freshyzoappmodule.ui.adapter.DeliveryAdapter
import com.example.freshyzoappmodule.ui.viewmodel.DeliveryViewModel
import org.koin.androidx.viewmodel.ext.android.viewModel

class DeliveriesDetailsFragment : Fragment() {
    private var _binding: FragmentDeliveriesDetailsBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: DeliveryAdapter
    private val viewModel: DeliveryViewModel by viewModel()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDeliveriesDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        setupRecyclerView()
        observeViewModel()
        viewModel.fetchDeliveries()

        binding.ivBack?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }
//    private fun setupViewModel() {
//        val apiService = RetrofitClient.api
//        val repository = DeliveryRepository(apiService)
//        val factory = DeliveryViewModelFactory(repository)
//        viewModel = ViewModelProvider(this, factory)[DeliveryViewModel::class.java]
//    }
    private fun setupRecyclerView() {
        adapter = DeliveryAdapter(requireContext()) { delivery ->
            Toast.makeText(
                requireContext(),
                "Clicked: ${delivery.productName}",
                Toast.LENGTH_SHORT
            ).show()
        }
        binding.rvDeliveries.apply {
            layoutManager = LinearLayoutManager(requireContext())
            this.adapter = this@DeliveriesDetailsFragment.adapter
        }
    }

    private fun observeViewModel() {
        viewModel.deliveries.observe(viewLifecycleOwner) { list ->
            submitList(list)
        }
        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            showLoading(isLoading)
        }
        viewModel.error.observe(viewLifecycleOwner) { _ ->
            // Silent error as we have dummy data fallback
        }
    }

    private fun submitList(list: List<Delivery>) {
        adapter.submitList(list)
        val isEmpty = list.isEmpty()
        binding.llEmptyState.visibility = if (isEmpty) View.VISIBLE else View.GONE
        binding.rvDeliveries.visibility = if (isEmpty) View.GONE else View.VISIBLE
    }
    private fun showLoading(show: Boolean) {
        binding.progressBar.visibility = if (show) View.VISIBLE else View.GONE
        // IMPORTANT: Only hide the RecyclerView if we are loading AND have no data yet
        val currentDeliveries = viewModel.deliveries.value
        if (show && (currentDeliveries == null || currentDeliveries.isEmpty())) {
            binding.rvDeliveries.visibility = View.GONE
        } else if (!show) {
            // Restore visibility based on whether we have items
            val hasData = currentDeliveries?.isNotEmpty() == true
            binding.rvDeliveries.visibility = if (hasData) View.VISIBLE else View.GONE
            binding.llEmptyState.visibility = if (hasData) View.GONE else View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
