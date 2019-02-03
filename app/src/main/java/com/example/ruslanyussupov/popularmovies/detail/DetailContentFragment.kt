package com.example.ruslanyussupov.popularmovies.detail


import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import android.graphics.Bitmap
import androidx.databinding.DataBindingUtil
import android.graphics.BitmapFactory
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
import com.example.ruslanyussupov.popularmovies.adapters.ReviewAdapter
import com.example.ruslanyussupov.popularmovies.adapters.VideoAdapter
import com.example.ruslanyussupov.popularmovies.data.model.Review
import com.example.ruslanyussupov.popularmovies.data.model.Video
import com.example.ruslanyussupov.popularmovies.databinding.FragmentDetailContentBinding
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.Result
import com.squareup.picasso.Picasso


import kotlinx.android.synthetic.main.fragment_detail_content.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import timber.log.Timber

class DetailContentFragment : Fragment() {

    private var movie: Movie? = null
    private lateinit var binding: FragmentDetailContentBinding
    private lateinit var viewModel: DetailViewModel
    private var isFavourite: Boolean = false
    private val videosAdapter = VideoAdapter(emptyList(), ::onVideoClick)
    private val reviewsAdapter = ReviewAdapter(emptyList(), ::onReviewClick)

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

        } else {

            movie = savedInstanceState.getParcelable(BUNDLE_MOVIE)
            isFavourite = savedInstanceState.getBoolean(BUNDLE_IS_FAVOURITE)

        }

        if (movie == null) {

            empty_state_text_view.visibility = View.VISIBLE

        } else {

            empty_state_text_view.visibility = View.GONE

            viewModel = ViewModelProviders.of(this, DetailViewModelFactory(movie as Movie))
                    .get(DetailViewModel::class.java)

            viewModel.isFavouriteLiveData.observe(this, Observer {
                this.isFavourite = it
                initUI()
            })

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

    private fun initUI() {

        binding.movie = movie

        binding.favoriteIb.isSelected = isFavourite

        setFavBtnClickListener()

        binding.videos.videosRv.adapter = videosAdapter
        binding.videos.videosRv.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false)
        val videosOffset = resources.getDimensionPixelOffset(R.dimen.video_item_offset)
        binding.videos.videosRv.addItemDecoration(ItemDecoration(0, 0, videosOffset, 0))

        binding.reviews.reviewsRv.adapter = reviewsAdapter
        binding.reviews.reviewsRv.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false)
        val reviewsOffset = resources.getDimensionPixelOffset(R.dimen.review_item_offset)
        binding.reviews.reviewsRv.addItemDecoration(ItemDecoration(0, 0, reviewsOffset, 0))

        viewModel.videosResultLiveData.observe(this, Observer { result ->
            when (result.state) {
                Result.State.SUCCESS -> if (result.data.isNullOrEmpty()) {
                    binding.videosContainer.visibility = View.GONE
                } else {
                    binding.videosContainer.visibility = View.VISIBLE
                    videosAdapter.updateData(result.data)
                }
                Result.State.ERROR -> binding.videosContainer.visibility = View.GONE
            }
        })

        viewModel.reviewsResultLiveData.observe(this, Observer { result ->
            when (result.state) {
                Result.State.SUCCESS -> if (result.data.isNullOrEmpty()) {
                    binding.reviewsContainer.visibility = View.GONE
                } else {
                    binding.reviewsContainer.visibility = View.VISIBLE
                    reviewsAdapter.updateData(result.data)
                }
                Result.State.ERROR -> binding.reviewsContainer.visibility = View.GONE
            }
        })

    }

    private fun setFavBtnClickListener() {
        binding.favoriteIb.setOnClickListener {
            if (isFavourite) {

                binding.favoriteIb.isSelected = false
                isFavourite = false

                GlobalScope.launch {
                    viewModel.deleteFromFavourites()
                }

                Toast.makeText(activity, getString(R.string.removed_from_favourite),
                        Toast.LENGTH_SHORT).show()

            } else {

                binding.favoriteIb.isSelected = true
                isFavourite = true

                GlobalScope.launch {
                    viewModel.addToFavourites()
                }

                Toast.makeText(activity, getString(R.string.added_to_favourite),
                        Toast.LENGTH_SHORT).show()

            }
        }
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
