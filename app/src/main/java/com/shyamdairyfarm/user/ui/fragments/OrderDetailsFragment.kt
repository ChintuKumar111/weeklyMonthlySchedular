package com.shyamdairyfarm.user.ui.fragments
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.shyamdairyfarm.user.R
import com.shyamdairyfarm.user.data.model.OrderHistoryModel
import com.shyamdairyfarm.user.data.model.DeliveryStatus
import com.shyamdairyfarm.user.databinding.FragmentOrderDetailsBinding
class OrderDetailsFragment : Fragment() {
    private var _binding: FragmentOrderDetailsBinding? = null
    private val binding get() = _binding!!
    private var delivery: OrderHistoryModel? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        delivery = if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("delivery", OrderHistoryModel::class.java)
        } else {
            @Suppress("DEPRECATION")
            arguments?.getParcelable("delivery")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        delivery?.let { setupUI(it) }
    }

    private fun setupUI(item: OrderHistoryModel) {
        applyStatus(item.status)
        
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun applyStatus(status: DeliveryStatus) {
        val ctx = requireContext()
        when (status) {
            DeliveryStatus.PLACED -> {
                binding.tvStatus.text = "● Placed"
                binding.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.emerald))
                binding.tvStatus.setBackgroundResource(R.drawable.bg_badge_placed)
            }
            DeliveryStatus.PENDING -> {
                binding.tvStatus.text = "⏳ Pending"
                binding.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.amber))
                binding.tvStatus.setBackgroundResource(R.drawable.bg_badge_pending)
            }
            DeliveryStatus.CANCELLED -> {
                binding.tvStatus.text = "✕ Cancelled"
                binding.tvStatus.setTextColor(ContextCompat.getColor(ctx, R.color.coral))
                binding.tvStatus.setBackgroundResource(R.drawable.bg_badge_cancelled)
            }
        }
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
