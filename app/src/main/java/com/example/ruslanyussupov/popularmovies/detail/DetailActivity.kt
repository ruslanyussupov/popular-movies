package com.example.ruslanyussupov.popularmovies.detail

import androidx.databinding.DataBindingUtil
import androidx.core.app.NavUtils
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.fragment.app.transaction
import com.example.ruslanyussupov.popularmovies.BaseActivity

import com.example.ruslanyussupov.popularmovies.R
import com.example.ruslanyussupov.popularmovies.databinding.ActivityDetailBinding
import com.example.ruslanyussupov.popularmovies.data.model.Movie
import kotlinx.android.synthetic.main.activity_detail.*


class DetailActivity : BaseActivity() {

    private lateinit var binding: ActivityDetailBinding
    var onShare: (() -> Unit)? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_detail)

        setSupportActionBar(binding.toolbar)

        val movie = intent.getParcelableExtra<Movie?>(EXTRA_MOVIE)
        val filter = intent.getStringExtra(EXTRA_FILTER)

        if (movie == null || filter == null) {
            empty_state_text_view.visibility = View.VISIBLE
            return
        }

        empty_state_text_view.visibility = View.GONE

        val actionBar = supportActionBar
        actionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = movie.originalTitle
        }

        supportFragmentManager.transaction {
            replace(R.id.fragment_container, DetailContentFragment.create(movie, filter))
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
                onShare?.invoke()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    companion object {
        const val EXTRA_FILTER = "filter"
        const val EXTRA_MOVIE = "movie"
    }

}
