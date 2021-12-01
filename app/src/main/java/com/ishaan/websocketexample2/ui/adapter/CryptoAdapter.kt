package com.ishaan.websocketexample2.ui.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.ishaan.websocketexample2.databinding.ItemCryptoBinding
import com.ishaan.websocketexample2.util.Crypto

class CryptoAdapter : RecyclerView.Adapter<CryptoAdapter.CryptoViewHolder>() {

    var data = mapOf<String, String?>()
        @SuppressLint("NotifyDataSetChanged")
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CryptoViewHolder {
        val binding = ItemCryptoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CryptoViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CryptoViewHolder, position: Int) {
        val crypto = Crypto.values()[position]
        val price = data[crypto.id]

        holder.update(crypto, price)
    }

    override fun getItemCount() = Crypto.values().size

    class CryptoViewHolder(private val binding: ItemCryptoBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun update(crypto: Crypto, price: String?) {
            binding.code.text = crypto.code
            binding.name.text = crypto.friendlyName
            binding.price.text = price?.let {
                "$ $it"
            }
        }
    }
}