package com.example.hcmus_quickhelper.features.service_browsing.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.features.service_browsing.model.ServiceDto
import com.example.hcmus_quickhelper.R

class ServiceAdapter(
    private var services: List<ServiceDto> = emptyList(),
    private val onClick: (ServiceDto) -> Unit
) : RecyclerView.Adapter<ServiceAdapter.ServiceViewHolder>() {

    fun updateData(newServices: List<ServiceDto>) {
        services = newServices
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServiceViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_service_home, parent, false)
        return ServiceViewHolder(view)
    }

    override fun onBindViewHolder(holder: ServiceViewHolder, position: Int) {
        val service = services[position]
        holder.bind(service)
    }

    override fun getItemCount() = services.size

    inner class ServiceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvServiceName: TextView = itemView.findViewById(R.id.tvServiceName)

        fun bind(service: ServiceDto) {
            tvServiceName.text = service.name

            itemView.setOnClickListener {
                onClick(service)
            }
        }
    }
}