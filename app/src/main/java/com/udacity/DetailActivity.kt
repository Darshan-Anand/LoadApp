package com.udacity

import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.databinding.ActivityDetailBinding
import kotlinx.android.synthetic.main.activity_detail.*

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_detail
        )
        setSupportActionBar(toolbar)
        val extras = intent.extras;
        if (extras != null) {
            val status = extras.getString("status")
            val fileName = extras.getString("file_name")
            val notificationId = extras.getInt("notification_id")
            if (status != null && fileName != null) {
                cancelNotification(notificationId)
                updateFileNameAndStatus(fileName, status)
            }
        }

        binding.contentDetail.okBut.setOnClickListener {
            onBackPressed()
        }
    }

    private fun updateFileNameAndStatus(file: String, status: String) {
        binding.contentDetail.filenameBodyTv.text = file
        binding.contentDetail.statusBodyTv.text = status
    }

    private fun cancelNotification(notificationId: Int) {
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.cancel(notificationId)
    }


}
