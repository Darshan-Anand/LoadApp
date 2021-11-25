package com.udacity

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.udacity.utils.createDownloadedNotification
import com.udacity.databinding.ActivityMainBinding
import kotlinx.android.synthetic.main.activity_main.*
import timber.log.Timber


class MainActivity : AppCompatActivity() {

    private var downloadID: Long = 0
    private var downloadNumber = -1
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(
            this, R.layout.activity_main
        )
        setSupportActionBar(toolbar)
        registerReceiver(receiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))

        binding.contentMain.customButton.setOnClickListener {
            download()
        }
        binding.contentMain.downloadblesRg.setOnCheckedChangeListener { _, checkedId ->
            downloadNumber = when (checkedId) {
                R.id.glide_download_but -> 0
                R.id.loadapp_download_but -> 1
                R.id.retrofit_download_but -> 2
                else -> -1
            }
        }

    }

    private fun checkRadioButtonsClicked(): Boolean {
        return if (downloadNumber == -1) {
            Toast.makeText(this, getString(R.string.choose_file), Toast.LENGTH_SHORT).show()
            false
        } else
            true
    }


    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            val id = intent?.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
            if (id == downloadID) {
                binding.contentMain.customButton.text = getString(R.string.button_download)
                binding.contentMain.customButton.animateButton = ButtonState.Completed
                val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
                val query = DownloadManager.Query()
                query.setFilterById(id)
                val cursor = downloadManager.query(query)
                if (cursor.moveToFirst()) {
                    val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                    val detailIntent = Intent()
                    when (status) {
                        DownloadManager.STATUS_SUCCESSFUL -> {
                            detailIntent.putExtra("status", "Success")
                        }
                        DownloadManager.STATUS_FAILED -> {
                            detailIntent.putExtra("status", "Failed")
                        }
                        DownloadManager.ERROR_UNKNOWN -> {
                            detailIntent.putExtra("status", "Unknown")
                        }
                    }
                    callNotification(detailIntent)
                }
            }
        }
    }

    private fun callNotification(intent: Intent) {
        val downloadFile = getDownloadFile()
        intent.putExtra("notification_id", downloadID.toInt())
        intent.putExtra("file_name", downloadFile[1])
        createDownloadedNotification(this, intent, downloadFile[1], downloadFile[2], downloadID.toInt())
    }

    private fun download() {
        if (checkRadioButtonsClicked()) {
            binding.contentMain.customButton.text = getString(R.string.button_loading)
            binding.contentMain.customButton.animateButton = ButtonState.Loading
            val downloadFile = getDownloadFile()
            val request =
                DownloadManager.Request(Uri.parse(downloadFile[0]))
                    .setTitle(downloadFile[1])
                    .setDescription(downloadFile[2])
                    .setRequiresCharging(false)
                    .setAllowedOverMetered(true)
                    .setAllowedOverRoaming(true)
                    .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)

            val downloadManager = getSystemService(DOWNLOAD_SERVICE) as DownloadManager
            downloadID =
                downloadManager.enqueue(request)// enqueue puts the download request in the queue.
            if (downloadID != 0L) {
                Timber.d(" downloadID=$downloadID")
                binding.contentMain.customButton.isEnabled = false
            }
        }
    }

    private fun getDownloadFile(): ArrayList<String> {
        val arrayList = ArrayList<String>()
        when (downloadNumber) {
            0 -> {
                arrayList.add(0, GlideDownloadUrl)
                arrayList.add(1, getString(R.string.glide))
                arrayList.add(2,getString(R.string.glide_library_description))
            }
            1 -> {
                arrayList.add(0, LoadAppDownloadURL)
                arrayList.add(1, getString(R.string.loadapp))
                arrayList.add(2,getString(R.string.loadapp_project_description))
            }
            2 -> {
                arrayList.add(0, RetrofitDownloadUrl)
                arrayList.add(1, getString(R.string.retrofit))
                arrayList.add(2,getString(R.string.retrofit_library_description))
            }
        }
        return arrayList
    }

    companion object {
        private const val LoadAppDownloadURL =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/master.zip"
        private const val GlideDownloadUrl =
            "https://github.com/bumptech/glide/archive/refs/heads/master.zip"
        private const val RetrofitDownloadUrl =
            "https://github.com/udacity/nd940-c3-advanced-android-programming-project-starter/archive/refs/heads/master.zip"
    }

}
