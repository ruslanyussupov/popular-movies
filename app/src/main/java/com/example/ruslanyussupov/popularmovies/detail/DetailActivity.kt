package com.example.ruslanyussupov.popularmovies.detail

import androidx.lifecycle.ViewModelProviders
import android.content.Intent
import androidx.databinding.DataBindingUtil
import androidx.core.app.NavUtils
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.fragment.app.transaction
import androidx.lifecycle.Observer

import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.Result
import com.example.ruslanyussupov.popularmovies.databinding.ActivityDetailBinding
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import com.example.ruslanyussupov.popularmovies.data.model.Video
import com.example.ruslanyussupov.popularmovies.list.MovieGridFragment
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    private lateinit var viewModel: DetailViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        setSupportActionBar(binding.toolbar)

        val movie = intent.getParcelableExtra<Movie?>(MovieGridFragment.EXTRA_MOVIE)

        if (movie == null) {
            empty_state_text_view.visibility = View.VISIBLE
            return
        }

        empty_state_text_view.visibility = View.GONE

        viewModel = ViewModelProviders.of(this, DetailViewModelFactory(movie))
                .get(DetailViewModel::class.java)

        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = movie.originalTitle
        }

        supportFragmentManager.transaction {
            replace(R.id.fragment_container, DetailContentFragment.create(movie))
        }

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
        viewModel.videosResultLiveData
                .observe(this, Observer<Result<List<Video>>> { result ->
            if (result.state == Result.State.SUCCESS) {
                if (result.data.isNullOrEmpty()) {
                    Toast.makeText(this@DetailActivity, getString(R.string.nothing_to_share),
                            Toast.LENGTH_SHORT).show()
                } else {
                    val shareIntent = Intent(Intent.ACTION_SEND)
                    shareIntent.apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, getString(R.string.action_share_subject))
                        putExtra(Intent.EXTRA_TEXT, result.data[0].url())
                    }
                    startActivity(Intent.createChooser(shareIntent, getString(R.string.action_share_subject)))
                }
            }
        })
    }

}
