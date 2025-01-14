package com.kpstv.yts.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kpstv.yts.databinding.ActivityImageViewBinding
import com.kpstv.yts.extensions.utils.GlideApp
import com.kpstv.common_moviesy.extensions.viewBinding

class ImageViewActivity : AppCompatActivity() {

    companion object {
        const val IMAGE_URL = "com.kpstv.yts.image_url"
    }

    private val binding by viewBinding(ActivityImageViewBinding::inflate)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }

        GlideApp.with(applicationContext).load(intent.extras?.getString(IMAGE_URL))
            .into(binding.photoView)
    }
}
