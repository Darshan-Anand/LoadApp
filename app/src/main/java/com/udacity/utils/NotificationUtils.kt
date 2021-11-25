package com.udacity.utils

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.udacity.DetailActivity
import com.udacity.R
import timber.log.Timber


fun createNotificationChannel(context: Context) {
    Timber.d("noti channel creation called")
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            context.getString(R.string.channel_id),
            context.getString(R.string.channel_name),
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = context.getString(R.string.channel_description)
        }
        val notificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

    }
}

fun createDownloadedNotification(
    context: Context,
    intent: Intent,
    title: String,
    description: String,
    notificationId: Int
) {
    val pendingIntent : PendingIntent = getPendingIntForDetailActivity(context, intent)
    val builder = NotificationCompat.Builder(context, context.getString(R.string.channel_id))
        .setSmallIcon(R.drawable.ic_assistant_black_24dp)
        .setContentTitle(title)
        .setContentText(description)
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .addAction(R.drawable.ic_assistant_black_24dp,context.getString(R.string.notifi_action_but_name),pendingIntent)
    with(NotificationManagerCompat.from(context)) {
        notify(notificationId, builder.build())
    }
}

private fun getPendingIntForDetailActivity(context: Context, detailIntent: Intent): PendingIntent {
    detailIntent.setClass(context, DetailActivity::class.java)
    val detailPendingIntent: PendingIntent = TaskStackBuilder.create(context).run {
        addNextIntentWithParentStack(detailIntent)
        getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
    }
    return detailPendingIntent
}
