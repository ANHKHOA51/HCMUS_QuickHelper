package com.example.hcmus_quickhelper.features.booking.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.example.hcmus_quickhelper.databinding.ItemEvidenceImageBinding
import com.example.hcmus_quickhelper.features.booking.model.BookingEvidence
import com.example.hcmus_quickhelper.R

class EvidenceViewerAdapter(
    private var evidences: List<BookingEvidence>,
    private val onImageClick: (String) -> Unit
) : RecyclerView.Adapter<EvidenceViewerAdapter.ViewHolder>() {

    class ViewHolder(val binding: ItemEvidenceImageBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemEvidenceImageBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val evidence = evidences[position]
        holder.binding.btnRemove.visibility = View.GONE

        val directImageUrl = convertDriveLinkToDirectLink(evidence.evidenceUrl)

        holder.binding.ivEvidence.load(directImageUrl) {
            crossfade(true)
            placeholder(R.drawable.ic_launcher_background)
            error(R.drawable.ic_close)
        }

        // THÊM: Bắt sự kiện click vào tấm ảnh
        holder.binding.ivEvidence.setOnClickListener {
            onImageClick(directImageUrl)
        }
    }

    override fun getItemCount(): Int = evidences.size

    fun updateData(newList: List<BookingEvidence>) {
        evidences = newList
        notifyDataSetChanged()
    }

    // Ép link Google Drive thành link ảnh trực tiếp
    private fun convertDriveLinkToDirectLink(url: String): String {
        return if (url.contains("drive.google.com/file/d/")) {
            val id = url.substringAfter("/d/").substringBefore("/")
            "https://drive.google.com/uc?export=view&id=$id"
        } else {
            url
        }
    }
}