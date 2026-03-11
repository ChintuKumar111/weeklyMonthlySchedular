package com.example.freshyzoappmodule.ui.Fragments

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.FragmentVacationBinding
import java.text.SimpleDateFormat
import java.util.*

class VacationFragment : Fragment() {

    // ── ViewBinding ──────────────────────────────────────────────────────────
    private var _binding: FragmentVacationBinding? = null
    private val binding get() = _binding!!

    // ── State ────────────────────────────────────────────────────────────────
    private var selectedStartCal: Calendar? = null
    private var selectedEndCal: Calendar? = null
    private var isVacationActive: Boolean = false
    private var activeHistoryCardView: View? = null
    private val displayFmt = SimpleDateFormat("d MMM yyyy", Locale.getDefault())

    // ── MOCK: Replace with your actual session/auth check ───────────────────
    // true  = logged-in subscriber  → full access
    // false = guest / non-subscriber → show gate dialog
    private val isSubscriptionCustomer: Boolean = false

    // ── Lifecycle ────────────────────────────────────────────────────────────

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVacationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupClickListeners()
        startHeroAnimations()
        animateContentIn()

        // ── Gate check: show on EVERY first entry to this screen ────────────
        if (!isSubscriptionCustomer) {
            showSubscriptionGateDialog()
        } else {
            loadDummyHistory()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    // ── Subscription Gate Dialog ─────────────────────────────────────────────

    private fun showSubscriptionGateDialog() {
        // Build a custom view for a richer dialog
        val dialogView = LayoutInflater.from(requireContext())
            .inflate(R.layout.dialog_subscription_gate, null)

        val dialog = AlertDialog.Builder(requireContext(), R.style.RoundedDialogStyle)
            .setView(dialogView)
            .setCancelable(false)   // force user to make a choice
            .create()

        // "Login" button → navigate to login screen
        dialogView.findViewById<View>(R.id.btnDialogLogin).setOnClickListener {
            dialog.dismiss()
            // TODO: Replace with your actual navigation to LoginFragment/Activity
            // e.g. findNavController().navigate(R.id.action_vacation_to_login)
            showToast("Navigating to Login…")
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // "Go Back" button → just close the screen
        dialogView.findViewById<View>(R.id.btnDialogGoBack).setOnClickListener {
            dialog.dismiss()
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        dialog.show()
    }

    // ── Click Listeners ──────────────────────────────────────────────────────

    private fun setupClickListeners() {

        binding.ivBack.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.tvStartDate.setOnClickListener { showDatePicker(isStart = true) }
        binding.tvEndDate.setOnClickListener   { showDatePicker(isStart = false) }

        binding.btnStartVacation.setOnClickListener {
            if (isVacationActive) {
                showCancelConfirmationDialog()
            } else {
                val start = selectedStartCal
                val end   = selectedEndCal
                when {
                    start == null || end == null ->
                        showToast("⚠️ Please select both dates")
                    end.before(start) ->
                        showToast("⚠️ End date must be after start date")
                    else -> activateVacation(start, end)
                }
            }
        }

        binding.btnClearVacation.setOnClickListener { clearVacation() }
    }

    // ── Date Picker ──────────────────────────────────────────────────────────

    private fun showDatePicker(isStart: Boolean) {
        val cal = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val picked = Calendar.getInstance().apply { set(year, month, day) }
                if (isStart) {
                    selectedStartCal = picked
                    binding.tvStartDate.text = displayFmt.format(picked.time)
                    binding.tvStartDate.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.text_dark)
                    )
                    if (selectedEndCal?.before(picked) == true) {
                        selectedEndCal = null
                        binding.tvEndDate.text = "Select Date"
                        binding.tvEndDate.setTextColor(
                            ContextCompat.getColor(requireContext(), R.color.text_light)
                        )
                    }
                } else {
                    selectedEndCal = picked
                    binding.tvEndDate.text = displayFmt.format(picked.time)
                    binding.tvEndDate.setTextColor(
                        ContextCompat.getColor(requireContext(), R.color.text_dark)
                    )
                }
                updateDurationPill()
            },
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH),
            cal.get(Calendar.DAY_OF_MONTH)
        ).apply {
            datePicker.minDate = System.currentTimeMillis() - 1000
            if (!isStart) selectedStartCal?.let { datePicker.minDate = it.timeInMillis }
            show()
        }
    }

    // ── Duration Pill ────────────────────────────────────────────────────────

    private fun updateDurationPill() {
        val s = selectedStartCal ?: return hideDurationPill()
        val e = selectedEndCal   ?: return hideDurationPill()
        val days = daysBetween(s, e) + 1
        if (days < 1) return hideDurationPill()

        binding.tvDuration.text = "$days day${if (days > 1) "s" else ""} selected"
        if (binding.llDurationPill.visibility != View.VISIBLE) {
            binding.llDurationPill.visibility = View.VISIBLE
            binding.llDurationPill.startAnimation(
                AnimationUtils.loadAnimation(requireContext(), R.anim.anim_pop_in)
            )
        }
    }

    private fun hideDurationPill() { binding.llDurationPill.visibility = View.GONE }

    // ── Activate Vacation ────────────────────────────────────────────────────

    private fun activateVacation(start: Calendar, end: Calendar) {
        isVacationActive = true

        // Show active banner
        binding.cardActiveBanner.visibility = View.VISIBLE
        binding.cardActiveBanner.startAnimation(
            AnimationUtils.loadAnimation(requireContext(), R.anim.anim_banner_slide_down)
        )
        binding.tvBannerEndDate.text = "Paused until ${displayFmt.format(end.time)}"
        val remaining = daysBetween(Calendar.getInstance(), end)
        binding.tvCountdown.text = when {
            remaining > 0   -> "$remaining day${if (remaining > 1) "s" else ""} remaining"
            remaining == 0L -> "Ends today"
            else            -> "Vacation ended"
        }

        // Add card to history list + keep reference
        activeHistoryCardView = addHistoryCard(start, end, isActive = true)
        binding.llEmptyHistory.visibility = View.GONE

        // Transform button → Cancel (red)
        binding.btnStartVacation.text = "✕  Cancel Vacation"
        binding.btnStartVacation.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_balance_card)

        // Disable date pickers
        binding.tvStartDate.isEnabled = false
        binding.tvEndDate.isEnabled   = false
        binding.tvStartDate.alpha     = 0.5f
        binding.tvEndDate.alpha       = 0.5f

        // Hide Clear while active
        binding.btnClearVacation.visibility = View.GONE

        showToast("🏖️ Vacation mode activated!")
    }

    // ── Clear (before activation only) ──────────────────────────────────────

    private fun clearVacation() {
        selectedStartCal = null
        selectedEndCal   = null
        binding.tvStartDate.text = "Select Date"
        binding.tvEndDate.text   = "Select Date"
        binding.tvStartDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_light))
        binding.tvEndDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_light))
        hideDurationPill()
        showToast("✓ Dates cleared")
    }

    // ── Cancel Active Vacation ───────────────────────────────────────────────

    private fun showCancelConfirmationDialog() {
        AlertDialog.Builder(requireContext())
            .setTitle("Cancel Vacation?")
            .setMessage("Are you sure? Your deliveries will resume immediately.")
            .setPositiveButton("Yes, Cancel") { dialog, _ ->
                cancelActiveVacation()
                dialog.dismiss()
            }
            .setNegativeButton("Keep Vacation") { dialog, _ -> dialog.dismiss() }
            .show()
    }

    private fun cancelActiveVacation() {
        isVacationActive = false

        binding.cardActiveBanner.visibility = View.GONE

        // Update the active history card badge → "Cancelled"
        activeHistoryCardView?.let { card ->
            val badge = card.findViewById<TextView>(R.id.tvVCardBadge)
            badge.text = "Cancelled"
            badge.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_dark))
            badge.background =
                ContextCompat.getDrawable(requireContext(), R.drawable.bg_badge_cancelled)
        }
        activeHistoryCardView = null

        // Reset dates
        selectedStartCal = null
        selectedEndCal   = null
        binding.tvStartDate.text  = "Select Date"
        binding.tvEndDate.text    = "Select Date"
        binding.tvStartDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_light))
        binding.tvEndDate.setTextColor(ContextCompat.getColor(requireContext(), R.color.text_light))
        binding.tvStartDate.isEnabled = true
        binding.tvEndDate.isEnabled   = true
        binding.tvStartDate.alpha     = 1f
        binding.tvEndDate.alpha       = 1f
        hideDurationPill()

        // Restore button → Start (green)
        binding.btnStartVacation.text = "🏖️  Start Vacation"
        binding.btnStartVacation.background =
            ContextCompat.getDrawable(requireContext(), R.drawable.bg_btn_primary)

        binding.btnClearVacation.visibility = View.VISIBLE
        showToast("✓ Vacation cancelled. Deliveries resumed!")
    }

    // ── History Cards ────────────────────────────────────────────────────────

    private fun addHistoryCard(start: Calendar, end: Calendar, isActive: Boolean): View {
        val card = LayoutInflater.from(requireContext())
            .inflate(R.layout.item_vacation_history, binding.llVacationHistory, false)

        val days = daysBetween(start, end) + 1
        card.findViewById<TextView>(R.id.tvVCardDates).text =
            "${displayFmt.format(start.time)} – ${displayFmt.format(end.time)}"
        card.findViewById<TextView>(R.id.tvVCardDuration).text =
            "$days day${if (days > 1) "s" else ""} · All subscriptions paused"
        card.findViewById<TextView>(R.id.tvVCardIcon).text = "🏖️"

        val badge = card.findViewById<TextView>(R.id.tvVCardBadge)
        badge.text = if (isActive) "Active" else "Done"
        badge.setTextColor(ContextCompat.getColor(
            requireContext(),
            if (isActive) R.color.badge_active_text else R.color.badge_done_text
        ))
        badge.background = ContextCompat.getDrawable(
            requireContext(),
            if (isActive) R.drawable.bg_badge_active else R.drawable.bg_badge_done
        )

        card.startAnimation(AnimationUtils.loadAnimation(requireContext(), R.anim.anim_card_enter))
        binding.llVacationHistory.addView(card, 0)
        return card
    }

    private fun loadDummyHistory() {
        val entries = listOf(
            Triple("5 Jan 2026",  "14 Jan 2026",  false),
            Triple("20 Dec 2025", "28 Dec 2025",  false)
        )
        if (entries.isEmpty()) {
            binding.llEmptyHistory.visibility = View.VISIBLE
            return
        }
        binding.llEmptyHistory.visibility = View.GONE
        entries.forEachIndexed { i, (s, e, active) ->
            val start = parseDisplayDate(s) ?: return@forEachIndexed
            val end   = parseDisplayDate(e) ?: return@forEachIndexed
            val card  = LayoutInflater.from(requireContext())
                .inflate(R.layout.item_vacation_history, binding.llVacationHistory, false)

            val days = daysBetween(start, end) + 1
            card.findViewById<TextView>(R.id.tvVCardDates).text  = "$s – $e"
            card.findViewById<TextView>(R.id.tvVCardDuration).text =
                "$days day${if (days > 1) "s" else ""} · Subscriptions paused"
            card.findViewById<TextView>(R.id.tvVCardIcon).text = if (i == 0) "🌴" else "☀️"

            val badge = card.findViewById<TextView>(R.id.tvVCardBadge)
            badge.text = if (active) "Active" else "Done"
            badge.setTextColor(ContextCompat.getColor(
                requireContext(),
                if (active) R.color.badge_active_text else R.color.badge_done_text
            ))
            badge.background = ContextCompat.getDrawable(
                requireContext(),
                if (active) R.drawable.bg_badge_active else R.drawable.bg_badge_done
            )
            binding.llVacationHistory.addView(card)
        }
    }

    // ── Hero Animations ──────────────────────────────────────────────────────

    private fun startHeroAnimations() {
        val ctx = requireContext()
        binding.ivSun.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.anim_sun_pulse))
        binding.tvHeroLine1.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.anim_float))
        binding.tvHeroLine2.postDelayed({
            if (_binding != null)
                binding.tvHeroLine2.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.anim_float))
        }, 300)
        binding.ivCloud1.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.anim_cloud_drift))
        binding.ivCloud2.postDelayed({
            if (_binding != null)
                binding.ivCloud2.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.anim_cloud_drift_slow))
        }, 4000)
        binding.ivCloud3.postDelayed({
            if (_binding != null)
                binding.ivCloud3.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.anim_cloud_drift))
        }, 9000)
        binding.viewWave.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.anim_wave))
        binding.tvPalmLeft.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.anim_sway))
        binding.tvPalmRight.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.anim_sway_reverse))
        binding.tvUmbrella.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.anim_sway))
        binding.tvSurfboard.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.anim_sway_reverse))
        binding.tvSparkle1.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.anim_sparkle))
        binding.tvSparkle2.postDelayed({
            if (_binding != null)
                binding.tvSparkle2.startAnimation(AnimationUtils.loadAnimation(ctx, R.anim.anim_sparkle))
        }, 600)
    }

    // ── Content Reveal ───────────────────────────────────────────────────────

    private fun animateContentIn() {
        val slideUp = AnimationUtils.loadAnimation(requireContext(), R.anim.anim_slide_up_fade)
        listOf(
            binding.llDisclaimer,
            binding.btnStartVacation,
            binding.btnClearVacation,
            binding.llVacationHistory
        ).forEachIndexed { i, v ->
            v.postDelayed({ if (_binding != null) v.startAnimation(slideUp) }, (i * 80).toLong())
        }
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun daysBetween(from: Calendar, to: Calendar): Long =
        (to.timeInMillis - from.timeInMillis) / (1000 * 60 * 60 * 24)

    private fun parseDisplayDate(s: String): Calendar? = try {
        Calendar.getInstance().apply { time = displayFmt.parse(s)!! }
    } catch (e: Exception) { null }

    private fun showToast(msg: String) =
        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()

    companion object {
        fun newInstance() = VacationFragment()
    }
}