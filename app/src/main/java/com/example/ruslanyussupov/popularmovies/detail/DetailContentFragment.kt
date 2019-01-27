package com.example.ruslanyussupov.popularmovies.detail


import androidx.lifecycle.ViewModelProviders
import android.content.Intent
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
import com.example.ruslanyussupov.popularmovies.list.MovieGridFragment
import com.example.ruslanyussupov.popularmovies.Result
import com.squareup.picasso.Picasso


import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import timber.log.Timber

class DetailContentFragment : Fragment() {

    private var mMovie: Movie? = null
    private lateinit var mBinding: FragmentDetailContentBinding
    private lateinit var mViewModel: DetailViewModel
    private var mIsFavourite: Boolean = false
    private val mVideosAdapter = VideoAdapter(emptyList(), ::onVideoClick)
    private val mReviewsAdapter = ReviewAdapter(emptyList(), ::onReviewClick)
    private var disposable: Disposable? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {

        Timber.d("onCreateView")
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_detail_content, container, false)
        return mBinding.root

    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        Timber.d("onActivityCreated")

        mBinding.videos.videosRv.adapter = mVideosAdapter
        mBinding.videos.videosRv.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false)
        val videosOffset = resources.getDimensionPixelOffset(R.dimen.video_item_offset)
        mBinding.videos.videosRv.addItemDecoration(ItemDecoration(0, 0, videosOffset, 0))


        mBinding.reviews.reviewsRv.adapter = mReviewsAdapter
        mBinding.reviews.reviewsRv.layoutManager = LinearLayoutManager(activity,
                LinearLayoutManager.HORIZONTAL, false)
        val reviewsOffset = resources.getDimensionPixelOffset(R.dimen.review_item_offset)
        mBinding.reviews.reviewsRv.addItemDecoration(ItemDecoration(0, 0, reviewsOffset, 0))

        setFavBtnClickListener()

        if (savedInstanceState == null) {

            val intent = activity?.intent

            if (intent?.hasExtra(MovieGridFragment.EXTRA_MOVIE) == true) {
                mMovie = intent.getParcelableExtra(MovieGridFragment.EXTRA_MOVIE)
            } else if (arguments?.containsKey(MovieGridFragment.EXTRA_MOVIE) == true) {
                mMovie = arguments?.getParcelable(MovieGridFragment.EXTRA_MOVIE)
            }

        } else {

            mMovie = savedInstanceState.getParcelable(BUNDLE_MOVIE)
            mIsFavourite = savedInstanceState.getBoolean(BUNDLE_IS_FAVOURITE)

        }

        if (mMovie != null) {
            val movie = mMovie
            val viewModelFactory = DetailViewModelFactory(movie as Movie)
            mViewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel::class.java)

            disposable = mViewModel.movieFromFavourites
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        mIsFavourite = true
                        updateUi()
                    }, {
                        mIsFavourite = false
                        updateUi()
                    })

            inflateUi()
        } else {
            showEmptyState()
        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putParcelable(BUNDLE_MOVIE, mMovie)
        outState.putBoolean(BUNDLE_IS_FAVOURITE, mIsFavourite)
        super.onSaveInstanceState(outState)
    }

    override fun onDestroy() {
        Timber.d("onDestroy")
        super.onDestroy()
        disposable!!.dispose()
    }

    private fun updateUi() {

        if (mIsFavourite) {

            mBinding.favoriteIb.isSelected = true

            if (!mViewModel.utils.hasNetworkConnection()) {
                val backdrop = BitmapFactory.decodeFile(mMovie!!.backdropLocalPath)

                if (backdrop == null) {
                    mBinding.backdropIv.setImageResource(R.drawable.backdrop_placeholder)
                } else {
                    mBinding.backdropIv.setImageBitmap(backdrop)
                }
                return
            }

        } else {
            mBinding.favoriteIb.isSelected = false
        }

        Picasso.get()
                .load(mMovie!!.fullBackdropPath)
                .error(R.drawable.backdrop_error)
                .placeholder(R.drawable.poster_placeholder)
                .into(mBinding.backdropIv)

    }

    private fun inflateUi() {
        mBinding.titleTv.text = mMovie?.originalTitle
        mBinding.releaseDateTv.text = mMovie?.releaseDate
        mBinding.userRatingTv.text = mMovie?.voteAverage.toString()
        mBinding.overviewTv.text = mMovie?.overview

        mViewModel.videosResultLiveData.observe(this, Observer { result ->
            when (result.state) {
                Result.State.SUCCESS -> if (result.data == null || result.data.isEmpty()) {
                    mBinding.videosContainer.visibility = View.GONE
                } else {
                    mBinding.videosContainer.visibility = View.VISIBLE
                    mVideosAdapter.updateData(result.data)
                }
                Result.State.ERROR -> mBinding.videosContainer.visibility = View.GONE
            }
        })

        mViewModel.reviewsResultLiveData.observe(this, Observer { result ->
            when (result.state) {
                Result.State.SUCCESS -> if (result.data == null || result.data.isEmpty()) {
                    mBinding.reviewsContainer.visibility = View.GONE
                } else {
                    mBinding.reviewsContainer.visibility = View.VISIBLE
                    mReviewsAdapter.updateData(result.data)
                }
                Result.State.ERROR -> mBinding.reviewsContainer.visibility = View.GONE
            }
        })

    }

    private fun setFavBtnClickListener() {
        mBinding.favoriteIb.setOnClickListener {
            if (mIsFavourite) {

                mBinding.favoriteIb.isSelected = false
                mIsFavourite = false
                mViewModel.deleteFromFavourites()

                Toast.makeText(activity, getString(R.string.removed_from_favourite),
                        Toast.LENGTH_SHORT).show()

            } else {

                mBinding.favoriteIb.isSelected = true
                mIsFavourite = true
                mViewModel.addToFavourites()

                Toast.makeText(activity, getString(R.string.added_to_favourite),
                        Toast.LENGTH_SHORT).show()

            }
        }
    }

    private fun showEmptyState() {

    }

    private fun onVideoClick(video: Video) {
        val packageManager = activity?.packageManager ?: return
        val openVideoIntent = Intent(Intent.ACTION_VIEW, Uri.parse(video.url))
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
