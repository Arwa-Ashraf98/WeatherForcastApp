package com.example.weatherforecastapp.ui.alerts.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.example.weatherforecastapp.data.models.AlertEntity
import com.example.weatherforecastapp.databinding.ItemAlertBinding
import com.example.weatherforecastapp.ui.alerts.alertUtils.AlertConverters

class AlertAdapter : RecyclerView.Adapter<AlertAdapter.Holder>() {

    private var list: List<AlertEntity> = emptyList()
    private var alertClickListener: AlertClickListener? = null

    fun setList(list: List<AlertEntity>) {
        this.list = list
    }

    fun setOnAlertItemClickListener(alertClickListener: AlertClickListener) {
        this.alertClickListener = alertClickListener
    }

    private val differCallBack = object : DiffUtil.ItemCallback<AlertEntity>() {
        override fun areItemsTheSame(oldItem: AlertEntity, newItem: AlertEntity): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: AlertEntity, newItem: AlertEntity): Boolean {
            return oldItem.id == newItem.id
        }
    }

     val differ = AsyncListDiffer(this, differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val binding = ItemAlertBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return Holder(binding)
    }


    override fun onBindViewHolder(holder: Holder, position: Int) {
        val model = list[position]
        holder.binding.apply {
            val start = AlertConverters.convertDayToString(
                model.startDate ?: 0,
                holder.binding.root.context
            ) + " - " + AlertConverters.convertTimeToString(
                model.startTime ?: 0,
                holder.binding.root.context
            )

            val end = AlertConverters.convertDayToString(
                model.endDate ?: 0,
                holder.binding.root.context
            ) + " - " + AlertConverters.convertTimeToString(
                model.endTime ?: 0,
                holder.binding.root.context
            )
            textViewStartDate.text = start
            textViewEndDate.text = end
            textViewCity.text = model.city
        }
    }

    override fun getItemCount(): Int {
        return if (list.isEmpty()) {
            0
        } else {
            list.size
        }
    }


    inner class Holder(var binding: ItemAlertBinding) : ViewHolder(binding.root) {
        init {
            binding.apply {
                btnDelete.setOnClickListener {
                    alertClickListener?.onDeleteAlertClickListener(list[layoutPosition])
                }
            }
        }
    }

    interface AlertClickListener {
        fun onDeleteAlertClickListener(alertEntity: AlertEntity)
    }
}