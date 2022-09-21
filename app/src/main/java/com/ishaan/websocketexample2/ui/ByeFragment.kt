package com.ishaan.websocketexample2.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.ishaan.websocketexample2.databinding.FragmentByeBinding

class ByeFragment : Fragment() {

    private lateinit var binding: FragmentByeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentByeBinding.inflate(layoutInflater, container, false)
        return binding.root
    }
}