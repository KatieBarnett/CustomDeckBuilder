package dev.katiebarnett.decktagram.presentation.util

interface OnItemClickListener<T> {
    fun onItemClicked(item: T)
}