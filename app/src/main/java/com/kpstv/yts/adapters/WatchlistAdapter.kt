package com.kpstv.yts.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.request.LoadRequest
import com.kpstv.yts.R
import com.kpstv.yts.data.models.response.Model
import com.kpstv.yts.extensions.execute
import com.kpstv.yts.extensions.hide
import com.kpstv.yts.extensions.utils.AppUtils
import kotlinx.android.synthetic.main.item_watchlist.view.*

class WatchlistAdapter(
    private val context: Context,
    private var models: List<Model.response_favourite>
) : RecyclerView.Adapter<WatchlistAdapter.WatchlistHolder>() {

    lateinit var onClickListener: (Model.response_favourite, Int) -> Unit
    lateinit var onItemRemoveListener: (Model.response_favourite, Int) -> Unit

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        WatchlistHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.item_watchlist, parent, false
            )
        )

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: WatchlistHolder, i: Int) {
        val model = models[i]

        LoadRequest.Builder(context.applicationContext)
            .data(model.imageUrl)
            .target { drawable ->
                holder.image.setImageDrawable(drawable)
                holder.itemView.shimmerFrame.hideShimmer()
                holder.itemView.shimmerFrame.hide()
            }
            .execute()

        holder.title.text = model.title
        holder.subTitle.text = "${model.year} ${AppUtils.getBulletSymbol()} ${model.runtime} mins"

        holder.mainLayout.setOnClickListener { onClickListener.invoke(model, i) }

        holder.removeFavourite.setOnClickListener { onItemRemoveListener.invoke(model, i) }
    }

    fun updateModels(it: List<Model.response_favourite>) {
        models = it
        notifyDataSetChanged()
    }

    override fun getItemCount() = models.size

    class WatchlistHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainLayout = view.mainLayout
        val title = view.item_title
        val subTitle = view.item_subText
        val image = view.item_image
        val removeFavourite = view.item_remove_favourites
    }
}