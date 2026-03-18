package com.example.freshyzoappmodule.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.example.freshyzoappmodule.databinding.FragmentBlogReportBinding

class BlogReportFragment : Fragment() {
    private var _binding: FragmentBlogReportBinding? = null
    private val binding get() = _binding!!
    private val args: BlogReportFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentBlogReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupUI()
    }

    private fun setupUI() {
        val blog = args.blog

        binding.tvBlogTitle.text = blog.title
        binding.tvBlogDescription.text = blog.description

        Glide.with(requireContext())
            .load(blog.imageUrl)
            .into(binding.ivBlogImage)

        binding.toolbar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
