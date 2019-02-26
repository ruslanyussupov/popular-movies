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
import com.example.ruslanyussupov.popularmovies.adapters.ReviewAdapter
import com.example.ruslanyussupov.popularmovies.data.DataSource.Filter
import com.example.ruslanyussupov.popularmovies.databinding.MovieDetailsBinding


import timber.log.Timber

class DetailContentFragment : Fragment() {

    private lateinit var binding: FragmentDetailContentBinding
    private lateinit var viewModel: DetailViewModel
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

        viewModel = ViewModelProviders.of(this).get(DetailViewModel::class.java)

        if (savedInstanceState == null) {

            val movie: Movie? = arguments?.getParcelable(BUNDLE_MOVIE)
            val filter = arguments?.getString(BUNDLE_FILTER)

            if (movie == null || filter == null) {
                showEmptyState()
                return
            }

            viewModel.movie = movie
            viewModel.filter = Filter.valueOf(filter)

        }

        hideEmptyState()

        initUI()

    }

    override fun onSaveInstanceState(outState: Bundle) {
        Timber.d("onSaveInstanceState")
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
    }

    private fun createHeaderView(): View {

        val headerBinding = DataBindingUtil.inflate<MovieDetailsBinding>(layoutInflater,
                R.layout.movie_details, binding.reviewsRv, false)

        headerBinding.movie = viewModel.movie

        headerBinding.videosRv.adapter = videoAdapter
        headerBinding.videosRv.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false)
        val videosOffset = resources.getDimensionPixelOffset(R.dimen.video_item_offset)
        headerBinding.videosRv.addItemDecoration(ItemDecoration(0, 0, videosOffset, 0))

        viewModel.isFavorite().observe(this, Observer {
            headerBinding.favouriteFab.isSelected = it
        })

        headerBinding.favouriteFab.setOnClickListener {
            viewModel.setIsFavourite(!headerBinding.favouriteFab.isSelected)
        }

        fun hideVideos() {
            headerBinding.trailersLabel.visibility = View.GONE
            headerBinding.trailersLabelUnderline.visibility = View.GONE
            headerBinding.videosRv.visibility = View.GONE
        }

        fun showVideos() {
            headerBinding.trailersLabel.visibility = View.VISIBLE
            headerBinding.trailersLabelUnderline.visibility = View.VISIBLE
            headerBinding.videosRv.visibility = View.VISIBLE
        }

        hideVideos()

        var retried = 0

        viewModel.videos().observe(this, Observer {

            if (it.isNullOrEmpty()) {
                if (retried < 1) {
                    viewModel.retryVideos()
                    retried++
                }
                if (activity is DetailActivity) {
                    val detailActivity = activity as DetailActivity
                    detailActivity.onShare = {
                        Toast.makeText(activity, getString(R.string.nothing_to_share),
                                Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                showVideos()
                videoAdapter.updateData(it)
                if (activity is DetailActivity) {
                    val detailActivity = activity as DetailActivity
                    detailActivity.onShare = {
                        val shareIntent = Intent(Intent.ACTION_SEND)
                        shareIntent.apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.action_share_subject))
                            putExtra(Intent.EXTRA_TEXT, it[0].url())
                        }
                        startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share_subject)))
                    }
                }
            }

        })

        headerBinding.executePendingBindings()

        return headerBinding.root
    }

    private fun initUI() {

        binding.reviewsRv.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.VERTICAL, false)
        reviewsAdapter = ReviewAdapter(createHeaderView(), ::onReviewClick)
        binding.reviewsRv.adapter = reviewsAdapter

        viewModel.reviews().observe(this, Observer {
            reviewsAdapter.submitList(it)
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
        private const val BUNDLE_FILTER = "filter"

        fun create(movie: Movie, filter: String): DetailContentFragment {
            val detailContentFragment = DetailContentFragment()
            val args = Bundle()
            args.putParcelable(BUNDLE_MOVIE, movie)
            args.putString(BUNDLE_FILTER, filter)
            detailContentFragment.arguments = args
            return detailContentFragment
        }
    }

}
