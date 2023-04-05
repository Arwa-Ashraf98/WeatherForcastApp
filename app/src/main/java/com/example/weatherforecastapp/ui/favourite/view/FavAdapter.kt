package com.example.weatherforecastapp.ui.favourite.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.weatherforecastapp.data.models.FavAddress
import com.example.weatherforecastapp.databinding.ItemFavouritePlacesBinding

class FavAdapter() : RecyclerView.Adapter<FavAdapter.Holder>() {

    private var list: List<FavAddress> = listOf()
    private var setOnItemFavClickListener: SetOnItemFavClickListener? = null

    fun setList(list: List<FavAddress>) {
        this.list = list
    }
    fun setOnItemFavClickListener(setOnItemFavClickListener: SetOnItemFavClickListener){
         this.setOnItemFavClickListener = setOnItemFavClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding =
            ItemFavouritePlacesBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int = if (list.isEmpty()) 0 else list.size

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.binding.apply {
            val model = list[position]
            textViewAddress.text = model.city
        }
    }

    inner class Holder(val binding: ItemFavouritePlacesBinding) : ViewHolder(binding.root) {
        init {
            binding.apply {
                deleteImageView.setOnClickListener {
                    setOnItemFavClickListener?.onDeleteClickListener(list[layoutPosition])
                }
                textViewAddress.setOnClickListener {
                    setOnItemFavClickListener?.onItemFavClickListener(list[layoutPosition])
                }

            }

        }
    }

    interface SetOnItemFavClickListener {
        fun onDeleteClickListener(favAddress: FavAddress)
        fun onItemFavClickListener(favAddress: FavAddress)

    }
}