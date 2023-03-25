package com.example.weatherforecastapp.ui.home.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.weatherforecastapp.data.models.Daily
import com.example.weatherforecastapp.databinding.ItemDaysBinding
import com.example.weatherforecastapp.utils.Converters
import com.example.weatherforecastapp.utils.HandleIcon
import com.google.android.material.transition.Hold

class DailyAdapter : RecyclerView.Adapter<DailyAdapter.Holder>() {

    private var list: List<Daily> = listOf()

    fun setDailyList(list: List<Daily>) {
        this.list = list
    }

    inner class Holder(val binding: ItemDaysBinding) : ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDaysBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return if (list.isEmpty()) {
            0
        } else {
            list.size
        }
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        val model = list[position]
        holder.binding.apply {
            textviewItemDayDay.text =
                Converters.convertTimestampToString(model.dt, Converters.DAY_PATTERN)
            textviewItemDayMaxDegree.text =
                Converters.convertTemperature(model.temp.max, holder.binding.root.context)
            textviewItemDayMinDegree.text =
                Converters.convertTemperature(model.temp.min, holder.binding.root.context)
            textviewItemDayDescription.text = model.weather[0].description
            Glide.with(holder.binding.root.context)
                .load(HandleIcon.getIcon(model.weather[0].icon))
                .into(imageItemDayIcon)
        }
    }
}