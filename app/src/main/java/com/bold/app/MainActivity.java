package com.bold.app;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.media.ExifInterface;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Base64;
import android.webkit.JavascriptInterface;
import android.webkit.ValueCallback;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import androidx.core.content.FileProvider;
import java.io.ByteArrayOutputStream;
import java.io.File;

public class MainActivity extends Activity {
    private WebView webView;
    private ValueCallback<Uri[]> fileCallback;
    private static final int FILE_REQUEST = 1001;
    private static final int CAMERA_REQUEST = 1002;
    private static final int NOTIFICATION_REQUEST = 2001;
    private Uri cameraOutputUri;
    private File cameraOutputFile;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        webView = new WebView(this);
        setContentView(webView);
        WebSettings s = webView.getSettings();
        s.setJavaScriptEnabled(true);
        s.setDomStorageEnabled(true);
        s.setAllowFileAccess(true);
        s.setAllowContentAccess(true);
        s.setMediaPlaybackRequiresUserGesture(false);
        s.setSupportZoom(false);
        s.setBuiltInZoomControls(false);
        s.setDisplayZoomControls(false);
        webView.addJavascriptInterface(new ReminderBridge(), "AndroidReminder");
        webView.addJavascriptInterface(new AndroidBridge(), "AndroidBridge");
        webView.setWebViewClient(new WebViewClient() {
            @Override public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url != null && url.startsWith("mailto:")) {
                    try { startActivity(new Intent(Intent.ACTION_SENDTO, Uri.parse(url))); }
                    catch (Exception e) { view.evaluateJavascript("toast('برنامه ایمیل پیدا نشد.')", null); }
                    return true;
                }
                return false;
            }
        });
        webView.setWebChromeClient(new WebChromeClient() {
            @Override public boolean onShowFileChooser(WebView view, ValueCallback<Uri[]> callback, FileChooserParams params) {
                if (fileCallback != null) fileCallback.onReceiveValue(null);
                fileCallback = callback;
                try {
                    Intent i = params.createIntent();
                    startActivityForResult(i, FILE_REQUEST);
                    return true;
                } catch (Exception e) {
                    fileCallback = null;
                    return false;
                }
            }
        });
        webView.loadUrl("file:///android_asset/index.html");
    }

    private void launchCamera() {
        try {
            if (Build.VERSION.SDK_INT >= 23 && checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_REQUEST);
                return;
            }
            cameraOutputFile = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), "BOLD_CAMERA_" + System.currentTimeMillis() + ".jpg");
            File parent = cameraOutputFile.getParentFile();
            if (parent != null && !parent.exists()) parent.mkdirs();
            cameraOutputUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", cameraOutputFile);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, cameraOutputUri);
            intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION | Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivityForResult(intent, CAMERA_REQUEST);
        } catch (Exception e) {
            webView.post(() -> webView.evaluateJavascript("toast('دوربین در این دستگاه در دسترس نیست.')", null));
        }
    }

    private void sendCameraImageToWeb(final File photo) {
        if (photo == null || !photo.exists()) return;
        try {
            final int maxDim = 1600;
            String path = photo.getAbsolutePath();
            BitmapFactory.Options bounds = new BitmapFactory.Options();
            bounds.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(path, bounds);
            int sample = 1;
            int longSide = Math.max(bounds.outWidth, bounds.outHeight);
            while (longSide / (sample * 2) >= maxDim) sample *= 2;
            BitmapFactory.Options opts = new BitmapFactory.Options();
            opts.inSampleSize = sample;
            Bitmap bitmap = BitmapFactory.decodeFile(path, opts);
            if (bitmap == null) throw new Exception("decode");

            // Rotate according to the EXIF orientation so the photo is not sideways
            int degrees = 0;
            try {
                int o = new ExifInterface(path).getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
                if (o == ExifInterface.ORIENTATION_ROTATE_90) degrees = 90;
                else if (o == ExifInterface.ORIENTATION_ROTATE_180) degrees = 180;
                else if (o == ExifInterface.ORIENTATION_ROTATE_270) degrees = 270;
            } catch (Exception ignored) {}
            Bitmap prepared = bitmap;
            if (degrees != 0) {
                Matrix m = new Matrix();
                m.postRotate(degrees);
                prepared = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, true);
                if (prepared != bitmap) bitmap.recycle();
            }
            int max = Math.max(prepared.getWidth(), prepared.getHeight());
            if (max > maxDim) {
                float ratio = maxDim / (float) max;
                Bitmap scaled = Bitmap.createScaledBitmap(prepared, Math.round(prepared.getWidth() * ratio), Math.round(prepared.getHeight() * ratio), true);
                if (scaled != prepared) prepared.recycle();
                prepared = scaled;
            }
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            prepared.compress(Bitmap.CompressFormat.JPEG, 85, bos);
            prepared.recycle();
            String b64 = Base64.encodeToString(bos.toByteArray(), Base64.NO_WRAP);
            final String js = "onNativeCameraImage('data:image/jpeg;base64," + b64 + "')";
            webView.post(() -> webView.evaluateJavascript(js, null));
        } catch (Throwable e) {
            webView.post(() -> webView.evaluateJavascript("toast('خواندن عکس دوربین انجام نشد.')", null));
        } finally {
            try { photo.delete(); } catch (Exception ignored) {}
        }
    }

    @Override protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST) {
            final File photo = cameraOutputFile;
            cameraOutputFile = null;
            cameraOutputUri = null;
            if (photo != null) {
                if (resultCode == RESULT_OK) new Thread(() -> sendCameraImageToWeb(photo)).start();
                else { try { photo.delete(); } catch (Exception ignored) {} }
            }
            return;
        }
        if (requestCode == FILE_REQUEST && fileCallback != null) {
            Uri[] results = null;
            if (resultCode == RESULT_OK && data != null) {
                if (data.getData() != null) results = new Uri[]{data.getData()};
                else if (data.getClipData() != null) {
                    int n = data.getClipData().getItemCount();
                    results = new Uri[n];
                    for (int i = 0; i < n; i++) results[i] = data.getClipData().getItemAt(i).getUri();
                }
            }
            fileCallback.onReceiveValue(results);
            fileCallback = null;
        } else super.onActivityResult(requestCode, resultCode, data);
    }

    @Override public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_REQUEST && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) launchCamera();
        else if (requestCode == CAMERA_REQUEST) webView.post(() -> webView.evaluateJavascript("toast('اجازه دوربین داده نشد.')", null));
    }

    private void ensureNotificationPermission() {
        if (Build.VERSION.SDK_INT >= 33 && checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
            requestPermissions(new String[]{Manifest.permission.POST_NOTIFICATIONS}, NOTIFICATION_REQUEST);
        if (Build.VERSION.SDK_INT >= 31) {
            AlarmManager am = (AlarmManager)getSystemService(ALARM_SERVICE);
            if (am != null && !am.canScheduleExactAlarms()) try { startActivity(new Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM)); } catch (Exception ignored) {}
        }
    }

    private class ReminderBridge {
        @JavascriptInterface public void setMotivationReminder(int h, int m, String message) { setMotivationReminder(h, m, message, "daily"); }
        @JavascriptInterface public void setMotivationReminder(int h, int m, String message, String mode) { runOnUiThread(() -> { ensureNotificationPermission(); ReminderScheduler.schedule(MainActivity.this, h, m, message, mode); }); }
        @JavascriptInterface public void setGratitudeReminder(int h, int m) { runOnUiThread(() -> { ensureNotificationPermission(); GratitudeReminderScheduler.schedule(MainActivity.this, h, m); }); }
        @JavascriptInterface public void cancelMotivationReminder() { ReminderScheduler.cancel(MainActivity.this); }
        @JavascriptInterface public void cancelGratitudeReminder() { GratitudeReminderScheduler.cancel(MainActivity.this); }
        @JavascriptInterface public boolean isReminderEnabled() { return ReminderScheduler.isEnabled(MainActivity.this); }
        @JavascriptInterface public int getReminderHour() { return ReminderScheduler.getHour(MainActivity.this); }
        @JavascriptInterface public int getReminderMinute() { return ReminderScheduler.getMinute(MainActivity.this); }
    }

    private void hideKeyboard() {
        try {
            android.view.View v = webView;
            android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
            if (imm != null) imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
            v.clearFocus();
        } catch (Exception ignored) {}
    }

    private class AndroidBridge {
        @JavascriptInterface public void hideKeyboard() { MainActivity.this.hideKeyboard(); }
        @JavascriptInterface public void openCamera() { runOnUiThread(MainActivity.this::launchCamera); }
    }

    // Android back button: let the web layer close the photo viewer / sheets first,
    // and only leave the app when there is nothing left to close.
    @Override public void onBackPressed() {
        webView.evaluateJavascript("(function(){try{return window.handleAndroidBack?window.handleAndroidBack():false}catch(e){return false}})()", value -> {
            if (!"true".equals(value)) MainActivity.super.onBackPressed();
        });
    }
}
