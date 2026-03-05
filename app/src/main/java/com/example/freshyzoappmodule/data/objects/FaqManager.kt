package com.example.freshyzoappmodule.data.objects

import com.example.freshyzoappmodule.data.model.FaqItem


object FaqManager {

    // ── Product-specific FAQ banks ────────────────────────────────

    private val cowMilkFaqs = listOf(
        FaqItem(
            "What is A2 Gir Cow milk?",
            "A2 Gir Cow milk contains only the A2 beta-casein protein, unlike regular milk which has A1 protein. It is easier to digest, naturally nutritious, and sourced from purebred Gir cows raised on natural grass feed."
        ),
        FaqItem(
            "How is Freshyzo cow milk different from packet milk?",
            "Our milk is farm-fresh, delivered within 8 hours of milking in glass bottles. It is free from preservatives, artificial whiteners, and synthetic hormones — unlike most packaged brands."
        ),
        FaqItem(
            "Is A2 cow milk safe for lactose-sensitive people?",
            "Many people with mild lactose sensitivity tolerate A2 milk better than regular A1 milk. However, it is not lactose-free. If you have a severe dairy allergy, please consult your doctor."
        ),
        FaqItem(
            "Why does the milk look slightly yellow?",
            "The natural yellow tint comes from beta-carotene present in Gir cow milk. It is a sign of purity and rich nutrition — not adulteration. No artificial whiteners are added."
        ),
        FaqItem(
            "Is it safe for children and elderly people?",
            "Yes. A2 Gir cow milk is gentle on the stomach and suitable for all age groups — from infants (6 months+) to the elderly. It is free from antibiotics and synthetic hormones."
        ),
        FaqItem(
            "How should I store the milk after delivery?",
            "Always refrigerate immediately after delivery. Consume within 24–36 hours for best taste. Boil before drinking if you prefer warm milk. Do not freeze."
        ),
        FaqItem(
            "Why is Freshyzo cow milk priced higher?",
            "Purebred Gir cows yield less milk than crossbred ones. Ethical farming, daily lab testing, glass-bottle packaging, and cold-chain delivery all add to the cost — ensuring you get real quality."
        )
    )

    private val buffaloMilkFaqs = listOf(
        FaqItem(
            "How is buffalo milk different from cow milk?",
            "Buffalo milk has higher fat content (6–8%) compared to cow milk (3–4%), making it creamier and richer. It is ideal for making thick curd, paneer, and ghee at home."
        ),
        FaqItem(
            "Is buffalo milk heavier to digest?",
            "Buffalo milk is slightly denser due to higher fat and protein. It is nourishing but may feel heavy for some people. It is best consumed in moderate quantities, especially by young children."
        ),
        FaqItem(
            "Why is buffalo milk preferred for making sweets?",
            "The high fat content in buffalo milk gives sweets, kheer, and halwa a naturally rich and creamy texture. It also produces thicker rabdi and condensed milk."
        ),
        FaqItem(
            "Is Freshyzo buffalo milk pasteurised?",
            "Yes. Our buffalo milk is gently pasteurised to eliminate harmful bacteria while preserving natural nutrients. It is tested in our lab before every dispatch."
        ),
        FaqItem(
            "Does buffalo milk contain more calcium than cow milk?",
            "Yes. Buffalo milk generally contains more calcium, protein, and fat than cow milk. It is a great source of minerals for growing children and adults."
        ),
        FaqItem(
            "How long does buffalo milk stay fresh?",
            "Refrigerate immediately and consume within 24–36 hours. Always boil before use if not pre-boiled. Do not leave at room temperature for more than 2 hours."
        ),
        FaqItem(
            "Can I make paneer and curd from buffalo milk at home?",
            "Absolutely. Buffalo milk's high fat content makes it the best choice for home-made paneer, thick curd, and cream. You will get a much higher yield compared to toned or cow milk."
        )
    )

    private val dahiFaqs = listOf(
        FaqItem(
            "What type of milk is used to make Freshyzo dahi?",
            "Our dahi is set using fresh A2 Gir cow milk or full-fat buffalo milk. No milk powder, preservatives, or stabilisers are added. It is pure, natural, and traditionally set."
        ),
        FaqItem(
            "Is Freshyzo dahi probiotic?",
            "Yes. Our dahi is made with live active cultures (Lactobacillus) that promote healthy gut bacteria, improve digestion, and boost immunity naturally."
        ),
        FaqItem(
            "Why does the dahi taste slightly sour sometimes?",
            "Sourness depends on temperature and fermentation time. In warmer weather dahi ferments faster and turns slightly tangier. This is completely natural and indicates live cultures are active."
        ),
        FaqItem(
            "Can I use Freshyzo dahi for cooking and raita?",
            "Yes. It is ideal for raita, kadhi, marinades, lassi, and all cooking needs. The thick texture and natural fat content give dishes an authentic homemade taste."
        ),
        FaqItem(
            "How should I store the dahi after delivery?",
            "Keep refrigerated at all times. Consume within 2–3 days of delivery for best taste and probiotic benefit. Do not freeze — it breaks the curd texture."
        ),
        FaqItem(
            "Is your dahi suitable for people with lactose intolerance?",
            "Many people with mild lactose sensitivity tolerate dahi better than plain milk because fermentation partially breaks down lactose. However, consult your doctor if you have severe intolerance."
        ),
        FaqItem(
            "Does Freshyzo dahi contain any added sugar or flavour?",
            "No. Our plain dahi is 100% natural with no added sugar, artificial flavour, or colour. What you get is pure set curd — just like homemade."
        )
    )

    private val paneerFaqs = listOf(
        FaqItem(
            "What milk is used to make Freshyzo paneer?",
            "We use full-fat A2 Gir cow milk or fresh buffalo milk. No starch, maida, or milk powder is added — which means our paneer is softer, whiter, and does not crumble when cooked."
        ),
        FaqItem(
            "How can I tell if paneer has starch in it?",
            "Drop a piece of paneer in iodine solution — if it turns blue-black, starch is present. Freshyzo paneer will show no colour change because we never add starch or fillers."
        ),
        FaqItem(
            "Why is Freshyzo paneer softer than market paneer?",
            "Most commercial paneer uses milk powder and starch, which makes it rubbery. Ours is made fresh daily from whole milk, giving it a naturally soft and spongy texture."
        ),
        FaqItem(
            "Can I use it directly without boiling or soaking?",
            "Yes. Our fresh paneer can be used directly. However, if you prefer softer paneer in curries, soak in warm water for 10 minutes before adding to the dish."
        ),
        FaqItem(
            "How long does Freshyzo paneer stay fresh?",
            "Keep refrigerated and consume within 3–4 days. For longer storage (up to 2 weeks), keep submerged in water in an airtight container and change the water daily."
        ),
        FaqItem(
            "Is Freshyzo paneer suitable for high-protein diets?",
            "Yes. Paneer is one of the best vegetarian sources of protein. A 100g serving provides approximately 18–20g of protein, making it excellent for fitness and weight management."
        ),
        FaqItem(
            "Can I freeze the paneer?",
            "Yes. Wrap tightly in cling film and freeze for up to 1 month. Thaw in the refrigerator overnight and soak in warm water before use to restore softness."
        )
    )

    private val gheeFaqs = listOf(
        FaqItem(
            "What is Bilona Ghee and how is it different?",
            "Bilona ghee is made by the traditional hand-churned method — curd is churned to extract butter, which is then slow-cooked into ghee. This preserves CLA, Omega-3, and fat-soluble vitamins that are lost in the commercial centrifuge process."
        ),
        FaqItem(
            "Is Freshyzo ghee made from A2 Gir cow milk?",
            "Yes. We use only A2 Gir cow milk curd for our Bilona ghee. It takes approximately 30 litres of milk to produce 1 kg of ghee — which is why A2 ghee is priced higher than regular ghee."
        ),
        FaqItem(
            "What is the smoke point of Freshyzo ghee?",
            "Our ghee has a high smoke point of around 250°C, making it safe and stable for deep frying, sautéing, and high-heat cooking — far superior to refined oils."
        ),
        FaqItem(
            "Why does Freshyzo ghee look grainy or crystallised?",
            "Granular texture is a natural sign of pure, unadulterated ghee. It occurs when the ghee cools slowly. Commercial ghee is often blended with vanaspati to prevent this — ours is 100% pure."
        ),
        FaqItem(
            "Can people with lactose intolerance consume ghee?",
            "Yes. During the ghee-making process, milk solids (which contain lactose and casein) are removed. Pure ghee is virtually lactose-free and is generally well tolerated."
        ),
        FaqItem(
            "How should I store Freshyzo ghee?",
            "Store in a cool, dry place away from direct sunlight. No refrigeration is needed. Use a dry spoon every time to prevent moisture contamination. Shelf life is 12 months when stored properly."
        ),
        FaqItem(
            "Is ghee good for health or does it increase cholesterol?",
            "Pure A2 ghee in moderate amounts is beneficial — it contains healthy saturated fats, CLA, and butyrate which support gut health, immunity, and brain function. Excessive consumption of any fat can be harmful."
        )
    )

    private val defaultFaqs = listOf(
        FaqItem(
            "Are Freshyzo products free from preservatives?",
            "Yes. All Freshyzo products are made without artificial preservatives, colours, or flavours. Every product is made fresh and lab-tested before delivery."
        ),
        FaqItem(
            "How does Freshyzo ensure product quality?",
            "We conduct daily lab testing on all batches, maintain cold-chain logistics, and deliver in food-safe glass or BPA-free packaging to ensure freshness and purity."
        ),
        FaqItem(
            "What is the delivery time for Freshyzo products?",
            "We deliver by 7:00 AM the next morning. Orders placed before midnight are dispatched from the farm the same night to ensure maximum freshness."
        ),
        FaqItem(
            "Can I subscribe for daily delivery?",
            "Yes. Freshyzo offers daily, weekly, and monthly subscription plans. You can pause or resume your subscription anytime from the app."
        ),
        FaqItem(
            "What if I receive a damaged or spoiled product?",
            "Contact our support team within 2 hours of delivery with a photo. We will arrange an immediate replacement or refund — no questions asked."
        )
    )

    // ── Public API ────────────────────────────────────────────────

    /**
     * Returns the correct FAQ list based on product name.
     * Matching is case-insensitive keyword search.
     *
     * Usage:
     *   val faqs = FaqManager.getFaqList(product.name)
     *   faqAdapter.submitList(faqs)
     */
    fun getFaqList(productName: String): List<FaqItem> {
        val name = productName.lowercase()
        return when {
            name.containsAny("cow milk", "a2 milk", "gir milk", "fresh milk")
                -> cowMilkFaqs

            name.containsAny("buffalo milk", "bhains", "buffalo")
                -> buffaloMilkFaqs

            name.containsAny("dahi", "curd", "yogurt", "yoghurt")
                -> dahiFaqs

            name.containsAny("paneer", "cottage cheese", "chenna")
                -> paneerFaqs

            name.containsAny("ghee", "bilona", "clarified butter")
                -> gheeFaqs

            else -> defaultFaqs
        }
    }

    private fun String.containsAny(vararg keywords: String): Boolean =
        keywords.any { this.contains(it) }
}