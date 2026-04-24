package com.example.hcmus_quickhelper.features.booking.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.hcmus_quickhelper.R
import com.example.hcmus_quickhelper.core.model.BookingStatus
import com.example.hcmus_quickhelper.databinding.ItemEvidenceImageBinding
import com.example.hcmus_quickhelper.features.booking.model.BookingEvidence

class EvidenceImageAdapter(
    private val onRemoveClick: (Int) -> Unit
) : RecyclerView.Adapter<EvidenceImageAdapter.ViewHolder>() {

    private var evidences: MutableList<BookingEvidence> = mutableListOf()

    private var bookingStatus: String = BookingStatus.IN_PROGRESS.toString();

    fun updateEvidences(newEvidences: List<BookingEvidence>) {
        evidences.clear()
        evidences.addAll(newEvidences)
        notifyDataSetChanged()
    }

    fun setStatus(status: String) {
        bookingStatus = status
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
        holder.bind(evidences[position], position)
    }

    override fun getItemCount(): Int = evidences.size

    inner class ViewHolder(private val binding: ItemEvidenceImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(evidence: BookingEvidence, position: Int) {
            val directImageUrl = convertDriveLinkToDirectLink(evidence.evidenceUrl)
            binding.ivEvidence.load(directImageUrl) {
                crossfade(true)
                placeholder(R.drawable.ic_launcher_background)
                error(R.drawable.ic_close)
            }

            if(bookingStatus == BookingStatus.IN_PROGRESS.toString()) {
                binding.btnRemove.setOnClickListener {
                    onRemoveClick(position)
                }
            } else {
                binding.btnRemove.visibility = ViewGroup.GONE
            }
        }
    }

    private fun convertDriveLinkToDirectLink(url: String): String {
        return if (url.contains("drive.google.com/file/d/")) {
            val id = url.substringAfter("/d/").substringBefore("/")
            "https://drive.google.com/uc?export=view&id=$id"
        } else {
            url
        }
    }
}
