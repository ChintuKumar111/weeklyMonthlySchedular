package com.example.freshyzoappmodule.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.freshyzoappmodule.data.model.Complaint
import com.example.freshyzoappmodule.databinding.FragmentComplaintHistoryBinding
import com.example.freshyzoappmodule.ui.adapter.ComplaintAdapter

class ComplaintHistoryFragment : Fragment() {

    private var _binding: FragmentComplaintHistoryBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentComplaintHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        setupRecyclerView()
    }

    private fun setupRecyclerView() {
        // Mock data for demonstration
        val mockComplaints = listOf(
            Complaint(
                "1", "App Related Issue", "App crashing or freezing",
                "App closes when I try to recharge my wallet.", "Pending", "28 Oct 2025, 03:45 PM"
            ),
            Complaint(
                "2", "Product Related Issue", "Quality Issue",
                "Milk was sour when delivered today morning.", "Resolved", "25 Oct 2025, 07:20 AM"
            ),
            Complaint(
                "3", "Delivery Related Issue", "Late Delivery",
                "Delivery was delayed by 2 hours without any notice.", "Resolved", "20 Oct 2025, 09:15 AM"
            )
        )

        if (mockComplaints.isEmpty()) {
            binding.llEmptyState.visibility = View.VISIBLE
            binding.rvComplaints.visibility = View.GONE
        } else {
            binding.llEmptyState.visibility = View.GONE
            binding.rvComplaints.visibility = View.VISIBLE
            binding.rvComplaints.layoutManager = LinearLayoutManager(requireContext())
            binding.rvComplaints.adapter = ComplaintAdapter(mockComplaints)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
