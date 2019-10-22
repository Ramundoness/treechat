package com.example.raymondyao.cs193a_hw7_kyguo_ryao28

import android.app.*
import android.app.PendingIntent.FLAG_CANCEL_CURRENT
import android.content.Intent
import android.os.Build
import android.os.IBinder
import android.util.Log

class TreeChatService : Service() {

    companion object {
        const val TREECHAT_NOTIFICATION_ID = 1231
        const val TREECHAT_NOTIFICATION_NAME = "TreeChat"
    }
    private lateinit var channelName: String

    // Function called upon starting the service
    override fun onStartCommand(intent: Intent?, flags: Int, id: Int): Int {
        // unpack any parameters that were passed to us
        if (intent != null) {
            channelName = intent.getStringExtra("channel")

            if(intent.action == "receive") {
                makePlayerNotification()
            }
        }
        return START_STICKY   // stay running
    }

    // Function called to create a notification
    private fun makePlayerNotification() {
        // create a notification channel for new Android versions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                TREECHAT_NOTIFICATION_NAME,
                TREECHAT_NOTIFICATION_NAME,
                NotificationManager.IMPORTANCE_DEFAULT)
            val manager = getSystemService(NOTIFICATION_SERVICE)
                    as NotificationManager
            manager.createNotificationChannel(channel)
            val builder = Notification.Builder(this, TREECHAT_NOTIFICATION_NAME)
            builder.setContentTitle("TreeChat")
                .setContentText("You have a new message in #$channelName!")
                .setAutoCancel(false)
                .setSmallIcon(R.drawable.chatbox)

            val intent = Intent(this, ChannelActivity::class.java)
            intent.putExtra("serviceChannel", "$channelName")
            val pending = PendingIntent.getActivity(
                this, 0, intent, FLAG_CANCEL_CURRENT)
            builder.setContentIntent(pending)

            //send the notification
            val notification = builder.build()
            manager.notify(TREECHAT_NOTIFICATION_ID, notification)
        }
    }

    // Function called to stop the service
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }
}
