package com.kpstv.yts.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.api.load
import com.kpstv.yts.R
import com.kpstv.yts.data.models.Torrent
import com.kpstv.yts.databinding.ItemTorrentDownload1Binding

class JobQueueAdapter(
    private val context: Context,
    private val models: ArrayList<Torrent>
) :
    RecyclerView.Adapter<JobQueueAdapter.JobHolder>() {

    private lateinit var listener: CloseClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): JobHolder {
        return JobHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_torrent_download_1, parent, false
            )
        )
    }

    override fun onBindViewHolder(holder: JobHolder, i: Int) {
        val model = models[i]

        holder.binding.itemTitle.text = model.title
        holder.binding.itemImage.load(model.banner_url)

        holder.binding.itemClose.setOnClickListener {
            listener.onClick(model, i)
        }
    }

    override fun getItemCount(): Int {
        return models.size
    }

    interface CloseClickListener {
        fun onClick(model: Torrent, pos: Int)
    }

    fun setCloseClickListener(listener: CloseClickListener) {
        this.listener = listener
    }

    class JobHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemTorrentDownload1Binding.bind(view)
    }
}