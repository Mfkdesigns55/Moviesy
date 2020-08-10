package com.kpstv.yts.ui.fragments.sheets

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.viewModels
import coil.request.LoadRequest
import com.kpstv.common_moviesy.extensions.viewBinding
import com.kpstv.yts.R
import com.kpstv.yts.data.models.MovieShort
import com.kpstv.yts.data.models.response.Model
import com.kpstv.yts.databinding.BottomSheetQuickinfoBinding
import com.kpstv.yts.extensions.ExtendedBottomSheetDialogFragment
import com.kpstv.yts.extensions.execute
import com.kpstv.yts.extensions.utils.AppUtils.Companion.getBulletSymbol
import com.kpstv.yts.extensions.utils.CustomBottomItem
import com.kpstv.yts.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import es.dmoral.toasty.Toasty

@AndroidEntryPoint
class BottomSheetQuickInfo : ExtendedBottomSheetDialogFragment(R.layout.bottom_sheet_quickinfo) {

    private val viewModel by viewModels<MainViewModel>()
    private val binding by viewBinding(BottomSheetQuickinfoBinding::bind)

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val movie = arguments?.getSerializable("model") as MovieShort

        LoadRequest.Builder(requireContext())
            .data(movie.bannerUrl)
            .target { drawable ->
                binding.shimmerImageView.setImage(drawable)
            }
            .execute()

        binding.itemTitle.text = movie.title
        binding.itemSubText.text = "${movie.year} ${getBulletSymbol()} ${movie.runtime} mins"

        /** Injecting view options */
        viewModel.isFavourite({ b ->
            var title = "Add to watchlist"
            var icon = R.drawable.ic_favorite_no

            if (b) {
                title = "Remove from watchlist"
                icon = R.drawable.ic_favorite_yes
            }

            val watchlistLayout = CustomBottomItem(requireContext())
            watchlistLayout.setUp(icon, title, binding.addLayout)
            watchlistLayout.onClickListener = {
                if (b) {
                    viewModel.removeFavourite(movie.movieId!!)
                    Toasty.info(requireContext(), getString(R.string.remove_watchlist)).show()
                } else {
                    viewModel.addToFavourite(Model.response_favourite.from(movie))
                    Toasty.info(requireContext(), getString(R.string.add_watchlist)).show()
                }
                dismiss()
            }

        }, movie.movieId!!)
    }
}