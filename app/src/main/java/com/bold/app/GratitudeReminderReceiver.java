package com.bold.app;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
public class GratitudeReminderReceiver extends BroadcastReceiver {
 private static final String CHANNEL_ID="bold_gratitude_reminders";
 @Override public void onReceive(Context context,Intent intent){
  if(!GratitudeReminderScheduler.isEnabled(context))return;createChannel(context);
  Intent open=new Intent(context,MainActivity.class);open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP);int flags=PendingIntent.FLAG_UPDATE_CURRENT;if(Build.VERSION.SDK_INT>=23)flags|=PendingIntent.FLAG_IMMUTABLE;PendingIntent pi=PendingIntent.getActivity(context,1,open,flags);
  String text="وقتشه یکی از داشته‌هات رو بولد کنی و برایش شکرگزار باشی 🌱";Notification.Builder b=Build.VERSION.SDK_INT>=26?new Notification.Builder(context,CHANNEL_ID):new Notification.Builder(context);b.setSmallIcon(android.R.drawable.ic_dialog_info).setContentTitle("BOLD · شکرگزاری").setContentText(text).setAutoCancel(true).setContentIntent(pi).setStyle(new Notification.BigTextStyle().bigText(text));NotificationManager nm=(NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);if(nm!=null)nm.notify(2002,b.build());GratitudeReminderScheduler.rescheduleNextDay(context);
 }
 private void createChannel(Context c){if(Build.VERSION.SDK_INT>=26){NotificationManager nm=(NotificationManager)c.getSystemService(Context.NOTIFICATION_SERVICE);if(nm!=null)nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID,"یادآوری شکرگزاری BOLD",NotificationManager.IMPORTANCE_DEFAULT));}}
}
