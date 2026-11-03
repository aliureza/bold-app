package com.bold.app;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import java.util.Calendar;
public class GratitudeReminderScheduler {
 private static final String PREFS="bold_gratitude_reminder_prefs";
 private static final int REQUEST_CODE=3002;
 public static void schedule(Context c,int h,int m){
  c.getSharedPreferences(PREFS,0).edit().putBoolean("enabled",true).putInt("hour",h).putInt("minute",m).apply();
  Calendar x=Calendar.getInstance();x.set(Calendar.HOUR_OF_DAY,h);x.set(Calendar.MINUTE,m);x.set(Calendar.SECOND,0);x.set(Calendar.MILLISECOND,0);if(x.getTimeInMillis()<=System.currentTimeMillis())x.add(Calendar.DAY_OF_YEAR,1);setAlarm(c,x.getTimeInMillis());
 }
 public static void restoreIfEnabled(Context c){if(isEnabled(c))schedule(c,getHour(c),getMinute(c));}
 public static boolean isEnabled(Context c){return c.getSharedPreferences(PREFS,0).getBoolean("enabled",false);}
 public static int getHour(Context c){return c.getSharedPreferences(PREFS,0).getInt("hour",21);}
 public static int getMinute(Context c){return c.getSharedPreferences(PREFS,0).getInt("minute",0);}
 public static void cancel(Context c){c.getSharedPreferences(PREFS,0).edit().putBoolean("enabled",false).apply();AlarmManager a=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);if(a!=null)a.cancel(pi(c));}
 public static void rescheduleNextDay(Context c){if(!isEnabled(c))return;Calendar x=Calendar.getInstance();x.set(Calendar.HOUR_OF_DAY,getHour(c));x.set(Calendar.MINUTE,getMinute(c));x.set(Calendar.SECOND,0);x.set(Calendar.MILLISECOND,0);x.add(Calendar.DAY_OF_YEAR,1);setAlarm(c,x.getTimeInMillis());}
 private static void setAlarm(Context c,long at){AlarmManager a=(AlarmManager)c.getSystemService(Context.ALARM_SERVICE);if(a==null)return;PendingIntent p=pi(c);if(Build.VERSION.SDK_INT>=31&&a.canScheduleExactAlarms())a.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,p);else if(Build.VERSION.SDK_INT>=23)a.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP,at,p);else a.set(AlarmManager.RTC_WAKEUP,at,p);}
 private static PendingIntent pi(Context c){Intent i=new Intent(c,GratitudeReminderReceiver.class);int f=PendingIntent.FLAG_UPDATE_CURRENT;if(Build.VERSION.SDK_INT>=23)f|=PendingIntent.FLAG_IMMUTABLE;return PendingIntent.getBroadcast(c,REQUEST_CODE,i,f);}
}
