package com.example.hcmus_quickhelper.features.booking.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.hcmus_quickhelper.databinding.ItemEvidenceImageBinding

class EvidenceImageAdapter(
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<EvidenceImageAdapter.ViewHolder>() {

    private val images = mutableListOf<Uri>()

    fun updateImages(newImages: List<Uri>) {
        images.clear()
        images.addAll(newImages)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEvidenceImageBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(images[position], position)
    }

    override fun getItemCount(): Int = images.size

    inner class ViewHolder(private val binding: ItemEvidenceImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(uri: Uri, position: Int) {
            binding.ivEvidence.setImageURI(uri)
            binding.btnRemove.setOnClickListener {
                onRemoveClick(position)
            }
        }
    }
}
