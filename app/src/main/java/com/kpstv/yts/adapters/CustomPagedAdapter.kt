package com.kpstv.yts.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.request.CachePolicy
import coil.request.LoadRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.kpstv.yts.AppInterface.Companion.IS_DARK_THEME
import com.kpstv.yts.AppInterface.Companion.MOVIE_ID
import com.kpstv.yts.R
import com.kpstv.yts.data.models.MovieShort
import com.kpstv.yts.extensions.MovieBase
import com.kpstv.yts.extensions.execute
import com.kpstv.yts.extensions.hide
import com.kpstv.yts.extensions.utils.AppUtils.Companion.getBulletSymbol
import com.kpstv.yts.extensions.utils.AppUtils.Companion.getImdbUrl
import com.kpstv.yts.extensions.utils.AppUtils.Companion.launchUrl
import com.kpstv.yts.extensions.utils.GlideApp
import com.kpstv.yts.ui.activities.FinalActivity
import kotlinx.android.synthetic.main.item_common_banner.view.*

/** An adapter class to manage the pagination library
 */
class CustomPagedAdapter(
    private val context: Context,
    private val base: MovieBase
) :
    PagedListAdapter<MovieShort, CustomPagedAdapter.CustomHolder>(DIFF_CALLBACK) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
        /** We will show different layout when the item count is only one.
         *  This is implemented in SearchActivity
         */
        if (itemCount == 1) {
            CustomHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_search_single_main, parent, false
                )
            )
        } else {
            CustomHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_more, parent, false
                )
            )
        }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: CustomHolder, position: Int) {
        val movie = getItem(position)

        if (movie != null) {

            /** This are some extra stuffs we are doing when count it one
             *  i.e there is only single search
             */
            if (itemCount == 1) {
                holder.mainSubTextView.text =
                    "${movie.year} ${getBulletSymbol()} ${movie.runtime} mins"
                holder.mainImdbButton.text = "imdb ${movie.rating}"
                holder.mainImdbButton.setOnClickListener {
                    movie.imdbCode?.let { launchUrl(context, getImdbUrl(it), IS_DARK_THEME) }
                }
                holder.mainLayout.setOnClickListener {
                    val intent = Intent(context, FinalActivity::class.java)
                    intent.putExtra(MOVIE_ID, movie.movieId)
                    context.startActivity(intent)
                }
            }

            LoadRequest.Builder(context.applicationContext)
                .data(movie.bannerUrl)
                .target(
                    onSuccess = { drawable ->
                        holder.mainImage.setImageDrawable(drawable)
                        holder.itemView.shimmerFrame.hide()
                    },
                    onError = {
                        Log.e("CustomPagedAdapter", "=> Glide failed for: ${movie.title}")
                    }
                )
                .target { drawable ->
                    holder.mainImage.setImageDrawable(drawable)
                    holder.itemView.shimmerFrame.hide()
                }
                .networkCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .execute()

          /*  Glide.with(context.applicationContext).asBitmap().load(movie.bannerUrl)
                .listener(object : RequestListener<Bitmap> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        Log.e("CustomPagedAdapter", "=> Glide failed for: ${movie.title}")
                        return false
                    }

                    override fun onResourceReady(
                        resource: Bitmap?,
                        model: Any?,
                        target: Target<Bitmap>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {
                        holder.mainImage.setImageBitmap(resource)
                        holder.itemView.shimmerFrame.hide()
                        return true
                    }

                }).into(holder.mainImage)*/
            holder.mainText.text = movie.title

            holder.mainCard.setOnClickListener {
                val intent = Intent(context, FinalActivity::class.java)
                when (base) {
                    MovieBase.YTS -> {
                        /** Passing movie Id as Int to normally fetch the movie details.
                         */
                        intent.putExtra(MOVIE_ID, movie.movieId)
                        context.startActivity(intent)
                    }
                    MovieBase.TMDB -> {
                        /** Here we are passing movie Id as string to fetch movie
                         *  using second route.
                         */
                        intent.putExtra(MOVIE_ID, "${movie.movieId}")
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    companion object {
        private val DIFF_CALLBACK: DiffUtil.ItemCallback<MovieShort?> =
            object : DiffUtil.ItemCallback<MovieShort?>() {
                override fun areItemsTheSame(oldItem: MovieShort, newItem: MovieShort) =
                    oldItem.imdbCode == newItem.imdbCode

                override fun areContentsTheSame(oldItem: MovieShort, newItem: MovieShort) =
                    oldItem == oldItem
            }
    }

    /** Some of the Id does not exist in either one of view. That's why we are
     *  using standard findViewById so that even though view doesn't exist it
     *  will be null instead of producing crash which synthetic binding would cause.
     */
    class CustomHolder(view: View) : RecyclerView.ViewHolder(view) {
        val mainCard = view.findViewById<CardView>(R.id.mainCard)
        val mainText = view.findViewById<TextView>(R.id.mainText)
        val mainImage = view.findViewById<ImageView>(R.id.mainImage)

        val mainLayout = view.findViewById<ConstraintLayout>(R.id.mainLayout)
        val mainSubTextView = view.findViewById<TextView>(R.id.mainSubText)
        val mainImdbButton = view.findViewById<Button>(R.id.mainImdbButton)
    }
}