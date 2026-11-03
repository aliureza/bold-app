package com.bold.app;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

public class ReminderReceiver extends BroadcastReceiver {
    public static final String CHANNEL_ID = "bold_daily_reminders";
    
    @Override 
    public void onReceive(Context context, Intent intent) {
        createChannel(context);
        Intent open = new Intent(context, MainActivity.class);
        open.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        int flags = PendingIntent.FLAG_UPDATE_CURRENT;
        if (Build.VERSION.SDK_INT >= 23) flags |= PendingIntent.FLAG_IMMUTABLE;
        PendingIntent pi = PendingIntent.getActivity(context, 0, open, flags);

        Notification.Builder b = Build.VERSION.SDK_INT >= 26
                ? new Notification.Builder(context, CHANNEL_ID)
                : new Notification.Builder(context);
        
        String message = ReminderScheduler.getMessage(context); 
        if ("daily".equals(ReminderScheduler.getMode(context))) {
            String[] q = {
                "امروز فقط یک چیز خوب را ببین.",
                "برای چیزهای کوچک هم جا برای قدردانی هست.",
                "یک نفس آرام بکش و ببین همین حالا چه چیزی خوب است.",
                "قدردانی، توجه تو را به داشته‌ها برمی‌گرداند.",
                "به خودت بابت ادامه دادن افتخار کن.",
                "امروز فقط یک قدم کافی است.",
                "چیزهای کوچک، زندگی روزمره را زیبا می‌کنند.",
                "یک دقیقه سکوت، گاهی کافی است.",
                "به تلاش‌های کوچک خودت اعتبار بده.",
                "امروز را با یک ممنونم تمام کن."
            }; 
            message = q[(java.util.Calendar.getInstance().get(java.util.Calendar.DAY_OF_YEAR) - 1) % q.length];
        }
        
        b.setSmallIcon(android.R.drawable.ic_dialog_info)
         .setContentTitle("BOLD")
         .setContentText(message)
         .setAutoCancel(true)
         .setContentIntent(pi)
         .setStyle(new Notification.BigTextStyle().bigText(message));
        
        NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (nm != null) nm.notify(2001, b.build());
        ReminderScheduler.rescheduleNextDay(context);
    }
    
    private void createChannel(Context context) {
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager nm = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (nm != null) nm.createNotificationChannel(new NotificationChannel(CHANNEL_ID, "یادآوری روزانه BOLD", NotificationManager.IMPORTANCE_DEFAULT));
        }
    }
}
