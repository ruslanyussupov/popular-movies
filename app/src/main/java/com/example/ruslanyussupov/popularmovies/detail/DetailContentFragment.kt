package com.example.ruslanyussupov.popularmovies.detail


import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer

import com.example.ruslanyussupov.popularmovies.ItemDecoration
import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.adapters.VideoAdapter
import com.example.ruslanyussupov.popularmovies.data.model.Review
import com.example.ruslanyussupov.popularmovies.data.model.Video
import com.example.ruslanyussupov.popularmovies.databinding.FragmentDetailContentBinding
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.Result
import com.example.ruslanyussupov.popularmovies.adapters.ReviewAdapter
import com.example.ruslanyussupov.popularmovies.databinding.MovieDetailsBinding


import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailContentFragment : Fragment() {

    private var movie: Movie? = null
    private lateinit var binding: FragmentDetailContentBinding
    private lateinit var viewModel: DetailViewModel
    private var isFavourite: Boolean = false
    private lateinit var reviewsAdapter: ReviewAdapter
    private val videoAdapter = VideoAdapter(emptyList(), ::onVideoClick)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        Timber.d("onCreateView")
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_content, container, false)
        return binding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Timber.d("onActivityCreated")

        if (savedInstanceState == null) {
            if (arguments?.containsKey(BUNDLE_MOVIE) == true) {
                movie = arguments?.getParcelable(BUNDLE_MOVIE)
            }

            if (movie == null) {

                showEmptyState()

            } else {

                hideEmptyState()

                viewModel = ViewModelProviders.of(this, DetailViewModelFactory(movie as Movie))
                        .get(DetailViewModel::class.java)

                viewModel.isFavouriteLiveData.observe(this, Observer {
                    this.isFavourite = it
                    initUI()
                })

            }

        } else {

            movie = savedInstanceState.getParcelable(BUNDLE_MOVIE)
            isFavourite = savedInstanceState.getBoolean(BUNDLE_IS_FAVOURITE)

            if (movie == null) {

                showEmptyState()

            } else {

                hideEmptyState()

                viewModel = ViewModelProviders.of(this, DetailViewModelFactory(movie as Movie))
                        .get(DetailViewModel::class.java)

                initUI()
            }

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(BUNDLE_MOVIE, movie)
        outState.putBoolean(BUNDLE_IS_FAVOURITE, isFavourite)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
    }

    private fun createHeaderView(): View {

        val headerBinding = DataBindingUtil.inflate<MovieDetailsBinding>(layoutInflater, R.layout.movie_details, binding.reviewsRv, false)

        headerBinding.movie = movie

        headerBinding.videosRv.adapter = videoAdapter
        headerBinding.videosRv.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false)
        val videosOffset = resources.getDimensionPixelOffset(R.dimen.video_item_offset)
        headerBinding.videosRv.addItemDecoration(ItemDecoration(0, 0, videosOffset, 0))

        headerBinding.favouriteFab.isSelected = isFavourite

        headerBinding.favouriteFab.setOnClickListener {
            if (isFavourite) {

                isFavourite = false
                headerBinding.favouriteFab.isSelected = isFavourite

                GlobalScope.launch {
                    viewModel.deleteFromFavourites()
                }

                Toast.makeText(activity, getString(R.string.removed_from_favourite),
                        Toast.LENGTH_SHORT).show()

            } else {

                isFavourite = true
                headerBinding.favouriteFab.isSelected = isFavourite

                GlobalScope.launch {
                    viewModel.addToFavourites()
                }

                Toast.makeText(activity, getString(R.string.added_to_favourite),
                        Toast.LENGTH_SHORT).show()

            }
        }

        headerBinding.executePendingBindings()

        fun hideTrailers() {
            headerBinding.trailersLabel.visibility = View.GONE
            headerBinding.trailersLabelUnderline.visibility = View.GONE
            headerBinding.videosRv.visibility = View.GONE
        }

        fun showTrailers() {
            headerBinding.trailersLabel.visibility = View.VISIBLE
            headerBinding.trailersLabelUnderline.visibility = View.VISIBLE
            headerBinding.videosRv.visibility = View.VISIBLE
        }

        viewModel.videosResultLiveData.observe(this, Observer { result ->
            when (result.state) {
                Result.State.SUCCESS -> if (result.data.isNullOrEmpty()) {
                    hideTrailers()
                    videoAdapter.updateData(emptyList())
                } else {
                    showTrailers()
                    videoAdapter.updateData(result.data)
                }
                Result.State.ERROR -> videoAdapter.updateData(emptyList())
            }
        })

        return headerBinding.root
    }

    private fun initUI() {

        binding.reviewsRv.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL, false)
        reviewsAdapter = ReviewAdapter(emptyList(), createHeaderView(), ::onReviewClick)
        binding.reviewsRv.adapter = reviewsAdapter

        viewModel.reviewsResultLiveData.observe(this, Observer { result ->
            when (result.state) {
                Result.State.SUCCESS -> if (result.data.isNullOrEmpty()) {
                    reviewsAdapter.updateData(emptyList())
                } else {
                    reviewsAdapter.updateData(result.data)
                }
                Result.State.ERROR -> reviewsAdapter.updateData(emptyList())
            }
        })

    }

    private fun showEmptyState() {
        binding.reviewsRv.visibility = View.GONE
        binding.emptyState.visibility = View.VISIBLE
    }

    private fun hideEmptyState() {
        binding.reviewsRv.visibility = View.VISIBLE
        binding.emptyState.visibility = View.GONE
    }

    private fun onVideoClick(video: Video) {
        val packageManager = activity?.packageManager ?: return
        val openVideoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(video.url()))
        if (openVideoIntent.resolveActivity(packageManager) != null) {
            startActivity(openVideoIntent)
        }
    }

    private fun onReviewClick(review: Review) {
        val packageManager = activity?.packageManager ?: return
        val openReviewIntent = Intent(Intent.ACTION_VIEW, Uri.parse(review.url))
        if (openReviewIntent.resolveActivity(packageManager) != null) {
            startActivity(openReviewIntent)
        }
    }

    companion object {

        private const val BUNDLE_MOVIE = "movie"
        private const val BUNDLE_IS_FAVOURITE = "isFavourite"

        fun create(movie: Movie): DetailContentFragment {
            val detailContentFragment = DetailContentFragment()
            val args = Bundle()
            args.putParcelable(BUNDLE_MOVIE, movie)
            detailContentFragment.arguments = args
            return detailContentFragment
        }
    }

}
