package com.example.ruslanyussupov.popularmovies.detail

import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.core.app.NavUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer

import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.Result
import com.example.ruslanyussupov.popularmovies.databinding.ActivityDetailBinding
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.data.model.Video
import com.example.ruslanyussupov.popularmovies.list.MovieGridFragment


class DetailActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityDetailBinding
    private lateinit var mViewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        setSupportActionBar(mBinding.toolbar)

        val actionBar = supportActionBar

        val movie = intent.getParcelableExtra<Movie>(MovieGridFragment.EXTRA_MOVIE)

        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = movie.originalTitle
        }

        val viewModelFactory = DetailViewModelFactory(movie)
        mViewModel = ViewModelProviders.of(this, viewModelFactory).get(DetailViewModel::class.java)

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.share, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        val itemId = item.itemId

        return when (itemId) {
            android.R.id.home -> {
                NavUtils.navigateUpFromSameTask(this)
                true
            }
            R.id.action_share -> {
                onShare()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun onShare() {
        mViewModel.videosResultLiveData.observe(this, Observer<Result<List<Video>>> { result ->
            if (result.state == Result.State.SUCCESS) {
                if (result.data == null || result.data.isEmpty()) {
                    Toast.makeText(this@DetailActivity, getString(R.string.nothing_to_share),
                            Toast.LENGTH_SHORT).show()
                    return@Observer
                }
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.apply {
                    type = "text/plain"
                    putExtra(Intent.EXTRA_SUBJECT, getString(R.string.action_share_subject))
                    putExtra(Intent.EXTRA_TEXT, result.data[0].url)
                }
                startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share_subject)))
            }
        })
    }

}
