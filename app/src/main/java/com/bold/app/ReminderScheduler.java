package com.bold.app;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import java.util.Calendar;

public class ReminderScheduler {
 private static final String PREFS="bold_reminder_prefs", ENABLED="enabled", HOUR="hour", MINUTE="minute", MESSAGE="message", MODE="mode";
 private static final int REQUEST_CODE=3001;
 public static void schedule(Context c,int h,int m,String message){schedule(c,h,m,message,"fixed");}
 public static void schedule(Context c,int h,int m,String message,String mode){
  c.getSharedPreferences(PREFS,0).edit().putBoolean(ENABLED,true).putInt(HOUR,h).putInt(MINUTE,m).putString(MESSAGE,message==null?"وقتشه یک لحظه برای قدردانی به خودت هدیه بدی 🌱":message).putString(MODE,mode==null?"fixed":mode).apply();
  Calendar x=Calendar.getInstance();x.set(Calendar.HOUR_OF_DAY,h);x.set(Calendar.MINUTE,m);x.set(Calendar.SECOND,0);x.set(Calendar.MILLISECOND,0);
  if(x.getTimeInMillis()<=System.currentTimeMillis())x.add(Calendar.DAY_OF_YEAR,1);setAlarm(c,x.getTimeInMillis());
 }
 public static void rescheduleNextDay(Context c){
  if(!isEnabled(c))return; Calendar x=Calendar.getInstance();x.set(Calendar.HOUR_OF_DAY,getHour(c));x.set(Calendar.MINUTE,getMinute(c));x.set(Calendar.SECOND,0);x.set(Calendar.MILLISECOND,0);x.add(Calendar.DAY_OF_YEAR,1);setAlarm(c,x.getTimeInMillis());
 }
 public static void restoreIfEnabled(Context c){if(isEnabled(c))schedule(c,getHour(c),getMinute(c),getMessage(c),getMode(c));}
 public static void cancel(Context c){c.getSharedPreferences(PREFS,0).edit().putBoolean(ENABLED,false).apply();AlarmManager a=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);if(a!=null)a.cancel(pi(c));}
 public static boolean isEnabled(Context c){return c.getSharedPreferences(PREFS,0).getBoolean(ENABLED,false);}
 public static int getHour(Context c){return c.getSharedPreferences(PREFS,0).getInt(HOUR,20);}
 public static int getMinute(Context c){return c.getSharedPreferences(PREFS,0).getInt(MINUTE,0);}
 public static String getMessage(Context c){return c.getSharedPreferences(PREFS,0).getString(MESSAGE,"وقتشه یک لحظه برای قدردانی به خودت هدیه بدی 🌱");}
 public static String getMode(Context c){return c.getSharedPreferences(PREFS,0).getString(MODE,"fixed");}
 private static void setAlarm(Context c,long at){AlarmManager a=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);if(a==null)return;PendingIntent p=pi(c);if(Build.VERSION.SDK_INT>=31&&a.canScheduleExactAlarms())a.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,p);else if(Build.VERSION.SDK_INT>=23)a.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,p);else a.set(AlarmManager.RTC_WAKEUP,at,p);}
 private static PendingIntent pi(Context c){Intent i=new Intent(c,ReminderReceiver.class);int f=PendingIntent.FLAG_UPDATE_CURRENT;if(Build.VERSION.SDK_INT>=23)f|=PendingIntent.FLAG_IMMUTABLE;return PendingIntent.getBroadcast(c,REQUEST_CODE,i,f);}
}
