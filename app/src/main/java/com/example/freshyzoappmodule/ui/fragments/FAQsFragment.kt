package com.example.freshyzoappmodule.ui.fragments

import android.animation.ObjectAnimator
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.data.model.FaqQuestion
import com.example.freshyzoappmodule.databinding.FragmentFAQsBinding
import com.example.freshyzoappmodule.ui.activity.ChatActivity
import com.example.freshyzoappmodule.ui.activity.ChatListActivity
import com.example.freshyzoappmodule.ui.adapter.FaqQuestionAdapter

class FAQsFragment : Fragment() {
    private var _binding: FragmentFAQsBinding? = null
    private val binding get() = _binding!!
    private lateinit var faqAdapter: FaqQuestionAdapter
    private var selectedCategory = "all"
    private val handler = Handler(Looper.getMainLooper())
    private var searchRunnable: Runnable? = null

    // ─────────────────────────────────────────────────────────────
    //  All FAQ data
    // ─────────────────────────────────────────────────────────────
    private val allFaqs = listOf(
        FaqQuestion(
            1, "products", "What is A2 Gir Cow milk and why is it better?",
            "A2 milk comes from purebred Gir cows that produce only the A2 beta-casein protein — unlike regular milk which has A1 protein. It is naturally easier to digest, richer in Omega-3 fatty acids, and free from synthetic hormones or antibiotics. Our cows are raised on natural grass feed at our partner farms."
        ),
        FaqQuestion(2,  "products",     "Is Freshyzo milk safe for children and elderly?",
            "Absolutely. Our A2 Gir cow milk is gentle on the stomach and suitable for all age groups — from infants (6 months+) to the elderly. It is free from preservatives, artificial whiteners, and synthetic hormones, making it one of the safest dairy choices for the whole family."),
        FaqQuestion(3,  "products",     "Why does the milk look slightly yellow or have a cream layer?",
            "The natural yellow tint comes from beta-carotene present in Gir cow milk — a sign of purity and rich nutrition. The cream layer on top is natural fat. No artificial whiteners are added. This is completely normal and actually indicates that the milk is unadulterated."),
        FaqQuestion(4,  "products",     "Does Freshyzo paneer contain starch or fillers?",
            "Never. Our paneer is made fresh daily from full-fat A2 or buffalo milk with zero starch, maida, or milk powder. You can test this — place a piece in iodine solution and it will show no blue-black colour change. The result is naturally soft, non-rubbery paneer that doesn't crumble when cooked."),
        FaqQuestion(5,  "products",     "What is Bilona Ghee and how is it different?",
            "Bilona ghee is made by the traditional hand-churned method — curd is slow-churned to extract butter, which is then slow-cooked into ghee. This preserves CLA, Omega-3, and fat-soluble vitamins that are destroyed in the commercial centrifuge process. It takes ~30 litres of A2 milk to produce just 1 kg of Bilona ghee."),
        FaqQuestion(6,  "delivery",     "What time will my order be delivered?",
            "All orders are delivered by 7:00 AM the next morning. Orders placed before midnight are dispatched from the farm the same night via our cold-chain vehicles to ensure maximum freshness at your doorstep."),
        FaqQuestion(7,  "delivery",     "Is delivery free? What is the minimum order?",
            "Yes! Delivery is completely free for all subscription orders. For one-time purchases, free delivery applies on orders above ₹99. We currently deliver across all serviceable pin codes in your city — check availability by entering your pin code on the home screen."),
        FaqQuestion(8,  "delivery",     "What if I receive a damaged or spoiled product?",
            "We have a 100% replacement guarantee. If you receive a damaged, spoiled, or incorrect product, contact our support team within 2 hours of delivery with a photo. We will arrange an immediate replacement or full refund — no questions asked."),
        FaqQuestion(9,  "subscription", "Can I pause or cancel my subscription anytime?",
            "Yes. You can pause, resume, or cancel your subscription at any time directly from the app — no phone calls needed. Pausing stops future deliveries while keeping your subscription active. Cancellation takes effect from the next billing cycle."),
        FaqQuestion(10, "subscription", "How does subscription recharge work?",
            "You recharge your Freshyzo wallet in advance. Each delivery deducts the product price from your wallet balance. You'll receive a low-balance notification before your wallet runs out so you can top up without missing a delivery. First-time recharge comes with a flat 25% bonus."),
        FaqQuestion(11, "subscription", "Can I change my delivery quantity after subscribing?",
            "Yes. You can edit your daily quantity anytime from the My Subscriptions screen. Changes made before 10 PM apply from the very next morning's delivery."),
        FaqQuestion(12, "quality",      "How does Freshyzo ensure product quality?",
            "Every batch undergoes daily lab testing for fat %, SNF, adulteration, and bacterial count before dispatch. We maintain cold-chain logistics from farm to door and use food-safe glass or BPA-free packaging. Our farms are regularly audited for hygiene and animal welfare standards."),
        FaqQuestion(13, "quality",      "Are your products free from preservatives and additives?",
            "100% yes. All Freshyzo products — milk, dahi, paneer, ghee — are made without artificial preservatives, colours, flavours, or stabilisers. What you receive is exactly what comes from the farm, processed minimally and delivered fresh."),
        FaqQuestion(14, "payment",      "What payment methods does Freshyzo accept?",
            "We accept UPI, credit/debit cards, net banking, and all major wallets (Paytm, PhonePe, Google Pay). For subscriptions, we use a prepaid wallet system — you recharge your Freshyzo wallet and deliveries are auto-deducted daily."),
        FaqQuestion(15, "payment",      "How do I get a refund for a cancelled order?",
            "Refunds for cancelled orders are processed back to your Freshyzo wallet within 24 hours. For refunds to your original payment method (bank/card), it takes 5–7 business days depending on your bank. Wallet refunds can be used immediately for future purchases."),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFAQsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupToolbar()
        setupRecyclerView()
        setupCategoryChips()
        setupSearch()
        setupHelpBanner()

    }

    private fun setupToolbar() {
        binding.btnBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupRecyclerView() {
        faqAdapter = FaqQuestionAdapter()
        binding.rvFaq.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = faqAdapter
            itemAnimator = null
        }
        faqAdapter.submitList(allFaqs)
    }

    private fun setupCategoryChips() {
        val chipMap = mapOf(
            binding.chipAll          to "all",
            binding.chipProducts     to "products",
            binding.chipDelivery     to "delivery",
            binding.chipSubscription to "subscription",
            binding.chipQuality      to "quality",
            binding.chipPayment      to "payment",
        )
        chipMap.forEach { (chip, cat) ->
            chip.setOnClickListener {
                selectedCategory = cat
                updateChipStyles(chipMap, chip)
                showLoadingAndFilter()
            }
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) = Unit
            override fun onTextChanged(s: CharSequence?, st: Int, c: Int, a: Int) = Unit
            override fun afterTextChanged(s: Editable?) {
                binding.ivClearSearch.visibility =
                    if (s.isNullOrEmpty()) View.GONE else View.VISIBLE
                
                searchRunnable?.let { handler.removeCallbacks(it) }
                searchRunnable = Runnable { showLoadingAndFilter() }
                handler.postDelayed(searchRunnable!!, 300) // Small debounce for typing
            }
        })
        binding.ivClearSearch.setOnClickListener {
            binding.etSearch.text.clear()
        }
    }

    private fun setupHelpBanner() {
        binding.btnChatNow.setOnClickListener {
            val intent = Intent(requireContext(), ChatActivity::class.java).apply {
                putExtra("CHAT_ID", "default_support_chat") // You might want to generate a unique ID here
                putExtra("OTHER_USER_NAME", "Customer Support")
            }
            startActivity(intent)
        }
    }

    private fun showLoadingAndFilter() {
        // Stop any pending operations
        handler.removeCallbacksAndMessages(null)
        
        binding.rvFaq.visibility = View.GONE
        binding.animNotMatch.visibility = View.GONE // Hide "Not Found" while loading
        binding.progressBar.visibility = View.VISIBLE
        
        handler.postDelayed({
            if (_binding != null) {
                applyFilters()
                binding.progressBar.visibility = View.GONE
                binding.rvFaq.visibility = View.VISIBLE
            }
        }, 600) // less than  second delay
    }

    private fun applyFilters() {
        val query = binding.etSearch.text.toString().trim().lowercase()
        val filtered = allFaqs.filter { faq ->
            val matchesCat = selectedCategory == "all" || faq.category == selectedCategory
            val matchesText = query.isEmpty() ||
                    faq.question.lowercase().contains(query) ||
                    faq.answer.lowercase().contains(query)
            matchesCat && matchesText
        }
        if (filtered.isEmpty()) {
            binding.rvFaq.visibility = View.GONE
            binding.lytNotFound.visibility = View.VISIBLE
        } else {
            binding.rvFaq.visibility = View.VISIBLE
            binding.lytNotFound.visibility = View.GONE
            faqAdapter.submitList(filtered)

        }
    }

    private fun updateChipStyles(
        chipMap: Map<TextView, String>,
        activeChip: TextView
    ) {
        chipMap.keys.forEach { chip ->
            val isActive = chip == activeChip
            chip.background = ContextCompat.getDrawable(
                requireContext(),
                if (isActive) R.drawable.bg_cat_chip_active
                else          R.drawable.bg_cat_chip_default
            )
            chip.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    if (isActive) R.color.white else R.color.text_mid
                )
            )
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        handler.removeCallbacksAndMessages(null)
        _binding = null
    }
}
