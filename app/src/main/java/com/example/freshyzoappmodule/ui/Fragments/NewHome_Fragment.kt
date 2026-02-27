package com.example.freshyzoappmodule.ui.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.example.freshyzoappmodule.data.model.SliderItem
import com.example.freshyzoappmodule.databinding.FragmentNewHomeBinding
import com.example.freshyzoappmodule.ui.activity.NewHomeActivity
import com.example.freshyzoappmodule.ui.activity.NotificationActivity
import com.example.freshyzoappmodule.ui.adapter.ImageSliderAdapter
import com.example.freshyzoappmodule.viewmodel.HomeFragmentViewModel
import com.google.android.material.tabs.TabLayoutMediator

class NewHome_Fragment : Fragment() {
    private var _binding: FragmentNewHomeBinding? = null
    private val binding get() = _binding!!

    private val viewModel: HomeFragmentViewModel by viewModels()

    private val defaultImages = listOf(
        SliderItem(0, "https://static1.squarespace.com/static/638d8044b6fc77648ebcedba/t/67a5b74af834d07712692f36/1738913639066/Top+10+dairy+products+for+your+kitchen+-+Kota+Fresh+Dairy.png?format=1500w"),
        SliderItem(1, "https://images.squarespace-cdn.com/content/v1/638d8044b6fc77648ebcedba/7d7c7c4f-34b6-4381-b8ad-d88433c86f62/4.png"),
        SliderItem(2, "https://asset7.ckassets.com/blog/wp-content/uploads/sites/5/2021/12/Best-Milk-Brands.jpg")
    )

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentNewHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSlider()
        observeSlider()
        
        // Delay fetching slider until the fragment is fully transitioned to reduce lag
        view.postDelayed({
            if (isAdded) viewModel.fetchSlider()
        }, 300)

        binding.iconNotification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }
    }

    private fun setupSlider() {
        binding.productSliderCart.adapter = ImageSliderAdapter(defaultImages)
        TabLayoutMediator(binding.tabLayout, binding.productSliderCart) { _, _ -> }.attach()
        
        // Reduce the ViewPager's sensitivity/rendering weight
        binding.productSliderCart.offscreenPageLimit = 1
    }

    private fun observeSlider() {
        viewModel.sliderData.observe(viewLifecycleOwner) { sliderList ->
            if (!sliderList.isNullOrEmpty()) {
                binding.productSliderCart.adapter = ImageSliderAdapter(sliderList)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
