package com.kpstv.yts.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.kpstv.yts.R
import com.kpstv.yts.data.models.response.Model
import com.kpstv.yts.databinding.ItemWatchlistBinding
import com.kpstv.yts.extensions.load
import com.kpstv.yts.extensions.utils.AppUtils

class WatchlistAdapter(
    private val onClickListener: (Model.response_favourite, Int) -> Unit,
    private val onItemRemoveListener: (Model.response_favourite, Int) -> Unit
) : ListAdapter<Model.response_favourite, WatchlistAdapter.WatchlistHolder>(diffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WatchlistHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_watchlist, parent, false
            )
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WatchlistHolder, i: Int) {
        val model = getItem(i)
        
        holder.binding.itemImageCard.getImageView().load(
            uri = model.imageUrl,
            onSuccess = { resource ->
                holder.binding.itemImageCard.setImage(resource)
            }
        )

        holder.binding.itemTitle.text = model.title
        holder.binding.itemSubText.text = "${model.year} ${AppUtils.getBulletSymbol()} ${model.runtime} mins"

        holder.binding.root.setOnClickListener { onClickListener.invoke(model, i) }

        holder.binding.itemRemoveFavourites.setOnClickListener { onItemRemoveListener.invoke(model, i) }
    }

    companion object {
        private val diffCallback: DiffUtil.ItemCallback<Model.response_favourite> =
            object : DiffUtil.ItemCallback<Model.response_favourite>() {
                override fun areItemsTheSame(
                    oldItem: Model.response_favourite,
                    newItem: Model.response_favourite
                ) =
                    oldItem.imdbCode == newItem.imdbCode

                override fun areContentsTheSame(
                    oldItem: Model.response_favourite,
                    newItem: Model.response_favourite
                ) =
                    oldItem == newItem
            }
    }

    class WatchlistHolder(view: View) : RecyclerView.ViewHolder(view) {
        val binding = ItemWatchlistBinding.bind(view)
    }
}