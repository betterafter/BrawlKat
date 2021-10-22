package com.keykat.keykat.brawlkat.common

import android.app.Notification
import android.content.Context
import android.widget.RemoteViews
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.DecodeFormat
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.NotificationTarget

val option = RequestOptions()
    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
    .centerCrop()
    .priority(Priority.HIGH)
    .format(DecodeFormat.PREFER_RGB_565)

fun glideImageWithNotification(
    context: Context,
    ImageViewId: Int,
    contentView: RemoteViews?,
    notification: Notification,
    notificationId: Int,
    url: String
) {
    val notificationTarget = NotificationTarget(
        context,
        ImageViewId,
        contentView,
        notification,
        notificationId
    )

    Glide.with(context)
        .applyDefaultRequestOptions(option)
        .asBitmap()
        .load(url)
        .into(notificationTarget)
}