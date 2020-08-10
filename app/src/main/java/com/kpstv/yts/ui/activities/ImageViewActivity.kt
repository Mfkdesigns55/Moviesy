package com.kpstv.yts.ui.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import coil.api.load
import com.kpstv.common_moviesy.extensions.viewBinding
import com.kpstv.yts.databinding.ActivityImageViewBinding

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

        binding.photoView.load(intent.extras?.getString(IMAGE_URL))
    }
}
