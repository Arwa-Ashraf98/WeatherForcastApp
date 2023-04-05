package com.example.weatherforecastapp.ui.home.view

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.example.weatherforecastapp.R
import com.example.weatherforecastapp.data.models.Hourly
import com.example.weatherforecastapp.databinding.ItemHoursBinding
import com.example.weatherforecastapp.utils.Constants
import com.example.weatherforecastapp.utils.HandleIcon
import com.example.weatherforecastapp.utils.Converters

class HourlyAdapter : RecyclerView.Adapter<HourlyAdapter.Holder>() {

    private var list: List<Hourly>? = null

    fun setList(list: List<Hourly>) {
        this.list = list
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val inflater =
            parent.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val binding = ItemHoursBinding.inflate(inflater, parent, false)
        return Holder(binding)
    }

    override fun getItemCount(): Int {
        return if (list.isNullOrEmpty()) {
            0
        } else {
            list?.size as Int
        }
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        val model = list?.get(position)
        holder.binding.apply {
            textViewDegree.text =
                Converters.convertTemperature(model?.temp as Double, holder.binding.root.context)
            textViewTime.text = Converters.convertTimestampToString(model.dt.toLong(), Converters.TIME_PATTERN_HOUR)
            Glide
                .with(holder.itemView.context)
                .load(Constants.WEATHER_IMAGE_BASE_URL + model.weather[0].icon + ".png")
                .placeholder(R.drawable.loading)
                .into(imageHour)
        }

    }


    inner class Holder(val binding: ItemHoursBinding) : ViewHolder(binding.root)
}