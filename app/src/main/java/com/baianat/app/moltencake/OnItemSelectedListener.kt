package com.baianat.app.moltencake

interface OnItemSelectedListener<in T> {
    fun onItemSelected(selectedItem: T)
}