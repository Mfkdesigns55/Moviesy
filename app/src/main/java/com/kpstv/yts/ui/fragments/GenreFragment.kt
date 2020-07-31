package com.kpstv.yts.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kpstv.yts.AppInterface.Companion.GENRE_CATEGORY_LIST
import com.kpstv.yts.R
import com.kpstv.yts.databinding.FragmentGenreBinding
import com.kpstv.yts.databinding.ItemLocalGenreBinding
import com.kpstv.yts.extensions.YTSQuery
import com.kpstv.yts.extensions.utils.CustomMovieLayout
import com.kpstv.common_moviesy.extensions.viewBinding

class GenreFragment : Fragment(R.layout.fragment_genre) {

    private val binding by viewBinding(FragmentGenreBinding::bind)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.recyclerView.layoutManager = LinearLayoutManager(view.context)

        val adapter = LocalGenreAdapter(view.context, GENRE_CATEGORY_LIST) { model, _ ->
            val queryMap = YTSQuery.ListMoviesBuilder().apply {
                setGenre(model.genre)
                setOrderBy(YTSQuery.OrderBy.descending)
            }.build()

            CustomMovieLayout.invokeMoreFunction(
                requireContext(), "Based on ${model.title}", queryMap
            )
        }

        binding.recyclerView.adapter = adapter
    }

    data class LocalGenreModel(
        val title: String, @DrawableRes val drawable: Int,
        val genre: YTSQuery.Genre
    )

    class LocalGenreAdapter(
        private val context: Context,
        private val list: ArrayList<LocalGenreModel>,
        private val listener: ((LocalGenreModel, Int) -> Unit)
    ) : RecyclerView.Adapter<LocalGenreAdapter.LocalGenreHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            LocalGenreHolder(
                LayoutInflater.from(parent.context).inflate(
                    R.layout.item_local_genre,
                    parent,
                    false
                )
            )

        override fun onBindViewHolder(holder: LocalGenreHolder, i: Int) {
            holder.binding.itemTitle.text = list[i].title
            holder.binding.itemImage.setImageDrawable(
                context.getDrawable(list[i].drawable)
            )
            holder.binding.root.setOnClickListener {

                listener(list[i], i)
            }
        }

        override fun getItemCount() = list.size

        class LocalGenreHolder(view: View) : RecyclerView.ViewHolder(view) {
            val binding = ItemLocalGenreBinding.bind(view)
        }
    }
}

