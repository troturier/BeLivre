package com.openclassrooms.belivre.utils

import android.content.Context
import com.google.firebase.storage.StorageReference
import com.bumptech.glide.Glide
import com.bumptech.glide.Registry
import com.bumptech.glide.annotation.GlideModule
import com.bumptech.glide.module.AppGlideModule
import java.io.InputStream

/**
 * Custom GlideModule used to load pictures from Firestore with Glide
 */
@GlideModule
class BeLivreGlideModule : AppGlideModule() {

    override fun registerComponents(context: Context, glide: Glide, registry: Registry) {
        // Register FirebaseImageLoader to handle StorageReference
        registry.append(
            StorageReference::class.java, InputStream::class.java,
            FirebaseImageLoader.Factory()
        )
    }
}