package activities

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.example.firestore.R

class NotificationActivity : AppCompatActivity() {
    lateinit var notificationManager: NotificationManager
    lateinit var notificationChannel: NotificationChannel
    lateinit var builder: Notification.Builder
    private val channelId = "i.apps.notifications"
    private val description = "Test notification"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        findViewById<Button>(R.id.btnNotification).setOnClickListener {
            val intent = Intent(this, AfterNotificationActivity::class.java)
            val pendingIntent =
                PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                notificationChannel =
                    NotificationChannel(channelId, description, NotificationManager.IMPORTANCE_HIGH)
                notificationChannel.enableLights(true)
                notificationChannel.lightColor = Color.GREEN
                notificationChannel.enableVibration(false)
                notificationManager.createNotificationChannel(notificationChannel)
                builder =
                    Notification.Builder(this, channelId).setContentTitle("NOTIFICATION DEMO ")
                        .setContentText("Test Notification")
                        .setSmallIcon(R.drawable.ic_launcher_foreground).setLargeIcon(
                            BitmapFactory.decodeResource(
                                this.resources, R.drawable
                                    .ic_launcher_background
                            )
                        ).setContentIntent(pendingIntent)
            }
            notificationManager.notify(1234, builder.build())
        }
    }
}