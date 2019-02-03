package com.example.ruslanyussupov.popularmovies

import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.squareup.picasso.Picasso
import timber.log.Timber

@BindingAdapter(value = ["imageUrl", "pathCached", "placeholder", "error"], requireAll = false)
fun setImageUrl(imageView: ImageView,
                imageUrl: String?,
                pathCached: String?,
                placeholder: Drawable,
                error: Drawable) {

    Timber.d("Binding $imageUrl")

    var errorPlaceholder = error

    if (pathCached != null) {
        val cache = Drawable.createFromPath(pathCached)

        if (cache != null) {
            errorPlaceholder = cache
        }
    }

    Picasso.get()
            .load(imageUrl)
            .placeholder(placeholder)
            .error(errorPlaceholder)
            .into(imageView)
}