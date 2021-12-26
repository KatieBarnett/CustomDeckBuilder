package dev.katiebarnett.decktagram.presentation.util

import android.view.View
import android.widget.ImageView
import androidx.annotation.IntegerRes
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import coil.load
import dev.katiebarnett.decktagram.R
import java.io.File

@BindingAdapter("visibleOrGone")
fun View.setVisibleOrGone(show: Boolean?) {
    visibility = if (show == true) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleOrGone")
fun View.setVisibleOrGone(text: String?) {
    visibility = if (!text.isNullOrBlank()) View.VISIBLE else View.GONE
}

@BindingAdapter("visibleOrInvisible")
fun View.setVisibleOrInvisible(text: String?) {
    visibility = if (!text.isNullOrBlank()) View.INVISIBLE else View.GONE
}

@BindingAdapter("loadImage")
fun ImageView.loadImage(url: String?) {
    if (!url.isNullOrBlank()) {
        if (url.startsWith("http")) {
            load(url) {
                crossfade(true)
                placeholder(R.mipmap.ic_launcher_foreground)
                error(R.mipmap.ic_launcher_foreground)
            }
        } else {
            load(File(url)) {
                crossfade(true)
                placeholder(R.mipmap.ic_launcher_foreground)
                error(R.mipmap.ic_launcher_foreground)
            }
        }
    } else {
        load(R.mipmap.ic_launcher_foreground)
    }
}

@BindingAdapter("columns")
fun RecyclerView.setColumns(@IntegerRes columns: Int) {
    layoutManager = GridLayoutManager(this.context, columns)
}

@BindingAdapter("expandCollapse")
fun ImageView.setColumns(expanded: Boolean) {
    if (expanded) {
        load(R.drawable.ic_expand_less)
    } else {
        load(R.drawable.ic_expand_more)
    }
}