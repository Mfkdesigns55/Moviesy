package com.kpstv.yts.ui.fragments

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.StringDef
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SortedList
import com.danimahardhika.cafebar.CafeBar
import com.google.android.material.snackbar.Snackbar
import com.kpstv.common_moviesy.extensions.viewBinding
import com.kpstv.yts.AppInterface.Companion.MOVIE_ID
import com.kpstv.yts.R
import com.kpstv.yts.adapters.WatchlistAdapter
import com.kpstv.yts.databinding.FragmentWatchlistBinding
import com.kpstv.common_moviesy.extensions.hide
import com.kpstv.common_moviesy.extensions.show
import com.kpstv.yts.ui.activities.FinalActivity
import com.kpstv.yts.ui.activities.MainActivity
import com.kpstv.yts.ui.activities.SearchActivity
import com.kpstv.yts.ui.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.ref.WeakReference

@AndroidEntryPoint
class WatchlistFragment : Fragment(R.layout.fragment_watchlist) {

    private val viewModel by activityViewModels<MainViewModel>()

    private val binding by viewBinding(FragmentWatchlistBinding::bind)
    private lateinit var mainActivity: MainActivity

    private val TAG = "WatchListFragment"
    private lateinit var adapter: WeakReference<WatchlistAdapter>

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mainActivity = requireActivity() as MainActivity

        setToolBar()

        initRecyclerView()

        bindUI()
    }

    /** This will bind the fragment with the viewModel returning LiveData.
     */
    private fun bindUI() {
        viewModel.favouriteMovieIds.observe(viewLifecycleOwner, Observer {
            adapter.get()?.submitList(it)

            if (it.isNotEmpty()) {
                binding.layoutNoFavourite.root.hide()
            } else
                binding.layoutNoFavourite.root.show()

            /** Restore previous state of recyclerView */
            if (viewModel.watchFragmentState.recyclerViewState != null) {
                binding.recyclerView.layoutManager?.onRestoreInstanceState(viewModel.watchFragmentState.recyclerViewState)
                viewModel.watchFragmentState.recyclerViewState = null
            }
        })
    }

    private fun initRecyclerView() {
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = WeakReference(
            WatchlistAdapter(
                onClickListener = { model, _ ->
                    val intent = Intent(requireContext(), FinalActivity::class.java)
                    intent.putExtra(MOVIE_ID, model.movieId)
                    startActivity(intent)
                },
                onItemRemoveListener = { model, _ ->
                    viewModel.removeFavourite(model.movieId)

                    Snackbar.make(binding.root, getString(R.string.remove_watchlist), Snackbar.LENGTH_SHORT)
                        .setAction(getString(R.string.undo)) {
                            viewModel.addToFavourite(model)
                        }
                        .show()
                }
            )
        )
        binding.recyclerView.adapter = adapter.get()
    }

    private fun setToolBar() {
        binding.toolbar.title = getString(R.string.watchlist)
        binding.toolbar.setNavigationIcon(R.drawable.ic_menu)
        binding.toolbar.setNavigationOnClickListener {
            mainActivity.drawerLayout.openDrawer(GravityCompat.START)
        }
        binding.toolbar.inflateMenu(R.menu.fragment_watchlist_menu)

        binding.toolbar.setOnMenuItemClickListener {
            if (it.itemId == R.id.action_search) {
                val intent = Intent(mainActivity, SearchActivity::class.java)
                startActivity(intent)
            }
            return@setOnMenuItemClickListener true
        }

        /** Restoring AppBarLayout State */
        if (viewModel.watchFragmentState.isAppBarExpanded == false)
            binding.appBarLayout.collapse()
    }

    /**
     * Save all your view state here
     */
    override fun onStop() {
        super.onStop()
        viewModel.watchFragmentState.recyclerViewState =
            binding.recyclerView.layoutManager?.onSaveInstanceState()
        viewModel.watchFragmentState.isAppBarExpanded =
            binding.appBarLayout.isAppBarExpanded
    }
}
