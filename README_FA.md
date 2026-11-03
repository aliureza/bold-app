# پروژه اندروید اپ بولد

این پروژه HTML فعلی شما را به صورت آفلاین داخل WebView اندروید اجرا می‌کند.

## ساخت APK
پروژه را با Android Studio یا یک سرویس Build اندروید باز/Build کنید.
خروجی Debug APK معمولاً در:
app/build/outputs/apk/debug/

## ساخت نسخه Google Play
برای انتشار در Google Play باید نسخه Release را با keystore خودتان امضا کنید و AAB بسازید.

نکته:
- اطلاعات اصلی نسخه فعلی در localStorage ذخیره می‌شود.
- قابلیت‌های تبلیغاتی موجود در HTML فعلاً شبیه‌سازی شده‌اند و به شبکه تبلیغات واقعی متصل نیستند.
- برای انتشار نهایی، آیکون، نام بسته، privacy policy، امضای release و تنظیمات Google Play باید تکمیل شوند.

## ⏰ سیستم یادآوری (جدید)
یک سیستم یادآوری روزانه بومی اندروید اضافه شد:
- در صفحه اصلی اپ، کارت «⏰ یادآوری روزانه» ساعت دلخواه را از کاربر می‌پرسد: «کی بهت پیام بدم تا بیای شکر کنی؟»
- با AlarmManager هر روز در همان ساعت یک نوتیفیکیشن ارسال می‌شود.
- بعد از ری‌استارت گوشی هم یادآوری خودش دوباره فعال می‌شود (BootReceiver).
- در اندروید 13+ مجوز نوتیفیکیشن و در اندروید 12+ مجوز Exact Alarm به‌صورت خودکار درخواست می‌شود.

فایل‌های جدید:
- app/src/main/java/com/bold/app/ReminderReceiver.java
- app/src/main/java/com/bold/app/ReminderScheduler.java
- app/src/main/java/com/bold/app/BootReceiver.java
