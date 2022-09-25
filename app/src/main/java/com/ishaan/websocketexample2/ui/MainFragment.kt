package com.ishaan.websocketexample2.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.ishaan.websocketexample2.databinding.FragmentMainBinding
import com.ishaan.websocketexample2.ui.adapter.CryptoAdapter
import com.ishaan.websocketexample2.ui.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainFragment : Fragment() {
    private lateinit var binding: FragmentMainBinding
    private val viewModel: MainViewModel by viewModels()
    private lateinit var mAdapter: CryptoAdapter


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMainBinding.inflate(inflater, container, false)
        viewModel.createService(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAdapter = CryptoAdapter()
        binding.recyclerView.adapter = mAdapter

        binding.btnBye.setOnClickListener {
            findNavController().navigate(MainFragmentDirections.actionMainFragmentToByeFragment())
        }

        observePrices()
    }

    private fun observePrices() {
        viewModel.prices.observe(viewLifecycleOwner) {
            Log.d("Pui","data to adapter")
            mAdapter.data = it
        }
    }
}