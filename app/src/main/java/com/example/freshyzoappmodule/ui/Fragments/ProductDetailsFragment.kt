package com.example.freshyzoappmodule.ui.Fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.freshyzoappmodule.R
import com.example.freshyzoappmodule.databinding.FragmentProductDetailsBinding
import com.example.freshyzoappmodule.databinding.FragmentProductSectionBinding


class ProductDetailsFragment : Fragment() {
    private var _binding: FragmentProductDetailsBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

            // tot for ui related work

        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        _binding = FragmentProductDetailsBinding.inflate(inflater,container,false)
        return _binding?.root


    }


}